package uk.ac.york.eng1.goosepatrol;

/**
 * Base class for all items the player can use to deal with the geese.
 * Each item has a limited number of uses and a cooldown between uses.
 */
public abstract class Item {

    /** Display name of the item. */
    private final String name;

    /** How many times the item can be used in total. */
    private final int maxUses;

    /** How many times the item can still be used. */
    private int usesLeft;

    /** Seconds the player has to wait between two uses. */
    private final float cooldownSeconds;

    /** Seconds left until the item can be used again (0 means ready). */
    private float cooldownLeft;

    /**
     * Creates an item.
     *
     * @param name            display name of the item
     * @param uses            how many times it can be used
     * @param cooldownSeconds seconds to wait between two uses
     */
    public Item(String name, int uses, float cooldownSeconds) {
        this.name = name;
        this.maxUses = uses;
        this.usesLeft = uses;
        this.cooldownSeconds = cooldownSeconds;
        this.cooldownLeft = 0f;
    }

    /**
     * Tells whether the item can be used right now.
     *
     * @return true if there are uses left and the cooldown is over
     */
    public boolean canUse() {
        return usesLeft > 0 && cooldownLeft <= 0f;
    }

    /**
     * Uses the item: spends one use, starts the cooldown and applies the effect.
     * Does nothing if the item cannot be used.
     *
     * @param x where the player aimed (world x), ignored by items that need no target
     * @param y where the player aimed (world y), ignored by items that need no target
     * @return true if the item was used, false if it was not ready
     */
    public boolean use(float x, float y) {
        if (!canUse()) {
            return false;
        }
        usesLeft--;
        cooldownLeft = cooldownSeconds;
        applyEffect(x, y);
        return true;
    }

    /**
     * Counts the cooldown down. Called once per frame.
     *
     * @param delta seconds since the last frame
     */
    public void update(float delta) {
        if (cooldownLeft > 0f) {
            cooldownLeft = Math.max(0f, cooldownLeft - delta);
        }
    }

    /**
     * What the item does. Each kind of item defines its own effect.
     *
     * @param x where the player aimed (world x)
     * @param y where the player aimed (world y)
     */
    protected abstract void applyEffect(float x, float y);

    /**
     * Returns the display name of the item.
     *
     * @return the item name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns how many uses the item had at the start.
     *
     * @return total uses
     */
    public int getMaxUses() {
        return maxUses;
    }

    /**
     * Returns how many uses are left.
     *
     * @return remaining uses
     */
    public int getUsesLeft() {
        return usesLeft;
    }

    /**
     * Returns the seconds left until the item is ready again.
     *
     * @return remaining cooldown in seconds
     */
    public float getCooldownLeft() {
        return cooldownLeft;
    }
}
