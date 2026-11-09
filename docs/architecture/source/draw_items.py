"""Draws the class diagram and the sequence diagram of the item package as SVG.

Usage: python3 draw_items.py <output folder>
Writes items-class.svg and items-sequence.svg. No libraries needed.
"""
import sys
from pathlib import Path
from xml.sax.saxutils import escape

FONT = "Arial, Helvetica, sans-serif"
LINE = 17          # height of one text line in a class box
PAD = 8            # padding inside a compartment
INK = "#222"
GREY = "#777"
FILL_MINE = "#eef5ee"
FILL_IFACE = "#e6f3fb"
FILL_OTHER = "#f4f4f4"


def text(x, y, s, size=13, weight="normal", style="normal", anchor="start", fill=INK):
    return (f'<text x="{x}" y="{y}" font-family="{FONT}" font-size="{size}" '
            f'font-weight="{weight}" font-style="{style}" text-anchor="{anchor}" '
            f'fill="{fill}">{escape(s)}</text>')


def header(w, h):
    return [f'<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}">',
            '<defs>',
            # filled arrow head: calls and associations
            f'<marker id="arrow" viewBox="0 0 10 10" refX="10" refY="5" markerWidth="9" markerHeight="9" orient="auto-start-reverse">'
            f'<path d="M0,0 L10,5 L0,10 z" fill="{INK}"/></marker>',
            # open arrow head: dependencies and return messages
            f'<marker id="open" viewBox="0 0 10 10" refX="10" refY="5" markerWidth="9" markerHeight="9" orient="auto-start-reverse">'
            f'<path d="M0,0 L10,5 L0,10" fill="none" stroke="{INK}" stroke-width="1.3"/></marker>',
            # hollow triangle: generalisation and realisation
            f'<marker id="tri" viewBox="0 0 12 12" refX="12" refY="6" markerWidth="13" markerHeight="13" orient="auto-start-reverse">'
            f'<path d="M0,0 L12,6 L0,12 z" fill="#fff" stroke="{INK}" stroke-width="1.2"/></marker>',
            # hollow diamond: aggregation
            f'<marker id="diamond" viewBox="0 0 16 10" refX="0" refY="5" markerWidth="16" markerHeight="10" orient="auto-start-reverse">'
            f'<path d="M0,5 L8,0 L16,5 L8,10 z" fill="#fff" stroke="{INK}" stroke-width="1.2"/></marker>',
            '</defs>',
            f'<rect width="{w}" height="{h}" fill="#fff"/>']


# ---------------------------------------------------------------- class diagram

class Box:
    def __init__(self, x, y, w, name, stereo=None, attrs=(), ops=(), kind="mine", abstract=False):
        self.x, self.y, self.w = x, y, w
        self.name, self.stereo, self.attrs, self.ops = name, stereo, list(attrs), list(ops)
        self.kind, self.abstract = kind, abstract
        self.head_h = PAD * 2 + LINE * (2 if stereo else 1)
        self.attr_h = PAD * 2 + LINE * len(self.attrs) if self.attrs else 0
        self.ops_h = PAD * 2 + LINE * len(self.ops) if self.ops else 0
        self.h = self.head_h + self.attr_h + self.ops_h

    @property
    def cx(self):
        return self.x + self.w / 2

    @property
    def bottom(self):
        return self.y + self.h

    def svg(self):
        fill = {"mine": FILL_MINE, "iface": FILL_IFACE, "other": FILL_OTHER}[self.kind]
        dash = ' stroke-dasharray="6 4"' if self.kind == "other" else ""
        stroke = GREY if self.kind == "other" else INK
        out = [f'<rect x="{self.x}" y="{self.y}" width="{self.w}" height="{self.h}" fill="{fill}" stroke="{stroke}" stroke-width="1.3"{dash}/>']
        y = self.y + PAD + 13
        if self.stereo:
            out.append(text(self.cx, y, f"«{self.stereo}»", 12, anchor="middle", fill=stroke))
            y += LINE
        out.append(text(self.cx, y, self.name, 14, "bold", "italic" if self.abstract else "normal", "middle", stroke))
        y = self.y + self.head_h
        for rows, h in ((self.attrs, self.attr_h), (self.ops, self.ops_h)):
            if not rows:
                continue
            out.append(f'<line x1="{self.x}" y1="{y}" x2="{self.x + self.w}" y2="{y}" stroke="{stroke}"{dash}/>')
            ty = y + PAD + 12
            for row in rows:
                italic = "italic" if row.startswith("#~") else "normal"
                out.append(text(self.x + PAD, ty, row.replace("#~", "#"), 12, style=italic, fill=stroke))
                ty += LINE
            y += h
        return "\n".join(out)


def path(points, marker_end=None, marker_start=None, dashed=False, color=INK):
    d = "M" + " L".join(f"{x},{y}" for x, y in points)
    extra = ' stroke-dasharray="6 4"' if dashed else ""
    if marker_end:
        extra += f' marker-end="url(#{marker_end})"'
    if marker_start:
        extra += f' marker-start="url(#{marker_start})"'
    return f'<path d="{d}" fill="none" stroke="{color}" stroke-width="1.3"{extra}/>'


def label(x, y, s, anchor="start", size=12, fill=INK):
    return text(x, y, s, size, anchor=anchor, fill=fill)


def class_diagram():
    W, H = 1200, 1190
    inv_input = Box(40, 40, 300, "InventoryInput",
                    attrs=["- inventory: Inventory"],
                    ops=["+ InventoryInput(inventory: Inventory)",
                         "+ slotForKey(keycode: int): int {static}",
                         "+ update(): void"])
    screen = Box(480, 40, 330, "GameScreen", "screen", kind="other",
                 ops=["calls update(delta) every frame",
                      "draws slotText(1..9) and getItemsUsed()",
                      "on click: useSelected(mouseX, mouseY)"])
    inventory = Box(40, 260, 360, "Inventory",
                    attrs=["+ MAX_SLOTS: int = 9 {static}",
                           "- items: List<Item>",
                           "- selected: int",
                           "- itemsUsed: int"],
                    ops=["+ addItem(item: Item): boolean",
                         "+ select(number: int): boolean",
                         "+ useSelected(x: float, y: float): boolean",
                         "+ update(delta: float): void",
                         "+ slotText(number: int): String",
                         "+ getItems(): List<Item>",
                         "+ getSelectedNumber(): int",
                         "+ getItemsUsed(): int"])
    item = Box(510, 260, 340, "Item", "abstract", abstract=True,
               attrs=["- name: String",
                      "- maxUses: int",
                      "- usesLeft: int",
                      "- cooldownSeconds: float",
                      "- cooldownLeft: float"],
               ops=["+ canUse(): boolean",
                    "+ use(x: float, y: float): boolean",
                    "+ update(delta: float): void",
                    "#~ applyEffect(x: float, y: float): boolean",
                    "+ addUses(extra: int): void",
                    "+ getName(): String",
                    "+ getUsesLeft(): int",
                    "+ getMaxUses(): int",
                    "+ getCooldownLeft(): float"])
    hidden = Box(40, 660, 300, "HEItems", "hidden_event", kind="other",
                 ops=["its EventSpot effect: addItem(new",
                      "EnergyDrinkItem(...)), addUses(10) on Bread"])
    oats = Box(380, 700, 260, "OatsItem",
               attrs=["+ HUNGER_FILLED = 0.5f", "+ START_USES = 10", "- geese: GooseLocator"],
               ops=["# applyEffect(x, y): boolean"])
    bread = Box(660, 700, 250, "BreadItem",
                attrs=["+ HUNGER_FILLED = 0.5f", "+ RADIUS_PIXELS = 30f",
                       "+ COOLDOWN_SECONDS = 3f", "+ START_USES = 5", "- geese: GooseLocator"],
                ops=["# applyEffect(x, y): boolean"])
    drink = Box(930, 700, 250, "EnergyDrinkItem",
                attrs=["+ SPEED_MULTIPLIER = 1.5f", "+ BOOST_SECONDS = 5f",
                       "+ COOLDOWN_SECONDS = 10f", "+ DEFAULT_USES = 3", "- target: SpeedBoostTarget"],
                ops=["# applyEffect(x, y): boolean"])
    locator = Box(380, 940, 380, "GooseLocator", "interface", kind="iface",
                  ops=["+ gooseAt(x, y): Feedable",
                       "+ geeseNear(x, y, radius): List<? extends Feedable>"])
    feedable = Box(790, 940, 160, "Feedable", "interface", kind="iface",
                   ops=["+ feed(amount: float)"])
    boost = Box(970, 940, 210, "SpeedBoostTarget", "interface", kind="iface",
                ops=["+ applySpeedBoost(", "    multiplier, seconds)"])
    goose_mgr = Box(420, 1100, 300, "GooseManager", "enemy, planned", kind="other")
    goose = Box(790, 1100, 160, "Goose", "enemy, planned", kind="other")
    player = Box(990, 1100, 170, "Player", "player", kind="other")

    boxes = [inv_input, screen, inventory, item, hidden, oats, bread, drink,
             locator, feedable, boost, goose_mgr, goose, player]
    out = header(W, H)
    out.append(text(W / 2, 24, "Package uk.ac.york.eng1.goosepatrol.item: class diagram", 16, "bold", anchor="middle"))

    # InventoryInput -> Inventory
    out.append(path([(140, inv_input.bottom), (140, inventory.y)], "arrow"))
    out.append(label(148, inv_input.bottom + 26, "select(1..9)"))
    # GameScreen -> Inventory (elbow into the free top-right corner of Inventory)
    out.append(path([(screen.x, 110), (370, 110), (370, inventory.y)], "arrow", dashed=True, color=GREY))
    out.append(label(378, inventory.y - 12, "uses", fill=GREY))
    # Inventory <>-- Item : 0..9
    out.append(path([(inventory.x + inventory.w, 330), (item.x, 330)], "arrow", "diamond"))
    out.append(label(inventory.x + inventory.w + 20, 322, "items"))
    out.append(label(item.x - 8, 322, "0..9", "end"))
    # generalisation: one shared triangle under Item
    stem_y = 670
    out.append(path([(item.cx, stem_y), (item.cx, item.bottom)], "tri"))
    out.append(path([(oats.cx, oats.y), (oats.cx, stem_y), (drink.cx, stem_y), (drink.cx, drink.y)]))
    out.append(path([(bread.cx, stem_y), (bread.cx, bread.y)]))
    # HEItems -> Inventory
    out.append(path([(190, hidden.y), (190, inventory.bottom)], "arrow", dashed=True, color=GREY))
    out.append(label(198, inventory.bottom + 30, "addItem(), addUses()", fill=GREY))
    # items -> the interfaces they need
    out.append(path([(oats.cx, oats.bottom), (oats.cx, locator.y)], "arrow"))
    out.append(path([(720, bread.bottom), (720, locator.y)], "arrow"))
    out.append(path([(870, bread.bottom), (870, feedable.y)], "arrow"))
    out.append(label(878, feedable.y - 10, "feed()"))
    out.append(path([(drink.x + 190, drink.bottom), (drink.x + 190, boost.y)], "arrow"))
    # GooseLocator returns Feedables
    out.append(path([(locator.x + locator.w, 985), (feedable.x, 985)], "open", dashed=True))
    # realisations by classes in other packages
    for impl, iface in ((goose_mgr, locator), (goose, feedable), (player, boost)):
        out.append(path([(impl.cx, impl.y), (impl.cx, iface.bottom)], "tri", dashed=True))

    out.extend(b.svg() for b in boxes)

    # legend
    lx, ly = 40, 900
    out.append(f'<rect x="{lx}" y="{ly}" width="300" height="250" fill="#fff" stroke="#bbb"/>')
    out.append(text(lx + 12, ly + 24, "Key", 13, "bold"))
    rows = [(FILL_MINE, INK, "", "class in the item package"),
            (FILL_IFACE, INK, "", "interface in the item package"),
            (FILL_OTHER, GREY, ' stroke-dasharray="6 4"', "other package")]
    for i, (f, s, d, t) in enumerate(rows):
        y = ly + 40 + i * 28
        out.append(f'<rect x="{lx + 12}" y="{y}" width="34" height="18" fill="{f}" stroke="{s}"{d}/>')
        out.append(label(lx + 56, y + 14, t))
    arrows = [("arrow", False, "association / calls"),
              ("diamond", False, "aggregation (Inventory holds Items)"),
              ("tri", False, "inherits from (extends)"),
              ("tri", True, "implements the interface")]
    for i, (m, dashed, t) in enumerate(arrows):
        y = ly + 140 + i * 26
        if m == "diamond":
            out.append(path([(lx + 12, y), (lx + 50, y)], marker_start="diamond"))
        else:
            out.append(path([(lx + 12, y), (lx + 50, y)], m, dashed=dashed))
        out.append(label(lx + 60, y + 4, t))
    out.append(label(lx + 12, ly + 244, "# = protected, italic = abstract", size=11, fill=GREY))
    out.append("</svg>")
    return "\n".join(out)


# ------------------------------------------------------------- sequence diagram

def sequence_diagram():
    W, H = 1200, 960
    names = [("Player", "user"), ("GameScreen", "screen"), ("InventoryInput", ""), ("Inventory", ""),
             ("bread: BreadItem", ""), ("GooseLocator", "enemy"), ("goose: Feedable", "enemy")]
    xs = [80, 250, 420, 590, 770, 950, 1110]
    top, bottom = 60, 930
    out = header(W, H)
    out.append(text(W / 2, 26, "Sequence diagram: the player feeds a group of geese with Bread (FR_MULTI_TARGET)",
                    16, "bold", anchor="middle"))
    for (n, pkg), x in zip(names, xs):
        w = max(120, len(n) * 8 + 20)
        if n == "Player":
            # stick figure for the user
            out.append(f'<circle cx="{x}" cy="{top + 8}" r="8" fill="#fff" stroke="{INK}" stroke-width="1.3"/>')
            out.append(path([(x, top + 16), (x, top + 34)]))
            out.append(path([(x - 12, top + 22), (x + 12, top + 22)]))
            out.append(path([(x - 10, top + 46), (x, top + 34), (x + 10, top + 46)]))
            out.append(text(x, top + 62, n, 13, "bold", anchor="middle"))
            life_top = top + 70
        else:
            other = pkg in ("screen", "enemy")
            dash = ' stroke-dasharray="6 4"' if other else ""
            fill = FILL_OTHER if other else FILL_MINE
            out.append(f'<rect x="{x - w / 2}" y="{top}" width="{w}" height="44" fill="{fill}" stroke="{INK}" stroke-width="1.3"{dash}/>')
            out.append(text(x, top + 20, n, 13, "bold", anchor="middle"))
            if pkg:
                out.append(text(x, top + 37, f"({pkg} package)", 11, anchor="middle", fill=GREY))
            life_top = top + 44
        out.append(f'<line x1="{x}" y1="{life_top}" x2="{x}" y2="{bottom}" stroke="{GREY}" stroke-dasharray="5 5"/>')

    def act(i, y1, y2, dx=0):
        out.append(f'<rect x="{xs[i] - 6 + dx}" y="{y1}" width="12" height="{y2 - y1}" fill="#fff" stroke="{INK}"/>')

    def msg(i, j, y, s, ret=False, dx_from=0, dx_to=0):
        x1 = xs[i] + (6 if j > i else -6) + dx_from
        x2 = xs[j] + (-6 if j > i else 6) + dx_to
        out.append(path([(x1, y), (x2, y)], "open" if ret else "arrow", dashed=ret))
        out.append(text((x1 + x2) / 2, y - 6, s, 12, anchor="middle"))

    def self_msg(i, y, s, h=22, dx=0):
        x = xs[i] + 6 + dx
        out.append(path([(x, y), (x + 34, y), (x + 34, y + h), (x + 6, y + h)], "arrow"))
        out.append(text(x + 40, y + 14, s, 12))

    def frame(x1, y1, x2, y2, kind, guard):
        out.append(f'<rect x="{x1}" y="{y1}" width="{x2 - x1}" height="{y2 - y1}" fill="none" stroke="{INK}"/>')
        tag_w = len(kind) * 8 + 18
        out.append(f'<path d="M{x1},{y1 + 20} L{x1 + tag_w - 8},{y1 + 20} L{x1 + tag_w},{y1 + 12} L{x1 + tag_w},{y1}" fill="#fff" stroke="{INK}"/>')
        out.append(text(x1 + 6, y1 + 15, kind, 12, "bold"))
        out.append(text(x1 + tag_w + 8, y1 + 15, guard, 12))

    # 1. select the bread with key 2
    y = 160
    out.append(text(20, y - 20, "1. Select", 12, "bold", fill=GREY))
    act(1, y - 8, y + 70)
    msg(0, 1, y, "presses key 2")
    msg(1, 2, y + 26, "update()")
    act(2, y + 20, y + 66)
    msg(2, 3, y + 50, "select(2)")
    act(3, y + 44, y + 80)
    msg(3, 2, y + 76, "true", ret=True)

    # 2. click to use
    y = 300
    out.append(text(20, y - 20, "2. Use", 12, "bold", fill=GREY))
    act(1, y - 8, y + 520)
    msg(0, 1, y, "clicks at (x, y)")
    msg(1, 3, y + 30, "useSelected(x, y)")
    act(3, y + 24, y + 500)
    msg(3, 4, y + 60, "use(x, y)")
    act(4, y + 54, y + 440)
    self_msg(4, y + 78, "canUse(): usesLeft > 0 and cooldownLeft = 0")

    # alt: cannot be used
    fy = y + 118
    frame(606, fy, 1190, y + 430, "alt", "[canUse() is false]")
    msg(4, 3, fy + 44, "false (no use spent)", ret=True)
    out.append(f'<line x1="606" y1="{fy + 58}" x2="1190" y2="{fy + 58}" stroke="{INK}" stroke-dasharray="8 4"/>')
    out.append(text(612, fy + 76, "[else]", 12))
    self_msg(4, fy + 86, "applyEffect(x, y)", dx=6)
    act(4, fy + 96, fy + 250, dx=6)
    msg(4, 5, fy + 128, "geeseNear(x, y, 30)", dx_from=6)
    act(5, fy + 122, fy + 160)
    msg(5, 4, fy + 156, "geese within 30 px", ret=True, dx_to=6)
    frame(700, fy + 172, 1178, fy + 240, "loop", "[for each goose in the list]")
    msg(4, 6, fy + 222, "feed(0.5)", dx_from=6)
    act(6, fy + 216, fy + 234)
    self_msg(4, fy + 258, "usesLeft - 1,  cooldownLeft = 3 s")
    msg(4, 3, fy + 300, "true", ret=True)

    # back in Inventory
    self_msg(3, y + 448, "itemsUsed + 1  (FR_COUNTERS)")
    msg(3, 1, y + 496, "true", ret=True)

    # 3. every frame
    y = 860
    out.append(text(20, y - 24, "3. Every frame", 12, "bold", fill=GREY))
    act(1, y - 10, y + 50)
    msg(1, 3, y, "update(delta)  counts the cooldown down")
    act(3, y - 6, y + 44)
    msg(1, 3, y + 34, "slotText(2)  →  \"2: Bread 4/5 (wait 3s)\" on the HUD")
    out.append("</svg>")
    return "\n".join(out)


if __name__ == "__main__":
    out_dir = Path(sys.argv[1] if len(sys.argv) > 1 else ".")
    (out_dir / "items-class.svg").write_text(class_diagram(), encoding="utf-8")
    (out_dir / "items-sequence.svg").write_text(sequence_diagram(), encoding="utf-8")
    print("wrote", out_dir / "items-class.svg", "and", out_dir / "items-sequence.svg")
