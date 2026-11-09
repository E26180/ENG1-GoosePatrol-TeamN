# Assessment 1 Deliverables

Place the final PDFs here before submission:

| File        | Deliverable                     |
|-------------|---------------------------------|
| `url1.txt`  | URL of the team website         |
| `Req1.pdf`  | Requirements                    |
| `Arch1.pdf` | Architecture                    |
| `Plan1.pdf` | Method selection and planning   |
| `Risk1.pdf` | Risk assessment and mitigation  |
| `Impl1.pdf` | Implementation                  |

## Rebuilding Impl1.pdf

`Impl1.pdf` is printed from `source/Impl1.html` (A4, Arial 11pt, tables 10pt). After changing the HTML, run this from the repository root (macOS; on other systems use the path to Chrome or Chromium):

```
"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome" --headless=new --no-pdf-header-footer --print-to-pdf="$PWD/docs/assessment1/Impl1.pdf" "file://$PWD/docs/assessment1/source/Impl1.html"
```
