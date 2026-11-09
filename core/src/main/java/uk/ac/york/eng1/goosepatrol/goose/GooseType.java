package uk.ac.york.eng1.goosepatrol.goose;

/**
 * The kinds of goose. A new kind (for example a faster goose for a harder difficulty,
 * FR_GOOSE_VAR) is one new constant here.
 */
public enum GooseType {

    /** An ordinary campus goose. */
    NORMAL("Goose", 60f, 1f, 1f),

    /** A big, slow goose that needs twice as much food; one comes in the last wave. */
    HEAD("Head Goose", 45f, 2f, 1.5f);

    private final String displayName;
    private final float speed;
    private final float appetite;
    private final float scale;

    GooseType(String displayName, float speed, float appetite, float scale) {
        this.displayName = displayName;
        this.speed = speed;
        this.appetite = appetite;
        this.scale = scale;
    }

    /**
     * Returns the name shown to the player.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the walking speed.
     *
     * @return speed in world units (pixels) per second
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Returns how many full hunger bars this goose eats: food fills its bar
     * {@code appetite} times more slowly than an ordinary goose's.
     *
     * @return the appetite (1 for an ordinary goose)
     */
    public float getAppetite() {
        return appetite;
    }

    /**
     * Returns how much bigger this goose is drawn and how much bigger its collision box is.
     *
     * @return the size multiplier (1 for an ordinary goose)
     */
    public float getScale() {
        return scale;
    }
}
