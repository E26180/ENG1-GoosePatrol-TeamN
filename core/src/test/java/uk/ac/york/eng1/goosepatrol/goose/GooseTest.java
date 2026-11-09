package uk.ac.york.eng1.goosepatrol.goose;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.Test;

import uk.ac.york.eng1.goosepatrol.player.CollisionChecker;

/** Tests for {@link Goose}: walking, feeding, the warning before a snatch, and obstacles (FR_ENEMY, QR_ALERT). */
class GooseTest {

    private static final Rectangle WORLD = new Rectangle(0f, 0f, 2000f, 2000f);
    private static final CollisionChecker NOTHING = bounds -> false;

    private static Goose goose(float x, float y) {
        return new Goose(GooseType.NORMAL, x, y, WORLD, NOTHING);
    }

    /** Runs the goose for some time in small frames and says whether a snatch landed. */
    private static boolean run(Goose goose, float seconds, float targetX, float targetY) {
        boolean snatched = false;
        for (float t = 0f; t < seconds; t += 0.05f) {
            snatched |= goose.update(0.05f, targetX, targetY, 1f);
        }
        return snatched;
    }

    @Test
    void walksTowardsThePlayerAtItsSpeed() {
        Goose goose = goose(100f, 100f);
        goose.update(1f, 1000f, 100f, 1f);
        assertEquals(100f + GooseType.NORMAL.getSpeed(), goose.getCenterX(), 0.01f);
        assertEquals(100f, goose.getCenterY(), 0.01f);
        assertFalse(goose.isFacingLeft());
    }

    @Test
    void speedMultiplierMakesItFaster() {
        Goose goose = goose(100f, 100f);
        goose.update(1f, 1000f, 100f, 1.3f);
        assertEquals(100f + GooseType.NORMAL.getSpeed() * 1.3f, goose.getCenterX(), 0.01f);
    }

    @Test
    void stopsAndWarnsWhenItReachesThePlayer() {
        Goose goose = goose(100f, 100f);
        run(goose, 0.2f, 100f + Goose.REACH - 1f, 100f);
        assertTrue(goose.isAlerting());
        assertEquals(Goose.State.WIND_UP, goose.getState());
        float x = goose.getCenterX();
        goose.update(0.1f, 100f + Goose.REACH - 1f, 100f, 1f);
        assertEquals(x, goose.getCenterX(), 0.001f, "stands still while warning");
    }

    @Test
    void snatchLandsOnlyAfterTheFullWarning() {
        Goose goose = goose(100f, 100f);
        float px = 120f;
        assertFalse(goose.update(0.05f, px, 100f, 1f));
        assertTrue(goose.isAlerting());
        assertFalse(run(goose, Goose.WIND_UP_SECONDS - 0.15f, px, 100f), "no snatch during the warning");
        assertTrue(run(goose, 0.2f, px, 100f), "snatch at the end of the warning");
        assertEquals(Goose.State.RECOVER, goose.getState());
    }

    @Test
    void steppingAwayDuringTheWarningDodgesTheSnatch() {
        Goose goose = goose(100f, 100f);
        goose.update(0.05f, 120f, 100f, 1f);
        assertTrue(goose.isAlerting());
        assertFalse(run(goose, Goose.WIND_UP_SECONDS + 0.1f, 100f + Goose.SNATCH_RANGE + 20f, 100f));
    }

    @Test
    void feedingAWarningGooseStopsItsSnatch() {
        Goose goose = goose(100f, 100f);
        goose.update(0.05f, 120f, 100f, 1f);
        assertTrue(goose.isAlerting());
        goose.feed(0.5f);
        assertEquals(Goose.State.RECOVER, goose.getState());
        assertFalse(run(goose, Goose.WIND_UP_SECONDS + 0.1f, 120f, 100f), "no snatch after being fed");
    }

    @Test
    void comesBackAfterRecovering() {
        Goose goose = goose(100f, 100f);
        goose.update(0.05f, 120f, 100f, 1f);
        run(goose, Goose.WIND_UP_SECONDS + 0.1f, 120f, 100f);
        assertEquals(Goose.State.RECOVER, goose.getState());
        run(goose, Goose.RECOVER_SECONDS, 400f, 100f);
        assertEquals(Goose.State.APPROACH, goose.getState());
    }

    @Test
    void twoHalfFeedsFillItAndItFliesAway() {
        Goose goose = goose(100f, 100f);
        goose.feed(0.5f);
        assertEquals(0.5f, goose.getFill(), 0.001f);
        assertTrue(goose.isHungry());
        assertTrue(goose.wasJustFed());
        goose.feed(0.5f);
        assertFalse(goose.isHungry());
        assertEquals(Goose.State.LEAVING, goose.getState());
        run(goose, Goose.FLY_AWAY_SECONDS + 0.1f, 0f, 0f);
        assertEquals(Goose.State.GONE, goose.getState());
        assertEquals(0f, goose.getAlpha());
    }

    @Test
    void headGooseNeedsTwiceAsMuchFood() {
        Goose head = new Goose(GooseType.HEAD, 100f, 100f, WORLD, NOTHING);
        head.feed(0.5f);
        head.feed(0.5f);
        assertTrue(head.isHungry());
        assertEquals(0.5f, head.getFill(), 0.001f);
        head.feed(1f);
        assertFalse(head.isHungry());
    }

    @Test
    void foodForALeavingGooseIsIgnored() {
        Goose goose = goose(100f, 100f);
        goose.feed(1f);
        goose.feed(0.5f);
        assertEquals(1f, goose.getFill(), 0.001f);
        assertEquals(Goose.State.LEAVING, goose.getState());
    }

    @Test
    void neverLeavesTheMap() {
        Goose goose = goose(15f, 15f);
        run(goose, 2f, -500f, -500f);
        assertTrue(goose.getBounds().x >= 0f);
        assertTrue(goose.getBounds().y >= 0f);
    }

    @Test
    void walksAroundAWallItCanSlideAlong() {
        Rectangle wall = new Rectangle(200f, 0f, 40f, 150f);
        Goose goose = new Goose(GooseType.NORMAL, 150f, 100f, WORLD, wall::overlaps);
        run(goose, 0.5f, 400f, 300f);
        assertFalse(wall.overlaps(goose.getBounds()), "not inside the wall while walking");
        run(goose, 8f, 400f, 300f);
        assertTrue(goose.getCenterX() > 240f, "got past the wall");
    }

    @Test
    void flapsOverAnObstacleItIsStuckBehind() {
        // a long wall straight across the path: sliding cannot get round it in time
        Rectangle wall = new Rectangle(200f, 0f, 40f, 2000f);
        Goose goose = new Goose(GooseType.NORMAL, 150f, 1000f, WORLD, wall::overlaps);
        boolean flapped = false;
        for (float t = 0f; t < 6f; t += 0.05f) {
            goose.update(0.05f, 600f, 1000f, 1f);
            flapped |= goose.isFlapping();
        }
        assertTrue(flapped, "hops when stuck");
        assertTrue(goose.getCenterX() > 240f, "ends up on the other side");
        assertFalse(wall.overlaps(goose.getBounds()), "never stays on the wall");
    }
}
