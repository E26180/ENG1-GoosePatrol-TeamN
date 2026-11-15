package uk.ac.york.eng1.goosepatrol.hidden_event;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;

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
    private final float activationRadius = 150;

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
     * @param player (x, y) of the player's current location through PolygonSprite class
     * @return distance between the player and event epicentre
     */

    public float distanceFromPlayer(PolygonSprite player) {

        float[] playerPosition = getPlayerPosition(player);
        float[] eventPosition = getPos();

        //Horizontal distance
        float distanceX = playerPosition[0] - eventPosition[0];

        //Vertical distance
        float distanceY = playerPosition[1] - eventPosition[1];

        //Distance as the crow flies
        return (float) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
    }

    /**
     * Sets the boolean flag showing if the event has been activated to true
     */

    public void activate() { this.isActivated = true; }

    /**
     * Plays the event sound, then disposes it
     */

    protected void playSound() {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal("audio/event.wav"));
        sound.play(1.0f);
        sound.dispose();
    }

    /*
    Get Methods
     */

    /**
     * Checks if the player is close enough to activate the event
     *
     * @param player (x, y) of the player's current location through PolygonSprite class
     * @return boolean showing if the event should be activated
     */

    public boolean isPlayerInActiveRange(PolygonSprite player) {
        return this.distanceFromPlayer(player) < this.activationRadius;
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


    /**
     * Gets the player position from PolygonSprite and returns (x, y) as float[]
     *
     * @param player PolygonSprite object
     * @return (x, y)
     */
    public float[] getPlayerPosition(PolygonSprite player) {
        return new float[] {player.getX() + player.getWidth() / 2, player.getY() +
            player.getHeight() / 2};
    }
}
