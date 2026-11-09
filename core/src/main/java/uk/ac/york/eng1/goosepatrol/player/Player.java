package uk.ac.york.eng1.goosepatrol.player;

import com.badlogic.gdx.math.Rectangle;

import uk.ac.york.eng1.goosepatrol.item.SpeedBoostTarget;

/**
 * The student the user controls (FR_AVATAR). It walks in the direction the keys give
 * (FR_CONTROLS), stops at buildings, obstacles and geese (QR_COLLISION) and at the edge of
 * the map (QR_OUT_OF_BOUNDS), and can be sped up by an Energy Drink.
 * Reading the keys and drawing are done by {@code GameScreen}, so this class can be unit tested.
 */
public class Player implements SpeedBoostTarget {

    /** Walking speed, in world units (pixels) per second. */
    public static final float BASE_SPEED = 150f;

    /** Width of the player's collision box. */
    public static final float WIDTH = 20f;

    /** Height of the player's collision box. */
    public static final float HEIGHT = 26f;

    /** Longest single step, in world units, so a slow frame cannot jump through a thin wall. */
    private static final float MAX_STEP = 4f;

    /** Collision box; its x and y are the bottom-left corner. */
    private final Rectangle bounds;

    /** The playable area; the player can never leave it. */
    private final Rectangle world;

    /** Walls and buildings: never walked through. */
    private final CollisionChecker walls;

    /** Things that move, such as hungry geese: they block the player, but if one moves onto the player they can walk out. */
    private final CollisionChecker movers;

    /** Current speed multiplier (1 when not boosted). */
    private float speedMultiplier = 1f;

    /** Seconds left of the current speed boost. */
    private float boostLeft = 0f;

    /** Whether the player moved in the last update, for the walking animation. */
    private boolean moving = false;

    /** Whether the player last walked to the left, so the sprite can face that way. */
    private boolean facingLeft = false;

    /**
     * Creates the player.
     *
     * @param x         world x of the bottom-left corner
     * @param y         world y of the bottom-left corner
     * @param world     the playable area
     * @param walls     what the player cannot walk through, such as buildings
     */
    public Player(float x, float y, Rectangle world, CollisionChecker walls) {
        this(x, y, world, walls, bounds -> false);
    }

    /**
     * Creates the player with moving obstacles as well as walls.
     *
     * @param x      world x of the bottom-left corner
     * @param y      world y of the bottom-left corner
     * @param world  the playable area
     * @param walls  what the player can never walk through, such as buildings
     * @param movers moving obstacles, such as hungry geese: they block the player too, but if
     *               one moves onto the player, the player can walk out of it (never through a wall)
     */
    public Player(float x, float y, Rectangle world, CollisionChecker walls, CollisionChecker movers) {
        this.bounds = new Rectangle(x, y, WIDTH, HEIGHT);
        this.world = new Rectangle(world);
        this.walls = walls;
        this.movers = movers;
    }

    /**
     * Finds the free place nearest to a wished start point, searching outwards in
     * rings, so the player never starts inside a building whatever the map looks like.
     *
     * @param world     the playable area
     * @param obstacles what the player cannot stand on
     * @param x         wished world x of the bottom-left corner
     * @param y         wished world y of the bottom-left corner
     * @return {x, y} of a free place, or the wished point if the whole area is blocked
     */
    public static float[] findFreeSpot(Rectangle world, CollisionChecker obstacles, float x, float y) {
        Rectangle test = new Rectangle(0f, 0f, WIDTH, HEIGHT);
        float step = 8f;
        int maxRing = (int) (Math.max(world.width, world.height) / step);
        for (int ring = 0; ring <= maxRing; ring++) {
            for (int i = -ring; i <= ring; i++) {
                for (int j = -ring; j <= ring; j++) {
                    if (Math.max(Math.abs(i), Math.abs(j)) != ring) {
                        continue; // only the edge of this ring
                    }
                    test.setPosition(x + i * step, y + j * step);
                    boolean inside = world.contains(test);
                    if (inside && !obstacles.collidesWith(test)) {
                        return new float[] {test.x, test.y};
                    }
                }
            }
        }
        return new float[] {x, y};
    }

    /**
     * Moves the player for one frame and counts the speed boost down.
     * The two axes are moved one at a time, so the player slides along a wall
     * instead of sticking to it.
     *
     * @param delta seconds since the last frame
     * @param dirX  -1 for left, 1 for right, 0 for no horizontal movement
     * @param dirY  -1 for down, 1 for up, 0 for no vertical movement
     */
    public void update(float delta, float dirX, float dirY) {
        if (boostLeft > 0f) {
            boostLeft = Math.max(0f, boostLeft - delta);
            if (boostLeft == 0f) {
                speedMultiplier = 1f;
            }
        }

        float length = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        moving = length > 0f && delta > 0f;
        if (!moving) {
            return;
        }
        if (dirX != 0f) {
            facingLeft = dirX < 0f;
        }
        // Diagonal movement is as fast as straight movement.
        float distance = getSpeed() * delta;
        int steps = Math.max(1, (int) Math.ceil(distance / MAX_STEP));
        float step = distance / length / steps;
        for (int i = 0; i < steps; i++) {
            moveAxis(dirX * step, 0f);
            moveAxis(0f, dirY * step);
        }
    }

    /**
     * Tries one step along one axis, and undoes it if the new place is blocked. A wall always
     * blocks. A moving obstacle blocks too, unless the player was already overlapping one
     * (a goose pushed onto them), so they can walk out of it.
     */
    private void moveAxis(float dx, float dy) {
        if (dx == 0f && dy == 0f) {
            return;
        }
        float oldX = bounds.x;
        float oldY = bounds.y;
        boolean wasInMover = movers.collidesWith(bounds);
        bounds.x = clamp(bounds.x + dx, world.x, world.x + world.width - bounds.width);
        bounds.y = clamp(bounds.y + dy, world.y, world.y + world.height - bounds.height);
        if (walls.collidesWith(bounds) || (!wasInMover && movers.collidesWith(bounds))) {
            bounds.x = oldX;
            bounds.y = oldY;
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Speeds the player up. A new boost replaces the old one instead of adding to it.
     *
     * @param multiplier how much faster the player moves (1.5 means 50% faster)
     * @param seconds    how long the boost lasts
     */
    @Override
    public void applySpeedBoost(float multiplier, float seconds) {
        if (multiplier <= 0f || seconds <= 0f) {
            return;
        }
        speedMultiplier = multiplier;
        boostLeft = seconds;
    }

    /**
     * Returns the current walking speed, including any boost.
     *
     * @return speed in world units per second
     */
    public float getSpeed() {
        return BASE_SPEED * speedMultiplier;
    }

    /**
     * Returns the seconds left of the speed boost.
     *
     * @return seconds left, 0 when not boosted
     */
    public float getBoostLeft() {
        return boostLeft;
    }

    /**
     * Returns the collision box. Do not change it; use {@link #update} to move.
     *
     * @return the bounds, bottom-left corner first
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Returns the world x of the player's centre.
     *
     * @return centre x
     */
    public float getCenterX() {
        return bounds.x + bounds.width / 2f;
    }

    /**
     * Returns the world y of the player's centre.
     *
     * @return centre y
     */
    public float getCenterY() {
        return bounds.y + bounds.height / 2f;
    }

    /**
     * Returns the player's centre as {x, y}, the form the hidden events expect.
     *
     * @return a new array with the centre
     */
    public float[] getPosition() {
        return new float[] {getCenterX(), getCenterY()};
    }

    /**
     * Tells whether the player moved in the last update.
     *
     * @return true if walking
     */
    public boolean isMoving() {
        return moving;
    }

    /**
     * Tells whether the player faces left.
     *
     * @return true if the last horizontal step was to the left
     */
    public boolean isFacingLeft() {
        return facingLeft;
    }
}
