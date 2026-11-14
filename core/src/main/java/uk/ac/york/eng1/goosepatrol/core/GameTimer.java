package uk.ac.york.eng1.goosepatrol.core;

/** 5-minute countdown, advanced by frame delta so it freezes when not updated. */
public class GameTimer {

    public static final float DEFAULT_DURATION = 5f * 60f;

    public GameTimer() { }

    public GameTimer(float durationSeconds) { }

    /** Counts down by delta. Only called while RUNNING. */
    public void update(float delta) { }

    /** Resets to the full duration. */
    public void reset() { }

    public boolean isFinished() { return false; }

    public float getRemaining() { return 0f; }

    /** Remaining time as "m:ss" for the HUD. */
    public String format() { return ""; }
}
