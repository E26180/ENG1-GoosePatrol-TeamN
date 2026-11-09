# Assessment 1 submission checklist

Deadline: **Monday 16 November 2026, 12:00**. Check every line against the Assessment 1 brief; the brief wins if anything here differs.

## Deliverables

| File | Owner | State | Notes |
|------|-------|-------|-------|
| `url1.txt` | Ahmet | ✅ | `https://e26180.github.io/ENG1-GoosePatrol-TeamN/` |
| `Req1.pdf` | Ahmet, Alex | ✅ | 5 pages |
| `Arch1.pdf` | Ahmet | ✅ | Cover + 6 pages, printed from `docs/architecture/source/Arch1.html` |
| `Plan1.pdf` | Ahmet | ✅ | Cover + 2 + 1 + 2 pages, printed from `source/Plan1.html`; Gantt charts from `docs/plan/source/draw_plan.py` |
| `Risk1.pdf` | Ahmet (from the team's register) | ✅ | Cover + 1 + 3 pages, printed from `source/Risk1.html`; check the owners with the team |
| `Impl1.pdf` | Ahmet, Alex | ✅ | Cover + 1 page |
| Game JAR | everyone | ✅ | Built by the website workflow from `main`; full games on macOS and Ubuntu (9 Nov); still to be started on a real Windows PC |

## Before submitting

- [ ] Each PDF: single-spaced sans-serif 11 pt, no text in figures or tables below 10 pt (check the printed PDF, not the HTML), margins of at least 1 cm, a title page with the group number, name and member names, within its page limit (title page and references not counted).
- [ ] Every requirement ID used in Arch1 and Impl1 exists in Req1.
- [ ] Impl1 lists every library and asset with its licence, and every A1 requirement that is not complete.
- [x] Ranitaya City pack licence confirmed on its itch.io page (Impl1 and `assets/CREDITS.md`).
- [ ] `./gradlew test` passes and `./gradlew lwjgl3:jar` builds.
- [ ] The JAR starts with `java -jar GoosePatrol-1.0.0.jar` on Windows, macOS and Linux, and a full 5-minute game runs without an exception (QR_PLATFORM, QR_STABLE). macOS and Linux done on 9 Nov (`docs/testing/platform-test.md`); Windows still needs a real PC.
- [ ] Every public class and method has Javadoc (`javadoc -Xdoclint:all` shows no warnings).
- [ ] The repository is public (or otherwise reachable by the markers and other teams), as the website links to it.
- [ ] Every link on the website works, and the deliverables table links every final PDF.
- [ ] The final PDFs are in this folder with exactly the file names above.
