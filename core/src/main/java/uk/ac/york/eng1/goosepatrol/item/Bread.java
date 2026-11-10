package uk.ac.york.eng1.goosepatrol.item;

import java.util.List;

/**
 * Feeds hungry targets around a clicked position.
 */
public class Bread extends Item {

    private final float radius;

    /**
     * Creates bread.
     *
     * @param uses starting number of uses
     * @param radius feeding radius in world units
     * @throws IllegalArgumentException if uses is negative or radius is
     *         negative or non-finite
     */
    public Bread(int uses, float radius) {
        super("Bread", uses);

        if (!Float.isFinite(radius) || radius < 0) {
            throw new IllegalArgumentException("Invalid feeding radius");
        }

        this.radius = radius;
    }

    /**
     * Feeds all hungry targets within the feeding radius.
     *
     * @param x world x position
     * @param y world y position
     * @param geese current feeding targets
     * @return true if at least one target was fed
     */
    @Override
    protected boolean apply(
        float x, float y, List<? extends Feedable> geese
    ) {
        boolean fed = false;
        double limit = (double) this.radius * this.radius;

        for (Feedable goose : geese) {
            if (goose.isHungry()) {
                double dx = (double) goose.getX() - x;
                double dy = (double) goose.getY() - y;
                double dist = dx * dx + dy * dy;

                if (dist <= limit) {
                    goose.feed();
                    fed = true;
                }
            }
        }

        // The parent class spends one use for the whole group.
        return fed;
    }
}
