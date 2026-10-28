package uk.ac.york.eng1.goosepatrol.item;

/**
 * Bread: an area item. Clicking a point throws bread there and fills the hunger
 * bar of every goose within {@link #RADIUS_PIXELS} of that point by
 * {@link #HUNGER_FILLED}. A hidden event can give more bread with
 * {@link Item#addUses(int)}.
 */
public class BreadItem extends Item {

    /** Share of the hunger bar the bread fills for each goose in range (0.5 = 50%). */
    public static final float HUNGER_FILLED = 0.5f;

    /** Radius around the click in which geese are fed, in pixels. Easy to tune. */
    public static final float RADIUS_PIXELS = 30f;

    /** Seconds to wait between two uses. */
    public static final float COOLDOWN_SECONDS = 3f;

    /** Number of uses the player starts with. */
    public static final int START_USES = 5;

    /** Finds the geese near the click. */
    private final GooseLocator geese;

    /**
     * Creates the bread item.
     *
     * @param geese used to find the geese near the click position
     */
    public BreadItem(GooseLocator geese) {
        super("Bread", START_USES, COOLDOWN_SECONDS);
        this.geese = geese;
    }

    @Override
    protected boolean applyEffect(float x, float y) {
        for (Feedable goose : geese.geeseNear(x, y, RADIUS_PIXELS)) {
            goose.feed(HUNGER_FILLED);
        }
        return true;
    }
}
