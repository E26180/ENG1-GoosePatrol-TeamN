# Asset credits and licences

Every file in `assets/` must be listed here, with where it came from and its licence (QR_LICENSING).
Add a row in the same pull request that adds the asset.

| File(s) | What it is | Source | Licence | Credit needed in game? |
|---------|-----------|--------|---------|------------------------|
| `textures/items/oats.png`, `bread.png`, `energy-drink.png` | Item icons, 32x32 pixel art | Made by the team (drawn with `tools/draw_item_sprites.py`) | Our own work, no third-party content | No |
| `textures/player/student.png` | Player sprite sheet, two 32x32 walking frames | Made by the team (drawn with `tools/draw_player_sprite.py`) | Our own work, no third-party content | No |
| `textures/geese/goose.png` | Goose sprite sheet: two walking frames, wings up, honking | Made by the team (drawn with `tools/draw_goose_sprite.py`) | Our own work, no third-party content | No |
| `textures/events/backpack.png`, `duck.png` | Hidden-event props: a lost backpack, a tall duck by a bucket | Made by the team (drawn with `tools/draw_event_sprites.py`) | Our own work, no third-party content | No |
| `audio/honk.wav`, `eat.wav`, `event.wav`, `wave.wav`, `win.wav`, `lose.wav` | Sound effects | Made by the team (synthesised with `tools/make_sounds.py`) | Our own work, no third-party content | No |
| *(inside the libGDX jar)* `lsans-15.fnt/.png` | Default HUD font, used through `new BitmapFont()` | Ships with libGDX 1.14.2 | Liberation Sans, SIL Open Font License 1.1 | No |
| `maps/Campus-Goose Patrol.tmx`, `maps/*.tsx` | Campus map and its tilesets, made in Tiled | Made by the team (Aybike) | Our own work; uses the tile packs below | No |
| `maps/cozy_world_kit_32_free/` | Cozy World Kit 32 (free): grass, paths, water and props | VibeForge, <https://vibeforge.itch.io> (licence in `LICENSE.txt` in the folder) | Free for personal and commercial use and may be modified; may not be redistributed as standalone assets, so only the tiles the map uses should stay in the repo. The author states the tiles were made with PixelLab's AI tools | No (credited here and on the website) |
| `maps/Mini Nature Tileset Free/` | Mini Nature Tileset 32x32: grass, dirt, water, stone, bush, tree | Anonymous author (licence in `readme.txt.txt`) | Free for personal and commercial use, may be modified and redistributed, no attribution required | No |
| `maps/Ranitaya’s 50+ City Essential Assets Pack/` | Buildings, benches, vending machines, signs, trees | Ranitaya Studios, <https://ranitaya-studios.itch.io> | No licence file in the pack; the author says in the itch.io comments that it can be used freely. Confirm on the store page before release | No (credited here and on the website) |
