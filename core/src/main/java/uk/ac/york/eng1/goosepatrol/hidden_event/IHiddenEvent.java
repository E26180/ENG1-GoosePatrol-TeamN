package uk.ac.york.eng1.goosepatrol.hidden_event;

/**
 * Baseline interface for a hidden event (FR_EVENTS).
 * Includes the get methods for configuration and checks for position and distance.
 * Includes triggers for both the event and the corresponding flag.
 * The game places each event with an {@code EventSpot}, which also runs the event's effect.
 */
public interface IHiddenEvent {

    //Range and Position Checks

    /**
     * Works out how far the player is from the event.
     *
     * @param playerPosition {x, y} of the player's centre, in world units
     * @return the straight-line distance
     */
    float distanceFromPlayer(float[] playerPosition);

    /**
     * Tells whether the player is close enough to trigger the event.
     *
     * @param playerPosition {x, y} of the player's centre
     * @return true if inside the activation radius
     */
    boolean isPlayerInActiveRange(float[] playerPosition);

    /**
     * Tells whether the player is close enough to see the hint flag (FR_EVENT_HINT, Assessment 2).
     *
     * @param playerPosition {x, y} of the player's centre
     * @return true if inside the flag radius
     */
    boolean isPlayerInFlagRange(float[] playerPosition);

    //Actions and Execution

    /** Shows a hint that an event is nearby, without saying which or exactly where. */
    void showFlag();

    /** Marks the event as triggered and starts any timed part of it. */
    void trigger();

    /**
     * Tells whether a timed effect of the event is still running.
     *
     * @return true while the effect lasts
     */
    boolean checkActive();

    //Get methods for config data

    /**
     * Returns how close the player must be to trigger the event.
     *
     * @return the activation radius, in world units
     */
    float getActivationRadius();

    /**
     * Returns where the event is.
     *
     * @return {x, y} of the event, in world units
     */
    float[] getPos();

    /**
     * Returns how close the player must be to see the hint flag.
     *
     * @return the flag radius, in world units
     */
    float getFlagRadius();

    /**
     * Tells whether the event has been triggered.
     *
     * @return true after {@link #trigger()}
     */
    boolean getIsActivated();
}
