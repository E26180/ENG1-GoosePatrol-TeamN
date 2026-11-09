package uk.ac.york.eng1.goosepatrol.game;

/**
 * The one message shown at the top of the screen, and how long it stays there.
 *
 * <p>Ordinary messages replace each other. An important message (a hidden event) cannot be
 * replaced for its first {@link #HOLD_SECONDS} seconds, so a new player has time to read it;
 * the newest ordinary message that arrives meanwhile is shown straight after (UR_EVENTS,
 * UR_NO_EXPERIENCE). Has no graphics, so it can be tested on its own.</p>
 */
public class Messages {

    /** How long an ordinary message stays on screen, in seconds. */
    public static final float MESSAGE_SECONDS = 4f;

    /** How long an important message stays on screen, in seconds. */
    public static final float IMPORTANT_SECONDS = 6f;

    /** How long an important message cannot be replaced, in seconds. */
    public static final float HOLD_SECONDS = 4f;

    private String current = "";
    private float left = 0f;
    private float hold = 0f;
    private String waiting;

    /**
     * Shows an ordinary message, or keeps it until an important message has been read.
     *
     * @param text the message
     */
    public void show(String text) {
        if (hold > 0f) {
            waiting = text;
            return;
        }
        current = text;
        left = MESSAGE_SECONDS;
    }

    /**
     * Shows an important message straight away; it is kept for at least {@link #HOLD_SECONDS}.
     *
     * @param text the message
     */
    public void showImportant(String text) {
        current = text;
        left = IMPORTANT_SECONDS;
        hold = HOLD_SECONDS;
        waiting = null;
    }

    /**
     * Counts the time down; call only while the game is being played, so a pause keeps the message.
     *
     * @param delta seconds since the last update
     */
    public void update(float delta) {
        left = Math.max(0f, left - delta);
        hold = Math.max(0f, hold - delta);
        if (hold == 0f && waiting != null) {
            current = waiting;
            left = MESSAGE_SECONDS;
            waiting = null;
        }
    }

    /**
     * Tells whether a message is on screen.
     *
     * @return true while there is a message to show
     */
    public boolean isShowing() {
        return left > 0f;
    }

    /**
     * Gets the message on screen.
     *
     * @return the message, or an empty string if there is none
     */
    public String getText() {
        return isShowing() ? current : "";
    }
}
