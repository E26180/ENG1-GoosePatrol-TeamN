package uk.ac.york.eng1.goosepatrol.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import uk.ac.york.eng1.goosepatrol.hidden_event.HEItems;

/** Tests for {@link EventSpot} with the real item hidden event (FR_EVENTS). */
class EventSpotTest {

    @Test
    void triggersOnceWhenThePlayerWalksIn() {
        int[] runs = {0};
        HEItems event = new HEItems(100f, 100f);
        EventSpot spot = new EventSpot(event, () -> runs[0]++);

        assertFalse(spot.update(new float[] {300f, 300f}), "far away");
        assertTrue(spot.update(new float[] {110f, 100f}), "inside the radius");
        assertTrue(event.getIsActivated());
        assertFalse(spot.update(new float[] {100f, 100f}), "only once");
        assertEquals(1, runs[0]);
        assertTrue(spot.isTriggered());
    }

    @Test
    void carriesThePropToDraw() {
        EventSpot spot = new EventSpot(new HEItems(0f, 0f), "textures/events/backpack.png", () -> { });
        assertEquals("textures/events/backpack.png", spot.getPropPath());
        assertTrue(new java.io.File("../assets/" + spot.getPropPath()).isFile());
    }
}
