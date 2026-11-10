package uk.ac.york.eng1.goosepatrol.item;

import java.util.List;
import java.util.Objects;

/**
 * Stores an item's name and remaining uses.
 */
public abstract class Item {

    private final String name;
    private int uses;

    /**
     * Creates an item.
     *
     * @param name item name
     * @param uses starting number of uses
     * @throws IllegalArgumentException if uses is negative
     */
    protected Item(String name, int uses) {
        if (uses < 0) {
            throw new IllegalArgumentException("Uses cannot be negative");
        }

        this.name = name;
        this.uses = uses;
    }

    /**
     * Returns the item name.
     *
     * @return item name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the remaining uses.
     *
     * @return number of uses left
     */
    public int getUses() {
        return this.uses;
    }

    /**
     * Checks whether the item has any uses left.
     *
     * @return true if a use remains
     */
    public boolean canUse() {
        return this.uses > 0;
    }

    /**
     * Performs an item action and spends one use if it succeeds.
     *
     * @param x world x position
     * @param y world y position
     * @param geese current feeding targets
     * @return true if the action succeeded
     * @throws NullPointerException if geese is null
     */
    public final boolean use(
        float x, float y, List<? extends Feedable> geese
    ) {
        Objects.requireNonNull(geese, "Geese list cannot be null");

        if (!this.canUse()) {
            return false;
        }

        // Reject invalid positions supplied by callers other than mouse input.
        if (!Float.isFinite(x) || !Float.isFinite(y)) {
            return false;
        }

        if (!this.apply(x, y, geese)) {
            return false;
        }

        this.uses--;
        return true;
    }

    /**
     * Performs the behaviour belonging to this item.
     *
     * @param x world x position
     * @param y world y position
     * @param geese current feeding targets
     * @return true if a target was affected
     */
    protected abstract boolean apply(
        float x, float y, List<? extends Feedable> geese
    );
}
