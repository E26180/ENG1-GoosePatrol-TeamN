package uk.ac.york.eng1.goosepatrol.hidden_event;

import java.time.Instant;

public class HEGooseSpeed implements IHiddenEvent{

    private final float posX;
    private final float posY;
    private final float activationRadius = 50;
    private final float flagRadius = 100;

    private float activationTime = 0f;
    private boolean isActivated = false;

    public HEGooseSpeed(float posX, float posY) {
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
        //TODO: Setup the flag to show an event is nearby.  Dont show which event or exactly where
    }
    @Override
    public void trigger() {
        //TODO: Goose speed multiplier
        this.activationTime = Instant.now().getEpochSecond() + 10;
        this.isActivated = true;
    }

    @Override
    public boolean checkActive() {
        if (Instant.now().getEpochSecond() > this.activationTime) {
            //TODO: Goose speed multiplier
            return false;
        } else {
            return true;
        }
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
