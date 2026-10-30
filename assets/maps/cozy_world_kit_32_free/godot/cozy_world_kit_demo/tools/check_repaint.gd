extends Node
# Addon self-test: godot --headless --path . res://tools/check_repaint.tscn
# Paints one world cell on plain grass and erases it again; the DualGridLayer must redraw the 4 display cells
# around it each time (the path the editor uses while you paint). Prints REPAINT OK / REPAINT FAIL.


func _ready() -> void:
	var demo: Node = load("res://demo.tscn").instantiate()
	add_child(demo)
	await get_tree().process_frame
	var world: TileMapLayer = demo.get_node("grass_dirt_world")
	var display: TileMapLayer = demo.get_node("grass_dirt")
	var cell := Vector2i(10, 4)  # open grass in the demo, away from the road
	var around := [cell, cell + Vector2i.RIGHT, cell + Vector2i.DOWN, cell + Vector2i.ONE]
	var before := around.filter(func(c): return display.get_cell_source_id(c) != -1).size()
	world.set_cell(cell, 0, Vector2i.ZERO)
	await get_tree().create_timer(0.4).timeout
	var painted := around.filter(func(c): return display.get_cell_source_id(c) != -1).size()
	world.erase_cell(cell)
	await get_tree().create_timer(0.4).timeout
	var erased := around.filter(func(c): return display.get_cell_source_id(c) != -1).size()
	var ok := before == 0 and painted == 4 and erased == 0
	print("REPAINT %s before=%d painted=%d erased=%d" % ["OK" if ok else "FAIL", before, painted, erased])
	get_tree().quit(0 if ok else 1)
