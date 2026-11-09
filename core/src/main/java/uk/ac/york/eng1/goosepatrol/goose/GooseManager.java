package uk.ac.york.eng1.goosepatrol.goose;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Rectangle;

import uk.ac.york.eng1.goosepatrol.game.WaveStatus;
import uk.ac.york.eng1.goosepatrol.item.Feedable;
import uk.ac.york.eng1.goosepatrol.item.GooseLocator;
import uk.ac.york.eng1.goosepatrol.item.SpeedBoostTarget;
import uk.ac.york.eng1.goosepatrol.player.CollisionChecker;

/**
 * Runs the geese: sends them in waves (FR_WAVES, QR_PROGRESSION), moves them, keeps them
 * apart, finds them for the items ({@link GooseLocator}), stops the player walking through
 * them (QR_COLLISION) and tells the rules when every wave is fed ({@link WaveStatus}).
 */
public class GooseManager implements GooseLocator, WaveStatus, CollisionChecker {

    /** The waves of a normal game: more geese each time, and a head goose at the end. */
    public static final List<Wave> DEFAULT_WAVES = List.of(new Wave(3, 0), new Wave(4, 0), new Wave(5, 1));

    /** Seconds from the start of the game to the first wave. */
    public static final float FIRST_WAVE_DELAY = 2f;

    /** Break between a cleared wave and the next one. */
    public static final float BREAK_SECONDS = 4f;

    /** The next wave comes after this long even if the current one is not cleared. */
    public static final float WAVE_COUNTDOWN_SECONDS = 90f;

    /** Time between two geese of the same wave arriving. */
    public static final float SPAWN_GAP_SECONDS = 0.8f;

    /** Geese arrive at least this far from the player, so they never appear on top of them. */
    public static final float MIN_SPAWN_DISTANCE = 320f;

    /** Extra space around a goose that still counts as clicking it, for new players (QR_NEWBIE). */
    public static final float CLICK_TOLERANCE = 10f;

    /** Geese closer than this (centre to centre) are pushed apart. */
    public static final float SEPARATION = 22f;

    /** The player's speed while startled by a snatch: 40% slower (QR_AVOID_EXTREME allows 50%). */
    public static final float STARTLE_MULTIPLIER = 0.6f;

    /** How long a snatch startles the player. */
    public static final float STARTLE_SECONDS = 2f;

    private final Rectangle world;
    private final CollisionChecker obstacles;
    private final Random random;
    private final List<Wave> waves;
    private final SpeedBoostTarget player;
    private final GooseListener listener;

    private final List<Goose> geese = new ArrayList<>();
    private final Deque<GooseType> toSpawn = new ArrayDeque<>();
    private int waveIndex = -1;
    private float nextWaveIn = FIRST_WAVE_DELAY;
    private float waveTime = 0f;
    private float spawnTimer = 0f;
    private boolean lastWaveClearedReported = false;
    private float speedMultiplier = 1f;
    private float speedUpLeft = 0f;
    private int snatches = 0;

    /**
     * Creates the manager for a normal game.
     *
     * @param world     the map area
     * @param obstacles the buildings the geese walk around
     * @param player    the player, startled by a snatch
     * @param listener  told about waves, warnings, snatches and full geese
     */
    public GooseManager(Rectangle world, CollisionChecker obstacles, SpeedBoostTarget player, GooseListener listener) {
        this(world, obstacles, player, listener, DEFAULT_WAVES, new Random());
    }

    /**
     * Creates the manager with any waves and random source, for example in tests.
     *
     * @param world     the map area
     * @param obstacles the buildings the geese walk around
     * @param player    the player, startled by a snatch
     * @param listener  told about waves, warnings, snatches and full geese
     * @param waves     the waves, in order (at least one)
     * @param random    used to choose where geese arrive
     */
    public GooseManager(Rectangle world, CollisionChecker obstacles, SpeedBoostTarget player,
                        GooseListener listener, List<Wave> waves, Random random) {
        if (waves.isEmpty()) {
            throw new IllegalArgumentException("at least one wave is needed");
        }
        this.world = new Rectangle(world);
        this.obstacles = obstacles;
        this.player = player;
        this.listener = listener;
        this.waves = List.copyOf(waves);
        this.random = random;
    }

    @Override
    public void update(float delta, float playerX, float playerY) {
        if (delta <= 0f) {
            return;
        }
        if (speedUpLeft > 0f) {
            speedUpLeft = Math.max(0f, speedUpLeft - delta);
            if (speedUpLeft == 0f) {
                speedMultiplier = 1f;
            }
        }
        updateWaves(delta, playerX, playerY);

        for (Goose goose : geese) {
            boolean wasAlerting = goose.isAlerting();
            boolean snatched = goose.update(delta, playerX, playerY, speedMultiplier);
            if (!wasAlerting && goose.isAlerting()) {
                listener.gooseAlert(goose);
            }
            if (snatched) {
                snatches++;
                player.applySpeedBoost(STARTLE_MULTIPLIER, STARTLE_SECONDS);
                listener.playerSnatched(goose);
            }
        }
        separate();

        Iterator<Goose> it = geese.iterator();
        while (it.hasNext()) {
            Goose goose = it.next();
            if (!goose.isHungry() && !goose.isLeavingAnnounced()) {
                goose.markLeavingAnnounced();
                listener.gooseFull(goose);
            }
            if (goose.getState() == Goose.State.GONE) {
                it.remove();
            }
        }
    }

    /** Starts waves, lets the geese of the current wave arrive, and notices cleared waves. */
    private void updateWaves(float delta, float playerX, float playerY) {
        if (nextWaveIn > 0f) {
            nextWaveIn -= delta;
            if (nextWaveIn <= 0f) {
                startNextWave();
            }
            return;
        }
        if (waveIndex < 0) {
            return;
        }
        waveTime += delta;
        if (!toSpawn.isEmpty()) {
            spawnTimer -= delta;
            if (spawnTimer <= 0f) {
                spawn(toSpawn.poll(), playerX, playerY);
                spawnTimer = SPAWN_GAP_SECONDS;
            }
        }
        boolean cleared = toSpawn.isEmpty() && hungryGeese() == 0;
        boolean lastWave = waveIndex == waves.size() - 1;
        if (cleared && !lastWave) {
            listener.waveCleared(waveIndex + 1);
            nextWaveIn = BREAK_SECONDS;
        } else if (cleared && !lastWaveClearedReported) {
            lastWaveClearedReported = true;
            listener.waveCleared(waveIndex + 1);
        } else if (!lastWave && waveTime >= WAVE_COUNTDOWN_SECONDS) {
            startNextWave();
        }
    }

    private void startNextWave() {
        waveIndex++;
        nextWaveIn = 0f;
        waveTime = 0f;
        spawnTimer = 0f;
        Wave wave = waves.get(waveIndex);
        for (int i = 0; i < wave.geese(); i++) {
            toSpawn.add(GooseType.NORMAL);
        }
        for (int i = 0; i < wave.headGeese(); i++) {
            toSpawn.add(GooseType.HEAD);
        }
        listener.waveStarted(waveIndex + 1, waves.size());
    }

    /** Puts a new goose on a free spot near the edge of the map, away from the player. */
    private void spawn(GooseType type, float playerX, float playerY) {
        float w = Goose.WIDTH * type.getScale();
        float h = Goose.HEIGHT * type.getScale();
        Rectangle test = new Rectangle(0f, 0f, w, h);
        float[] fallback = null;
        for (int attempt = 0; attempt < 200; attempt++) {
            float x;
            float y;
            if (attempt < 120) {
                // a random point on a random edge of the map
                int side = random.nextInt(4);
                float along = random.nextFloat();
                float inset = 4f + random.nextFloat() * 60f;
                x = side < 2 ? world.x + along * (world.width - w) : (side == 2 ? world.x + inset : world.x + world.width - w - inset);
                y = side < 2 ? (side == 0 ? world.y + inset : world.y + world.height - h - inset) : world.y + along * (world.height - h);
            } else {
                x = world.x + random.nextFloat() * (world.width - w);
                y = world.y + random.nextFloat() * (world.height - h);
            }
            test.setPosition(x, y);
            if (obstacles.collidesWith(test)) {
                continue;
            }
            float cx = x + w / 2f;
            float cy = y + h / 2f;
            if (Math.hypot(cx - playerX, cy - playerY) >= MIN_SPAWN_DISTANCE) {
                geese.add(new Goose(type, cx, cy, world, obstacles));
                return;
            }
            if (fallback == null) {
                fallback = new float[] {cx, cy};
            }
        }
        float[] at = fallback != null ? fallback : new float[] {world.x + w, world.y + h};
        geese.add(new Goose(type, at[0], at[1], world, obstacles));
    }

    /** Pushes apart hungry geese that stand too close, so each one can be seen and clicked. */
    private void separate() {
        for (int i = 0; i < geese.size(); i++) {
            Goose a = geese.get(i);
            if (!a.isHungry()) {
                continue;
            }
            for (int j = i + 1; j < geese.size(); j++) {
                Goose b = geese.get(j);
                if (!b.isHungry()) {
                    continue;
                }
                float dx = b.getCenterX() - a.getCenterX();
                float dy = b.getCenterY() - a.getCenterY();
                float d = (float) Math.sqrt(dx * dx + dy * dy);
                if (d >= SEPARATION) {
                    continue;
                }
                if (d < 0.01f) {
                    dx = (i % 2 == 0) ? 1f : -1f;
                    dy = 0f;
                    d = 1f;
                }
                float push = (SEPARATION - d) / 2f;
                a.nudge(-dx / d * push, -dy / d * push);
                b.nudge(dx / d * push, dy / d * push);
            }
        }
    }

    private int hungryGeese() {
        int count = 0;
        for (Goose goose : geese) {
            if (goose.isHungry()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Speeds every goose up for a while; used by the hindering hidden event (FR_EVENT_EFFECT).
     * The time counts down only while the game runs, so it stops during a pause.
     *
     * @param multiplier how much faster the geese walk (1.3 means 30% faster)
     * @param seconds    how long it lasts
     */
    public void speedUp(float multiplier, float seconds) {
        if (multiplier <= 0f || seconds <= 0f) {
            return;
        }
        speedMultiplier = multiplier;
        speedUpLeft = seconds;
    }

    /**
     * Finds the hungry goose the player clicked on, allowing a few pixels of error.
     *
     * @param x world x of the click
     * @param y world y of the click
     * @return the nearest hungry goose there, or null
     */
    @Override
    public Feedable gooseAt(float x, float y) {
        Goose best = null;
        double bestDistance = Double.MAX_VALUE;
        for (Goose goose : geese) {
            if (!goose.isHungry()) {
                continue;
            }
            Rectangle b = goose.getBounds();
            boolean hit = x >= b.x - CLICK_TOLERANCE && x <= b.x + b.width + CLICK_TOLERANCE
                && y >= b.y - CLICK_TOLERANCE && y <= b.y + b.height + CLICK_TOLERANCE;
            double distance = Math.hypot(goose.getCenterX() - x, goose.getCenterY() - y);
            if (hit && distance < bestDistance) {
                best = goose;
                bestDistance = distance;
            }
        }
        return best;
    }

    /**
     * Finds every hungry goose whose centre is within a radius.
     *
     * @param x      world x of the centre
     * @param y      world y of the centre
     * @param radius distance from the centre
     * @return the hungry geese in range, nearest first (empty if none)
     */
    @Override
    public List<Goose> geeseNear(float x, float y, float radius) {
        List<Goose> near = new ArrayList<>();
        for (Goose goose : geese) {
            if (goose.isHungry() && Math.hypot(goose.getCenterX() - x, goose.getCenterY() - y) <= radius) {
                near.add(goose);
            }
        }
        near.sort((a, b) -> Double.compare(Math.hypot(a.getCenterX() - x, a.getCenterY() - y),
            Math.hypot(b.getCenterX() - x, b.getCenterY() - y)));
        return near;
    }

    /**
     * Tells whether a space overlaps a hungry goose, so the player cannot walk through geese.
     *
     * @param bounds the space to check
     * @return true if a hungry goose is there
     */
    @Override
    public boolean collidesWith(Rectangle bounds) {
        for (Goose goose : geese) {
            if (goose.isHungry() && goose.getBounds().overlaps(bounds)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int geeseLeft() {
        return hungryGeese() + toSpawn.size();
    }

    @Override
    public int wavesLeft() {
        return waves.size() - 1 - waveIndex;
    }

    @Override
    public int currentWave() {
        return waveIndex + 1;
    }

    @Override
    public int totalWaves() {
        return waves.size();
    }

    @Override
    public float secondsToNextWave() {
        if (nextWaveIn > 0f) {
            return nextWaveIn;
        }
        if (waveIndex >= 0 && waveIndex < waves.size() - 1) {
            return Math.max(0f, WAVE_COUNTDOWN_SECONDS - waveTime);
        }
        return -1f;
    }

    @Override
    public boolean allWavesCleared() {
        return waveIndex == waves.size() - 1 && toSpawn.isEmpty() && hungryGeese() == 0;
    }

    /**
     * Returns the geese on the map, including those flying away, for drawing.
     *
     * @return the geese; the list cannot be changed
     */
    public List<Goose> getGeese() {
        return Collections.unmodifiableList(geese);
    }

    /**
     * Returns the seconds left of a hidden event's speed-up.
     *
     * @return seconds left, 0 when the geese walk at normal speed
     */
    public float getSpeedUpLeft() {
        return speedUpLeft;
    }

    /**
     * Returns how many snatches have reached the player.
     *
     * @return the number of snatches
     */
    public int getSnatches() {
        return snatches;
    }
}
