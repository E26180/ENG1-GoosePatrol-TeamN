package uk.ac.york.eng1.goosepatrol.game;

/**
 * What the game needs to know about the geese to decide the result and fill the HUD.
 * The goose manager implements this, so the game rules do not depend on how geese
 * are spawned or stored.
 */
public interface WaveStatus {

    /**
     * Updates the geese. Called once per frame while the game is running.
     *
     * @param delta   seconds since the last frame
     * @param playerX world x of the player's centre, so geese can walk towards it
     * @param playerY world y of the player's centre
     */
    void update(float delta, float playerX, float playerY);

    /**
     * Returns how many geese are still hungry: those on the map plus those of the
     * current wave that have not arrived yet.
     *
     * @return geese left to feed
     */
    int geeseLeft();

    /**
     * Returns how many waves are still to come after the current one.
     *
     * @return waves still to come
     */
    int wavesLeft();

    /**
     * Returns the number of the current wave.
     *
     * @return 1 for the first wave, 0 before the first wave starts
     */
    int currentWave();

    /**
     * Returns how many waves there are in the whole game.
     *
     * @return the number of waves
     */
    int totalWaves();

    /**
     * Returns the time until the next wave starts (QR_PROGRESSION).
     *
     * @return seconds to the next wave, or a negative number if no wave is left to come
     */
    float secondsToNextWave();

    /**
     * Tells whether every wave has been satisfied (FR_WAVE_COMPLETE).
     *
     * @return true when the player has won
     */
    boolean allWavesCleared();
}
