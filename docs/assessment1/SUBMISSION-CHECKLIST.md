# Assessment 1 submission checklist

Deadline: **Monday 16 November 2026, 12:00**. Check every line against the Assessment 1 brief; the brief wins if anything here differs.

## Deliverables

| File | Owner | State | Notes |
|------|-------|-------|-------|
| `url1.txt` | Ahmet | ✅ | `https://e26180.github.io/ENG1-GoosePatrol-TeamN/` |
| `Req1.pdf` | Ahmet, Alex | ✅ | 5 pages |
| `Arch1.pdf` | Ahmet | ✅ | Cover + 6 pages, printed from `docs/architecture/source/Arch1.html` |
| `Plan1.pdf` | Sean | ❓ | Not in the repository yet |
| `Risk1.pdf` | Ahmet (from the team's register) | ✅ | Cover + 3 pages, printed from `source/Risk1.html`; check the owners with the team |
| `Impl1.pdf` | Ahmet, Alex | ✅ | Cover + 1 page |
| Game JAR | everyone | ✅ | Built by the website workflow from `main`; still to be started on Windows and Linux |

## Before submitting

- [ ] Each PDF: Arial 11pt, tables 10pt, margins of at least 1 cm, names only on the cover page, within its page limit (cover not counted).
- [ ] Every requirement ID used in Arch1 and Impl1 exists in Req1.
- [ ] Impl1 lists every library and asset with its licence, and every A1 requirement that is not complete.
- [ ] Ranitaya City pack licence confirmed on its itch.io page (Impl1 and `assets/CREDITS.md`).
- [ ] `./gradlew test` passes and `./gradlew lwjgl3:jar` builds.
- [ ] The JAR starts with `java -jar GoosePatrol-1.0.0.jar` on Windows, macOS and Linux, and a full 5-minute game runs without an exception (QR_PLATFORM, QR_STABLE).
- [ ] Every public class and method has Javadoc (`javadoc -Xdoclint:all` shows no warnings).
- [ ] The repository is public, so the website and the code can be reached by the markers.
- [ ] Every link on the website works, and the deliverables table links every final PDF.
- [ ] The final PDFs are in this folder with exactly the file names above.
