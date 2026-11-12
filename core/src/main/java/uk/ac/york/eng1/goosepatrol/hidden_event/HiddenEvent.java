package uk.ac.york.eng1.goosepatrol.hidden_event;

public class HiddenEvent {

    private final float posX;
    private final float posY;
    private final float activationRadius = 50;
    private final float flagRadius = 100;
    private boolean isActivated = false;

    public HiddenEvent(float posx, float posy) {
        this.posX = posx;
        this.posY = posy;
    }

    public float distanceFromPlayer(float[] playerPosition) {
        float distanceX = playerPosition[0] - this.posX;
        float distanceY = playerPosition[1] - this.posY;
        return (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
    }

    public boolean isPlayerInActiveRange(float[] playerPosition) {
        return this.distanceFromPlayer(playerPosition) < this.activationRadius;
    }

    public boolean isPlayerInFlagRange(float[] playerPosition) {
        return this.distanceFromPlayer(playerPosition) < this.flagRadius;
    }

    public float getActivationRadius() { return this.activationRadius; }

    public float[] getPos() { return new float[] {this.posX, this.posY}; }

    public float getFlagRadius() { return this.flagRadius; }

    public boolean getIsActivated() { return this.isActivated; }

    public void activate() { this.isActivated = true; }
}
