package uk.ac.york.eng1.goosepatrol;

/**
 * Energy Drink: a speed boost item. Using it makes the player move
 * {@link #SPEED_MULTIPLIER} times as fast for {@link #BOOST_SECONDS} seconds.
 * The cooldown is longer than the boost, so boosts cannot be stacked.
 * The player normally gets these from a hidden event, not at the start.
 */
public class EnergyDrinkItem extends Item {

    /** How much faster the player moves while boosted (1.5 = 50% faster). */
    public static final float SPEED_MULTIPLIER = 1.5f;

    /** How long one boost lasts, in seconds. */
    public static final float BOOST_SECONDS = 5f;

    /** Seconds to wait between two uses. */
    public static final float COOLDOWN_SECONDS = 10f;

    /** Number of drinks the hidden event gives. */
    public static final int DEFAULT_USES = 3;

    /** The thing that gets faster, normally the player. */
    private final SpeedBoostTarget target;

    /**
     * Creates energy drinks.
     *
     * @param uses   how many drinks there are
     * @param target who gets the speed boost
     */
    public EnergyDrinkItem(int uses, SpeedBoostTarget target) {
        super("Energy Drink", uses, COOLDOWN_SECONDS);
        this.target = target;
    }

    /**
     * Creates the default 3 energy drinks.
     *
     * @param target who gets the speed boost
     */
    public EnergyDrinkItem(SpeedBoostTarget target) {
        this(DEFAULT_USES, target);
    }

    @Override
    protected void applyEffect() {
        target.applySpeedBoost(SPEED_MULTIPLIER, BOOST_SECONDS);
    }
}
