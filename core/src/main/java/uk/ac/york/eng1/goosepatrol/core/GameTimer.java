package uk.ac.york.eng1.goosepatrol.core;

/** 5-minute countdown, advanced by frame delta so it freezes when not updated. */
public class GameTimer {

    public static final float DEFAULT_DURATION = 5f * 60f;
    public float duration = DEFAULT_DURATION;
    public float timeRemaining = DEFAULT_DURATION;
    public boolean isPaused = true;

    public GameTimer() {
        this(DEFAULT_DURATION);
    }

    public GameTimer(float durationSeconds) {
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("Time Remaining is non negative");
        } else {
            this.timeRemaining = this.duration = durationSeconds;

        }
    }

    /** Counts down by delta. Only called while RUNNING. */
    public void update(float delta) {
        if (this.isPaused) {
            return;
        }
        this.timeRemaining -= delta;
    }

    /** Resets to the full duration. */
    public void reset() {
        this.timeRemaining = this.duration;
    }

    public void addTime(float timeAdded) {
        this.timeRemaining += timeAdded;
    }

    public boolean isFinished() {
        return this.timeRemaining <= 0;
    }

    public float getRemaining() {
        return this.timeRemaining;
    }

    /** Remaining time as "m:ss" for the HUD. */
    public String format() {
        return "Time Remaining: " + Math.floorDiv((int) Math.floor(this.timeRemaining), 60)
            + ":" + Math.floor(this.timeRemaining % 60);
    }
}
