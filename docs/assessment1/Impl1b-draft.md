# Impl1 part b: draft

Draft for the one-page part of `Impl1.pdf` (Ahmet and Alex). Update it whenever a library, an asset or a feature changes, then copy it into the final PDF.

## 1. Third-party libraries, tools and assets

| Name | Version | Used for | Licence |
|------|---------|----------|---------|
| libGDX (`gdx`, `gdx-backend-lwjgl3`, `gdx-platform`) | 1.14.2 | Game framework: rendering, input, audio, game loop | Apache 2.0 |
| LWJGL 3 (through the libGDX desktop backend) | 3.4.3 | Window, OpenGL and OpenAL on Windows, macOS and Linux | BSD 3-Clause |
| JUnit Jupiter and JUnit Platform Launcher (tests only, not in the JAR) | 5.11.4 / 1.11.4 | Unit tests | Eclipse Public License 2.0 |
| Gradle (wrapper) | 9.3.0 | Build and packaging of the JAR | Apache 2.0 |
| gdx-liftoff | – | Generated the starting project (not shipped) | Apache 2.0 |
| Construo Gradle plugin | 2.2.2 | Optional native packages (not used for the JAR) | to check |
| libGDX default UI skin and fonts (`assets/ui`) | – | Menus and on-screen text | Apache 2.0 (to confirm) |
| Item icons (`assets/textures/items`) | – | Bag of Oats, Bread, Energy Drink | Made by the team |

All assets are listed with their source in [`assets/CREDITS.md`](../../assets/CREDITS.md).
No asset needs a credit inside the game so far.

## 2. Features not (fully) implemented

Requirement IDs are from Req1. A1 = needed for Assessment 1.

| Requirement | A1 | State | Note |
|-------------|----|-------|------|
| FR_START_INVENTORY, FR_SINGLE_TARGET, FR_MULTI_TARGET | Yes | Item logic done and unit-tested (33 tests) | Works once the geese implement `Feedable` and `GooseLocator`; to be updated after integration |
| FR_COUNTERS (items used) | Yes | Counter done in `Inventory.getItemsUsed()` | Needs the HUD to show it |
| FR_EVENT_EFFECT (helping event) | Yes | Item side done (`addUses`, `EnergyDrinkItem`) | Needs the event package to call it |
| FR_PICKUP_ITEMS (UR_MAP_ITEMS) | No (A2) | Not implemented | New `Item` subclasses can be added without changing `Inventory` |
| FR_SCORE (UR_SCORE) | No (A2) | Not implemented | `getItemsUsed()` can feed a score later |
| *(other packages: add your rows)* | | | |
