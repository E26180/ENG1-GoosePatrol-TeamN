package uk.ac.york.eng1.goosepatrol.item;

/**
 * Bread: an area item. Clicking a point throws bread there and fills the hunger
 * bar of every goose within {@link #RADIUS_PIXELS} of that point by
 * {@link #HUNGER_FILLED}, up to the {@link #MAX_GEESE} nearest geese (QR_AVOID_EXTREME).
 * A hidden event can give more bread with
 * {@link Item#addUses(int)}.
 */
public class BreadItem extends Item {

    /** Share of the hunger bar the bread fills for each goose in range (0.5 = 50%). */
    public static final float HUNGER_FILLED = 0.5f;

    /** Radius around the click in which geese are fed, in pixels: about three geese wide. */
    public static final float RADIUS_PIXELS = 60f;

    /** Most geese one throw can feed, so no item can satisfy too many geese at once (QR_AVOID_EXTREME). */
    public static final int MAX_GEESE = 10;

    /** Seconds to wait between two uses. */
    public static final float COOLDOWN_SECONDS = 3f;

    /** Number of uses the player starts with. */
    public static final int START_USES = 6;

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
    public String getIconPath() {
        return "textures/items/bread.png";
    }

    @Override
    public float getSplashRadius() {
        return RADIUS_PIXELS;
    }

    @Override
    public boolean isFood() {
        return true;
    }

    /** Feeds the geese in range, nearest first, but no more than {@link #MAX_GEESE}. */
    @Override
    protected boolean applyEffect(float x, float y) {
        int fed = 0;
        for (Feedable goose : geese.geeseNear(x, y, RADIUS_PIXELS)) {
            if (fed == MAX_GEESE) {
                break;
            }
            goose.feed(HUNGER_FILLED);
            fed++;
        }
        return true;
    }
}
