"""Draws the Gantt charts of the project plan as SVG, one per plan version, for Plan1 and the website.

Usage: python3 draw_plan.py <output folder>
Writes plan-v1.svg (kick-off, 7 Oct), plan-v2.svg (week 2, 12 Oct), plan-v3.svg (re-plan, 9 Nov).
Planned dates come from the kick-off notes, the GitHub issues and the submission checklist;
actual dates come from the repository: the first commit of a piece of work to its merge on main.
"""
import sys
from datetime import date
from pathlib import Path
from xml.sax.saxutils import escape

FONT = "Arial, Helvetica, sans-serif"
START = date(2026, 10, 5)   # Monday of week 1
END = date(2026, 11, 16)    # submission day
DAY = 26                    # pixels per day
LEFT = 330                  # width of the task-name column
ROW = 30
TOP = 92


def d(month, day):
    return date(2026, month, day)


# id, task, critical?, v1 plan, v2 plan, v3 plan (remaining), actual segments
TASKS = [
    ("T1", "Set-up: repository, libGDX skeleton, tools", True,
     [(d(10, 5), d(10, 11))], [(d(10, 5), d(10, 11))], [], [(d(10, 5), d(10, 7))]),
    ("T2", "Client meeting", False, [], [(d(10, 14), d(10, 14))], [], [(d(10, 14), d(10, 14))]),
    ("T3", "Requirements (Req1)", True,
     [(d(10, 7), d(10, 18))], [(d(10, 12), d(10, 18))], [], [(d(10, 12), d(10, 27))]),
    ("T4", "Risk register (Risk1)", False,
     [(d(10, 7), d(10, 18))], [(d(10, 12), d(10, 18))], [(d(11, 9), d(11, 10))], [(d(10, 12), d(10, 28)), (d(11, 9), d(11, 9))]),
    ("T5", "Architecture diagrams (Arch1)", True,
     [(d(10, 7), d(10, 18)), (d(11, 2), d(11, 8))], [(d(10, 12), d(10, 18)), (d(11, 2), d(11, 8))], [(d(11, 9), d(11, 10))],
     [(d(10, 28), d(10, 28)), (d(11, 9), d(11, 9))]),
    ("T6", "Website and url1.txt", False,
     [(d(10, 12), d(11, 13))], [(d(10, 12), d(11, 13))], [(d(11, 9), d(11, 15))], [(d(10, 18), d(10, 28)), (d(11, 9), d(11, 9))]),
    ("T7", "Campus map and collisions", False,
     [(d(10, 12), d(10, 25))], [(d(10, 12), d(10, 25))], [], [(d(10, 30), d(10, 30))]),
    ("T8", "Player, timer, pause, HUD (game screen)", True,
     [(d(10, 19), d(10, 25))], [(d(10, 19), d(10, 25))], [], [(d(11, 9), d(11, 9))]),
    ("T9", "Items and inventory", False,
     [(d(10, 26), d(11, 1))], [(d(10, 26), d(11, 1))], [], [(d(10, 26), d(10, 28))]),
    ("T10", "Hidden events", False,
     [(d(10, 26), d(11, 1))], [(d(10, 26), d(11, 1))], [], [(d(10, 26), d(10, 27)), (d(11, 9), d(11, 9))]),
    ("T11", "Geese and waves", True,
     [(d(10, 26), d(11, 1))], [(d(10, 26), d(11, 1))], [], [(d(11, 9), d(11, 9))]),
    ("T12", "Integration and testing", True,
     [(d(11, 2), d(11, 8))], [(d(11, 2), d(11, 8))], [(d(11, 10), d(11, 13))], [(d(11, 9), d(11, 9))]),
    ("T13", "Documents: Impl1, Plan1 and final drafts", True,
     [(d(11, 2), d(11, 8))], [(d(11, 2), d(11, 8))], [(d(11, 9), d(11, 10))], [(d(10, 28), d(10, 28)), (d(11, 9), d(11, 9))]),
    ("T14", "Final review: formatting, page limits, links", True,
     [(d(11, 9), d(11, 13))], [(d(11, 9), d(11, 13))], [(d(11, 11), d(11, 14))], []),
    ("T15", "Submit", True,
     [(d(11, 15), d(11, 15))], [(d(11, 15), d(11, 15))], [(d(11, 15), d(11, 15))], []),
]

MILESTONES = [  # name, v1 date, version shown from
    ("Feature freeze", d(11, 8)),
    ("Deadline 16 Nov 12:00", d(11, 16)),
]

PLAN_FILL = "#cfe0f5"
PLAN_EDGE = "#5b84b8"
ACTUAL_FILL = "#2f6f3f"
REPLAN_FILL = "#f3c98b"
REPLAN_EDGE = "#b8792a"
CRIT = "#c0392b"


def text(x, y, s, size=12, weight="normal", anchor="start", fill="#222", style="normal"):
    return (f'<text x="{x}" y="{y}" font-family="{FONT}" font-size="{size}" font-weight="{weight}" '
            f'font-style="{style}" text-anchor="{anchor}" fill="{fill}">{escape(s)}</text>')


def x_of(day):
    return LEFT + (day - START).days * DAY


def chart(title, subtitle, version, today):
    days = (END - START).days + 1
    width = LEFT + days * DAY + 80
    height = TOP + len(TASKS) * ROW + 110
    out = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{width}" height="{height}" viewBox="0 0 {width} {height}">',
           f'<rect width="{width}" height="{height}" fill="#fff"/>',
           text(14, 24, title, 17, "bold"), text(14, 44, subtitle, 12, fill="#555")]
    # week bands and day grid
    for w in range(7):
        ws = START.toordinal() + 7 * w
        x = x_of(date.fromordinal(ws))
        span = min(7, days - 7 * w)  # the last week only shows submission day
        out.append(f'<rect x="{x}" y="{TOP - 30}" width="{span * DAY}" height="{len(TASKS) * ROW + 30}" fill="{"#f6f6f6" if w % 2 == 0 else "#ffffff"}"/>')
        out.append(text(x + span * DAY / 2, TOP - 16, f"Week {w + 1}" if span == 7 else f"W{w + 1}", 12, "bold", "middle"))
        out.append(text(x + span * DAY / 2, TOP - 3, date.fromordinal(ws).strftime("%d %b"), 10.5, anchor="middle", fill="#555"))
    for i in range(days + 1):
        x = LEFT + i * DAY
        out.append(f'<line x1="{x}" y1="{TOP}" x2="{x}" y2="{TOP + len(TASKS) * ROW}" stroke="#e2e2e2"/>')

    for r, (tid, name, critical, v1, v2, v3, actual) in enumerate(TASKS):
        y = TOP + r * ROW
        out.append(f'<line x1="10" y1="{y + ROW}" x2="{width - 10}" y2="{y + ROW}" stroke="#d6d6d6"/>')
        label = f"{tid}  {name}"
        out.append(text(14, y + 19, label, 12, "bold" if critical else "normal", fill=CRIT if critical else "#222"))
        if critical:
            out.append(text(LEFT - 8, y + 19, "CP", 10, "bold", "end", fill=CRIT))
        plan = v1 if version == 1 else v2
        for s, e in plan:
            bar(out, s, e, y + 5, 9, PLAN_FILL, PLAN_EDGE, critical)
        if version == 3:
            for s, e in v3:
                bar(out, s, e, y + 15, 9, REPLAN_FILL, REPLAN_EDGE, critical)
        if version >= 2:
            for s, e in actual:
                if s <= today:
                    bar(out, s, min(e, today), y + 15 if version == 2 else y + 16, 9 if version == 2 else 8,
                        ACTUAL_FILL, ACTUAL_FILL, False)

    for name, day in MILESTONES:
        x = x_of(day) + DAY / 2
        out.append(f'<line x1="{x}" y1="{TOP}" x2="{x}" y2="{TOP + len(TASKS) * ROW}" stroke="#7a3db8" stroke-dasharray="4 3"/>')
        out.append(text(x, TOP + len(TASKS) * ROW + 16, name, 11, "bold", "middle", fill="#7a3db8"))
    x = x_of(today) + DAY / 2
    out.append(f'<line x1="{x}" y1="{TOP - 34}" x2="{x}" y2="{TOP + len(TASKS) * ROW}" stroke="#000" stroke-width="2"/>')
    out.append(text(x + 4, TOP - 36, "plan made " + today.strftime("%d %b"), 11, "bold"))

    # key
    ky = TOP + len(TASKS) * ROW + 40
    items = [(PLAN_FILL, PLAN_EDGE, "planned (" + ("kick-off plan" if version == 1 else "plan at this version") + ")")]
    if version == 3:
        items.append((REPLAN_FILL, REPLAN_EDGE, "re-planned remaining work (9 Nov)"))
    if version >= 2:
        items.append((ACTUAL_FILL, ACTUAL_FILL, "actual: first commit to merge on main"))
    kx = 14
    for fill, edge, label in items:
        out.append(f'<rect x="{kx}" y="{ky}" width="26" height="10" fill="{fill}" stroke="{edge}"/>')
        out.append(text(kx + 32, ky + 9, label, 11.5))
        kx += 50 + len(label) * 6.4
    out.append(text(14, ky + 30, "CP and bold red = on the critical path. Weeks start on Monday. Dashed purple lines: milestones.", 11.5, fill="#444"))
    out.append("</svg>")
    return "\n".join(out)


def bar(out, s, e, y, h, fill, edge, critical):
    x1 = x_of(s) + 2
    x2 = x_of(e) + DAY - 2
    if s == e and fill != ACTUAL_FILL:
        # a one-day planned item is a milestone diamond
        cx = (x1 + x2) / 2
        out.append(f'<polygon points="{cx},{y - 3} {cx + 8},{y + h / 2} {cx},{y + h + 3} {cx - 8},{y + h / 2}" fill="{fill}" stroke="{CRIT if critical else edge}" stroke-width="1.5"/>')
        return
    stroke = CRIT if critical and fill != ACTUAL_FILL else edge
    width = 1.8 if critical and fill != ACTUAL_FILL else 1
    out.append(f'<rect x="{x1}" y="{y}" width="{x2 - x1}" height="{h}" rx="2" fill="{fill}" stroke="{stroke}" stroke-width="{width}"/>')


if __name__ == "__main__":
    out_dir = Path(sys.argv[1] if len(sys.argv) > 1 else ".")
    versions = [
        (1, "Plan v1 (kick-off, 7 Oct)", "Weekly milestones agreed at the kick-off meeting: build in weeks 3-4, feature freeze 8 Nov.", d(10, 7)),
        (2, "Plan v2 (week 2, 12 Oct)", "Week 2 split into GitHub issues for sub-teams (requirements, architecture, risk, planning, website) before the client meeting.", d(10, 12)),
        (3, "Plan v3 (re-plan, 9 Nov) with progress so far", "Most code arrived late; the remaining work was re-planned around the deadline. Actual work shown up to 9 Nov.", d(11, 9)),
    ]
    for version, title, subtitle, today in versions:
        (out_dir / f"plan-v{version}.svg").write_text(chart(title, subtitle, version, today), encoding="utf-8")
        print("wrote", out_dir / f"plan-v{version}.svg")
