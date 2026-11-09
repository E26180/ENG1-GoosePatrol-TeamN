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
     * Returns how many geese of the current wave are still hungry.
     *
     * @return hungry geese on the map
     */
    int geeseLeft();

    /**
     * Returns how many waves are still to come after the current one.
     *
     * @return waves still to come
     */
    int wavesLeft();

    /**
     * Tells whether every wave has been satisfied (FR_WAVE_COMPLETE).
     *
     * @return true when the player has won
     */
    boolean allWavesCleared();
}
