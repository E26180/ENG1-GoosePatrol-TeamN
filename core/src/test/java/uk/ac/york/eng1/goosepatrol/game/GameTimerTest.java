package uk.ac.york.eng1.goosepatrol.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests for {@link GameTimer} (FR_TIMER). */
class GameTimerTest {

    @Test
    void startsAtFiveMinutesAndStopped() {
        GameTimer timer = new GameTimer();
        assertEquals(300f, timer.getSecondsLeft(), 0.001f);
        assertFalse(timer.isRunning());
        assertEquals("5:00", timer.format());
    }

    @Test
    void doesNotCountWhileStopped() {
        GameTimer timer = new GameTimer();
        timer.update(10f);
        assertEquals(300f, timer.getSecondsLeft(), 0.001f);
    }

    @Test
    void countsDownWhileRunningAndStopsCountingWhenStopped() {
        GameTimer timer = new GameTimer();
        timer.start();
        timer.update(1.5f);
        assertEquals(298.5f, timer.getSecondsLeft(), 0.001f);
        timer.stop();
        timer.update(5f);
        assertEquals(298.5f, timer.getSecondsLeft(), 0.001f);
    }

    @Test
    void neverGoesBelowZero() {
        GameTimer timer = new GameTimer(2f);
        timer.start();
        timer.update(5f);
        assertEquals(0f, timer.getSecondsLeft());
        assertTrue(timer.isFinished());
    }

    @Test
    void formatRoundsUpSoZeroOnlyShowsWhenTimeIsUp() {
        GameTimer timer = new GameTimer(65f);
        assertEquals("1:05", timer.format());
        timer.start();
        timer.update(64.5f);
        assertEquals("0:01", timer.format());
        timer.update(0.5f);
        assertEquals("0:00", timer.format());
    }
}
