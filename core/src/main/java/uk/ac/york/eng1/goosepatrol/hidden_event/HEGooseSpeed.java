package uk.ac.york.eng1.goosepatrol.hidden_event;

/**
 * Hindering hidden event (FR_EVENT_EFFECT): when triggered, the geese move faster for
 * 10 seconds. The game applies the speed-up through its effect, {@code GooseManager.speedUp};
 * {@link #checkActive()} reports the same 10 seconds. Both count game time, so a pause
 * stops them.
 */
public class HEGooseSpeed implements IHiddenEvent{

    private final float posX;
    private final float posY;
    private final float activationRadius = 50;
    private final float flagRadius = 100;

    /** How long the speed-up lasts, in seconds of game time. */
    public static final float ACTIVE_SECONDS = 10f;

    /** Game time left of the speed-up, in seconds. */
    private float secondsLeft = 0f;
    private boolean isActivated = false;

    /**
     * Creates the event at a place on the map.
     *
     * @param posX world x of the event
     * @param posY world y of the event
     */
    public HEGooseSpeed(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public float distanceFromPlayer(float[] playerPosition) {
        float distanceX = playerPosition[0] - this.posX;
        float distanceY = playerPosition[1] - this.posY;
        return (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
    }

    @Override
    public boolean isPlayerInActiveRange(float[] playerPosition) {
        return this.distanceFromPlayer(playerPosition) < this.activationRadius;
    }

    @Override
    public boolean isPlayerInFlagRange(float[] playerPosition) {
        return this.distanceFromPlayer(playerPosition) < this.flagRadius;
    }

    @Override
    public void showFlag() {
        // Nothing to do here: WorldRenderer draws the "?" hint while isPlayerInFlagRange is true.
    }

    @Override
    public void trigger() {
        this.secondsLeft = ACTIVE_SECONDS;
        this.isActivated = true;
    }

    @Override
    public boolean checkActive() {
        return this.secondsLeft > 0f;
    }

    @Override
    public void elapse(float seconds) {
        this.secondsLeft = Math.max(0f, this.secondsLeft - seconds);
    }

    @Override
    public float getActivationRadius() {
        return this.activationRadius;
    }

    @Override
    public float[] getPos() {
        return new float[] {this.posX, this.posY};
    }

    @Override
    public float getFlagRadius() {
        return this.flagRadius;
    }

    @Override
    public boolean getIsActivated() {
        return this.isActivated;
    }
}
