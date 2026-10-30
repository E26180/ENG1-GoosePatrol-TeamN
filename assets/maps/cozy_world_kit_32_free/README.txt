Cozy World Kit — 32px Top-Down Village & Nature Tileset — FREE
==============================================================

Free starter: grass, dirt paths and a pond (water glints animate in Godot), 40 props and a ready Godot/Tiled scene.

CONTENTS

- 32 corner terrain tiles in 2 sets (grass_dirt, grass_water) + one atlas PNG/JSON per set
- 26 fill tiles
- 40 props: 26 small props (32 px), 9 large props (64 px), 5 water & shore pieces (64 px)
- palette/ (PNG, GIMP .gpl, .hex) and index.csv (every file with its size and group)
- tiled/  : Tiled 1.10+ tilesets (.tsx) and demo.tmx
- godot/  : a Godot 4.4 project with the demo scene and the cozy_world_kit addon

TECHNICAL

- Tile size 32×32. Props on transparent canvases (32, 64, 128 px); each prop's foot = bottom centre.
- Corner tiles are named <set>_<NW><NE><SE><SW>.png: l = lower terrain (grass), u = upper terrain (the path, water, sand ...), t = cliff face. The atlas JSON maps every corner code to its atlas position.
- Tiled: each corner set is a Corner wangset; demo.tmx keeps every terrain on its own layer over a grass layer. For a new terrain layer: fill it with the set's lower terrain first (Terrain brush, fill mode), then paint the upper terrain — every corner needs a known terrain. Checked with Tiled 1.11's own terrain engine: every two-terrain set paints blobs, 1- and 2-wide paths and 1-cell gaps without seams.
- Godot 4.4+: open godot/cozy_world_kit_demo/project.godot. To use the kit in your project, copy addons/cozy_world_kit/ and cozy_world_kit/. Add a TileMapLayer for painting (world layer, any tile = upper terrain) and a DualGridLayer node (display layer) with its world, tile set and atlas_json set; it redraws as you paint, in the editor and in game (tested in Godot 4.4). This is a dual-grid setup (the standard method for corner tilesets) — Godot's built-in terrain painter is not supported for these sets.
- WaterGlints (Godot): add the node, point it at the water DualGridLayer — glints appear on open water.

LICENSE

License: Commercial and personal use permitted. You may modify the assets and use them in any number of projects, including commercial games and apps. You may not resell or redistribute the assets, in original or modified form, as standalone assets or as part of another asset pack. The assets, in original or modified form, may not be used for training AI models.

AI DISCLOSURE

AI disclosure: All tiles, props and animation frames were generated with PixelLab's generative AI tools and processed through an automated pipeline (normalization, background removal, slicing, atlas assembly); some older props had their colours snapped to the kit palette and stray edge pixels removed by script in that pipeline, and one water piece (stepping_stones_water) had two baked-in terrain fragments cut from its top corners by script. The water glint animation is drawn at runtime by the included Godot script (no image files). There are no hand-drawn pixels. PixelLab has approved resale of these assets.

THE FULL KIT

The full kit has 17 terrain sets (15 more blends incl. cliffs), 601 props (buildings, trees, furniture, market, farm, harbour), a room kit and 8 animated loops: https://vibeforge.itch.io

Made by VibeForge (Hun AI Lab). Questions: reply through the store page.
More packs: https://vibeforge.itch.io
