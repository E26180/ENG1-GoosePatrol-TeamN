extends SceneTree
# Generates the kit's TileSets (.tres) and demo.tscn from demo_layout.json (written by the pack's export script).
#   godot --headless --path . --import
#   godot --headless --path . --script res://tools/build_demo.gd
# Buyers do not need to run this — the generated files ship with the project.

const KIT := "res://cozy_world_kit"
const T := 32


func terrain_tileset(set_name: String) -> TileSet:
	var meta: Dictionary = JSON.parse_string(FileAccess.get_file_as_string("%s/terrain/atlas/%s.json" % [KIT, set_name]))
	var ts := TileSet.new()
	ts.tile_size = Vector2i(T, T)
	var src := TileSetAtlasSource.new()
	src.texture = load("%s/terrain/atlas/%s.png" % [KIT, set_name])
	src.texture_region_size = Vector2i(T, T)
	ts.add_source(src, 0)
	for code in meta.tiles:
		for xy in meta.tiles[code]:
			src.create_tile(Vector2i(xy[0], xy[1]))
	ResourceSaver.save(ts, "%s/tilesets/%s.tres" % [KIT, set_name])
	return ts


func sheet_tileset(png: String, count: int, out: String) -> TileSet:
	var ts := TileSet.new()
	ts.tile_size = Vector2i(T, T)
	var src := TileSetAtlasSource.new()
	src.texture = load(png)
	src.texture_region_size = Vector2i(T, T)
	ts.add_source(src, 0)
	for i in count:
		src.create_tile(Vector2i(i, 0))
	ResourceSaver.save(ts, out)
	return ts


func own(n: Node, root: Node) -> void:
	n.owner = root
	for c in n.get_children():
		own(c, root)


func terrain_pair(root: Node2D, set_name: String, vertices: Array, cliff: bool, skip: PackedStringArray, paint: TileSet) -> TileMapLayer:  # returns the display layer
	var world := TileMapLayer.new()
	world.name = "%s_world" % set_name
	world.tile_set = paint
	world.position = Vector2(-T / 2.0, -T / 2.0)  # demo only: lines the dual grid up with the 0,0 pixel grid
	world.self_modulate = Color(1, 1, 1, 0.45)
	for y in vertices.size():
		for x in vertices[y].size():
			if vertices[y][x] == "u":
				world.set_cell(Vector2i(x, y), 0, Vector2i.ZERO)
	var display := TileMapLayer.new()
	display.name = set_name
	display.set_script(load("res://addons/cozy_world_kit/dual_grid_layer.gd"))
	display.tile_set = load("%s/tilesets/%s.tres" % [KIT, set_name])
	display.set("atlas_json", "%s/terrain/atlas/%s.json" % [KIT, set_name])
	display.set("cliff_mode", cliff)
	display.set("skip_codes", skip)
	root.add_child(display)
	root.add_child(world)
	display.set("world", world)
	return display


func _init() -> void:
	var lay: Dictionary = JSON.parse_string(FileAccess.get_file_as_string("res://demo_layout.json"))
	DirAccess.make_dir_recursive_absolute("%s/tilesets" % KIT)
	var sets := []
	for f in DirAccess.get_files_at("%s/terrain/atlas" % KIT):
		if f.ends_with(".json"):
			sets.append(f.get_basename())
			terrain_tileset(f.get_basename())
	var grass := sheet_tileset("%s/tilesets/fills_grass.png" % KIT, 16, "%s/tilesets/fills_grass.tres" % KIT)
	var paint := sheet_tileset("res://addons/cozy_world_kit/paint.png", 1, "res://addons/cozy_world_kit/world_paint.tres")

	var root := Node2D.new()
	root.name = "Demo"
	var ground := TileMapLayer.new()
	ground.name = "grass"
	ground.tile_set = grass
	root.add_child(ground)
	for y in lay.cells.size():
		for x in lay.cells[y].size():
			var c: Array = lay.cells[y][x]
			# grass under every cell (variant 01 under terrain), so erasing a terrain never leaves a hole
			ground.set_cell(Vector2i(x, y), 0, Vector2i(int(c[1].right(2)) - 1 if c[0] == "grass" else 0, 0))
	# same priority as the reference render: water over dirt over cliff
	if lay.vertices.has("cliff_grass"):  # the free demo has no plateau
		terrain_pair(root, "cliff_grass", lay.vertices.cliff_grass, true, PackedStringArray(["llll", "uuuu"]), paint)
	terrain_pair(root, "grass_dirt", lay.vertices.grass_dirt, false, PackedStringArray(["llll"]), paint)
	var water := terrain_pair(root, "grass_water", lay.vertices.grass_water, false, PackedStringArray(["llll"]), paint)
	var glints := Node2D.new()
	glints.name = "water_glints"
	glints.set_script(load("res://addons/cozy_world_kit/water_glints.gd"))
	root.add_child(glints)
	glints.set("water", water)
	var props := Node2D.new()
	props.name = "props"
	props.y_sort_enabled = true
	root.add_child(props)
	for p in lay.props:
		var s := Sprite2D.new()
		s.name = String(p.path).get_file().get_basename()
		s.texture = load("%s/%s" % [KIT, p.path])
		s.centered = false
		s.position = Vector2(p.foot[0], p.foot[1])  # origin at the foot, so y-sort uses where the prop stands
		s.offset = Vector2(p.pos[0] - p.foot[0], p.pos[1] - p.foot[1])
		props.add_child(s, true)
	own(root, root)
	for c in root.get_children():
		if c is TileMapLayer and c.get_script() != null:
			c.clear()  # display layers rebuild from their world layer on load
	var scene := PackedScene.new()
	scene.pack(root)
	ResourceSaver.save(scene, "res://demo.tscn")
	print("demo.tscn: %d terrain tilesets · %d props" % [sets.size(), lay.props.size()])
	quit()
