"""Draws the weekly snapshots of the project plan as Gantt charts (SVG), for Plan1 and the website.

Usage: python3 draw_plan.py <output folder>
Writes plan-week1.svg ... plan-week6.svg.

Planned dates come from the kick-off notes (plan v1, 7 Oct), the week 2 GitHub issues (plan v2,
12 Oct) and the re-plan of 9 Nov (plan v3). Actual dates come from the repository: the first commit
of a piece of work to its merge on main. We did not save a chart every week at the time, so all six
were drawn on 9-10 Nov from those records; each chart says which records it uses.

Every label is at least 14 px and the chart is 1090 px wide, so it prints at 10 pt or more
across a landscape A4 page.
"""
import sys
from datetime import date
from pathlib import Path
from xml.sax.saxutils import escape

SANS = "Arial, Helvetica, sans-serif"
START = date(2026, 10, 5)   # Monday of week 1
END = date(2026, 11, 16)    # submission day
LEFT = 250                  # task-name column
DAY = 18                    # pixels per day
ROW = 27
TOP = 84
FONT = 14


def d(month, day):
    return date(2026, month, day)


# id, short name, critical path?, priority, v1 plan, v2 plan, v3 re-plan of the remaining work, actual
TASKS = [
    ("T1", "Set-up, skeleton, tools", True, "Must", [(d(10, 5), d(10, 11))], [(d(10, 5), d(10, 11))], [], [(d(10, 5), d(10, 7))]),
    ("T2", "Client meeting", False, "Must", [], [(d(10, 14), d(10, 14))], [], [(d(10, 14), d(10, 14))]),
    ("T3", "Requirements (Req1)", True, "Must", [(d(10, 7), d(10, 18))], [(d(10, 12), d(10, 18))], [], [(d(10, 12), d(10, 27))]),
    ("T4", "Risk register (Risk1)", False, "Must", [(d(10, 7), d(10, 18))], [(d(10, 12), d(10, 18))], [(d(11, 9), d(11, 10))],
     [(d(10, 12), d(10, 28)), (d(11, 9), d(11, 9))]),
    ("T5", "Architecture (Arch1)", True, "Must", [(d(10, 7), d(10, 18)), (d(11, 2), d(11, 8))],
     [(d(10, 12), d(10, 18)), (d(11, 2), d(11, 8))], [(d(11, 9), d(11, 12))], [(d(10, 28), d(10, 28)), (d(11, 9), d(11, 9))]),
    ("T6", "Website, url1.txt", False, "Must", [(d(10, 12), d(11, 13))], [(d(10, 12), d(11, 13))], [(d(11, 9), d(11, 15))],
     [(d(10, 18), d(10, 28)), (d(11, 9), d(11, 9))]),
    ("T7", "Map and collisions", False, "Must", [(d(10, 12), d(10, 25))], [(d(10, 12), d(10, 25))], [], [(d(10, 30), d(10, 30))]),
    ("T8", "Game screen, player, timer", True, "Must", [(d(10, 19), d(10, 25))], [(d(10, 19), d(10, 25))], [], [(d(11, 9), d(11, 9))]),
    ("T9", "Items and inventory", False, "Must", [(d(10, 26), d(11, 1))], [(d(10, 26), d(11, 1))], [], [(d(10, 26), d(10, 28))]),
    ("T10", "Hidden events", False, "Must", [(d(10, 26), d(11, 1))], [(d(10, 26), d(11, 1))], [], [(d(10, 26), d(10, 27)), (d(11, 9), d(11, 9))]),
    ("T11", "Geese and waves", True, "Must", [(d(10, 26), d(11, 1))], [(d(10, 26), d(11, 1))], [], [(d(11, 9), d(11, 9))]),
    ("T12", "Integration and testing", True, "Must", [(d(11, 2), d(11, 8))], [(d(11, 2), d(11, 8))], [(d(11, 10), d(11, 13))], [(d(11, 9), d(11, 9))]),
    ("T13", "Documents (Impl1, Plan1)", True, "Must", [(d(11, 2), d(11, 8))], [(d(11, 2), d(11, 8))], [(d(11, 9), d(11, 14))],
     [(d(10, 28), d(10, 28)), (d(11, 9), d(11, 9))]),
    ("T14", "Final review", True, "Must", [(d(11, 9), d(11, 13))], [(d(11, 9), d(11, 13))], [(d(11, 13), d(11, 14))], []),
    ("T15", "Submit", True, "Must", [(d(11, 15), d(11, 15))], [(d(11, 15), d(11, 15))], [(d(11, 15), d(11, 15))], []),
]

PLAN_FILL, PLAN_EDGE = "#cfe0f5", "#4f78ad"
REPLAN_FILL, REPLAN_EDGE = "#f3c98b", "#a96b1f"
ACTUAL = "#2f6f3f"
CRIT = "#b03a2e"
MILESTONES = [("Feature freeze (v1, v2)", d(11, 8)), ("Deadline", d(11, 16))]


def t(x, y, s, size=FONT, weight="normal", anchor="start", fill="#222"):
    return (f'<text x="{x}" y="{y}" font-family="{SANS}" font-size="{size}" font-weight="{weight}" '
            f'text-anchor="{anchor}" fill="{fill}">{escape(s)}</text>')


def x_of(day):
    return LEFT + (day - START).days * DAY


def bar(out, s, e, y, h, fill, edge, critical):
    x1 = x_of(s) + 1
    x2 = x_of(e) + DAY - 1
    if s == e and fill != ACTUAL:
        cx = (x1 + x2) / 2
        out.append(f'<polygon points="{cx},{y - 3} {cx + 8},{y + h / 2} {cx},{y + h + 3} {cx - 8},{y + h / 2}" fill="{fill}" stroke="{CRIT if critical else edge}" stroke-width="1.5"/>')
        return
    stroke = CRIT if critical and fill != ACTUAL else edge
    out.append(f'<rect x="{x1}" y="{y}" width="{x2 - x1}" height="{h}" rx="2" fill="{fill}" stroke="{stroke}" stroke-width="{1.8 if stroke == CRIT else 1}"/>')


def chart(title, source, plan_version, today, show_replan):
    days = (END - START).days + 1
    width = LEFT + days * DAY + 66
    height = TOP + len(TASKS) * ROW + 74
    out = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}">',
           f'<rect width="{width}" height="{height}" fill="#fff"/>',
           t(8, 20, title, 16, "bold"), t(8, 40, source, FONT, fill="#444")]
    for w in range(7):
        ws = date.fromordinal(START.toordinal() + 7 * w)
        span = min(7, days - 7 * w)
        x = x_of(ws)
        out.append(f'<rect x="{x}" y="{TOP - 28}" width="{span * DAY}" height="{len(TASKS) * ROW + 28}" fill="{"#f3f3f3" if w % 2 == 0 else "#fff"}"/>')
        out.append(t(x + span * DAY / 2, TOP - 12, (f"W{w + 1} " + ws.strftime("%d %b")) if span == 7 else f"W{w + 1}", FONT, "bold", "middle"))
    for r, (tid, name, critical, _prio, v1, v2, v3, actual) in enumerate(TASKS):
        y = TOP + r * ROW
        out.append(f'<line x1="6" y1="{y + ROW}" x2="{width - 6}" y2="{y + ROW}" stroke="#d9d9d9"/>')
        out.append(t(8, y + 19, f"{tid} {name}", FONT, "bold" if critical else "normal", fill=CRIT if critical else "#222"))
        for s, e in (v1 if plan_version == 1 else v2):
            bar(out, s, e, y + 4, 9, PLAN_FILL, PLAN_EDGE, critical)
        if show_replan:
            for s, e in v3:
                bar(out, s, e, y + 14, 9, REPLAN_FILL, REPLAN_EDGE, critical)
        for s, e in actual:
            if s <= today and plan_version > 1:
                bar(out, s, min(e, today), y + 15, 8, ACTUAL, ACTUAL, False)
    for name, day in MILESTONES:
        x = x_of(day) + DAY / 2
        out.append(f'<line x1="{x}" y1="{TOP}" x2="{x}" y2="{TOP + len(TASKS) * ROW}" stroke="#7a3db8" stroke-dasharray="4 3" stroke-width="1.4"/>')
        out.append(t(x, TOP + len(TASKS) * ROW + 18, name, FONT, "bold", "end" if day == END else "middle", fill="#7a3db8"))
    x = x_of(today) + DAY / 2
    out.append(f'<line x1="{x}" y1="{TOP - 28}" x2="{x}" y2="{TOP + len(TASKS) * ROW}" stroke="#000" stroke-width="2"/>')
    ky = TOP + len(TASKS) * ROW + 44
    keys = [(PLAN_FILL, PLAN_EDGE, f"plan v{plan_version}")]
    if show_replan:
        keys.append((REPLAN_FILL, REPLAN_EDGE, "re-plan (9 Nov)"))
    if plan_version > 1:
        keys.append((ACTUAL, ACTUAL, "actual (repository)"))
    kx = 8
    for fill, edge, label in keys:
        out.append(f'<rect x="{kx}" y="{ky}" width="24" height="11" fill="{fill}" stroke="{edge}"/>')
        out.append(t(kx + 30, ky + 11, label))
        kx += 50 + len(label) * 7.6
    out.append(t(kx + 10, ky + 11, "black line: date of the snapshot; red and bold: critical path", FONT, fill="#333"))
    out.append("</svg>")
    return "\n".join(out)


SNAPSHOTS = [
    (1, "Week 1 (7 Oct): plan v1 from the kick-off", "Source: kick-off meeting notes (weekly milestones).", 1, d(10, 7), False),
    (2, "Week 2 (12 Oct): plan v2, week 2 split into sub-team issues", "Source: GitHub issues #11-#18 and the board; progress from the repository.", 2, d(10, 12), False),
    (3, "Week 3 (19 Oct): plan v2 and progress", "Reconstructed from the repository history; no chart was saved this week.", 2, d(10, 19), False),
    (4, "Week 4 (26 Oct): plan v2 and progress", "Reconstructed from the repository history; no chart was saved this week.", 2, d(10, 26), False),
    (5, "Week 5 (2 Nov): plan v2 and progress", "Reconstructed from the repository history; no chart was saved this week.", 2, d(11, 2), False),
    (6, "Week 6 (9 Nov): plan v3, re-plan of the remaining work", "Source: the re-plan of 9 Nov and the repository history.", 2, d(11, 9), True),
]

if __name__ == "__main__":
    out_dir = Path(sys.argv[1] if len(sys.argv) > 1 else ".")
    for week, title, source, version, today, replan in SNAPSHOTS:
        (out_dir / f"plan-week{week}.svg").write_text(chart(title, source, version, today, replan), encoding="utf-8")
        print("wrote", out_dir / f"plan-week{week}.svg")
