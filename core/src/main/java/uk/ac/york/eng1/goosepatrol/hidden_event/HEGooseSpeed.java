package uk.ac.york.eng1.goosepatrol.hidden_event;

import uk.ac.york.eng1.goosepatrol.geese.Geese;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Source code for the negative hidden event
 *      This speeds geese up by 30% (speed 5 up to 6.5) for 10 seconds, then brings them
 *          back to normal speed
 *      Extends HiddenEvent class which contains get methods, activate() and player distance
 *          methods
 */

public class HEGooseSpeed extends HiddenEvent {

    //Event end time
    private long activationTime = 0L;

    //All existing geese
    private List<Geese> existingGeese;

    //Event time remaining during pause
    private long remainingWhenPaused = 0L;

    //Boolean flag (true if the game is paused, false otherwise)
    private boolean isPaused = false;


    /**
     * Constructor that sets the event activation coordinates
     *
     * @param posX X-coordinate of event
     * @param posY Y-coordinate of event
     */

    public HEGooseSpeed(float posX, float posY) {
        super(posX, posY);
    }

    /**
     * Method that gets run upon event activation
     *
     * @param geeseList list of all existing geese
     */

    public void trigger(List<Geese> geeseList) {
        //Sets existingGeese field to match the currently rendered geese list
        this.existingGeese = geeseList;

        //Increases speed for all existing goose instances
        for (Geese goose : existingGeese) {
            goose.SetGooseSpeed(1.3f);
        }

        //Sets 10-second timer till the event ends
        this.activationTime = Instant.now().getEpochSecond() + 10;

        //Runs the activation method in the parent class
        activate();
    }

    /**
     * Checks whether the event timer (10s) has run out
     *
     * @param geeseList list of all existing geese
     * @return boolean flag
     */

    public List<Geese> checkActive(List<Geese> geeseList) {

        //Checks if the event is over
        if (Instant.now().getEpochSecond() > this.activationTime) {

            //Sets the speed of every existing goose to original value (5f)
            this.existingGeese = geeseList;
            for (Geese goose : existingGeese) {
                goose.ResetGooseSpeed();
            }
            return this.existingGeese;
        } else {
            return geeseList;
        }
    }

    /**
     * Ensures that pausing the game doesn't affect the remaining event time
     */

    public void onPause() {

        //Ends the function if the game is paused or there are no geese
        if (this.isPaused || this.existingGeese == null) {
            return;
        } else {

            //Calculates the time remaining
            long remaining = this.activationTime - Instant.now().getEpochSecond();

            //Ends function if event is over
            if (remaining <= 0) {
                return;
            }

            this.remainingWhenPaused = remaining;
            this.isPaused = true;
        }
    }

    /**
     * Resets the timer to where it left off when the game was paused
     */

    public void onResume() {

        //Ends the function if the game isn't paused
        if (!this.isPaused ) {
            return;

        } else {

            //Sets timer to time remaining when game was paused
            this.activationTime = this.remainingWhenPaused + Instant.now().getEpochSecond();

            this.remainingWhenPaused = 0L;
            this.isPaused = false;
        }
    }

}
