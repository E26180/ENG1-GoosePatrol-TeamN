package uk.ac.york.eng1.goosepatrol.item;

import java.util.List;

/**
 * Feeds one hungry target near a clicked position.
 */
public class Oats extends Item {

    private final float reach;

    /**
     * Creates oats.
     *
     * @param uses starting number of uses
     * @param reach maximum feeding distance in world units
     * @throws IllegalArgumentException if uses is negative or reach is
     *         negative or non-finite
     */
    public Oats(int uses, float reach) {
        super("Oats", uses);

        if (!Float.isFinite(reach) || reach < 0) {
            throw new IllegalArgumentException("Invalid feeding reach");
        }

        this.reach = reach;
    }

    /**
     * Feeds the nearest hungry target within reach.
     *
     * @param x world x position
     * @param y world y position
     * @param geese current feeding targets
     * @return true if one target was fed
     */
    @Override
    protected boolean apply(
        float x, float y, List<? extends Feedable> geese
    ) {
        Feedable target = null;
        double best = (double) this.reach * this.reach;

        for (Feedable goose : geese) {
            if (goose.isHungry()) {
                double dx = (double) goose.getX() - x;
                double dy = (double) goose.getY() - y;
                double dist = dx * dx + dy * dy;

                // Keep the nearest hungry target inside the feeding range.
                if (dist <= best) {
                    best = dist;
                    target = goose;
                }
            }
        }

        if (target == null) {
            return false;
        }

        target.feed();
        return true;
    }
}
