import html, sys, os

OUT = sys.argv[1]
FONT = "Arial, Helvetica, sans-serif"

def esc(t): return html.escape(t, quote=False)

class Svg:
    def __init__(self, w, h):
        self.w, self.h, self.parts = w, h, []
    def add(self, s): self.parts.append(s)
    def group(self, x, y, w, h, title, fill, stroke):
        self.add(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" fill="{fill}" stroke="{stroke}" stroke-width="1"/>')
        self.add(f'<text x="{x + 10}" y="{y + 16}" text-anchor="start" font-weight="bold" font-size="14">{esc(title)}</text>')
    def box(self, x, y, w, h, stereo, name, desc=(), reqs="", dashed=False, fill="#ffffff", stroke="#333"):
        dash = ' stroke-dasharray="6 4"' if dashed else ""
        self.add(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" fill="{fill}" stroke="{stroke}" stroke-width="1.3"{dash}/>')
        cx = x + w / 2
        lines = [(stereo, "10.5", "normal", "#555", False), (name, "12.5", "bold", "#111", False)]
        lines += [(d, "10.5", "normal", "#333", False) for d in desc]
        if reqs: lines.append((reqs, "10", "normal", "#555", True))
        total = sum(14 if i != 1 else 15 for i in range(len(lines)))
        ty = y + (h - total) / 2 + 11
        for t, size, weight, color, italic in lines:
            st = ' font-style="italic"' if italic else ""
            self.add(f'<text x="{cx}" y="{ty}" text-anchor="middle" font-size="{size}" font-weight="{weight}" fill="{color}"{st}>{esc(t)}</text>')
            ty += 15 if weight == "bold" else 14
    def line(self, pts, label=None, lpos=None, both=False, dashed=False, lanchor="start"):
        d = "M " + " L ".join(f"{x},{y}" for x, y in pts)
        dash = ' stroke-dasharray="5 4"' if dashed else ""
        ms = ' marker-start="url(#arr)"' if both else ""
        self.add(f'<path d="{d}" fill="none" stroke="#444" stroke-width="1.4"{dash} marker-end="url(#arr)"{ms}/>')
        if label:
            x, y = lpos
            self.add(f'<text x="{x}" y="{y}" font-size="10.5" fill="#222" text-anchor="{lanchor}" stroke="#fff" stroke-width="3.5" paint-order="stroke">{esc(label)}</text>')
    def text(self, x, y, t, size=11, anchor="start", weight="normal", color="#222"):
        self.add(f'<text x="{x}" y="{y}" font-size="{size}" text-anchor="{anchor}" font-weight="{weight}" fill="{color}">{esc(t)}</text>')
    def render(self):
        defs = ('<defs><marker id="arr" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">'
                '<path d="M 0 0 L 10 5 L 0 10 z" fill="#444"/></marker></defs>')
        return (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {self.w} {self.h}" font-family="{FONT}">'
                f'<rect width="{self.w}" height="{self.h}" fill="#fff"/>{defs}' + "".join(self.parts) + '</svg>')

# ======================================================================= component diagram
c = Svg(1120, 790)
c.group(20, 95, 270, 215, "Player Control", "#f4feff", "#9cc")
c.group(320, 95, 780, 130, "Game World", "#fff8f4", "#dba")
c.group(20, 345, 1080, 215, "Game Logic", "#f6faf6", "#9b9")
c.group(20, 600, 1080, 150, "User Feedback", "#fefef2", "#cc9")

c.box(450, 12, 220, 52, "<<Component>>", "Game Manager", ["game state: paused, running, won, lost"])
c.box(35, 125, 240, 66, "<<Component>>", "Player Control System", ["WASD, mouse, keys 1-9, pause key"], "FR_CONTROLS", fill="#e8fbff")
c.box(35, 222, 240, 66, "<<Component>>", "Player System", ["avatar, speed (SpeedBoostTarget)"], "FR_AVATAR", fill="#e8fbff")

c.box(335, 128, 180, 80, "<<Component>>", "Enemy System", ["geese: waves, hunger", "(Feedable)"], "FR_ENEMY, FR_WAVES", fill="#fdeee6")
c.box(530, 128, 180, 80, "<<Component>>", "Game Environment", ["map, bounds,", "collisions"], "FR_CAMPUS_MAP, QR_COLLISION", fill="#fdeee6")
c.box(725, 128, 180, 80, "<<Component>>", "Hidden Event System", ["IHiddenEvent, HEItems,", "HEGooseSpeed (event)"], "FR_EVENTS, FR_EVENT_EFFECT", fill="#fdeee6")
c.box(920, 128, 165, 80, "<<Component>> (A2)", "Collectible System", ["pick-up items", "on the map"], "FR_PICKUP_ITEMS", dashed=True, fill="#f2f2f2", stroke="#888")

c.box(35, 380, 230, 100, "<<Component>>", "Time Limit System", ["5-minute timer,", "pause"], "FR_TIMER, FR_PAUSE_KEY", fill="#eaf4ea")
c.box(340, 380, 240, 100, "<<Component>>", "Inventory System", ["Inventory, Item, OatsItem, BreadItem,", "EnergyDrinkItem (package item)"], "FR_START_INVENTORY, FR_MULTI_TARGET", fill="#eaf4ea")
c.box(625, 380, 230, 100, "<<Component>>", "Counter System", ["items used,", "events triggered"], "FR_COUNTERS", fill="#eaf4ea")
c.box(910, 380, 170, 100, "<<Component>> (A2)", "Score System", [], "FR_SCORE", dashed=True, fill="#f2f2f2", stroke="#888")

c.box(35, 645, 720, 90, "<<Component>>", "Visual Feedback / UI System",
      ["timer, item slots, counters, hunger bars, warnings (colour + sound + text)", "pause, win and lose screens"],
      "QR_ACCESS_CUES, QR_ALERT, FR_JUDGEMENT", fill="#fcfbe0")
c.box(790, 645, 290, 90, "<<Component>>", "Audio System", ["sound cues for warnings"], "QR_ALERT", fill="#fcfbe0")

# edges
c.line([(155, 191), (155, 222)], "move", (162, 211))
c.line([(155, 125), (155, 38), (450, 38)], "pause key", (260, 31))
c.line([(450, 56), (300, 56), (300, 255), (275, 255)], "start / stop", (306, 150))
c.line([(480, 64), (480, 128)], "spawn waves /", (488, 80), both=True)
c.text(488, 92, "all geese satisfied: won", 10.5)
c.line([(155, 288), (155, 330), (815, 330)], "position (collisions, hidden events)", (170, 323))
c.line([(620, 330), (620, 208)])
c.line([(815, 330), (815, 208)])
c.line([(425, 380), (425, 208)], "feed (Feedable, GooseLocator)", (432, 366))
c.line([(515, 168), (530, 168)])
c.line([(815, 128), (815, 110), (425, 110), (425, 128)], "goose speed +30%", (700, 105), lanchor="middle")
c.line([(760, 208), (760, 352), (540, 352), (540, 380)], "add items, addUses", (650, 345), lanchor="middle")
c.line([(580, 430), (625, 430)], "item used", (586, 424))
c.line([(870, 208), (870, 360), (740, 360), (740, 380)], "event triggered", (878, 300))
c.line([(855, 430), (910, 430)], dashed=True)
c.line([(35, 430), (10, 430), (10, 22), (450, 22)], "start timer / time up: lost", (16, 16), both=True)
c.line([(740, 480), (740, 645)], "counters", (748, 560))
c.line([(150, 480), (150, 645)], "time left", (158, 560))
c.line([(335, 170), (320, 170), (320, 645)], "hunger bars, attack warning", (326, 520))
c.line([(755, 690), (790, 690)], "cues", (759, 683))

c.text(20, 775, "Dashed boxes are Assessment 2. Italic text: requirement IDs from Req1. Arrows show which component calls or informs which.", 10.5, color="#444")
open(OUT + "/component.svg", "w").write(c.render())

# ======================================================================= activity diagram
a = Svg(860, 1030)
def anode(x, y, w, h, bold, text, fill, stroke):
    a.add(f'<rect x="{x - w/2}" y="{y - h/2}" width="{w}" height="{h}" rx="8" fill="{fill}" stroke="{stroke}" stroke-width="1.3"/>')
    lines = [bold] + text
    ty = y - (len(lines) * 14) / 2 + 11
    for i, t in enumerate(lines):
        a.add(f'<text x="{x}" y="{ty}" text-anchor="middle" font-size="11.5" font-weight="{"bold" if i == 0 else "normal"}" fill="#111">{esc(t)}</text>')
        ty += 14
def diamond(x, y, w, h, lines):
    a.add(f'<polygon points="{x},{y-h/2} {x+w/2},{y} {x},{y+h/2} {x-w/2},{y}" fill="#fde6cc" stroke="#c9962c" stroke-width="1.3"/>')
    ty = y - (len(lines) * 13) / 2 + 10
    for t in lines:
        a.add(f'<text x="{x}" y="{ty}" text-anchor="middle" font-size="11.5" fill="#111">{esc(t)}</text>'); ty += 13

CTL = ("#e6e0f0", "#7a6a90"); WRL = ("#dcead8", "#7a9a70"); SYS = ("#f4d4d2", "#a06a66"); STA = ("#d9e4f6", "#6a7fa8")
X = 300
a.add(f'<circle cx="{X}" cy="22" r="11" fill="#000"/>')
anode(X, 75, 360, 46, "Game Manager", ["initialise: 2 items in the inventory,", "counters = 0, timer 5:00"], *CTL)
anode(X, 140, 360, 40, "UI System", ["show the pause screen (the game starts paused)"], *SYS)
anode(X, 205, 360, 40, "Player Control System", ["read input: WASD, mouse, keys 1-9, pause key"], *CTL)
diamond(X, 275, 150, 54, ["Paused?"])
anode(620, 275, 250, 52, "Game Manager", ["freeze timer and game", "until the user resumes"], *STA)
anode(X, 355, 390, 54, "Player System, Enemy System", ["move the player (buildings and geese block it),", "update geese and waves"], *WRL)
diamond(X, 435, 190, 56, ["Player near a", "hidden event?"])
anode(X, 515, 420, 62, "Hidden Event System", ["apply the event (help: Energy Drinks + Bread,", "hinder: geese +30% for 10 s)", "Counter System: events + 1"], *SYS)
diamond(X, 600, 190, 56, ["Item selected and", "mouse clicked?"])
diamond(X, 680, 190, 56, ["Item ready and", "target found?"])
anode(X, 760, 420, 46, "Inventory System", ["apply the item effect (feed geese, speed boost)", "Counter System: items + 1"], *SYS)
anode(X, 830, 300, 36, "Time Limit System", ["update the timer"], *CTL)
diamond(X, 895, 170, 54, ["All geese", "satisfied?"])
diamond(X, 960, 150, 46, ["Time left?"])
anode(650, 895, 270, 46, "Game Manager: state = won", ["UI System: show the win screen"], *STA)
anode(650, 960, 270, 46, "Game Manager: state = lost", ["UI System: show the lose screen"], *STA)

def ar(pts, label=None, lpos=None):
    d = "M " + " L ".join(f"{x},{y}" for x, y in pts)
    a.add(f'<path d="{d}" fill="none" stroke="#444" stroke-width="1.3" marker-end="url(#arr)"/>')
    if label:
        a.add(f'<text x="{lpos[0]}" y="{lpos[1]}" font-size="10.5" fill="#222" stroke="#fff" stroke-width="3" paint-order="stroke">{label}</text>')
ar([(X, 33), (X, 52)]); ar([(X, 98), (X, 120)]); ar([(X, 160), (X, 185)]); ar([(X, 225), (X, 248)])
ar([(X + 75, 275), (X + 75 + 20, 275), (495, 275)], "yes", (X + 82, 268))
ar([(620, 249), (620, 205), (480, 205)])
ar([(X, 302), (X, 328)], "no", (X + 6, 318))
ar([(X, 382), (X, 407)])
ar([(X, 463), (X, 484)], "yes", (X + 6, 476))
ar([(X - 95, 435), (60, 435), (60, 600), (X - 95, 600)], "no", (66, 430))
ar([(X, 546), (X, 572)])
ar([(X, 628), (X, 652)], "yes", (X + 6, 644))
ar([(X, 708), (X, 737)], "yes", (X + 6, 726))
ar([(X, 783), (X, 812)])
# item not ready / not clicked -> skip to timer
ar([(X + 95, 600), (570, 600), (570, 830), (X + 150, 830)])
a.text(X + 100, 594, "no", 10.5)
a.add('<path d="M 395,680 L 540,680 L 540,830" fill="none" stroke="#444" stroke-width="1.3"/>')
a.text(X + 100, 674, "no", 10.5)
ar([(X, 848), (X, 868)])
ar([(X + 85, 895), (515, 895)], "yes", (X + 92, 888))
ar([(X, 922), (X, 937)], "no", (X + 6, 934))
ar([(X + 75, 960), (515, 960)], "no", (X + 82, 953))
# loop back
ar([(X - 75, 960), (30, 960), (30, 205), (X - 180, 205)], "yes: draw timer, item slots, counters, hunger bars; play cues (UI and Audio System)", (40, 992))
a.add(f'<circle cx="810" cy="928" r="13" fill="#fff" stroke="#000" stroke-width="3"/><circle cx="810" cy="928" r="7" fill="#000"/>')
ar([(785, 895), (810, 895), (810, 915)]); ar([(785, 960), (810, 960), (810, 941)])
a.text(20, 1022, "Colours: purple = control, green = world, red = systems, blue = game state, orange = decisions. Restart is Assessment 2.", 10.5, color="#444")
open(OUT + "/activity.svg", "w").write(a.render())
print("ok")
