# Arch1 outline

**Status:** the report is written in `source/Arch1.html` and printed to `Arch1-draft.pdf` (cover plus 6 pages). Only the goose package is still drawn as planned.

A skeleton for `Arch1.pdf` (22 marks, at most 6 pages, cover page not counted).
Check every heading against the Assessment 1 brief before writing; the brief wins if anything here differs.

Format: Arial 11pt, tables 10pt, margins of at least 1 cm, names only on the cover page.

## Part a: the architecture (about 4 pages)

| Section | Content | Status | Source |
|---------|---------|--------|--------|
| a1. Languages and tools | UML 2 for every diagram. Diagrams are written as code (Python scripts that output SVG) so each version can be rebuilt and compared; PDFs are printed with Chrome. Say why: versions live in Git next to the code, and changes show up in diffs. | to write | `source/` |
| a2. Structural view, abstract | Component diagram of the whole game | done (v2) | `v2-component.svg` |
| a3. Structural view, concrete | Class diagram(s): one per package, or one combined diagram of the main classes | items done; enemy, player, core, event to do | `items-class.svg` |
| a4. Behavioural view, abstract | Activity diagram of one game loop | done (v2) | `v2-activity.svg` |
| a5. Behavioural view, concrete | Sequence diagrams for key scenarios: using an item; a hidden event triggering; a wave ending; pause | item done; others to do | `items-sequence.svg` |
| a6. Responsibilities | CRC cards for the main classes (short, can be a table) | items done | `items-subsystem.pdf`, section 5 |

## Part b: justification (about 2 pages)

| Section | Content | Status | Source |
|---------|---------|--------|--------|
| b1. Why this architecture | Components that match the requirement groups; interfaces between packages (for example `Feedable`, `GooseLocator`, `SpeedBoostTarget`) so each developer can build and test alone; a game loop driven by LibGDX `render(delta)` | items part done | `items-subsystem.pdf`, section 2 |
| b2. Traceability | Table: requirement ID (Req1) to component, class and test | items rows done | `items-subsystem.pdf`, section 7 |
| b3. How the architecture evolved | v1 (first draft) to v2 (checked against Req1 and the code) to the final version; what changed and why. Interim versions must be on the website. | v1 to v2 done; item-level history done | `README.md`, `v2-draft.pdf`, `items-subsystem.pdf`, section 6 |
| b4. Assessment 2 | Which parts were designed to be extended (new item subclasses, new hidden events through `IHiddenEvent`, score) | to write | |

## Who does what

Each developer gives the class diagram, one sequence diagram and CRC cards for their own package, in the same style (`source/draw_items.py` can be copied as a starting point). The Arch1 owner joins them, writes the text and checks the page limit.

| Package | Developer | Class diagram | Sequence diagram | CRC cards |
|---------|-----------|:---:|:---:|:---:|
| item | Ahmet | ✅ | ✅ | ✅ |
| event | Alex | | | |
| enemy (geese) | Leo | | | |
| player, map | | | | |
| core, UI, timer, counters | | | | |
