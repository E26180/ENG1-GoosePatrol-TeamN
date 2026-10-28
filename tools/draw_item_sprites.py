"""Draws the item icons in assets/textures/items (32x32 PNG pixel art).

Usage: python3 tools/draw_item_sprites.py assets/textures/items   (needs Pillow)
Also writes a 128x128 *-preview.png for each icon; those are not committed.
"""
# 32x32 pixel-art item icons. Each grid row is 32 characters; '.' is transparent.
import sys
from PIL import Image
out = sys.argv[1]
O = "#3b2a1a"  # outline
pal_oats = {"o": O, "s": "#c9a26b", "S": "#a9824f", "h": "#e2c48f", "g": "#f3e2b3", "G": "#d8c08a", "t": "#7a5a2e", "l": "#e8d6a8"}
pal_bread = {"o": O, "c": "#c8813a", "C": "#a5642a", "h": "#e3a55c", "i": "#f6dfb0", "I": "#e8c88c"}
pal_drink = {"o": "#1e2430", "b": "#2f7fd8", "B": "#1f5fa8", "h": "#8fc4ff", "m": "#cfd6de", "M": "#9aa4ae", "y": "#ffd23f", "Y": "#e0a800", "w": "#ffffff"}

def blank():
    return [["."] * 32 for _ in range(32)]

def ellipse(g, cx, cy, rx, ry, ch):
    for y in range(32):
        for x in range(32):
            if ((x - cx) / rx) ** 2 + ((y - cy) / ry) ** 2 <= 1:
                g[y][x] = ch

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
                    if 0 <= nx < 32 and 0 <= ny < 32 and src[ny][nx] not in ".":
                        g[y][x] = o
                        break

def save(g, pal, name):
    im = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    for y in range(32):
        for x in range(32):
            c = g[y][x]
            if c != ".":
                h = pal[c].lstrip("#")
                im.putpixel((x, y), tuple(int(h[i:i + 2], 16) for i in (0, 2, 4)) + (255,))
    im.save(f"{out}/{name}.png")
    im.resize((128, 128), Image.NEAREST).save(f"{out}/{name}-preview.png")

# Bag of Oats: a sack tied at the top, oats spilling in front
g = blank()
ellipse(g, 15.5, 19, 10, 9.5, "s")
rect(g, 11, 6, 20, 11, "s")
ellipse(g, 15.5, 5, 6, 2.5, "s")
rect(g, 10, 10, 21, 11, "t")          # rope
for y in range(13, 28):
    for x in range(6, 26):
        if g[y][x] == "s" and x > 19:
            g[y][x] = "S"             # shadow side
for y in range(12, 22):
    for x in range(8, 11):
        if g[y][x] == "s":
            g[y][x] = "h"             # highlight
rect(g, 12, 15, 19, 21, "l")          # label
for x, y in ((13, 17), (15, 17), (17, 17), (14, 19), (16, 19), (18, 19)):
    g[y][x] = "G"                     # grain mark on the label
ellipse(g, 7, 27, 4, 2, "g")          # spilled oats
for x, y in ((5, 27), (7, 26), (9, 27), (6, 28)):
    g[y][x] = "G"
outline(g)
save(g, pal_oats, "oats")

# Bread: a loaf with a slice in front
g = blank()
ellipse(g, 17, 15, 11, 7, "c")
rect(g, 6, 15, 28, 22, "c")
for y in range(16, 23):
    for x in range(6, 29):
        if x > 23:
            g[y][x] = "C"
for x0 in (10, 15, 20):                # scores on the crust
    for i in range(3):
        g[11 + i][x0 + i] = "C"
for y in range(10, 14):
    for x in range(8, 13):
        if g[y][x] == "c":
            g[y][x] = "h"
ellipse(g, 9, 22, 6, 6, "C")           # slice: crust
ellipse(g, 9, 22, 4.6, 4.6, "i")       # slice: soft inside
for x, y in ((8, 21), (10, 23), (7, 24), (11, 20)):
    g[y][x] = "I"
outline(g)
save(g, pal_bread, "bread")

# Energy Drink: a can with a lightning bolt
g = blank()
rect(g, 10, 5, 21, 27, "b")
rect(g, 11, 3, 20, 4, "m")
rect(g, 11, 28, 20, 28, "m")
for y in range(5, 28):
    g[y][19] = "B"; g[y][20] = "B"; g[y][21] = "B"
    g[y][11] = "h"; g[y][12] = "h"
rect(g, 14, 2, 17, 2, "M")             # ring pull
bolt = [(17, 8), (16, 9), (17, 9), (15, 10), (16, 10), (14, 11), (15, 11), (14, 12), (15, 12), (16, 12), (17, 12), (18, 12),
        (17, 13), (16, 14), (17, 14), (15, 15), (16, 15), (14, 16), (15, 16), (13, 17), (14, 17), (15, 17), (16, 17), (17, 17),
        (16, 18), (15, 19), (16, 19), (14, 20), (15, 20), (13, 21), (14, 21), (13, 22)]
for x, y in bolt:
    g[y][x] = "y"
for x, y in ((17, 12), (18, 12), (16, 17), (17, 17)):
    g[y][x] = "Y"
rect(g, 13, 24, 18, 25, "w")           # label stripe
outline(g)
save(g, pal_drink, "energy-drink")
print("done")
