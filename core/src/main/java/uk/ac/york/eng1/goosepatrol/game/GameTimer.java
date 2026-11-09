package uk.ac.york.eng1.goosepatrol.game;

/**
 * The 5-minute countdown of one game (FR_TIMER).
 * It only counts down while it is running, so pausing the game stops it.
 */
public class GameTimer {

    /** Length of one game, in seconds. */
    public static final float GAME_SECONDS = 5 * 60f;

    /** Seconds left in the game. */
    private float secondsLeft;

    /** Whether the timer is counting down. */
    private boolean running = false;

    /** Creates a stopped timer set to {@link #GAME_SECONDS}. */
    public GameTimer() {
        this(GAME_SECONDS);
    }

    /**
     * Creates a stopped timer with any length, for example a short one in tests.
     *
     * @param seconds length of the countdown, in seconds
     */
    public GameTimer(float seconds) {
        this.secondsLeft = Math.max(0f, seconds);
    }

    /** Starts or resumes the countdown. */
    public void start() {
        running = true;
    }

    /** Stops the countdown, for example while the game is paused. */
    public void stop() {
        running = false;
    }

    /**
     * Counts down if the timer is running. Called once per frame.
     *
     * @param delta seconds since the last frame
     */
    public void update(float delta) {
        if (running && delta > 0f) {
            secondsLeft = Math.max(0f, secondsLeft - delta);
        }
    }

    /**
     * Tells whether the time is up.
     *
     * @return true when no time is left
     */
    public boolean isFinished() {
        return secondsLeft <= 0f;
    }

    /**
     * Tells whether the timer is counting down.
     *
     * @return true while running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Returns the seconds left.
     *
     * @return seconds left, never below 0
     */
    public float getSecondsLeft() {
        return secondsLeft;
    }

    /**
     * Returns the time left as minutes and seconds, for example "4:05".
     * Seconds are rounded up, so the display shows "0:00" only when the time is up.
     *
     * @return the time left as text
     */
    public String format() {
        int total = (int) Math.ceil(secondsLeft);
        return (total / 60) + ":" + String.format("%02d", total % 60);
    }
}
