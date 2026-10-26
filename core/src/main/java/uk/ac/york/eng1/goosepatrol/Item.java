package uk.ac.york.eng1.goosepatrol;

/**
 * Base class for all items the player can use to deal with the geese.
 * Each item has a limited number of uses and a cooldown between uses.
 */
public abstract class Item {

    /** Display name of the item. */
    private final String name;

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
     */
    public void use() {
        if (!canUse()) {
            return;
        }
        usesLeft--;
        cooldownLeft = cooldownSeconds;
        applyEffect();
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
     */
    protected abstract void applyEffect();

    /**
     * Returns the display name of the item.
     *
     * @return the item name
     */
    public String getName() {
        return name;
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
