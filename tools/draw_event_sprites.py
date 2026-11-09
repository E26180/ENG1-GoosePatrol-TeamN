"""Draws the hidden-event props in assets/textures/events (32x32 PNG pixel art).

Usage: python3 tools/draw_event_sprites.py assets/textures/events   (needs Pillow)
backpack.png: a student's lost backpack (the item event).
duck.png: a tall duck staring at a bucket (the goose speed event).
Also writes 4x *-preview.png files; those are not committed.
"""
import sys
from PIL import Image
out = sys.argv[1]
O = "#1f1a24"

def blank():
    return [["."] * 32 for _ in range(32)]

def rect(g, x0, y0, x1, y1, ch):
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            g[y][x] = ch

def ellipse(g, cx, cy, rx, ry, ch):
    for y in range(32):
        for x in range(32):
            if ((x - cx) / rx) ** 2 + ((y - cy) / ry) ** 2 <= 1:
                g[y][x] = ch

def outline(g):
    src = [r[:] for r in g]
    for y in range(32):
        for x in range(32):
            if src[y][x] == ".":
                for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    nx, ny = x + dx, y + dy
                    if 0 <= nx < 32 and 0 <= ny < 32 and src[ny][nx] != ".":
                        g[y][x] = "o"
                        break

def save(g, pal, name):
    pal = dict(pal, o=O)
    im = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    for y in range(32):
        for x in range(32):
            c = g[y][x]
            if c != ".":
                h = pal[c].lstrip("#")
                im.putpixel((x, y), tuple(int(h[i:i + 2], 16) for i in (0, 2, 4)) + (255,))
    im.save(f"{out}/{name}.png")
    im.resize((128, 128), Image.NEAREST).save(f"{out}/{name}-preview.png")

# Lost backpack: purple bag with a front pocket, straps and a book sticking out
g = blank()
rect(g, 8, 10, 23, 27, "b")
ellipse(g, 15.5, 10, 8, 4, "b")
rect(g, 8, 18, 9, 27, "B")
rect(g, 11, 19, 20, 25, "p")          # front pocket
rect(g, 11, 19, 20, 19, "B")
rect(g, 15, 21, 16, 22, "z")          # zip
rect(g, 13, 4, 18, 7, "s")            # strap loop
rect(g, 14, 5, 17, 6, ".")
rect(g, 20, 4, 24, 9, "k")            # book
rect(g, 21, 5, 23, 8, "K")
outline(g)
save(g, {"b": "#6d4aa3", "B": "#523783", "p": "#8762c2", "z": "#e6c84a", "s": "#3d2a63", "k": "#d94b3d", "K": "#f2efe6"}, "backpack")

# Tall duck staring at a bucket: white runner duck on the left, blue bucket on the right
g = blank()
ellipse(g, 9, 22, 5, 5, "w")          # body, upright
rect(g, 8, 8, 10, 18, "w")            # long neck
ellipse(g, 10, 7, 3, 2.5, "w")        # head
rect(g, 13, 7, 15, 8, "y")            # beak, looking right
g[6][11] = "e"
rect(g, 6, 27, 7, 29, "y")            # feet
rect(g, 10, 27, 11, 29, "y")
rect(g, 7, 20, 10, 24, "W")           # wing shade
rect(g, 18, 18, 28, 29, "u")          # bucket
rect(g, 18, 18, 28, 19, "U")
rect(g, 19, 20, 20, 28, "l")          # shine
rect(g, 17, 14, 29, 14, "h")          # handle
rect(g, 17, 14, 17, 18, "h")
rect(g, 29, 14, 29, 18, "h")
outline(g)
save(g, {"w": "#f4f1ea", "W": "#d9d3c6", "y": "#f0a23a", "e": "#111111", "u": "#3f7fc4", "U": "#2b5e96", "l": "#7fb2e6", "h": "#555555"}, "duck")
