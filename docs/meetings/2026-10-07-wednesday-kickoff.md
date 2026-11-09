# Goose Patrol: Wednesday Kick-off Meeting & Project Roadmap

**ENG1 Software Engineering · University of York · Team N**
**Meeting:** Wednesday 7 October 2026 (Week 1)
**Repository:** https://github.com/E26180/ENG1-GoosePatrol-TeamN (private)

---

## 0. Team Roster

> Fill in the blanks at the start of the meeting. Only the repo owner's details and the GitHub
> usernames already invited to the repository were known when this was written.

| # | Name | GitHub username | University email | Repo access |
|---|------|-----------------|------------------|-------------|
| 1 | Ahmet Ege Baykoz | `E26180` | gnn534@york.ac.uk | Owner |
| 2 | _TBC_ | `lmr561` | _TBC_ | Collaborator ✅ |
| 3 | _TBC_ | `8ange` | _TBC_ | Invite pending ⏳ |
| 4 | _TBC_ | `AlzYaPal` | _TBC_ | Invite pending ⏳ |
| 5 | _TBC_ | _TBC_ | _TBC_ | Not invited yet |
| 6 | _TBC_ | _TBC_ | _TBC_ | Not invited yet |
| 7 | _TBC_ | _TBC_ | _TBC_ | Not invited yet |
| 8 | _TBC_ | _TBC_ | _TBC_ | Not invited yet |

**Action:** everyone without repo access gives their GitHub username to Ahmet during the meeting.

---

## 1. Executive Summary & Assessment 1 Overview

| Item | Detail |
|------|--------|
| **Assessment** | Assessment 1: Greenfield Game Development |
| **Product** | *Goose Patrol*, a 5-minute escape game set on the University of York campus |
| **Tech stack** | Java 17 · LibGDX · Gradle · Tiled (maps) · GitHub |
| **Deadline** | **Monday 16 November 2026, 12:00 noon (Week 7)** |
| **Weight** | **35%** of the total module grade |
| **Time left** | 6 weeks from this meeting |

### Core scope (must-haves)

- **5-minute countdown timer** that is always visible, plus a **pause** function.
- **Simple York campus map** that the player can move around.
- **Exactly 2 initial inventory items.**
- **Exactly 2 hidden events.**
- **On-screen counters** for items collected and events triggered.

### Guiding principles

- Build the **minimum scope well**. Don't add extra features before the core loop works.
- Documentation is worth more marks than code. Treat the PDFs as first-class deliverables.
- Everything goes through GitHub, so contributions are visible and traceable.

---

## 2. 8-Person Team Role & Task Allocation

### Marks available per deliverable

| Deliverable | Marks |
|-------------|------:|
| `url1.txt` / team website | 3 |
| `Req1.pdf`: Requirements | 20 |
| `Arch1.pdf`: Architecture | 22 |
| `Plan1.pdf`: Method selection & planning | 10 |
| `Risk1.pdf`: Risk assessment & mitigation | 10 |
| `Impl1.pdf`: Implementation (incl. code) | 25 |
| **Total** | **90** |

### Role allocation

| # | Role | Primary deliverable(s) | Supporting deliverable | Marks contributed to | Assigned to |
|---|------|------------------------|------------------------|---------------------:|-------------|
| 1 | **Team Lead & Project Manager** | `Plan1.pdf` (10) + website / `url1.txt` (3) | Co-author `Risk1.pdf` (10) | **23** | Ahmet Ege Baykoz *(proposed)* |
| 2 | **Requirements Lead** | `Req1.pdf` (20) | | **20** | Member 2 |
| 3 | **Software Architect** | `Arch1.pdf` (22) | | **22** | Member 3 |
| 4 | **Risk & Quality Manager** | `Risk1.pdf` (10) + licence tracking | Co-author `Req1.pdf` (20) | **30** | Member 4 |
| 5 | **Core Engine & UI Developer** | `Impl1.pdf` (25): game loop, 5-min timer, pause, counters | | **25** | Member 5 |
| 6 | **Map & Movement Developer** | `Impl1.pdf` (25): campus map, player movement & collisions | | **25** | Member 6 |
| 7 | **Items & Inventory Developer** | `Impl1.pdf` (25): 2 initial items & inventory mechanics | | **25** | Member 7 |
| 8 | **Goose AI & Hidden Events Developer** | `Impl1.pdf` (25): goose behaviour & 2 hidden events | | **25** | Member 8 |

### ⚖️ Equitable work allocation rule

> **Every team member must contribute to at least 15 marks' worth of deliverables.**

- With primary deliverables only, the **Team Lead (13)** and **Risk & Quality Manager (10)** would be **below 15**.
  That is why each has a **supporting deliverable** in the table above. Every role now reaches **≥ 20 marks**.
- Contributions are tracked through **GitHub commits/PRs** and the **Plan1 task log**, so the allocation can be evidenced.
- Developers also give the Architect diagrams and class descriptions for their own subsystem, so `Arch1.pdf` matches the code.

---

## 3. Week 1 Action Plan (5–11 October 2026)

### Tasks per person

| Role | Week 1 tasks | Done by |
|------|--------------|---------|
| **Team Lead & PM** | Invite remaining members to GitHub · set up branch protection on `main` · create GitHub Project board · draft 6-week Gantt chart for `Plan1` · book a weekly meeting slot | Fri 9 Oct |
| **Requirements Lead** | Read the product brief · draft user & system requirements list · prepare questions for the customer interview | Fri 9 Oct |
| **Software Architect** | Sketch first CRC cards / class diagram (Game, Screen, Player, Goose, Item, Event, Timer) · pick diagram tool (e.g. PlantUML) | Sun 11 Oct |
| **Risk & Quality Manager** | Create risk register template (ID, description, likelihood, severity, owner, mitigation) · start licence log for all assets | Fri 9 Oct |
| **Core Engine & UI Dev** | Generate LibGDX project with **gdx-liftoff** (Java 17, Desktop/LWJGL3) · commit Gradle wrapper · empty game screen running | Thu 8 Oct |
| **Map & Movement Dev** | Install **Tiled** · sketch campus map layout (key York landmarks) · find a free tileset (licence noted) | Sun 11 Oct |
| **Items & Inventory Dev** | Propose the 2 initial items and what they do · draft the inventory data model | Sun 11 Oct |
| **Goose AI & Events Dev** | Propose goose behaviour (patrol / chase) · propose the 2 hidden events and their triggers | Sun 11 Oct |
| **Everyone** | Accept GitHub invite · clone repo · install JDK 17 + IntelliJ IDEA · run the game once it's on `main` | Sun 11 Oct |

### 🛠️ Development environment setup (everyone)

1. Install **JDK 17** (e.g. Eclipse Temurin 17) and check with `java -version`.
2. Install **IntelliJ IDEA** (free Community edition, or Ultimate with a student licence).
3. Clone the repository:
   ```bash
   git clone https://github.com/E26180/ENG1-GoosePatrol-TeamN.git
   ```
4. Open the folder in IntelliJ as a **Gradle project**, with the Project SDK set to **17**.
5. Once the LibGDX skeleton is merged, run it:
   ```bash
   ./gradlew lwjgl3:run
   ```

### 🌿 GitHub workflow rules

| Rule | Detail |
|------|--------|
| **Protected `main`** | No direct pushes to `main`. All changes go through a pull request. |
| **Feature branches** | `feature/<feature-name>`, e.g. `feature/countdown-timer`, `feature/goose-ai` |
| **Docs branches** | `docs/<deliverable>`, e.g. `docs/req1-draft` |
| **Pull requests** | At least **1 reviewer approval** before merging, and the game must still build and run |
| **Commits** | Small and focused, with clear messages, e.g. `Add pause menu toggle` |
| **Sync often** | `git pull` on `main` before starting work, and rebase or merge `main` into your branch regularly |
| **Issues** | Each task is a GitHub Issue on the Project board and is assigned to one person |

### 🌐 Team website setup

- Lives in the `website/` folder of the repo, published with **GitHub Pages**.
  - GitHub Pages on a private repo may need a paid plan. Check this, or host the site from a separate public repo.
- Minimum pages: **Home** (team + game overview) · **Deliverables** (links to all PDFs) · **Downloads** (game JAR).
- Put the final website URL in `docs/assessment1/url1.txt`.

---

## 4. Deliverable Checklist & Formatting Rules

### 📄 Page limits

| File | Page limit | Owner | Status |
|------|-----------|-------|--------|
| `url1.txt` | Website URL only | Team Lead | ☐ |
| `Req1.pdf` | **≤ 1 + 3 pages** | Requirements Lead | ☐ |
| `Arch1.pdf` | **≤ 6 pages** | Software Architect | ☐ |
| `Plan1.pdf` | **≤ 2 + 1 + 2 pages** | Team Lead | ☐ |
| `Risk1.pdf` | **≤ 1 + 3 pages** | Risk & Quality Manager | ☐ |
| `Impl1.pdf` | **≤ 1 page** | 4 Developers | ☐ |

> Content past the page limit **will not be marked**. Check page counts before every submission draft.

### ✍️ Formatting rules (all PDFs)

- **Font:** sans-serif (e.g. Arial, Calibri, Helvetica), **11pt**.
- **Line spacing:** single.
- **Margins:** at least **1 cm** on every side.
- **Cover page:** team name/number and **member names only**. **No exam numbers.**
- Use a shared template (Google Docs / Word / LaTeX) so all PDFs look the same.

### 🗓️ Internal milestones (proposed)

| Week | Date | Milestone |
|------|------|-----------|
| 1 | Sun 11 Oct | Environments set up · LibGDX skeleton on `main` · roles confirmed |
| 2 | Sun 18 Oct | Requirements draft · first architecture diagrams · risk register v1 |
| 3 | Sun 25 Oct | Map + movement + timer playable |
| 4 | Sun 1 Nov | Items, inventory, goose AI & hidden events working |
| 5 | Sun 8 Nov | Feature freeze · all PDF drafts complete |
| 6 | Fri 13 Nov | Final review · page-limit and formatting check · website live |
| 7 | **Mon 16 Nov, 12:00** | **Submit** (aim to submit by Sun 15 Nov) |

### 📝 Peer Assessment: mandatory

- **Every member must complete the Peer Assessment survey.** It is mandatory.
- Keep evidence of contributions (commits, PRs, meeting notes) up to date. It supports fair peer marks.

---

## 5. Decisions Needed Today

- [ ] Confirm all 8 names and contact details in the roster
- [ ] Confirm role allocation (who takes which of the 8 roles)
- [ ] Agree on a weekly meeting time and communication channel
- [ ] Agree on the document tool and template for the PDFs
- [ ] Agree on the website hosting approach
- [ ] Everyone has GitHub access before leaving the meeting
