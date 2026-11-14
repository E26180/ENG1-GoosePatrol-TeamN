package uk.ac.york.eng1.goosepatrol.core;

/** States a round can be in. A round starts PAUSED and ends WON or LOST. */
public enum GameState {
    PAUSED,
    RUNNING,
    WON,
    LOST
}
