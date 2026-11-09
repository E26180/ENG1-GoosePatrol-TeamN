package uk.ac.york.eng1.goosepatrol.item;

/**
 * Base class for all items the player can use to deal with the geese.
 * Each item has a limited number of uses and a cooldown between uses.
 */
public abstract class Item {

    /** Display name of the item. */
    private final String name;

    /** How many times the item can be used in total. */
    private int maxUses;

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
     * Uses the item: applies the effect, then spends one use and starts the cooldown.
     * Does nothing if the item is not ready or if the effect found nothing to act on
     * (for example a click that missed every goose).
     *
     * @param x where the player aimed (world x), ignored by items that need no target
     * @param y where the player aimed (world y), ignored by items that need no target
     * @return true if the item was used, false if it was not ready
     */
    public boolean use(float x, float y) {
        if (!canUse()) {
            return false;
        }
        if (!applyEffect(x, y)) {
            return false;
        }
        usesLeft--;
        cooldownLeft = cooldownSeconds;
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
     * @return true if the effect happened, false if there was nothing to act on
     */
    protected abstract boolean applyEffect(float x, float y);

    /**
     * Returns the display name of the item.
     *
     * @return the item name
     */
    public String getName() {
        return name;
    }

    /**
     * Adds uses, for example when a hidden event gives the player more food.
     *
     * @param extra how many uses to add (ignored if zero or less)
     */
    public void addUses(int extra) {
        if (extra > 0) {
            usesLeft += extra;
            maxUses = Math.max(maxUses, usesLeft);
        }
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
     * Returns where the item's icon is in the assets folder, so the inventory bar can draw
     * any item without knowing its class (QR_EXTENSIBLE).
     *
     * @return the icon path, or null if the item has no icon
     */
    public String getIconPath() {
        return null;
    }

    /**
     * Returns the radius of the ring drawn where the item was used, to show what it reached.
     *
     * @return the radius in world units, or 0 for no ring
     */
    public float getSplashRadius() {
        return 0f;
    }

    /**
     * Tells whether the item is food, so the screen plays the eating sound.
     *
     * @return true for food items
     */
    public boolean isFood() {
        return false;
    }

    /**
     * Returns a short message to show when the item is used, if the effect is not visible on its own.
     *
     * @return the message, or null for none
     */
    public String getUseMessage() {
        return null;
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
