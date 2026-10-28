# Architecture diagrams

Interim versions of the diagrams for Arch1 (the report has to show how the architecture evolved).

| File | What it is |
|------|-----------|
| `v1-draft.pdf` | First draft by the team: component diagram and activity diagram |
| `v2-draft.pdf` | v2: corrected against the requirements (Req1) and the code, with a table of what changed and how the components map to packages |
| `v2-component.svg`, `v2-activity.svg` | The two v2 diagrams as images |
| `source/` | The diagrams as code, so any version can be rebuilt and compared |

## Rebuild a version

```bash
cd docs/architecture/source
python3 draw_arch.py .          # writes component.svg and activity.svg next to the script
# then print arch_v2.html to PDF, for example with Chrome:
"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" --headless=new --no-pdf-header-footer --print-to-pdf=v2-draft.pdf "file://$PWD/arch_v2.html"
```

When the design changes, keep the old PDF (v2-draft.pdf, v3-draft.pdf, ...) and change the script for the next version.
Still to add: a class diagram (the Architecture team) and a sequence diagram for "the player uses an item".
