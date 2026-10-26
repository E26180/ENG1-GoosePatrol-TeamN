package uk.ac.york.eng1.goosepatrol;

/**
 * Bread Picnic: an area item. Using it spreads a picnic blanket at a point.
 * For {@link #EFFECT_SECONDS} seconds, geese within {@link #RADIUS_PIXELS}
 * pixels go to the blanket and eat. It can be used again after a 20 second cooldown.
 */
public class BreadPicnicItem extends Item {

    /** How long the geese are drawn to the blanket, in seconds. */
    public static final float EFFECT_SECONDS = 5f;

    /** Radius around the blanket in which geese are drawn to it, in pixels. */
    public static final float RADIUS_PIXELS = 150f;

    /** Seconds to wait between two uses. */
    public static final float COOLDOWN_SECONDS = 20f;

    /** Number of uses the player starts with. */
    public static final int START_USES = 5;

    /** Creates a bread picnic item. */
    public BreadPicnicItem() {
        super("Bread Picnic", START_USES, COOLDOWN_SECONDS);
    }

    @Override
    protected void applyEffect(float x, float y) {
        // TODO: send geese within RADIUS_PIXELS of (x, y) to the blanket for
        // EFFECT_SECONDS once the Goose class (Leo) can be sent to a point.
    }
}
