"""Draws the final (v3) architecture diagrams for Arch1 as SVG, matching the code on main.

Usage: python3 draw_final.py <output folder>
Writes final-component.svg, final-class.svg, final-activity.svg and final-sequence.svg.
Uses the helpers in draw_items.py, so both scripts must stay in the same folder.
Classes of the goose package are drawn dashed as "planned" until that package is merged.
"""
import sys
from pathlib import Path

from draw_items import Box, header, path, text, label, INK, GREY, LINE, PAD

# One colour per package, so the reader can see the package boundaries at a glance.
PKG = {
    "root": "#ffffff",
    "screen": "#fff1dc",
    "game": "#e9f4e9",
    "player": "#e4eefb",
    "item": "#f1e9f9",
    "map": "#fbe9e9",
    "event": "#fbf6d8",
    "goose": "#e3f1f0",
    "a2": "#f2f2f2",
}


class PBox(Box):
    """A class box filled with its package colour. Planned classes are grey and dashed."""

    def __init__(self, x, y, w, name, pkg, stereo=None, attrs=(), ops=(), abstract=False, planned=False):
        super().__init__(x, y, w, name, stereo, attrs, ops, "other" if planned else "mine", abstract)
        self.pkg, self.planned = pkg, planned

    def svg(self):
        fill = PKG[self.pkg]
        dash = ' stroke-dasharray="6 4"' if self.planned else ""
        stroke = GREY if self.planned else INK
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


def package(x1, y1, x2, y2, name, planned=False):
    """A UML package frame with its name on a tab."""
    dash = ' stroke-dasharray="6 4"' if planned else ""
    colour = GREY if planned else "#555"
    tab = len(name) * 7.5 + 20
    return (f'<path d="M{x1},{y1} L{x1 + tab},{y1} L{x1 + tab},{y1 + 18} L{x2},{y1 + 18} L{x2},{y2} L{x1},{y2} Z" '
            f'fill="none" stroke="{colour}" stroke-width="1.2"{dash}/>'
            + text(x1 + 8, y1 + 14, name, 12, "bold", fill=colour))


# ---------------------------------------------------------------- class diagram

def class_diagram():
    W, H = 1940, 1250
    main = PBox(40, 60, 210, "Main", "root", "libGDX Game",
                ops=["+ create(): void"])
    screen = PBox(320, 60, 440, "GameScreen", "screen", "libGDX Screen",
                  attrs=["- session: GameSession", "- player: Player", "- geese: GooseManager",
                         "- inventory: Inventory", "- events: List<EventSpot>",
                         "- mapManager: MapManager", "- hud, renderer, sfx"],
                  ops=["+ render(delta: float): void", "- handleInput(): boolean",
                       "- update(delta: float): void", "- draw(delta: float): void",
                       "- blocksPlayer(bounds): boolean"])
    hud = PBox(800, 60, 410, "Hud", "screen",
               attrs=["- font, goalFont: BitmapFont", "- message: String"],
               ops=["+ render(delta, session, waves, inventory, ...)",
                    "+ showMessage(text: String): void"])
    renderer = PBox(800, 212, 255, "WorldRenderer", "screen",
                    ops=["+ render(camera, player, geese, ...)", "+ addSplash(x, y, radius)"])
    sfx = PBox(1070, 212, 140, "Sfx", "screen", ops=["+ play(cue: Cue)", "+ toggleMute()"])

    session = PBox(40, 410, 300, "GameSession", "game",
                   attrs=["- state: GameState", "- eventsTriggered: int"],
                   ops=["+ play(): void", "+ togglePause(): void", "+ update(delta: float): void",
                        "+ eventTriggered(): void", "+ isPlaying(): boolean"])
    timer = PBox(380, 410, 330, "GameTimer", "game",
                 attrs=["+ GAME_SECONDS: float = 300 {static}", "- secondsLeft: float", "- running: boolean"],
                 ops=["+ start(), stop(): void", "+ update(delta: float): void",
                      "+ isFinished(): boolean", "+ format(): String"])
    state = PBox(40, 640, 230, "GameState", "game", "enumeration",
                 attrs=["READY, PLAYING, PAUSED,", "WON, LOST"], ops=["+ isOver(): boolean"])
    waves = PBox(300, 640, 270, "WaveStatus", "game", "interface",
                 ops=["+ update(delta, playerX, playerY)", "+ geeseLeft(), wavesLeft(): int",
                      "+ currentWave(), totalWaves(): int", "+ secondsToNextWave(): float",
                      "+ allWavesCleared(): boolean"])
    spot = PBox(40, 820, 300, "EventSpot", "game",
                attrs=["- event: IHiddenEvent", "- effect: Runnable", "- triggered: boolean"],
                ops=["+ update(playerPos: float[]): boolean"])

    player = PBox(800, 410, 400, "Player", "player",
                  attrs=["+ BASE_SPEED: float = 150 {static}", "- bounds: Rectangle",
                         "- speedMultiplier: float", "- boostLeft: float"],
                  ops=["+ update(delta, dirX, dirY): void", "+ applySpeedBoost(multiplier, seconds)",
                       "+ findFreeSpot(...): float[] {static}", "+ getPosition(): float[]"])
    checker = PBox(800, 670, 270, "CollisionChecker", "player", "interface",
                   ops=["+ collidesWith(bounds): boolean"])

    mapm = PBox(1270, 60, 280, "MapManager", "map",
                ops=["+ render(): void", "+ getCamera(): OrthographicCamera",
                     "+ getMapWidth(), getMapHeight(): float", "+ getCollisionManager()"])
    coll = PBox(1270, 230, 280, "CollisionManager", "map",
                attrs=["- collisionAreas: Array<Rectangle>"],
                ops=["+ collidesWith(r: Rectangle): boolean"])

    hidden = PBox(1270, 410, 280, "IHiddenEvent", "event", "interface",
                  ops=["+ isPlayerInActiveRange(pos): boolean", "+ trigger(): void",
                       "+ getIsActivated(): boolean"])
    heitems = PBox(1270, 580, 130, "HEItems", "event")
    hespeed = PBox(1420, 580, 130, "HEGooseSpeed", "event")

    gmgr = PBox(1620, 60, 285, "GooseManager", "goose",
                attrs=["- geese: List<Goose>", "- waves: List<Wave>", "- speedMultiplier: float"],
                ops=["+ update(delta, playerX, playerY)", "+ gooseAt(x, y): Feedable",
                     "+ geeseNear(x, y, radius): List", "+ collidesWith(bounds): boolean",
                     "+ speedUp(multiplier, seconds)", "+ allWavesCleared(): boolean"])
    goose = PBox(1620, 340, 285, "Goose", "goose",
                 attrs=["- state: State", "- fill: float", "- type: GooseType"],
                 ops=["+ feed(amount: float): void", "+ update(delta, x, y, speed): boolean",
                      "+ isHungry(), isAlerting(): boolean"])
    gtype = PBox(1620, 560, 140, "GooseType", "goose", "enumeration",
                 attrs=["NORMAL, HEAD"], ops=["+ getAppetite()"])
    wave = PBox(1775, 560, 130, "Wave", "goose", "record",
                attrs=["+ geese: int", "+ headGeese: int"])
    glisten = PBox(1620, 720, 285, "GooseListener", "goose", "interface",
                   ops=["+ waveStarted(number, total)", "+ gooseAlert(goose)", "+ playerSnatched(goose)",
                        "+ gooseFull(goose), waveCleared(n)"])

    inv_in = PBox(40, 1010, 200, "InventoryInput", "item", ops=["+ update(): void"])
    inv = PBox(280, 1010, 290, "Inventory", "item",
               attrs=["- itemsUsed: int"],
               ops=["+ select(number: int): boolean", "+ useSelected(x, y): boolean",
                    "+ update(delta: float): void"])
    item = PBox(610, 1010, 250, "Item", "item", "abstract", abstract=True,
                ops=["+ use(x, y): boolean", "#~ applyEffect(x, y): boolean"])
    oats = PBox(560, 1170, 110, "OatsItem", "item")
    bread = PBox(680, 1170, 110, "BreadItem", "item")
    drink = PBox(800, 1170, 150, "EnergyDrinkItem", "item")
    locator = PBox(900, 1010, 270, "GooseLocator", "item", "interface",
                   ops=["+ gooseAt(x, y): Feedable", "+ geeseNear(x, y, radius): List"])
    feedable = PBox(1210, 1030, 160, "Feedable", "item", "interface", ops=["+ feed(amount)"])
    boost = PBox(1390, 1030, 170, "SpeedBoostTarget", "item", "interface",
                 ops=["+ applySpeedBoost(...)"])

    boxes = [main, screen, hud, renderer, sfx, session, timer, state, waves, spot, player, checker,
             mapm, coll, hidden, heitems, hespeed, gmgr, goose, gtype, wave, glisten,
             inv_in, inv, item, oats, bread, drink, locator, feedable, boost]

    out = header(W, H)
    out.append(text(W / 2, 22, "Final class diagram (v3): main classes of every package", 17, "bold", anchor="middle"))
    out.append(package(300, 40, 1225, 350, "screen"))
    out.append(package(25, 390, 750, 970, "game"))
    out.append(package(785, 390, 1215, 770, "player"))
    out.append(package(1255, 40, 1565, 350, "map"))
    out.append(package(1255, 390, 1565, 630, "hidden_event"))
    out.append(package(1605, 40, 1920, 870, "goose"))
    out.append(package(25, 990, 1575, 1235, "item"))

    def arrow(points, marker="arrow", dashed=False, start=None, text_=None, at=None, anchor="start"):
        out.append(path(points, marker, start, dashed))
        if text_:
            out.append(label(at[0], at[1], text_, anchor))

    # Main creates the screen; the screen uses the HUD and the world renderer
    arrow([(main.x + main.w, 90), (screen.x, 90)], text_="creates", at=(260, 82))
    arrow([(screen.x + screen.w, 110), (hud.x, 110)], text_="draws with", at=(768, 102))
    arrow([(screen.x + screen.w, 255), (renderer.x, 255)])
    # GameScreen -> GameSession, Player, MapManager, EventSpot, Inventory, GooseManager
    arrow([(380, screen.bottom), (380, 375), (190, 375), (190, session.y)])
    out.append(label(198, 400, "rules"))
    arrow([(700, screen.bottom), (700, 370), (1000, 370), (1000, player.y)])
    out.append(label(1008, 400, "moves"))
    arrow([(screen.x + screen.w, 320), (1240, 320), (1240, 110), (mapm.x, 110)])
    out.append(label(1215, 312, "renders", "end"))
    arrow([(340, 380), (20, 380), (20, 870), (spot.x, 870)])
    out.append(label(24, 862, "0..*"))
    arrow([(330, screen.bottom), (330, 385), (12, 385), (12, 1000), (360, 1000), (360, inv.y)])
    out.append(label(368, 1004, "inventory"))
    arrow([(600, screen.y), (600, 34), (1760, 34), (1760, gmgr.y)])
    out.append(label(1450, 30, "geese"))
    # GameSession -> GameTimer, GameState, WaveStatus
    arrow([(session.x + session.w, 450), (timer.x, 450)])
    out.append(label(345, 442, "timer"))
    arrow([(150, session.bottom), (150, state.y)])
    arrow([(320, session.bottom), (320, 610), (420, 610), (420, waves.y)])
    out.append(label(428, 632, "win?"))
    # EventSpot -> IHiddenEvent
    arrow([(spot.x + spot.w, 880), (775, 880), (775, 790), (1248, 790), (1248, 460), (hidden.x, 460)])
    out.append(label(350, 873, "triggers once"))
    arrow([(heitems.cx, heitems.y), (heitems.cx, hidden.bottom)], "tri", dashed=True)
    arrow([(hespeed.cx, hespeed.y), (hespeed.cx, hidden.bottom)], "tri", dashed=True)
    # Player -> CollisionChecker; the map and the geese both fit it
    arrow([(900, player.bottom), (900, checker.y)])
    arrow([(coll.x, 300), (1233, 300), (1233, 700), (checker.x + checker.w, 700)], "open", dashed=True)
    out.append(label(1205, 692, "collidesWith fits", "end"))
    arrow([(gmgr.x, 230), (1592, 230), (1592, 728), (checker.x + checker.w, 728)], "tri", dashed=True)
    # MapManager owns CollisionManager
    out.append(path([(1410, mapm.bottom + 16), (1410, coll.y)], marker_start="diamond"))
    # Player implements SpeedBoostTarget
    arrow([(1150, player.bottom - 30), (1190, player.bottom - 30), (1190, 983), (1475, 983), (1475, boost.y)],
          "tri", dashed=True)
    # goose package
    arrow([(gmgr.x, 270), (1580, 270), (1580, 805), (440, 805), (440, waves.bottom)], "tri", dashed=True)
    arrow([(gmgr.x + gmgr.w, 150), (1930, 150), (1930, 975), (1100, 975), (1100, locator.y)], "tri", dashed=True)
    arrow([(goose.x + goose.w, 400), (1922, 400), (1922, 1000), (1290, 1000), (1290, feedable.y)], "tri", dashed=True)
    out.append(path([(1700, gmgr.bottom + 16), (1700, goose.y)], marker_start="diamond"))
    out.append(label(1708, goose.y - 8, "0..*"))
    arrow([(1690, goose.bottom), (1690, gtype.y)])
    arrow([(1840, gmgr.bottom), (1840, 320), (1880, 320), (1880, wave.y)])
    out.append(label(1846, 312, "waves"))
    arrow([(1905 - 10, gmgr.bottom), (1895, 315), (1912, 315), (1912, 700), (1860, 700), (1860, glisten.y)])
    out.append(label(1866, 714, "tells"))
    # item package
    arrow([(inv_in.x + inv_in.w, 1050), (inv.x, 1050)], text_="select", at=(246, 1042))
    arrow([(inv.x + inv.w + 16, 1050), (item.x, 1050)], start="diamond")
    out.append(label(item.x - 6, 1042, "0..9", "end"))
    stem = 1150
    out.append(path([(item.cx, stem), (item.cx, item.bottom)], "tri"))
    out.append(path([(oats.cx, oats.y), (oats.cx, stem), (drink.cx, stem), (drink.cx, drink.y)]))
    out.append(path([(bread.cx, stem), (bread.cx, bread.y)]))
    arrow([(bread.x + bread.w, 1190), (1000, 1190), (1000, locator.bottom)])
    out.append(label(1008, 1110, "Oats, Bread"))
    arrow([(drink.x + drink.w, 1200), (1470, 1200), (1470, boost.bottom)])
    arrow([(locator.x + locator.w, 1060), (feedable.x, 1060)], "open", dashed=True)

    out.extend(b.svg() for b in boxes)

    # key
    kx, ky = 40, 165
    out.append(f'<rect x="{kx}" y="{ky}" width="240" height="180" fill="#fff" stroke="#bbb"/>')
    out.append(text(kx + 10, ky + 18, "Key", 12, "bold"))
    out.append(path([(kx + 10, ky + 38), (kx + 50, ky + 38)], "arrow"))
    out.append(label(kx + 58, ky + 42, "uses / calls"))
    out.append(path([(kx + 10, ky + 60), (kx + 50, ky + 60)], "tri", dashed=True))
    out.append(label(kx + 58, ky + 64, "implements"))
    out.append(path([(kx + 10, ky + 82), (kx + 50, ky + 82)], "tri"))
    out.append(label(kx + 58, ky + 86, "extends"))
    out.append(path([(kx + 26, ky + 104), (kx + 50, ky + 104)], marker_start="diamond"))
    out.append(label(kx + 58, ky + 108, "holds (aggregation)"))
    out.append(label(kx + 10, ky + 134, "# protected, italic = abstract", fill=GREY))
    out.append(label(kx + 10, ky + 152, "colour = package", fill=GREY))
    out.append(label(kx + 10, ky + 170, "GameScreen.Feedback is the GooseListener", fill=GREY))
    out.append("</svg>")
    return "\n".join(out)


# ------------------------------------------------------------ component diagram

def lollipop(out, cx, cy, name, provider_pts, requirer_pts, lx, ly, anchor="start"):
    """A provided interface (ball on a line from the provider) and the component that needs it
    (dashed arrow to the ball)."""
    out.append(path(provider_pts + [(cx, cy)]))
    out.append(path(requirer_pts, "open", dashed=True))
    out.append(f'<circle cx="{cx}" cy="{cy}" r="9" fill="#fff" stroke="{INK}" stroke-width="1.4"/>')
    out.append(text(lx, ly, name, 12, "bold", "italic", anchor))


def component_diagram():
    W, H = 1200, 880
    out = header(W, H)
    out.append(text(W / 2, 26, "Final component diagram (v3): one component per package, joined by interfaces", 17, "bold", anchor="middle"))

    def comp(x, y, w, name, pkg, lines, reqs, planned=False, stereo="component"):
        b = PBox(x, y, w, name, pkg, stereo, ops=lines + ([reqs] if reqs else []), planned=planned)
        out.append(b.svg())
        # requirement line in italics: redraw the last row
        return b

    launcher = comp(40, 50, 230, "Desktop launcher", "root", ["lwjgl3: Lwjgl3Launcher", "core: Main"], "QR_PLATFORM")
    lib = comp(930, 50, 230, "libGDX 1.14.2", "root", ["rendering, input,", "tile maps (TmxMapLoader)"], "", stereo="external library")
    screen = comp(340, 50, 520, "Game Screen", "screen",
                  ["screen: GameScreen, Hud", "reads the keys and mouse, draws the map, player and HUD,",
                   "start, pause, win and lose screens"], "FR_START_PAUSED, FR_PAUSE_KEY, QR_ACCESS_CUES")
    rules = comp(40, 290, 330, "Game Rules", "game",
                 ["game: GameSession, GameTimer, GameState", "5-minute timer, pause, win or loss"], "FR_TIMER, FR_JUDGEMENT, FR_COUNTERS")
    player = comp(435, 290, 330, "Player", "player",
                  ["player: Player", "WASD movement, speed boost"], "FR_AVATAR, FR_CONTROLS, QR_OUT_OF_BOUNDS")
    mapc = comp(830, 290, 330, "Campus Map", "map",
                ["map: MapManager, CollisionManager", "map from Tiled, walls and buildings"], "FR_CAMPUS_MAP, QR_COLLISION")
    events = comp(40, 560, 330, "Hidden Events", "event",
                  ["hidden_event: IHiddenEvent, HEItems,", "HEGooseSpeed; game: EventSpot"], "FR_EVENTS, FR_EVENT_EFFECT")
    items = comp(435, 560, 330, "Inventory and Items", "item",
                 ["item: Inventory, Item, OatsItem,", "BreadItem, EnergyDrinkItem, keys 1-9"], "FR_START_INVENTORY, FR_SINGLE/MULTI_TARGET")
    geese = comp(830, 560, 330, "Geese", "goose",
                 ["goose: GooseManager, Goose, GooseType, Wave", "waves, hunger bars, warning before a snatch"], "FR_ENEMY, FR_WAVES, QR_ALERT")
    score = comp(435, 710, 200, "Score", "a2", ["Assessment 2"], "FR_SCORE", planned=True)
    pick = comp(655, 710, 200, "Collectibles", "a2", ["Assessment 2"], "FR_PICKUP_ITEMS", planned=True)

    def arrow(points, s=None, at=None, anchor="start", marker="arrow", dashed=False):
        out.append(path(points, marker, dashed=dashed))
        if s:
            out.append(label(at[0], at[1], s, anchor))

    arrow([(launcher.x + launcher.w, 90), (screen.x, 90)], "starts", (278, 82))
    arrow([(screen.x + screen.w, 90), (lib.x, 90)], "uses", (868, 82), dashed=True, marker="open")
    arrow([(screen.x, 172), (205, 172), (205, rules.y)], "play, togglePause, update", (213, 270))
    arrow([(600, screen.bottom), (600, player.y)], "update(delta, dirX, dirY)", (608, 270))
    arrow([(820, screen.bottom), (820, 240), (995, 240), (995, mapc.y)], "render", (1003, 270))
    arrow([(screen.x, 180), (22, 180), (22, 620), (events.x, 620)], "update(position)", (28, 545))
    arrow([(400, screen.bottom), (400, 530), (520, 530), (520, items.y)], "select, useSelected", (408, 522))

    # interfaces between components
    lollipop(out, 798, 345, "CollisionChecker (map, geese)", [(mapc.x, 345)], [(player.x + player.w, 345), (789, 345)], 798, 330, "middle")
    lollipop(out, 690, 500, "SpeedBoostTarget", [(690, player.bottom)], [(690, items.y), (690, 509)], 700, 495)
    lollipop(out, 798, 620, "GooseLocator, Feedable", [(geese.x, 620)], [(items.x + items.w, 620), (789, 620)], 798, 605, "middle")
    lollipop(out, 1100, 500, "WaveStatus", [(1100, geese.y)], [(rules.x + rules.w, 330), (395, 330), (395, 470), (1100, 470), (1100, 491)], 1110, 495)
    # the geese offer CollisionChecker too: hungry geese block the player (UR_BOUNDS)
    out.append(path([(808, geese.y), (808, 425), (798, 425), (798, 354)]))

    arrow([(events.x + events.w, 650), (items.x, 650)], "adds items", (374, 642))
    arrow([(150, events.y), (150, rules.bottom)], "eventTriggered", (158, 470))
    arrow([(205, events.bottom), (205, 840), (1100, 840), (1100, geese.bottom)], "speedUp(1.3, 10 s)  (HEGooseSpeed)", (880, 832))

    out.append(text(40, H - 6, "Dashed grey: Assessment 2. Ball and dashed arrow: the interface one component offers and another needs.", 12, fill=GREY))
    out.append("</svg>")
    return "\n".join(out)


# ------------------------------------------------------------- activity diagram

def activity_diagram():
    W, H = 1040, 1020
    out = header(W, H)
    out.append(text(W / 2, 26, "Final activity diagram (v3): GameScreen.render(delta), once per frame", 17, "bold", anchor="middle"))

    def node(cx, cy, w, h, title, lines, fill):
        out.append(f'<rect x="{cx - w / 2}" y="{cy - h / 2}" width="{w}" height="{h}" rx="10" fill="{fill}" stroke="{INK}" stroke-width="1.3"/>')
        rows = [title] + lines
        ty = cy - len(rows) * 17 / 2 + 13
        for i, r in enumerate(rows):
            out.append(text(cx, ty, r, 14, "bold" if i == 0 else "normal", anchor="middle"))
            ty += 17

    def diamond(cx, cy, w, h, lines):
        out.append(f'<polygon points="{cx},{cy - h / 2} {cx + w / 2},{cy} {cx},{cy + h / 2} {cx - w / 2},{cy}" fill="#fde6cc" stroke="#b07a2a" stroke-width="1.3"/>')
        ty = cy - len(lines) * 16 / 2 + 12
        for r in lines:
            out.append(text(cx, ty, r, 13.5, anchor="middle"))
            ty += 16

    def ar(pts, s=None, at=None):
        out.append(path(pts, "arrow"))
        if s:
            out.append(label(at[0], at[1], s))

    X = 380
    out.append(f'<circle cx="{X}" cy="56" r="11" fill="{INK}"/>')
    node(X, 112, 560, 64, "new GameScreen", ["load the Tiled map, put the player on a free spot,",
                                              "place 2 hidden events, inventory = Oats + Bread, state = READY"], PKG["screen"])
    node(X, 196, 380, 44, "handleInput()", ["read the keys and the mouse"], PKG["screen"])
    diamond(X, 270, 200, 64, ["state?"])
    node(830, 270, 320, 78, "READY or PAUSED", ["SPACE (or P, Esc): play()", "timer starts or resumes;", "the world does not move"], PKG["game"])
    node(830, 380, 320, 62, "WON or LOST", ["result screen with counters;", "R: start a new GameScreen"], PKG["game"])
    node(X - 30, 360, 500, 50, "keys 1-9: Inventory.select(n)", ["left click: Inventory.useSelected(x, y)  (see the item sequence)"], PKG["item"])
    node(X, 432, 560, 50, "Player.update(delta, dirX, dirY)", ["move one axis at a time; undo a step that hits a wall or the edge"], PKG["player"])
    node(X, 504, 560, 50, "Inventory.update(delta), geese.update(delta, x, y)", ["count cooldowns down; geese move towards the player"], PKG["item"])
    diamond(X, 590, 290, 84, ["player inside the radius", "of an event that has", "not triggered yet?"])
    node(830, 590, 320, 62, "EventSpot", ["trigger(), run the effect,", "GameSession.eventTriggered()"], PKG["event"])
    node(X, 676, 360, 44, "GameSession.update(delta)", ["timer counts down"], PKG["game"])
    diamond(X, 752, 240, 64, ["all waves cleared?"])
    node(830, 752, 320, 44, "state = WON", ["(can happen before the 5 minutes)"], PKG["game"])
    diamond(X, 840, 210, 64, ["time left = 0?"])
    node(830, 840, 320, 44, "state = LOST", ["timer stopped"], PKG["game"])
    node(X, 930, 600, 50, "draw(delta)", ["map around the player, the player, then the HUD or the overlay screen"], PKG["screen"])

    ar([(X, 67), (X, 81)])
    ar([(X, 144), (X, 174)])
    ar([(X, 218), (X, 238)])
    ar([(X + 100, 270), (670, 270)], "READY / PAUSED", (X + 108, 262))
    ar([(X + 50, 295), (X + 50, 312), (650, 312), (650, 380), (670, 380)], "WON / LOST", (X + 60, 306))
    ar([(X, 302), (X, 336)], "PLAYING", (X + 8, 325))
    ar([(X, 385), (X, 407)])
    ar([(X, 457), (X, 479)])
    ar([(X, 529), (X, 548)])
    ar([(X + 145, 590), (670, 590)], "yes", (X + 138, 582))
    ar([(830, 621), (830, 676), (X + 180, 676)])
    ar([(X, 632), (X, 654)], "no", (X + 8, 648))
    ar([(X, 698), (X, 720)])
    ar([(X + 120, 752), (670, 752)], "yes", (X + 118, 744))
    ar([(X, 784), (X, 808)], "no", (X + 8, 800))
    ar([(X + 105, 840), (670, 840)], "yes", (X + 108, 832))
    ar([(X, 872), (X, 905)], "no", (X + 8, 894))
    # the overlay states and the end states also draw this frame
    ar([(990, 270), (1015, 270), (1015, 930), (X + 300, 930)])
    ar([(990, 380), (1015, 380)])
    ar([(990, 752), (1015, 752)])
    ar([(990, 840), (1015, 840)])
    # next frame
    ar([(X - 300, 930), (40, 930), (40, 196), (X - 190, 196)], "next frame", (48, 560))
    out.append(text(20, H - 8, "Colour = package that does the step (orange screen, green game, purple item, blue player, yellow event). P or Esc pauses at any time.", 12, fill=GREY))
    out.append("</svg>")
    return "\n".join(out)


# ------------------------------------------------------------- sequence diagram

def sequence_diagram():
    W, H = 1560, 1070
    out = header(W, H)
    out.append(text(W / 2, 26, "Sequence diagram (v3): one frame while the game is playing", 17, "bold", anchor="middle"))
    names = [("libGDX", "root"), ("GameScreen", "screen"), ("player: Player", "player"), ("map", "map"),
             ("Inventory", "item"), ("spot: EventSpot", "game"), ("event: IHiddenEvent", "event"),
             ("GameSession", "game"), ("GameTimer", "game"), ("geese: GooseManager", "goose"), ("Hud", "screen")]
    xs = [60 + i * 144 for i in range(len(names))]
    top, bottom = 50, 1050
    for (n, pkg), x in zip(names, xs):
        w = 132
        out.append(f'<rect x="{x - w / 2}" y="{top}" width="{w}" height="36" fill="{PKG[pkg]}" stroke="{INK}" stroke-width="1.3"/>')
        out.append(text(x, top + 23, n, 13.5, "bold", anchor="middle"))
        out.append(f'<line x1="{x}" y1="{top + 36}" x2="{x}" y2="{bottom}" stroke="{GREY}" stroke-dasharray="5 5"/>')

    def act(i, y1, y2, dx=0):
        out.append(f'<rect x="{xs[i] - 6 + dx}" y="{y1}" width="12" height="{y2 - y1}" fill="#fff" stroke="{INK}"/>')

    def msg(i, j, y, s, ret=False, dx_from=0, dx_to=0):
        x1 = xs[i] + (6 if j > i else -6) + dx_from
        x2 = xs[j] + (-6 if j > i else 6) + dx_to
        out.append(path([(x1, y), (x2, y)], "open" if ret else "arrow", dashed=ret))
        out.append(text((x1 + x2) / 2, y - 6, s, 13.5, anchor="middle"))

    def self_msg(i, y, s, dx=0):
        x = xs[i] + 6 + dx
        out.append(path([(x, y), (x + 30, y), (x + 30, y + 20), (x + 6, y + 20)], "arrow"))
        out.append(text(x + 36, y + 14, s, 13.5))

    def frame(x1, y1, x2, y2, kind, guard):
        out.append(f'<rect x="{x1}" y="{y1}" width="{x2 - x1}" height="{y2 - y1}" fill="none" stroke="{INK}"/>')
        tag = len(kind) * 8 + 18
        out.append(f'<path d="M{x1},{y1 + 20} L{x1 + tag - 8},{y1 + 20} L{x1 + tag},{y1 + 12} L{x1 + tag},{y1}" fill="#fff" stroke="{INK}"/>')
        out.append(text(x1 + 6, y1 + 15, kind, 12, "bold"))
        out.append(text(x1 + tag + 8, y1 + 15, guard, 12))

    def step(y, s):
        out.append(text(xs[1] + 14, y, s, 12, "bold", fill=GREY))

    act(1, 112, 1030)
    msg(0, 1, 120, "render(delta)")
    step(150, "1. input (handleInput)")
    frame(xs[1] - 40, 160, xs[4] + 100, 222, "opt", "[left click on the map]")
    msg(1, 4, 206, "useSelected(x, y)  (see the item sequence diagram)")
    act(4, 198, 216)

    step(250, "2. move (update)")
    msg(1, 2, 272, "update(delta, dirX, dirY)")
    act(2, 264, 372)
    frame(xs[2] - 30, 284, xs[3] + 110, 366, "loop", "[each step of at most 4 px]")
    msg(2, 3, 322, "collidesWith(bounds)")
    act(3, 316, 352)
    msg(3, 2, 350, "true: undo the step", ret=True)

    msg(1, 4, 400, "update(delta)  cooldowns")
    act(4, 394, 412)
    msg(1, 9, 432, "update(delta, playerX, playerY)  geese walk, warn, snatch")
    act(9, 426, 504)
    frame(xs[2] - 30, 450, xs[9] + 100, 510, "opt", "[a goose's warning ended and the player is still close]")
    msg(9, 2, 494, "applySpeedBoost(0.6, 2)  startled: 40% slower for 2 s")
    act(2, 488, 504)

    step(536, "3. hidden events")
    frame(xs[1] - 40, 546, xs[7] + 110, 750, "loop", "[for each EventSpot]")
    msg(1, 5, 580, "update(player.getPosition())")
    act(5, 572, 710)
    msg(5, 6, 606, "isPlayerInActiveRange(pos)")
    act(6, 600, 620)
    frame(xs[5] - 30, 632, xs[7] + 100, 716, "opt", "[in range and not yet triggered]")
    msg(5, 6, 664, "trigger()")
    act(6, 658, 674)
    self_msg(5, 676, "effect.run()  e.g. +10 Bread")
    msg(5, 1, 708, "true", ret=True)
    msg(1, 7, 736, "eventTriggered()")
    act(7, 730, 744)

    step(772, "4. rules")
    msg(1, 7, 794, "update(delta)")
    act(7, 786, 930)
    msg(7, 8, 818, "update(delta)")
    act(8, 812, 830)
    msg(7, 9, 850, "allWavesCleared()")
    act(9, 844, 862)
    frame(xs[7] - 30, 872, xs[9] + 110, 926, "alt", "[cleared]  state = WON")
    out.append(f'<line x1="{xs[7] - 30}" y1="902" x2="{xs[9] + 110}" y2="902" stroke="{INK}" stroke-dasharray="8 4"/>')
    out.append(text(xs[7] + 14, 918, "[timer.isFinished()]  state = LOST", 12))

    step(958, "5. draw")
    msg(1, 3, 978, "render()  map around the camera, then WorldRenderer and Hud")
    act(3, 972, 988)
    msg(1, 10, 1010, "render(delta, session, geese, inventory, player, ...)")
    act(10, 1004, 1022)
    out.append(text(40, H - 4, "\"map\" is MapManager for drawing; for collisions the Player only knows a CollisionChecker, which asks the map and then the hungry geese.", 12, fill=GREY))
    out.append("</svg>")
    return "\n".join(out)


if __name__ == "__main__":
    out_dir = Path(sys.argv[1] if len(sys.argv) > 1 else ".")
    for name, draw in (("final-component.svg", component_diagram), ("final-class.svg", class_diagram),
                       ("final-activity.svg", activity_diagram), ("final-sequence.svg", sequence_diagram)):
        (out_dir / name).write_text(draw(), encoding="utf-8")
        print("wrote", out_dir / name)
