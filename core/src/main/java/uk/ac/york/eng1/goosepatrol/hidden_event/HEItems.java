package uk.ac.york.eng1.goosepatrol.hidden_event;

/**
 * Helping hidden event (FR_EVENT_EFFECT): finding it gives the player more food.
 * The reward itself (+10 Bread and 3 Energy Drinks) is applied by the effect that
 * {@code GameScreen} gives to this event's {@code EventSpot}, so this class does not
 * need a reference to the inventory.
 */
public class HEItems implements IHiddenEvent{

    private float posX;
    private float posY;
    private final float activationRadius = 50;
    private final float flagRadius = 100;

    private boolean isActivated = false;

    /**
     * Creates the event at a place on the map.
     *
     * @param posX world x of the event
     * @param posY world y of the event
     */
    public HEItems(float posX, float posY) {
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
        // The extra Bread and Energy Drinks are added by the EventSpot effect in GameScreen.
        this.isActivated = true;
    }

    @Override
    public boolean checkActive() {
        return false;
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
