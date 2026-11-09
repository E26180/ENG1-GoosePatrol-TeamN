package uk.ac.york.eng1.goosepatrol.hidden_event;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import uk.ac.york.eng1.goosepatrol.game.EventSpot;

class HEGooseSpeedTest {

    @Test
    void theSpeedUpLastsTenSecondsOfGameTime() {
        HEGooseSpeed duck = new HEGooseSpeed(0f, 0f);
        assertFalse(duck.checkActive());
        duck.trigger();
        duck.elapse(HEGooseSpeed.ACTIVE_SECONDS - 0.5f);
        assertTrue(duck.checkActive());
        duck.elapse(1f);
        assertFalse(duck.checkActive());
    }

    @Test
    void wallClockTimeDoesNotEndTheSpeedUp() throws InterruptedException {
        HEGooseSpeed duck = new HEGooseSpeed(0f, 0f);
        duck.trigger();
        Thread.sleep(20);
        assertTrue(duck.checkActive());
    }

    @Test
    void theEventSpotCountsGameTimeForItsEvent() {
        HEGooseSpeed duck = new HEGooseSpeed(0f, 0f);
        EventSpot spot = new EventSpot(duck, () -> { });
        assertTrue(spot.update(new float[] {0f, 0f}, 0f));
        spot.update(new float[] {500f, 500f}, HEGooseSpeed.ACTIVE_SECONDS + 1f);
        assertFalse(duck.checkActive());
    }
}
