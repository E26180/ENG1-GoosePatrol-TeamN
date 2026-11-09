# Architecture diagrams

Interim versions of the diagrams for Arch1 (the report has to show how the architecture evolved).

| File | What it is |
|------|-----------|
| `v1-draft.pdf` | First draft by the team: component diagram and activity diagram |
| `v2-draft.pdf` | v2: corrected against the requirements (Req1) and the code, with a table of what changed and how the components map to packages |
| `v2-component.svg`, `v2-activity.svg` | The two v2 diagrams as images |
| `items-subsystem.pdf` | Items and Inventory subsystem: class diagram, sequence diagram, CRC cards, design reasons, evolution and traceability |
| `items-class.svg`, `items-sequence.svg` | The two item diagrams as images |
| `Arch1-outline.md` | Skeleton of the Arch1 report: what each section needs and what is already done |
| `../assessment1/Arch1.pdf` | The Arch1 report (v3, final design), printed from `source/Arch1.html` |
| `final-component.svg`, `final-class.svg`, `final-activity.svg`, `final-sequence.svg` | The v3 diagrams used in Arch1, matching the code on main |
| `source/` | The diagrams as code, so any version can be rebuilt and compared |

## Rebuild a version

```bash
cd docs/architecture/source
python3 draw_arch.py .          # writes component.svg and activity.svg next to the script
# then print arch_v2.html to PDF, for example with Chrome:
"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" --headless=new --no-pdf-header-footer --print-to-pdf=v2-draft.pdf "file://$PWD/arch_v2.html"
```

The item diagrams and document:

```bash
cd docs/architecture
python3 source/draw_items.py .  # writes items-class.svg and items-sequence.svg
"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" --headless=new --no-pdf-header-footer --print-to-pdf=items-subsystem.pdf "file://$PWD/source/items_subsystem.html"
```

The final (v3) diagrams and the Arch1 report:

```bash
cd docs/architecture
python3 source/draw_final.py .  # writes the four final-*.svg files (uses draw_items.py)
"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" --headless=new --no-pdf-header-footer --print-to-pdf="$PWD/../assessment1/Arch1.pdf" "file://$PWD/source/Arch1.html"
```

When the design changes, keep the old PDF (v2-draft.pdf, v3-draft.pdf, ...) and change the script for the next version.
When the code changes, update `draw_final.py` and `Arch1.html` and print the PDF again; the page limit is 6 pages plus the cover.
