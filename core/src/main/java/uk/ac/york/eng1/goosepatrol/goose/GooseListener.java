package uk.ac.york.eng1.goosepatrol.goose;

/**
 * Hears what happens to the geese, so the screen can play sounds and show messages
 * without the goose package knowing about either. Every method does nothing by default.
 */
public interface GooseListener {

    /**
     * A new wave has started.
     *
     * @param number the wave number, starting at 1
     * @param total  how many waves there are
     */
    default void waveStarted(int number, int total) {
    }

    /**
     * A goose is about to snatch a snack: the start of its warning (QR_ALERT).
     *
     * @param goose the goose
     */
    default void gooseAlert(Goose goose) {
    }

    /**
     * A goose's snatch reached the player, who is startled for a moment.
     *
     * @param goose the goose
     */
    default void playerSnatched(Goose goose) {
    }

    /**
     * A goose is full and flies away.
     *
     * @param goose the goose
     */
    default void gooseFull(Goose goose) {
    }

    /**
     * Every goose of a wave has been fed.
     *
     * @param number the wave number, starting at 1
     */
    default void waveCleared(int number) {
    }
}
