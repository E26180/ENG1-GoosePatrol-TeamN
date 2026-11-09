package uk.ac.york.eng1.goosepatrol.game;

import uk.ac.york.eng1.goosepatrol.IHiddenEvent;

/**
 * Places one hidden event in the game and runs its effect the first time the
 * player walks into it (FR_EVENTS). The event class decides where it is and how
 * close the player must be; the effect is given here, so the event does not need
 * to know about the inventory or the geese.
 */
public class EventSpot {

    private final IHiddenEvent event;
    private final Runnable effect;
    private boolean triggered = false;

    /**
     * Creates the spot.
     *
     * @param event  the hidden event (position and radius)
     * @param effect what happens to the player or the geese when it triggers
     */
    public EventSpot(IHiddenEvent event, Runnable effect) {
        this.event = event;
        this.effect = effect;
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
