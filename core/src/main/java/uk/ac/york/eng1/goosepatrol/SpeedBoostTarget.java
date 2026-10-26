package uk.ac.york.eng1.goosepatrol;

/**
 * Something whose movement speed can be boosted for a short time,
 * for example the player's avatar. Keeping this as a small interface means
 * the items do not depend on the concrete player class.
 */
public interface SpeedBoostTarget {

    /**
     * Speeds the target up for a limited time.
     *
     * @param multiplier how much faster the target moves (1.5 means 50% faster)
     * @param seconds    how long the boost lasts, in seconds
     */
    void applySpeedBoost(float multiplier, float seconds);
}
