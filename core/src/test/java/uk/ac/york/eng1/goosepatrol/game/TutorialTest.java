package uk.ac.york.eng1.goosepatrol.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import uk.ac.york.eng1.goosepatrol.game.Tutorial.Tip;

/** Tests for {@link Tutorial}: tips appear once, in order, and can be hidden (UR_NO_EXPERIENCE). */
class TutorialTest {

    @Test
    void noTipUntilSomethingHappens() {
        assertNull(new Tutorial().current());
    }

    @Test
    void aTipShowsAndDisappearsAfterItsTime() {
        Tutorial tutorial = new Tutorial();
        tutorial.trigger(Tip.WALK);
        assertEquals(Tip.WALK, tutorial.current());
        tutorial.update(Tutorial.TIP_SECONDS + 0.1f);
        assertNull(tutorial.current());
    }

    @Test
    void eachTipIsShownOnlyOnce() {
        Tutorial tutorial = new Tutorial();
        tutorial.trigger(Tip.FEED);
        tutorial.update(Tutorial.TIP_SECONDS + 0.1f);
        tutorial.trigger(Tip.FEED);
        assertNull(tutorial.current());
    }

    @Test
    void aNewerTipWaitsForTheMinimumTimeThenReplacesTheOldOne() {
        Tutorial tutorial = new Tutorial();
        tutorial.trigger(Tip.WALK);
        tutorial.trigger(Tip.FEED);
        tutorial.update(Tutorial.MIN_SECONDS - 0.5f);
        assertEquals(Tip.WALK, tutorial.current(), "the first tip can be read");
        tutorial.update(1f);
        assertEquals(Tip.FEED, tutorial.current());
    }

    @Test
    void tipsCanBeHiddenAndShownAgain() {
        Tutorial tutorial = new Tutorial();
        tutorial.trigger(Tip.DODGE);
        tutorial.toggle();
        assertFalse(tutorial.isEnabled());
        assertNull(tutorial.current());
        tutorial.toggle();
        assertTrue(tutorial.isEnabled());
        assertEquals(Tip.DODGE, tutorial.current());
    }

    @Test
    void everyTipHasText() {
        for (Tip tip : Tip.values()) {
            assertFalse(tip.getText().isBlank());
        }
    }
}
