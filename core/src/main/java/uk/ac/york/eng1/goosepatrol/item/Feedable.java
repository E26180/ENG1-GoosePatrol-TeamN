package uk.ac.york.eng1.goosepatrol.item;

/**
 * Defines the information needed to feed a target.
 */
public interface Feedable {

    /**
     * Returns the target's horizontal position.
     *
     * @return world x position
     */
    float getX();

    /**
     * Returns the target's vertical position.
     *
     * @return world y position
     */
    float getY();

    /**
     * Checks whether the target needs food.
     *
     * @return true if the target is hungry
     */
    boolean isHungry();

    /**
     * Satisfies the target's hunger.
     */
    void feed();

    /**
     * How full the goose is, used by the HUD feed bar.
     * @return a value from 0 (empty) to 1 (full)
     */
    float getFeedLevel();
}
