extends Node
# Screenshot / frame capture of a scene: godot --path . res://tools/capture.tscn -- <scene> <out_prefix> [frames]
# frames = N → N PNGs, one per water-glint frame (glints stepped by hand, not by time); default 1;
# 0 = one PNG with the glints hidden (static ground + props, for comparison with a reference render).


func _ready() -> void:
	var args := OS.get_cmdline_user_args()
	var scene: Node = load(args[0]).instantiate()
	add_child(scene)
	var frames := int(args[2]) if args.size() > 2 else 1
	var glints := scene.find_children("*", "Node2D").filter(func(n): return n.get_script() and n.get("SHAPES") != null)
	for g in glints:
		g.set("playing", false)
		g.visible = frames > 0
	for k in 3:
		await RenderingServer.frame_post_draw
	for f in max(frames, 1):
		for g in glints:
			g.set("frame", f)
			g.queue_redraw()
		await RenderingServer.frame_post_draw
		await RenderingServer.frame_post_draw
		get_viewport().get_texture().get_image().save_png("%s_%02d.png" % [args[1], f])
	get_tree().quit()
