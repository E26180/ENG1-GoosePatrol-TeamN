package uk.ac.york.eng1.goosepatrol.game;

/** The stages one game goes through. */
public enum GameState {

    /** Before the first start: the game waits on the start screen (FR_START_PAUSED). */
    READY,

    /** The game is running and the timer counts down. */
    PLAYING,

    /** The player paused the game; nothing moves and the timer is stopped (FR_PAUSE_KEY). */
    PAUSED,

    /** Every wave of geese was satisfied in time (FR_JUDGEMENT). */
    WON,

    /** The time ran out before all geese were satisfied (FR_JUDGEMENT). */
    LOST;

    /**
     * Tells whether the game is over.
     *
     * @return true for {@link #WON} and {@link #LOST}
     */
    public boolean isOver() {
        return this == WON || this == LOST;
    }
}
