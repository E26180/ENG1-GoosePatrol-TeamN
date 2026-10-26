package uk.ac.york.eng1.goosepatrol;

/**
 * Bag of Oats: a single-target item. Using it on one goose fills that goose's
 * hunger bar by {@link #HUNGER_FILLED}. It has 10 uses and no cooldown.
 */
public class OatsItem extends Item {

    /** Share of the hunger bar that one use fills (0.5 = 50%). */
    public static final float HUNGER_FILLED = 0.5f;

    /** Number of uses the player starts with. */
    public static final int START_USES = 10;

    /** Creates a full bag of oats. */
    public OatsItem() {
        super("Bag of Oats", START_USES, 0f);
    }

    @Override
    protected void applyEffect(float x, float y) {
        // TODO: feed the goose at (x, y) by HUNGER_FILLED once the Goose class
        // (Leo) offers a feed(amount) method.
    }
}
