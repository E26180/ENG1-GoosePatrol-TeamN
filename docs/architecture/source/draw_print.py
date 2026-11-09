"""Draws the four diagrams printed in Arch1, sized so every label is at least 10 pt on the page.

Usage: python3 draw_print.py <output folder>
Writes print-component.svg, print-class.svg, print-activity.svg and print-sequence.svg.

The assessment asks for at least 10 pt text in figures. A label of FONT px is printed at
10 pt or more if the drawing is no wider than about 720 px on a portrait page or 1090 px on a
landscape page (Arch1 prints them at 100% width). These diagrams therefore keep fewer
details; the full-detail versions (draw_final.py, draw_items.py) are on the website.
"""
import sys
from pathlib import Path
from xml.sax.saxutils import escape

FONT = 14            # smallest label, in px
INK = "#222"
GREY = "#666"
SANS = "Arial, Helvetica, sans-serif"
COLOURS = {
    "root": "#ffffff", "screen": "#fff1dc", "game": "#e9f4e9", "player": "#e4eefb",
    "item": "#f1e9f9", "map": "#fbe9e9", "event": "#fbf6d8", "goose": "#e3f1f0", "a2": "#f2f2f2",
}


def t(x, y, s, size=FONT, weight="normal", anchor="start", fill=INK, style="normal"):
    return (f'<text x="{x}" y="{y}" font-family="{SANS}" font-size="{size}" font-weight="{weight}" '
            f'font-style="{style}" text-anchor="{anchor}" fill="{fill}">{escape(s)}</text>')


def svg(w, h, body):
    defs = ('<defs>'
            f'<marker id="a" viewBox="0 0 10 10" refX="10" refY="5" markerWidth="9" markerHeight="9" orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10 z" fill="{INK}"/></marker>'
            f'<marker id="o" viewBox="0 0 10 10" refX="10" refY="5" markerWidth="9" markerHeight="9" orient="auto-start-reverse"><path d="M0,0 L10,5 L0,10" fill="none" stroke="{INK}" stroke-width="1.4"/></marker>'
            f'<marker id="tri" viewBox="0 0 12 12" refX="12" refY="6" markerWidth="13" markerHeight="13" orient="auto-start-reverse"><path d="M0,0 L12,6 L0,12 z" fill="#fff" stroke="{INK}" stroke-width="1.2"/></marker>'
            f'<marker id="dia" viewBox="0 0 16 10" refX="0" refY="5" markerWidth="16" markerHeight="10" orient="auto-start-reverse"><path d="M0,5 L8,0 L16,5 L8,10 z" fill="#fff" stroke="{INK}" stroke-width="1.2"/></marker>'
            '</defs>')
    return (f'<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}">'
            f'<rect width="{w}" height="{h}" fill="#fff"/>{defs}' + "".join(body) + "</svg>")


def line(points, end=None, start=None, dashed=False, width=1.4):
    d = "M" + " L".join(f"{x},{y}" for x, y in points)
    extra = (' stroke-dasharray="6 4"' if dashed else "")
    if end:
        extra += f' marker-end="url(#{end})"'
    if start:
        extra += f' marker-start="url(#{start})"'
    return f'<path d="{d}" fill="none" stroke="{INK}" stroke-width="{width}"{extra}/>'


def box(x, y, w, lines, pkg, title=None, stereo=None, dashed=False, italic_title=False):
    """A box with an optional stereotype and bold title, then plain lines. Returns (svg, height)."""
    rows = []
    if stereo:
        rows.append(("«" + stereo + "»", "normal", "italic"))
    if title:
        rows.append((title, "bold", "italic" if italic_title else "normal"))
    head = len(rows)
    rows += [(s, "normal", "normal") for s in lines]
    h = 10 + 18 * len(rows)
    dash = ' stroke-dasharray="6 4"' if dashed else ""
    out = [f'<rect x="{x}" y="{y}" width="{w}" height="{h}" fill="{COLOURS[pkg]}" stroke="{GREY if dashed else INK}" stroke-width="1.3"{dash}/>']
    ty = y + 20
    for i, (s, weight, style) in enumerate(rows):
        anchor, tx = ("middle", x + w / 2) if i < head else ("start", x + 8)
        out.append(t(tx, ty, s, FONT, weight, anchor, GREY if dashed else INK, style))
        ty += 18
    if head and lines:
        sep = y + 10 + 18 * head
        out.append(f'<line x1="{x}" y1="{sep}" x2="{x + w}" y2="{sep}" stroke="{INK}" stroke-width="0.8"/>')
    return "".join(out), h


def ball(cx, cy, name, lx, ly, anchor="start"):
    return (f'<circle cx="{cx}" cy="{cy}" r="8" fill="#fff" stroke="{INK}" stroke-width="1.4"/>'
            + t(lx, ly, name, FONT, "bold", anchor, style="italic"))


# ---------------------------------------------------------------- component (portrait, 720 px)

def component():
    W, H = 720, 610
    b = [t(W / 2, 18, "Components and the interfaces between them", 16, "bold", "middle")]

    def comp(x, y, w, name, pkg, lines, dashed=False):
        s, h = box(x, y, w, lines, pkg, name, None, dashed)
        b.append(s)
        return h

    comp(230, 32, 260, "Game Screen", "screen", ["screen package: input, drawing,", "HUD, start/pause/result screens"])
    comp(0, 170, 220, "Game Rules", "game", ["GameSession, GameTimer", "FR_TIMER, FR_JUDGEMENT"])
    comp(250, 170, 220, "Player", "player", ["Player", "FR_AVATAR, FR_CONTROLS"])
    comp(500, 170, 220, "Campus Map", "map", ["MapManager, CollisionManager", "FR_CAMPUS_MAP"])
    comp(0, 380, 220, "Hidden Events", "event", ["IHiddenEvent, EventSpot", "FR_EVENTS"])
    comp(250, 380, 220, "Inventory and Items", "item", ["Inventory, Item and", "three item classes"])
    comp(500, 380, 220, "Geese", "goose", ["GooseManager, Goose", "FR_ENEMY, FR_WAVES"])
    # the screen uses every part once a frame
    b.append(line([(300, 114), (300, 140), (110, 140), (110, 170)], "a"))
    b.append(line([(360, 114), (360, 170)], "a"))
    b.append(line([(420, 114), (420, 140), (610, 140), (610, 170)], "a"))
    b.append(t(372, 135, "calls each frame"))
    # CollisionChecker: offered by the map and the geese, needed by the player
    b.append(line([(500, 210), (488, 210)]))
    b.append(line([(470, 210), (480, 210)], "o", dashed=True))
    b.append(ball(486, 210, "", 0, 0))
    b.append(t(486, 262, "CollisionChecker", FONT, "bold", "middle", style="italic"))
    b.append(line([(486, 380), (486, 218)]))
    # SpeedBoostTarget: offered by the player, needed by the items
    b.append(ball(360, 300, "SpeedBoostTarget", 372, 305))
    b.append(line([(360, 252), (360, 292)]))
    b.append(line([(360, 380), (360, 308)], "o", dashed=True))
    # GooseLocator / Feedable: offered by the geese, needed by the items
    b.append(ball(486, 440, "", 0, 0))
    b.append(line([(500, 440), (494, 440)]))
    b.append(line([(470, 440), (478, 440)], "o", dashed=True))
    b.append(t(486, 498, "GooseLocator,", FONT, "bold", "middle", style="italic"))
    b.append(t(486, 516, "Feedable", FONT, "bold", "middle", style="italic"))
    # WaveStatus: offered by the geese, needed by the rules
    b.append(ball(660, 330, "WaveStatus", 650, 322, "end"))
    b.append(line([(660, 380), (660, 338)]))
    b.append(line([(110, 252), (110, 290), (240, 290), (240, 330), (652, 330)], "o", dashed=True))
    # events change the items, the rules and the geese
    b.append(line([(220, 420), (250, 420)], "a"))
    b.append(line([(60, 380), (60, 252)], "a"))
    b.append(t(68, 330, "events + 1"))
    b.append(line([(110, 462), (110, 570), (610, 570), (610, 462)], "a"))
    b.append(t(360, 590, "speedUp (goose speed event)", anchor="middle"))
    return svg(W, H, b)


# ---------------------------------------------------------------- class overview (landscape, 1090 px)

def classes():
    W, H = 1090, 652
    b = [t(W / 2, 18, "Classes of every package, with the main relationships (full detail on the website)", 16, "bold", "middle")]
    placed = {}

    def frame(x1, y1, x2, y2, name):
        tab = len(name) * 8 + 16
        b.append(f'<path d="M{x1},{y1} L{x1 + tab},{y1} L{x1 + tab},{y1 + 20} L{x2},{y1 + 20} L{x2},{y2} L{x1},{y2} Z" fill="none" stroke="#555" stroke-width="1.1"/>')
        b.append(t(x1 + 6, y1 + 15, name, FONT, "bold", fill="#444"))

    def c(key, x, y, w, pkg, lines=(), stereo=None, title=None, italic=False):
        s, h = box(x, y, w, list(lines), pkg, title or key, stereo, italic_title=italic)
        b.append(s)
        placed[key] = (x, y, w, h)

    frame(5, 30, 360, 262, "screen")
    c("GameScreen", 15, 58, 160, "screen", ["update, draw"])
    c("Hud", 190, 58, 160, "screen", ["timer, goals, bar"])
    c("WorldRenderer", 15, 140, 160, "screen", ["geese, props, signs"])
    c("Sfx", 190, 140, 160, "screen", ["sounds, mute"])
    c("Landmark", 15, 208, 160, "screen", [], stereo="record")
    frame(375, 30, 730, 262, "game")
    c("GameSession", 385, 58, 165, "game", ["state, pause, result"])
    c("GameTimer", 560, 58, 160, "game", ["5-minute countdown"])
    c("WaveStatus", 385, 140, 165, "game", [], stereo="interface")
    c("EventSpot", 560, 140, 160, "game", ["triggers once"])
    c("GameState", 385, 208, 165, "game", [], stereo="enumeration")
    c("Tutorial", 560, 208, 160, "game", [])
    frame(745, 30, 1085, 262, "root (map)")
    c("Main", 755, 58, 150, "root", ["opens GameScreen"])
    c("MapManager", 920, 58, 155, "map", ["Tiled map"])
    c("CollisionManager", 920, 168, 155, "map", ["collidesWith"])
    frame(5, 276, 360, 420, "player")
    c("Player", 15, 304, 160, "player", ["moves in steps"])
    c("CollisionChecker", 190, 304, 160, "player", [], stereo="interface")
    frame(375, 276, 730, 420, "hidden_event")
    c("IHiddenEvent", 455, 304, 190, "event", [], stereo="interface")
    c("HEItems", 395, 380, 140, "event", [])
    c("HEGooseSpeed", 565, 380, 145, "event", [])
    frame(745, 276, 1085, 420, "goose")
    c("GooseManager", 755, 304, 160, "goose", ["waves, blocking"])
    c("Goose", 930, 304, 145, "goose", ["hunger, warning"])
    c("Wave", 755, 368, 160, "goose", [], stereo="record")
    c("GooseType", 930, 368, 145, "goose", [], stereo="enumeration")
    frame(5, 434, 1085, 650, "item")
    c("InventoryInput", 15, 462, 160, "item", ["keys 1-9"])
    c("Inventory", 190, 462, 150, "item", ["0..9 items"])
    c("Item", 395, 462, 150, "item", ["use, cooldown"], stereo="abstract", italic=True)
    c("OatsItem", 335, 580, 110, "item", [])
    c("BreadItem", 455, 580, 110, "item", [])
    c("EnergyDrinkItem", 575, 580, 160, "item", [])
    c("GooseLocator", 760, 462, 150, "item", [], stereo="interface")
    c("Feedable", 930, 462, 145, "item", [], stereo="interface")
    c("SpeedBoostTarget", 760, 560, 160, "item", [], stereo="interface")

    def mid(k, side):
        x, y, w, h = placed[k]
        return {"top": (x + w / 2, y), "bottom": (x + w / 2, y + h), "left": (x, y + h / 2), "right": (x + w, y + h / 2)}[side]

    # inside packages
    b.append(line([mid("InventoryInput", "right"), mid("Inventory", "left")], "a"))
    b.append(line([(340 + 16, 490), (395, 490)], start="dia"))
    stem = 560
    b.append(line([(470, stem), mid("Item", "bottom")], "tri"))
    b.append(line([(390, 580), (390, stem), (655, stem), (655, 580)]))
    b.append(line([(510, 580), (510, stem)]))
    b.append(line([(550, 368), mid("IHiddenEvent", "bottom")], "tri", dashed=True))
    b.append(line([(465, 380), (465, 368), (637, 368), (637, 380)], dashed=True))
    b.append(line([(997, 120 + 16), (997, 168)], start="dia"))
    b.append(line([(915 + 0, 336), (930, 336)], start="dia"))
    b.append(line([mid("Goose", "bottom"), mid("GooseType", "top")], "a"))
    b.append(line([mid("GooseManager", "bottom"), mid("Wave", "top")], "a"))
    b.append(line([mid("GameSession", "right"), mid("GameTimer", "left")], "a"))
    b.append(line([(467, 108), mid("WaveStatus", "top")], "a"))
    b.append(line([mid("Player", "right"), mid("CollisionChecker", "left")], "a"))
    # relationships across packages are listed under the figure in Arch1, to keep the lines readable
    return svg(W, H, b)


# ---------------------------------------------------------------- activity (portrait, 720 px)

def activity():
    W, H = 720, 900
    b = [t(W / 2, 18, "One frame: GameScreen.render(delta)", 16, "bold", "middle")]
    X = 235

    def node(cx, cy, w, h, lines, pkg):
        b.append(f'<rect x="{cx - w / 2}" y="{cy - h / 2}" width="{w}" height="{h}" rx="9" fill="{COLOURS[pkg]}" stroke="{INK}" stroke-width="1.3"/>')
        ty = cy - len(lines) * 18 / 2 + 14
        for i, s in enumerate(lines):
            b.append(t(cx, ty, s, FONT, "bold" if i == 0 else "normal", "middle"))
            ty += 18

    def diamond(cx, cy, w, h, lines):
        b.append(f'<polygon points="{cx},{cy - h / 2} {cx + w / 2},{cy} {cx},{cy + h / 2} {cx - w / 2},{cy}" fill="#fde6cc" stroke="#b07a2a" stroke-width="1.3"/>')
        ty = cy - len(lines) * 18 / 2 + 14
        for s in lines:
            b.append(t(cx, ty, s, FONT, anchor="middle"))
            ty += 18

    def ar(pts, s=None, at=None, anchor="start"):
        b.append(line(pts, "a"))
        if s:
            b.append(t(at[0], at[1], s, FONT, anchor=anchor))

    b.append(f'<circle cx="{X}" cy="42" r="10" fill="{INK}"/>')
    node(X, 92, 420, 62, ["new game", "load map, place player and events,", "Oats + Bread, state = READY"], "screen")
    node(X, 172, 300, 44, ["handleInput()", "keys and mouse"], "screen")
    diamond(X, 240, 150, 56, ["state?"])
    node(590, 240, 250, 62, ["READY or PAUSED", "SPACE, P or Esc plays;", "nothing moves"], "game")
    node(590, 330, 250, 44, ["WON or LOST", "result screen; R: new game"], "game")
    node(X, 330, 420, 44, ["use the selected item", "keys 1-9 choose, a click uses"], "item")
    node(X, 394, 420, 44, ["Player.update", "small steps; walls and geese block"], "player")
    node(X, 458, 420, 44, ["geese.update", "walk, warn (HONK!), snatch"], "goose")
    diamond(X, 534, 240, 70, ["near an event not", "triggered yet?"])
    node(590, 534, 250, 44, ["EventSpot", "trigger, effect, events + 1"], "event")
    node(X, 614, 300, 44, ["GameSession.update", "timer counts down"], "game")
    diamond(X, 684, 200, 56, ["all fed?"])
    node(590, 684, 250, 30, ["state = WON"], "game")
    diamond(X, 760, 200, 56, ["time is up?"])
    node(590, 760, 250, 30, ["state = LOST"], "game")
    node(X, 840, 420, 44, ["draw", "map, signs, geese, player, HUD"], "screen")

    ar([(X, 52), (X, 61)])
    ar([(X, 123), (X, 150)])
    ar([(X, 194), (X, 212)])
    ar([(X + 75, 240), (465, 240)], "ready / paused", (X + 80, 232))
    ar([(X + 40, 260), (X + 40, 282), (440, 282), (440, 330), (465, 330)], "won / lost", (330, 276))
    ar([(X, 268), (X, 308)], "playing", (X + 6, 298))
    ar([(X, 352), (X, 372)])
    ar([(X, 416), (X, 436)])
    ar([(X, 480), (X, 499)])
    ar([(X + 120, 534), (465, 534)], "yes", (X + 126, 526))
    ar([(590, 556), (590, 614), (X + 150, 614)])
    ar([(X, 569), (X, 592)], "no", (X + 6, 586))
    ar([(X, 636), (X, 656)])
    ar([(X + 100, 684), (465, 684)], "yes", (X + 106, 676))
    ar([(X, 712), (X, 732)], "no", (X + 6, 726))
    ar([(X + 100, 760), (465, 760)], "yes", (X + 106, 752))
    ar([(X, 788), (X, 818)], "no", (X + 6, 808))
    ar([(715, 240), (715, 840), (X + 210, 840)])
    b.append(line([(715, 330), (715, 330)]))
    for y in (330, 684, 760):
        b.append(line([(715, y), (715, y)]))
        b.append(line([(715, y), (715, y)]))
    b.append(line([(715, 240), (715, 240)]))
    for y in (240, 330, 684, 760):
        b.append(line([(715, y)], None) if False else "")
    b.append(line([(715, 330), (715, 330)]))
    ar([(X - 210, 840), (14, 840), (14, 172), (X - 150, 172)], "next frame", (20, 500))
    return svg(W, H, b)


# ---------------------------------------------------------------- sequence (landscape, 1090 px)

def sequence():
    W, H = 1090, 640
    b = [t(W / 2, 18, "One frame while playing (the full version with every object is on the website)", 16, "bold", "middle")]
    names = [("GameScreen", "screen"), ("player", "player"), ("map", "map"), ("spot: EventSpot", "game"),
             ("GameSession", "game"), ("geese: GooseManager", "goose"), ("Hud", "screen")]
    xs = [80, 230, 370, 520, 680, 850, 1015]
    top, bottom = 34, 620
    for (n, pkg), x in zip(names, xs):
        w = 150 if len(n) > 12 else 120
        b.append(f'<rect x="{x - w / 2}" y="{top}" width="{w}" height="30" fill="{COLOURS[pkg]}" stroke="{INK}" stroke-width="1.3"/>')
        b.append(t(x, top + 20, n, FONT, "bold", "middle"))
        b.append(f'<line x1="{x}" y1="{top + 30}" x2="{x}" y2="{bottom}" stroke="{GREY}" stroke-dasharray="5 5"/>')

    def act(i, y1, y2):
        b.append(f'<rect x="{xs[i] - 6}" y="{y1}" width="12" height="{y2 - y1}" fill="#fff" stroke="{INK}"/>')

    def msg(i, j, y, s, ret=False):
        x1 = xs[i] + (6 if j > i else -6)
        x2 = xs[j] + (-6 if j > i else 6)
        b.append(line([(x1, y), (x2, y)], "o" if ret else "a", dashed=ret))
        b.append(t((x1 + x2) / 2, y - 6, s, FONT, anchor="middle"))

    def frame(x1, y1, x2, y2, kind, guard):
        b.append(f'<rect x="{x1}" y="{y1}" width="{x2 - x1}" height="{y2 - y1}" fill="none" stroke="{INK}"/>')
        tag = len(kind) * 9 + 18
        b.append(f'<path d="M{x1},{y1 + 22} L{x1 + tag - 8},{y1 + 22} L{x1 + tag},{y1 + 14} L{x1 + tag},{y1}" fill="#fff" stroke="{INK}"/>')
        b.append(t(x1 + 5, y1 + 16, kind, FONT, "bold"))
        b.append(t(x1 + tag + 8, y1 + 16, guard, FONT))

    act(0, 70, 600)
    b.append(line([(4, 84), (74, 84)], "a"))
    b.append(t(8, 78, "render", FONT))
    msg(0, 1, 112, "update(delta, dirX, dirY)")
    act(1, 104, 196)
    frame(176, 124, 452, 196, "loop", "[each step of 4 px]")
    msg(1, 2, 166, "collidesWith")
    act(2, 160, 186)
    msg(2, 1, 186, "true: undo", ret=True)
    msg(0, 5, 226, "update(delta, x, y)")
    act(5, 218, 300)
    frame(160, 240, 940, 300, "opt", "[a warning ended and the player is close]")
    msg(5, 1, 288, "applySpeedBoost(0.6, 2): startled")
    frame(16, 316, 940, 444, "loop", "[for each EventSpot]")
    msg(0, 3, 360, "update(position)")
    act(3, 352, 410)
    msg(3, 3 if False else 0, 404, "true: triggered", ret=True)
    msg(0, 4, 432, "eventTriggered()")
    act(4, 426, 440)
    msg(0, 4, 480, "update(delta)")
    act(4, 472, 556)
    msg(4, 5, 510, "allWavesCleared()")
    act(5, 504, 520)
    frame(700, 526, 1080, 576, "alt", "[cleared] WON, [time up] LOST")
    msg(0, 6, 600, "render(...)")
    act(6, 594, 612)
    return svg(W, H, b)


if __name__ == "__main__":
    out = Path(sys.argv[1] if len(sys.argv) > 1 else ".")
    for name, draw in (("print-component.svg", component), ("print-class.svg", classes),
                       ("print-activity.svg", activity), ("print-sequence.svg", sequence)):
        (out / name).write_text(draw(), encoding="utf-8")
        print("wrote", out / name)
