package uk.ac.york.eng1.goosepatrol.game;

/**
 * The rules of one game: start, pause, the timer and the result.
 * It has no libGDX code, so it can be unit tested. {@code GameScreen} reads
 * the keys and calls these methods.
 */
public class GameSession {

    private final GameTimer timer;
    private final WaveStatus waves;
    private GameState state = GameState.READY;

    /** How many hidden events the player has triggered (FR_COUNTERS). */
    private int eventsTriggered = 0;

    /**
     * Creates a game that waits on the start screen.
     *
     * @param timer the countdown, stopped
     * @param waves the geese, used to detect a win
     */
    public GameSession(GameTimer timer, WaveStatus waves) {
        this.timer = timer;
        this.waves = waves;
    }

    /**
     * Starts the game from the start screen, or resumes it after a pause.
     * Does nothing once the game is over.
     */
    public void play() {
        if (state == GameState.READY || state == GameState.PAUSED) {
            state = GameState.PLAYING;
            timer.start();
        }
    }

    /** Pauses a running game, or resumes a paused one (the pause key). */
    public void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
            timer.stop();
        } else if (state == GameState.PAUSED) {
            play();
        }
    }

    /**
     * Moves the game on by one frame and decides the result.
     * The player wins as soon as every wave is cleared, even early (UR_RESULT),
     * and loses when the time runs out first.
     *
     * @param delta seconds since the last frame
     */
    public void update(float delta) {
        if (state != GameState.PLAYING) {
            return;
        }
        timer.update(delta);
        if (waves.allWavesCleared()) {
            end(GameState.WON);
        } else if (timer.isFinished()) {
            end(GameState.LOST);
        }
    }

    private void end(GameState result) {
        state = result;
        timer.stop();
    }

    /** Counts one more hidden event for the HUD. */
    public void eventTriggered() {
        eventsTriggered++;
    }

    /**
     * Tells whether the world should move this frame.
     *
     * @return true only while playing
     */
    public boolean isPlaying() {
        return state == GameState.PLAYING;
    }

    /**
     * Returns the current stage of the game.
     *
     * @return the state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Returns the countdown, for the HUD.
     *
     * @return the timer
     */
    public GameTimer getTimer() {
        return timer;
    }

    /**
     * Returns how many hidden events have been triggered.
     *
     * @return the event counter
     */
    public int getEventsTriggered() {
        return eventsTriggered;
    }
}
