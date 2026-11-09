package uk.ac.york.eng1.goosepatrol.item;

/**
 * Bag of Oats: a single-target item. Clicking a goose fills that goose's
 * hunger bar by {@link #HUNGER_FILLED}. It has {@link #START_USES} uses and no cooldown.
 * A click that misses every goose does not spend a use.
 */
public class OatsItem extends Item {

    /** Share of the hunger bar that one use fills (0.5 = 50%). */
    public static final float HUNGER_FILLED = 0.5f;

    /** Number of uses the player starts with. */
    public static final int START_USES = 15;

    /** Finds the goose the player clicked. */
    private final GooseLocator geese;

    /**
     * Creates a full bag of oats.
     *
     * @param geese used to find the goose at the click position
     */
    public OatsItem(GooseLocator geese) {
        super("Bag of Oats", START_USES, 0f);
        this.geese = geese;
    }

    @Override
    public String getIconPath() {
        return "textures/items/oats.png";
    }

    @Override
    public float getSplashRadius() {
        return 14f;
    }

    @Override
    public boolean isFood() {
        return true;
    }

    @Override
    protected boolean applyEffect(float x, float y) {
        Feedable goose = geese.gooseAt(x, y);
        if (goose == null) {
            return false;
        }
        goose.feed(HUNGER_FILLED);
        return true;
    }
}
