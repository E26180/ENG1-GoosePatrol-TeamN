package uk.ac.york.eng1.goosepatrol;

import java.util.List;

/**
 * Finds geese on the map for the items. The game world (or the goose manager)
 * implements this, so the items do not depend on how geese are stored.
 */
public interface GooseLocator {

    /**
     * Finds the goose at a position.
     *
     * @param x world x of the position
     * @param y world y of the position
     * @return the goose there, or null if there is none
     */
    Feedable gooseAt(float x, float y);

    /**
     * Finds all geese within a distance of a point.
     *
     * @param x      world x of the centre
     * @param y      world y of the centre
     * @param radius distance from the centre, in pixels
     * @return the geese in range (empty if none)
     */
    List<? extends Feedable> geeseNear(float x, float y, float radius);
}
