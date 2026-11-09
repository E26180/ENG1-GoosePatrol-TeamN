package uk.ac.york.eng1.goosepatrol.goose;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.ac.york.eng1.goosepatrol.item.BreadItem;
import uk.ac.york.eng1.goosepatrol.item.OatsItem;
import uk.ac.york.eng1.goosepatrol.item.SpeedBoostTarget;
import uk.ac.york.eng1.goosepatrol.screen.GameScreen;

/** Tests for {@link GooseManager}: waves, finding geese for the items, blocking the player and balance. */
class GooseManagerTest {

    private static final Rectangle WORLD = new Rectangle(0f, 0f, 1600f, 1280f);
    private static final float PX = 800f;
    private static final float PY = 640f;

    /** Remembers what the manager reported. */
    private static class Recorder implements GooseListener {
        final List<String> log = new ArrayList<>();

        @Override
        public void waveStarted(int number, int total) {
            log.add("start " + number + "/" + total);
        }

        @Override
        public void gooseAlert(Goose goose) {
            log.add("alert");
        }

        @Override
        public void playerSnatched(Goose goose) {
            log.add("snatch");
        }

        @Override
        public void gooseFull(Goose goose) {
            log.add("full");
        }

        @Override
        public void waveCleared(int number) {
            log.add("cleared " + number);
        }
    }

    /** A player that only remembers the last speed change. */
    private static class FakePlayer implements SpeedBoostTarget {
        float multiplier = 1f;
        float seconds = 0f;

        @Override
        public void applySpeedBoost(float multiplier, float seconds) {
            this.multiplier = multiplier;
            this.seconds = seconds;
        }
    }

    private Recorder recorder;
    private FakePlayer player;

    @BeforeEach
    void setUp() {
        recorder = new Recorder();
        player = new FakePlayer();
    }

    private GooseManager manager(List<Wave> waves) {
        return new GooseManager(WORLD, bounds -> false, player, recorder, waves, new Random(42));
    }

    /** Runs the manager with the player standing still at (PX, PY). */
    private static void run(GooseManager manager, float seconds) {
        for (float t = 0f; t < seconds; t += 0.05f) {
            manager.update(0.05f, PX, PY);
        }
    }

    /** Feeds every hungry goose on the map until it is full. */
    private static void feedAll(GooseManager manager) {
        for (Goose goose : manager.getGeese()) {
            goose.feed(goose.getType().getAppetite());
        }
    }

    @Test
    void firstWaveStartsAfterTheDelayAndGeeseArriveOneByOne() {
        GooseManager manager = manager(List.of(new Wave(3, 0), new Wave(2, 0)));
        assertEquals(0, manager.currentWave());
        assertEquals(GooseManager.FIRST_WAVE_DELAY, manager.secondsToNextWave(), 0.001f);
        run(manager, GooseManager.FIRST_WAVE_DELAY + 0.1f);
        assertEquals(1, manager.currentWave());
        assertEquals(List.of("start 1/2"), recorder.log);
        assertEquals(3, manager.geeseLeft(), "counts the geese still to arrive");
        run(manager, 3 * GooseManager.SPAWN_GAP_SECONDS);
        assertEquals(3, manager.getGeese().size());
        assertEquals(1, manager.wavesLeft());
    }

    @Test
    void geeseArriveFarFromThePlayer() {
        GooseManager manager = manager(List.of(new Wave(5, 0)));
        int seen = 0;
        for (float t = 0f; t < GooseManager.FIRST_WAVE_DELAY + 6f; t += 0.01f) {
            manager.update(0.01f, PX, PY);
            List<Goose> geese = manager.getGeese();
            if (geese.size() > seen) {
                Goose newest = geese.get(geese.size() - 1);
                double d = Math.hypot(newest.getCenterX() - PX, newest.getCenterY() - PY);
                assertTrue(d >= GooseManager.MIN_SPAWN_DISTANCE - 1f, "arrived " + d + " px from the player");
                seen = geese.size();
            }
        }
        assertEquals(5, seen);
    }

    @Test
    void geeseNeverArriveInsideABuilding() {
        Rectangle building = new Rectangle(0f, 0f, 1600f, 600f);
        GooseManager manager = new GooseManager(WORLD, building::overlaps, player, recorder,
            List.of(new Wave(6, 0)), new Random(1));
        manager.update(GooseManager.FIRST_WAVE_DELAY + 0.01f, PX, PY);
        for (int i = 0; i < 6; i++) {
            manager.update(GooseManager.SPAWN_GAP_SECONDS + 0.01f, PX, PY);
        }
        assertEquals(6, manager.getGeese().size());
        for (Goose goose : manager.getGeese()) {
            assertFalse(building.overlaps(goose.getBounds()));
        }
    }

    @Test
    void feedingAWaveStartsTheNextAfterABreak() {
        GooseManager manager = manager(List.of(new Wave(2, 0), new Wave(1, 0)));
        run(manager, GooseManager.FIRST_WAVE_DELAY + 2 * GooseManager.SPAWN_GAP_SECONDS + 0.2f);
        feedAll(manager);
        run(manager, 0.1f);
        assertTrue(recorder.log.contains("cleared 1"));
        assertEquals(2, recorder.log.stream().filter("full"::equals).count());
        assertEquals(1, manager.currentWave(), "short break first");
        run(manager, GooseManager.BREAK_SECONDS);
        assertEquals(2, manager.currentWave());
        assertFalse(manager.allWavesCleared());
    }

    @Test
    void nextWaveComesAfterTheCountdownEvenIfGeeseAreLeft() {
        GooseManager manager = manager(List.of(new Wave(1, 0), new Wave(1, 0)));
        run(manager, GooseManager.FIRST_WAVE_DELAY + 0.1f);
        run(manager, GooseManager.WAVE_COUNTDOWN_SECONDS + 0.1f);
        assertEquals(2, manager.currentWave());
        assertEquals(2, manager.geeseLeft(), "the old goose is still hungry");
    }

    @Test
    void allWavesClearedOnlyWhenTheLastGooseIsFed() {
        GooseManager manager = manager(List.of(new Wave(1, 0), new Wave(1, 1)));
        assertFalse(manager.allWavesCleared());
        run(manager, GooseManager.FIRST_WAVE_DELAY + 1f);
        feedAll(manager);
        run(manager, GooseManager.BREAK_SECONDS + 0.2f);
        assertFalse(manager.allWavesCleared(), "second wave has not arrived yet");
        run(manager, 2 * GooseManager.SPAWN_GAP_SECONDS + 0.2f);
        assertEquals(2, manager.geeseLeft());
        feedAll(manager);
        assertTrue(manager.allWavesCleared());
        assertEquals(0, manager.wavesLeft());
        assertEquals(-1f, manager.secondsToNextWave());
    }

    @Test
    void gooseAtFindsOnlyHungryGeeseNearTheClick() {
        GooseManager manager = manager(List.of(new Wave(1, 0)));
        run(manager, GooseManager.FIRST_WAVE_DELAY + 0.1f);
        Goose goose = manager.getGeese().get(0);
        float x = goose.getCenterX();
        float y = goose.getCenterY();
        assertNotNull(manager.gooseAt(x, y));
        assertNotNull(manager.gooseAt(goose.getBounds().x - GooseManager.CLICK_TOLERANCE + 1f, y), "forgiving click");
        assertNull(manager.gooseAt(x + 100f, y));
        goose.feed(1f);
        assertNull(manager.gooseAt(x, y), "a full goose cannot be fed");
    }

    @Test
    void geeseNearUsesTheRadius() {
        GooseManager manager = manager(List.of(new Wave(1, 0)));
        run(manager, GooseManager.FIRST_WAVE_DELAY + 0.1f);
        Goose goose = manager.getGeese().get(0);
        assertEquals(1, manager.geeseNear(goose.getCenterX() + 50f, goose.getCenterY(), 60f).size());
        assertEquals(0, manager.geeseNear(goose.getCenterX() + 70f, goose.getCenterY(), 60f).size());
    }

    @Test
    void hungryGeeseBlockThePlayerButFullOnesDoNot() {
        GooseManager manager = manager(List.of(new Wave(1, 0)));
        run(manager, GooseManager.FIRST_WAVE_DELAY + 0.1f);
        Goose goose = manager.getGeese().get(0);
        Rectangle onTop = new Rectangle(goose.getBounds());
        assertTrue(manager.collidesWith(onTop));
        goose.feed(1f);
        assertFalse(manager.collidesWith(onTop));
    }

    @Test
    void aSnatchStartlesThePlayerWithAWarningFirst() {
        GooseManager manager = manager(List.of(new Wave(1, 0)));
        // the player stands where the goose arrives, so it reaches them at once
        manager.update(GooseManager.FIRST_WAVE_DELAY + 0.01f, PX, PY);
        manager.update(0.01f, PX, PY);
        Goose goose = manager.getGeese().get(0);
        float gx = goose.getCenterX() + 20f;
        float gy = goose.getCenterY();
        for (float t = 0f; t < Goose.WIND_UP_SECONDS + 0.2f; t += 0.05f) {
            manager.update(0.05f, gx, gy);
        }
        int alert = recorder.log.indexOf("alert");
        int snatch = recorder.log.indexOf("snatch");
        assertTrue(alert >= 0 && snatch > alert, "warning comes before the snatch (QR_ALERT)");
        assertEquals(GooseManager.STARTLE_MULTIPLIER, player.multiplier, 0.001f);
        assertTrue(player.multiplier >= 0.5f, "never slows the player by more than half (QR_AVOID_EXTREME)");
        assertEquals(1, manager.getSnatches());
    }

    @Test
    void speedUpLastsOnlyItsTime() {
        GooseManager manager = manager(List.of(new Wave(1, 0)));
        manager.speedUp(1.3f, 10f);
        assertEquals(10f, manager.getSpeedUpLeft(), 0.001f);
        run(manager, 10.1f);
        assertEquals(0f, manager.getSpeedUpLeft());
    }

    @Test
    void geeseDoNotStandOnTopOfEachOther() {
        GooseManager manager = manager(List.of(new Wave(6, 0)));
        run(manager, 30f);
        List<Goose> geese = manager.getGeese();
        for (int i = 0; i < geese.size(); i++) {
            for (int j = i + 1; j < geese.size(); j++) {
                double d = Math.hypot(geese.get(i).getCenterX() - geese.get(j).getCenterX(),
                    geese.get(i).getCenterY() - geese.get(j).getCenterY());
                assertTrue(d > GooseManager.SEPARATION * 0.5f, "geese kept apart");
            }
        }
    }

    @Test
    void aWaveNeedsAtLeastOneGoose() {
        assertThrows(IllegalArgumentException.class, () -> new Wave(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Wave(-1, 2));
    }

    @Test
    void startingFoodIsEnoughToFeedEveryWaveWithoutAnyEvent() {
        // QR_WINNABLE: count bars of hunger. Bread is counted as feeding only one goose per throw,
        // the worst case; geese crowd round the player, so a throw usually reaches more.
        float demand = 0f;
        for (Wave wave : GooseManager.DEFAULT_WAVES) {
            demand += wave.totalAppetite();
        }
        int oatsUses = OatsItem.START_USES + GameScreen.OATS_PER_CLEARED_WAVE * (GooseManager.DEFAULT_WAVES.size() - 1);
        float supply = oatsUses * OatsItem.HUNGER_FILLED + BreadItem.START_USES * BreadItem.HUNGER_FILLED;
        assertTrue(supply >= demand, "supply " + supply + " must cover demand " + demand);
    }

    @Test
    void wavesGrowAndTheLastHasAHeadGoose() {
        List<Wave> waves = GooseManager.DEFAULT_WAVES;
        assertTrue(waves.size() >= 2, "several waves (UR_WAVES)");
        for (int i = 1; i < waves.size(); i++) {
            assertTrue(waves.get(i).totalAppetite() >= waves.get(i - 1).totalAppetite());
        }
        assertEquals(1, waves.get(waves.size() - 1).headGeese());
    }
}
