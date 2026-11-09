"""Draws the goose sprite sheet in assets/textures/geese (four 32x32 frames side by side).

Usage: python3 tools/draw_goose_sprite.py assets/textures/geese   (needs Pillow)
Frames: 0 and 1 walking, 2 wings up (hopping or flying away), 3 honking (neck forward, beak open).
Also writes a 4x goose-preview.png; that one is not committed.
"""
# A greylag goose, like the ones on campus, facing right.
import sys
from PIL import Image
out = sys.argv[1]
pal = {
    "o": "#2a2622",  # outline
    "b": "#9a8f82", "B": "#7d7266", "l": "#c9c0b4",  # body: grey-brown, shadow, light belly
    "w": "#e9e4dc",  # white under the tail
    "n": "#8a8075",  # neck
    "k": "#f08a24", "K": "#c96a10",  # orange beak
    "f": "#f29b46",  # feet
    "e": "#111111",  # eye
}

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

def body(g, wings_up=False):
    ellipse(g, 13, 20, 9, 5.5, "b")          # body
    ellipse(g, 13, 22, 7, 3, "l")            # belly
    rect(g, 4, 18, 6, 21, "w")               # tail
    if wings_up:
        for i in range(7):                   # raised wing
            rect(g, 8 + i, 9 + i // 2, 9 + i, 16, "B")
    else:
        ellipse(g, 12, 19, 6, 3, "B")        # folded wing

def head(g, forward=False):
    if forward:
        rect(g, 19, 15, 22, 18, "n")         # neck stretched forward
        ellipse(g, 25, 14, 3.5, 3, "n")
        rect(g, 28, 12, 31, 13, "k")         # open beak, upper
        rect(g, 28, 15, 31, 16, "K")         # open beak, lower
        g[13][25] = "e"
    else:
        rect(g, 19, 9, 21, 17, "n")          # upright neck
        ellipse(g, 21, 8, 3, 2.6, "n")
        rect(g, 24, 8, 27, 9, "k")           # beak
        g[7][22] = "e"

def feet(g, frame):
    if frame == 0:
        rect(g, 11, 26, 11, 28, "f"); rect(g, 10, 29, 12, 29, "f")
        rect(g, 16, 26, 16, 28, "f"); rect(g, 15, 29, 17, 29, "f")
    else:
        rect(g, 12, 26, 12, 28, "f"); rect(g, 12, 29, 14, 29, "f")
        rect(g, 15, 26, 15, 27, "f"); rect(g, 14, 28, 16, 28, "f")

frames = []
for kind in ("walk0", "walk1", "wings", "honk"):
    g = blank()
    body(g, wings_up=(kind == "wings"))
    head(g, forward=(kind == "honk"))
    if kind != "wings":
        feet(g, 1 if kind == "walk1" else 0)
    outline(g)
    frames.append(g)

sheet = Image.new("RGBA", (32 * len(frames), 32), (0, 0, 0, 0))
for i, g in enumerate(frames):
    for y in range(32):
        for x in range(32):
            c = g[y][x]
            if c != ".":
                h = pal[c].lstrip("#")
                sheet.putpixel((i * 32 + x, y), tuple(int(h[k:k + 2], 16) for k in (0, 2, 4)) + (255,))
sheet.save(f"{out}/goose.png")
sheet.resize((sheet.width * 4, 128), Image.NEAREST).save(f"{out}/goose-preview.png")
