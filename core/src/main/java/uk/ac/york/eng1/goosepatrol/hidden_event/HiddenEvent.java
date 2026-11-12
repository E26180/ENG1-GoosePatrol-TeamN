package uk.ac.york.eng1.goosepatrol.hidden_event;

/**
 * Contains get methods, activate() and player distance methods for the
 *      hidden event classes
 */

public class HiddenEvent {

    //X-coordinate for event activation
    private final float posX;

    //Y-coordinate for event activation
    private final float posY;

    //Radius in which the event is triggered
    private final float activationRadius = 50;

    //Shows if the event has been activated
    //  Prevents multiple activations
    private boolean isActivated = false;

    /**
     * Super class constructor method
     * Sets the event activation position
     *
     * @param posX X-coordinate of event
     * @param posY Y-coordinate of event
     */

    public HiddenEvent(float posX, float posY) {
        this.posX = posX;
        this.posY = posY;
    }

    /**
     * Measures the distance between the event epicentre and the player's position
     *
     * @param playerPosition (x, y) of the player's current location
     * @return distance between the player and event epicentre
     */

    public float distanceFromPlayer(float[] playerPosition) {
        //Horizontal distance
        float distanceX = playerPosition[0] - this.posX;

        //Vertical distance
        float distanceY = playerPosition[1] - this.posY;

        //Distance as the crow flies
        return (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
    }

    /**
     * Sets the boolean flag showing if the event has been activated to true
     */

    public void activate() { this.isActivated = true; }

    /*
    Get Methods
     */

    /**
     * Checks if the player is close enough to activate the event
     *
     * @param playerPosition (x, y) of the player's current location
     * @return boolean showing if the event should be activated
     */

    public boolean isPlayerInActiveRange(float[] playerPosition) {
        return this.distanceFromPlayer(playerPosition) < this.activationRadius;
    }

    /**
     * Returns the activation radius field
     *
     * @return activation radius
     */

    public float getActivationRadius() { return this.activationRadius; }


    /**
     * Returns the posX and posY fields
     *
     * @return posX and posY as a float array ({posX, posY})
     */

    public float[] getPos() { return new float[] {this.posX, this.posY}; }


    /**
     * Returns the boolean flag showing if teh event has been activated
     *
     * @return if event has been activated
     */

    public boolean getIsActivated() { return this.isActivated; }
}
