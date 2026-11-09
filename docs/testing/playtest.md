# Play-test with first-time players

Checks QR_NEWBIE ("all testers win on easy mode on their first or second attempt"),
QR_WINNABLE ("testers beat the game more than 80% of the time"), UR_NO_EXPERIENCE and QR_PLATFORM.
Results go into Impl1 (features table) and Risk1 (R17, R18).
The Assessment 1 game has one difficulty, so "easy mode" in QR_NEWBIE and QR_WINNABLE means the normal game.
Record every game played, including lost ones, and note which build was used (the SHA-256 of the JAR).

## How to run a test (10 minutes per player)

1. Use someone who has **not** played Goose Patrol before. Note whether they play video games often.
2. Give them only this: *"Feed all the geese before the time runs out."* Do not explain the controls; the start screen and the tips should do that.
3. Let them play up to **two** games. Do not help unless they are stuck for more than a minute (write it down if you help).
4. Watch and note: did they read the tips, use Bread as well as Oats, find a hidden event, notice the red honking goose?
5. Afterwards ask the three questions below.
6. Note the computer: **Windows, macOS or Linux**, and whether the JAR started with a double-click or needed `java -jar`.

## Questions for the player

- What was hardest or most confusing?
- Did you always know what to do next? If not, when?
- Was anything hard to see or hear?

## Results

| # | Date | Plays games often? | OS | Game 1 (won/lost, time left) | Game 2 (won/lost, time left) | Found an event? | Helped? | Comments |
|---|------|--------------------|----|------------------------------|------------------------------|-----------------|---------|----------|
| 1 | | | | | | | | |
| 2 | | | | | | | | |
| 3 | | | | | | | | |

**Pass mark:** every player wins in game 1 or game 2 (QR_NEWBIE), and more than 80% of all games played are won (QR_WINNABLE).
Write the rate as games won / games played (for example 5/6 = 83%); it describes these players only.
If the pass mark is missed, change the balance values or the tips and test again with new players.
