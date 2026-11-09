package uk.ac.york.eng1.goosepatrol.hidden_event;

/**
 * Baseline interface for a hidden event
 * Includes the get methods for configuration and checks for position and distance
 * Includes triggers for both the event and the corresponding flag
 */
public interface IHiddenEvent {

    //Range and Position Checks

    //Will be completed when there is a player class to use as a parameter
    float distanceFromPlayer(float[] playerPosition);

    boolean isPlayerInActiveRange(float[] playerPosition);
    boolean isPlayerInFlagRange(float[] playerPosition);

    //Actions and Execution
    void showFlag();
    void trigger();
    boolean checkActive();

    //Get methods for config data
    float getActivationRadius();
    float[] getPos();
    float getFlagRadius();
    boolean getIsActivated();
}
