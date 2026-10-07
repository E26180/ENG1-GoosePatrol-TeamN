# Goose Patrol - Greenfield Game Development (Assessment 1)

University of York · ENG1 Software Engineering · Team N

## Tech Stack

| Component       | Choice                          |
|-----------------|---------------------------------|
| Language        | Java 17 (LTS)                   |
| Game framework  | [LibGDX](https://libgdx.com/)   |
| Build tool      | Gradle (via the Gradle wrapper) |
| Maps            | [Tiled](https://www.mapeditor.org/) (`.tmx`) |

## Game Concept

**Goose Patrol** is a short, top-down escape game set on the University of York campus.
The player is a student who has five minutes to make it off campus, avoiding the
infamous campus geese along the way.

- **Time limit:** each run lasts 5 minutes; the clock is always visible on screen.
- **Map:** a stylised University of York campus built with Tiled.
- **Obstacles:** patrolling geese and other campus hazards that slow down or catch the player.
- **Events:** positive, negative and hidden events encountered along the route.
- **Win/lose:** escape before the timer runs out to win; get caught or run out of time to lose.

## Repository Layout

```
ENG1-GoosePatrol-TeamN/
├── core/              Game code shared by all platforms (package uk.ac.york.eng1.goosepatrol)
├── lwjgl3/            Desktop launcher (LWJGL3 backend)
├── assets/            Game assets
│   ├── textures/      Sprites, tilesets, UI images
│   ├── audio/         Music and sound effects
│   └── maps/          Tiled map files (.tmx / .tsx)
├── docs/assessment1/  Assessment 1 deliverables (url1.txt, Req1/Arch1/Plan1/Risk1/Impl1.pdf)
└── website/           Static files for the team website
```

## Getting Started

### Prerequisites

- **JDK 17** installed, with `JAVA_HOME` pointing at it (check with `java -version`).
- **Git**.
- An IDE such as IntelliJ IDEA (recommended), Eclipse or VS Code with the Java extensions.

You do **not** need to install Gradle yourself; the project uses the Gradle wrapper (`gradlew`).

### 1. Clone the repository

```bash
git clone https://github.com/E26180/ENG1-GoosePatrol-TeamN.git
cd ENG1-GoosePatrol-TeamN
```

### 2. Build

macOS / Linux:

```bash
./gradlew build
```

Windows:

```bat
gradlew.bat build
```

### 3. Run the game

macOS / Linux:

```bash
./gradlew lwjgl3:run
```

Windows:

```bat
gradlew.bat lwjgl3:run
```

### 4. Open in an IDE

- **IntelliJ IDEA:** *File → Open…* and select the project folder; import it as a Gradle project.
- **Eclipse:** *File → Import → Gradle → Existing Gradle Project*.
- **VS Code:** open the folder with the *Extension Pack for Java* and *Gradle for Java* installed.

## Team Workflow

1. Pull the latest `main` before starting work: `git pull origin main`.
2. Create a feature branch: `git checkout -b feature/<short-description>`.
3. Commit small, focused changes with clear messages.
4. Push your branch and open a pull request into `main`.
5. Get at least one teammate to review before merging.

`main` is protected: direct pushes are blocked, and a pull request needs **1 approval** from
someone other than its author before it can be merged.

When you add a new file (e.g. an image in `assets/`), check `git status` before committing so
it is not left out.

## Assessment 1 Deliverables

| File        | Deliverable                    |
|-------------|--------------------------------|
| `url1.txt`  | Team website URL               |
| `Req1.pdf`  | Requirements                   |
| `Arch1.pdf` | Architecture                   |
| `Plan1.pdf` | Method selection and planning  |
| `Risk1.pdf` | Risk assessment and mitigation |
| `Impl1.pdf` | Implementation                 |

All files live in [`docs/assessment1/`](docs/assessment1/).
