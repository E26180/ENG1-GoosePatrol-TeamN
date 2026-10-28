# Client Meeting Notes · Wednesday 14 October 2026

**Project:** Goose Patrol (ENG1 Team 2)
**Source:** Voice memo of the meeting (21 min), auto-transcribed and summarised. Times like `[03:00]` point to the place in the recording.
**Note:** The automatic transcript has some errors (e.g. "bulletail" = *bullet hell*, "keys" = *geese*, "long boy" = *Long Boi*). Check the recording if something below looks wrong.

---

## 1. What kind of game it is

- **A bullet-hell / wave-survival game** (like *Vampire Survivors*), "reskinned" to be **non-violent**. All mechanics stay the same as in the genre. `[01:00]`
- Geese come in **waves** and the player has to **disperse** them. Geese are **not killed**: each goose has a hidden "hunger" bar, and when it is full it **flies / quacks away**. `[02:00]` `[12:02]`
- The **player's health** is reframed too, e.g. tiredness or annoyance. When it runs out, the player gives up and goes back to their room ("I'm too tired to go to the gym"). `[12:02]`
- **No violence at all.** This is the main constraint, because the game goes on the website of a "University for the public good". Weapons that look violent (e.g. a "bread sniper") should be avoided unless the tone is clearly humorous. `[20:03]`
- The **environment is purely visual**: no navigating to a goal, no puzzles, no doors. The geese are the only adversary. `[01:00]` `[11:02]`

## 2. Winning, losing and the timer

- **Win:** disperse all the waves of geese. Good players can **finish early**. `[00:00]` `[03:00]`
- **Lose:** geese are still left when the **5-minute timer** runs out, or the player's tiredness/annoyance bar runs out. `[00:00]` `[12:02]`
- Finishing earlier should give **more points**, once there is a score system. `[03:00]`

## 3. Controls

- Follow **industry standards** for the genre: **keyboard to move**, **mouse to aim / click items**. Keyboard-only is fine if no aiming is needed. `[02:00]`

## 4. Audience, length and difficulty

- **Target audience:** A-level students (~16) and first-year students. It is **marketing material** for the university website. `[05:01]` `[06:01]`
- **Short and engaging** (they compare it to scrolling TikTok), which is why it lasts 5 minutes. `[03:00]`
- **Easy by default.** Anyone, including people who have never played a game, should be able to beat it after a few tries. Avoid frustrating mechanics. `[07:01]`
- A **harder difficulty** can come later, after the first tested version. `[07:01]`

## 5. Items

- At least **2 starting items**, used like weapons in the genre: **single-target** (feed one goose) or **area / distraction** (e.g. bread thrown to an area). `[12:02]` `[05:01]`
- Items should fit the story the team chooses (bread fits a realistic setting). `[05:01]`
- **Inventory must be extensible**, so more items can be added later. `[11:02]`
- **No in-game currency or shop** in this version. Nice idea for later, but it adds complexity. `[10:02]`

## 6. Hidden events

- At least **2 hidden events**, built as an **extensible collection** so more can be added later. `[13:02]`
- "Hidden" does **not** mean invisible: the player sees something (e.g. a duck looking at a bucket) but doesn't know what it will do. `[04:00]`
- An event changes the game state, either for the **player** or for the **geese**. It can help or hurt, but it should be **interesting**. `[13:02]`
- **Easy difficulty: no instant loss** from an event. Bad effects must be **gradual**. A hard mode could be harsher, with hints. `[13:02]`
- Triggering events should give **points**. `[03:00]`

## 7. Geese

- Different waves can have **different geese**: faster, tougher, hungrier. A **boss goose** per wave is a good idea. The team decides. `[14:02]`

## 8. Story, theme and map

- **The team has full creative control** of the story (realistic, cartoon, fantasy…), as long as it is **consistent and immersive** from start to end, including the music. `[04:00]` `[17:02]`
- It must have **loose but obvious links to the University of York**: landmarks, departments, **Long Boi**, etc. It should not feel like an advert. `[04:00]` `[05:01]`
- Tone: **satire and humour** are welcome. `[06:01]` `[20:03]`
- **Map:** anywhere on campus. **Campus East** makes sense (Computer Science), but West is fine. It doesn't need to be to scale or realistic. `[11:02]`

## 9. Accessibility (built in)

- Must work for people who are **colour-blind**, **deaf / hard of hearing** or who **find reading difficult**. `[08:01]` `[15:02]`
- Use **multimodal cues**: anything important (enemies, events, items) must **never be shown by colour alone**. Example: a goose about to bite flashes red **and** quacks **and** shows a "quack!" speech bubble. `[08:01]` `[09:02]`
- Background and scenery colours don't matter, because they aren't interactive. `[08:01]`
- Accessibility should be **built into the design**, not added as separate versions. `[08:01]`

## 10. Platform and technical

- **Single-player PC game** that runs on **Windows, macOS and Linux**. `[15:02]`
- Delivered as **one downloadable executable**. **No online or mobile** version. `[16:02]`
- **No saving, no leaderboard, no user data stored.** This keeps it simple and avoids data-protection and security issues. `[09:02]`
- **Graphics:** PNG or pixel art are both fine. Lecturer's advice: **don't draw your own pixel art**, it's worth 0 marks. Use existing tools or assets, and record their licences. `[18:03]` `[19:03]`
- **Music / sound effects:** nice to have and should match the atmosphere, but **low priority ("may")** for this version. `[16:02]` `[17:02]`
- **Open source:** the customer says **not at this stage**, because it's marketing material and the legal side needs checking. The university notes the repository will become public after submission anyway. `[19:03]`

## 11. Project process

- **Alpha version** (mechanics and systems only, map can be ugly) **before the halfway point** of the project. `[14:02]`
- After that: **rapid iteration**, with user testing of each prototype. `[14:02]`
- Build the systems to be **extensible** (inventory, events, scoring, difficulty), so features can be added in future years. `[03:00]` `[11:02]`
- Patches after release would be nice, as separate paid work. `[17:02]`
- The meeting acts as the "contract" with the customer. `[18:03]`

---

## Requirement ideas for Req1 (draft)

Priorities: **Shall** = must have, **Should** = important, **May** = nice to have.

| ID | Requirement idea | Priority | Source |
|----|------------------|----------|--------|
| UR_NONVIOLENT | The game shows no violence: geese are fed / dispersed, never hurt | Shall | `[01:00]` `[20:03]` |
| UR_WAVES | The player disperses waves of geese to win | Shall | `[00:00]` |
| UR_TIMER | The game lasts at most 5 minutes; geese left when time runs out = loss | Shall | `[00:00]` |
| UR_EARLY_WIN | The player can win before the timer ends by clearing all waves | Shall | `[03:00]` |
| UR_EASY | A first-time player can beat the default difficulty after a few tries | Shall | `[07:01]` |
| UR_YORK | The game contains recognisable University of York references | Shall | `[04:00]` |
| UR_ACCESS | Colour-blind, deaf and low-literacy players can play | Shall | `[08:01]` |
| FR_CONTROLS | Move with the keyboard; aim / select with the mouse | Shall | `[02:00]` |
| FR_ITEMS | The player starts with 2 items (e.g. single-target and area effect) | Shall | `[12:02]` |
| FR_EVENTS | At least 2 hidden events that affect the player or the geese | Shall | `[13:02]` |
| FR_NO_INSTANT_LOSS | On easy difficulty, no event can make the player lose instantly | Shall | `[13:02]` |
| FR_PLAYER_BAR | The player has a tiredness / annoyance bar instead of health | Shall | `[12:02]` |
| FR_GOOSE_HUNGER | Each goose has a hunger bar and leaves when it is full | Shall | `[12:02]` |
| FR_MULTIMODAL | Important warnings use colour + sound + text / symbol | Shall | `[09:02]` |
| FR_SCORE | Score rewards clearing quickly and triggering events | Should | `[03:00]` |
| FR_GOOSE_TYPES | Waves can contain different goose types and a boss | Should | `[14:02]` |
| FR_HARD_MODE | A harder difficulty | May | `[07:01]` |
| FR_MUSIC | Music and sound effects that fit the theme | May | `[17:02]` |
| NFR_PLATFORM | Runs on Windows, macOS and Linux as one executable | Shall | `[15:02]` |
| NFR_NO_DATA | No saving, leaderboard or personal data | Shall | `[09:02]` |
| NFR_EXTENSIBLE | Items, events, score and difficulty can be extended easily | Shall | `[11:02]` |
| NFR_LICENCES | All third-party assets have licences that allow use | Shall | `[19:03]` |

**Out of scope for this version:** in-game currency and shop, saving / replay features, leaderboard, online or mobile versions, open-sourcing the game for players.

**Open questions for the team:**
- Which story / setting do we choose? (realistic, cartoon, fantasy…)
- What are our 2 starting items and 2 hidden events?
- Which part of campus is the map? (East suggested)
