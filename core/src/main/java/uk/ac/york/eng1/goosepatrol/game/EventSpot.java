package uk.ac.york.eng1.goosepatrol.game;

import uk.ac.york.eng1.goosepatrol.hidden_event.IHiddenEvent;

/**
 * Places one hidden event in the game and runs its effect the first time the
 * player walks into it (FR_EVENTS). The event class decides where it is and how
 * close the player must be; the effect is given here, so the event does not need
 * to know about the inventory or the geese.
 */
public class EventSpot {

    private final IHiddenEvent event;
    private final String propPath;
    private final Runnable effect;
    private boolean triggered = false;

    /**
     * Creates the spot.
     *
     * @param event  the hidden event (position and radius)
     * @param effect what happens to the player or the geese when it triggers
     */
    public EventSpot(IHiddenEvent event, Runnable effect) {
        this(event, null, effect);
    }

    /**
     * Creates the spot with something to see on the map, so the event is hidden but not
     * invisible: the player sees an odd object but not what it does.
     *
     * @param event    the hidden event (position and radius)
     * @param propPath the image drawn at the event, in the assets folder, or null for none
     * @param effect   what happens to the player or the geese when it triggers
     */
    public EventSpot(IHiddenEvent event, String propPath, Runnable effect) {
        this.event = event;
        this.propPath = propPath;
        this.effect = effect;
    }

    /**
     * Returns the image drawn at the event.
     *
     * @return the path in the assets folder, or null for none
     */
    public String getPropPath() {
        return propPath;
    }

    /**
     * Counts game time for the event's timed effect, then triggers the event if the player has
     * just walked into its range.
     *
     * @param playerPosition {x, y} of the player's centre
     * @param delta          game time since the last frame, in seconds
     * @return true if the event triggered this frame
     */
    public boolean update(float[] playerPosition, float delta) {
        event.elapse(delta);
        return update(playerPosition);
    }

    /**
     * Triggers the event if the player has just walked into its range.
     * Each event triggers only once per game.
     *
     * @param playerPosition {x, y} of the player's centre
     * @return true if the event triggered this frame
     */
    public boolean update(float[] playerPosition) {
        if (triggered || !event.isPlayerInActiveRange(playerPosition)) {
            return false;
        }
        triggered = true;
        event.trigger();
        effect.run();
        return true;
    }

    /**
     * Tells whether the event has already triggered.
     *
     * @return true after the first trigger
     */
    public boolean isTriggered() {
        return triggered;
    }

    /**
     * Returns the hidden event.
     *
     * @return the event
     */
    public IHiddenEvent getEvent() {
        return event;
    }
}
