package uk.ac.york.eng1.goosepatrol.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests for {@link GameSession}: start paused, pause key, win and loss (FR_START_PAUSED, FR_PAUSE_KEY, FR_JUDGEMENT). */
class GameSessionTest {

    /** Geese whose "all cleared" answer the test can switch. */
    private static class FakeWaves implements WaveStatus {
        boolean cleared = false;

        @Override
        public void update(float delta, float playerX, float playerY) {
        }

        @Override
        public int geeseLeft() {
            return cleared ? 0 : 3;
        }

        @Override
        public int wavesLeft() {
            return 0;
        }

        @Override
        public boolean allWavesCleared() {
            return cleared;
        }
    }

    private FakeWaves waves;
    private GameTimer timer;
    private GameSession session;

    @BeforeEach
    void setUp() {
        waves = new FakeWaves();
        timer = new GameTimer(10f);
        session = new GameSession(timer, waves);
    }

    @Test
    void startsOnTheStartScreenWithTheTimerStopped() {
        assertEquals(GameState.READY, session.getState());
        session.update(5f);
        assertEquals(10f, timer.getSecondsLeft(), 0.001f);
        assertFalse(session.isPlaying());
    }

    @Test
    void playStartsTheTimer() {
        session.play();
        assertTrue(session.isPlaying());
        session.update(1f);
        assertEquals(9f, timer.getSecondsLeft(), 0.001f);
    }

    @Test
    void pauseStopsTheTimerAndResumeCarriesOn() {
        session.play();
        session.update(1f);
        session.togglePause();
        assertEquals(GameState.PAUSED, session.getState());
        session.update(5f);
        assertEquals(9f, timer.getSecondsLeft(), 0.001f);
        session.togglePause();
        assertEquals(GameState.PLAYING, session.getState());
        session.update(1f);
        assertEquals(8f, timer.getSecondsLeft(), 0.001f);
    }

    @Test
    void pauseKeyDoesNothingOnTheStartScreen() {
        session.togglePause();
        assertEquals(GameState.READY, session.getState());
    }

    @Test
    void losesWhenTheTimeRunsOut() {
        session.play();
        session.update(10f);
        assertEquals(GameState.LOST, session.getState());
        assertTrue(session.getState().isOver());
    }

    @Test
    void winsEarlyWhenAllWavesAreCleared() {
        session.play();
        session.update(2f);
        waves.cleared = true;
        session.update(0.1f);
        assertEquals(GameState.WON, session.getState());
        assertFalse(timer.isRunning());
    }

    @Test
    void winBeatsTheTimerOnTheSameFrame() {
        session.play();
        waves.cleared = true;
        session.update(10f);
        assertEquals(GameState.WON, session.getState());
    }

    @Test
    void cannotBeResumedOrPausedOnceOver() {
        session.play();
        session.update(10f);
        session.play();
        session.togglePause();
        assertEquals(GameState.LOST, session.getState());
    }

    @Test
    void countsEvents() {
        session.eventTriggered();
        session.eventTriggered();
        assertEquals(2, session.getEventsTriggered());
    }
}
