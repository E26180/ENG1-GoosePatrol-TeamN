package uk.ac.york.eng1.goosepatrol;

public class HEItems implements IHiddenEvent{

    private float posX;
    private float posY;
    private final float activationRadius = 50;
    private final float flagRadius = 100;

    public HEItems(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    //Will be completed when there is a player class to use as a parameter
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

    }
    @Override
    public void trigger() {

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
}
