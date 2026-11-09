"""Draws the player sprite sheet in assets/textures/player (two 32x32 walking frames side by side).

Usage: python3 tools/draw_player_sprite.py assets/textures/player   (needs Pillow)
Also writes a 4x student-preview.png; that one is not committed.
"""
# A student in a hoodie with a backpack, facing right. Frame 0 and frame 1 differ in the legs.
import sys
from PIL import Image
out = sys.argv[1]
pal = {
    "o": "#1f1a24",  # outline
    "s": "#f1c9a0", "S": "#d9a77c",  # skin
    "h": "#5a3a22",  # hair
    "b": "#2e5d8a", "B": "#234869", "l": "#4f86b8",  # hoodie
    "p": "#3a3f4a", "P": "#2a2e36",  # trousers
    "k": "#c0392b", "K": "#922b21",  # backpack
    "w": "#f4f4f4",  # shoes and eye
    "e": "#1f1a24",  # eye
}

def blank():
    return [["."] * 32 for _ in range(32)]

def rect(g, x0, y0, x1, y1, ch):
    for y in range(y0, y1 + 1):
        for x in range(x0, x1 + 1):
            g[y][x] = ch

def outline(g, o="o"):
    src = [r[:] for r in g]
    for y in range(32):
        for x in range(32):
            if src[y][x] == ".":
                for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    nx, ny = x + dx, y + dy
                    if 0 <= nx < 32 and 0 <= ny < 32 and src[ny][nx] != ".":
                        g[y][x] = o
                        break

def body(g):
    rect(g, 10, 13, 15, 22, "k")      # backpack behind
    rect(g, 10, 13, 11, 22, "K")
    rect(g, 13, 2, 21, 4, "h")        # hair
    rect(g, 13, 5, 21, 11, "s")       # face
    rect(g, 13, 5, 14, 9, "h")        # hair at the back
    rect(g, 13, 10, 21, 11, "S")      # chin shadow
    g[7][19] = "e"                    # eye
    rect(g, 12, 12, 22, 22, "b")      # hoodie
    rect(g, 12, 12, 13, 22, "B")
    rect(g, 15, 12, 20, 13, "l")      # hood edge
    rect(g, 18, 15, 21, 20, "B")      # arm
    rect(g, 18, 21, 20, 22, "s")      # hand

def legs(g, frame):
    if frame == 0:
        rect(g, 13, 23, 16, 28, "p")
        rect(g, 18, 23, 21, 28, "P")
        rect(g, 13, 29, 17, 30, "w")
        rect(g, 18, 29, 22, 30, "w")
    else:
        rect(g, 14, 23, 17, 27, "p")
        rect(g, 17, 23, 20, 28, "P")
        rect(g, 15, 28, 18, 29, "w")
        rect(g, 17, 29, 21, 30, "w")

sheet = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
for frame in (0, 1):
    g = blank()
    body(g)
    legs(g, frame)
    outline(g)
    for y in range(32):
        for x in range(32):
            c = g[y][x]
            if c != ".":
                h = pal[c].lstrip("#")
                sheet.putpixel((frame * 32 + x, y), tuple(int(h[i:i + 2], 16) for i in (0, 2, 4)) + (255,))
sheet.save(f"{out}/student.png")
sheet.resize((256, 128), Image.NEAREST).save(f"{out}/student-preview.png")
