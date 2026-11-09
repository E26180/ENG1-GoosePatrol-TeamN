package uk.ac.york.eng1.goosepatrol.goose;

import com.badlogic.gdx.math.Rectangle;

import uk.ac.york.eng1.goosepatrol.item.Feedable;
import uk.ac.york.eng1.goosepatrol.player.CollisionChecker;

/**
 * One hungry goose (FR_ENEMY). It walks towards the player, and when it is close it
 * warns for {@link #WIND_UP_SECONDS} (QR_ALERT) and then tries to snatch a snack. Food fills
 * its hunger bar; when the bar is full it flies away happily and leaves the game.
 * Nobody is ever hurt (UR_NONVIOLENT). Drawing is done by the screen, so this class
 * can be unit tested.
 */
public class Goose implements Feedable {

    /** What the goose is doing. */
    public enum State {
        /** Walking towards the player. */
        APPROACH,
        /** Standing still and honking: the warning before a snatch. */
        WIND_UP,
        /** Backing off after a snatch attempt, then waiting. */
        RECOVER,
        /** Full and flying away; no longer in the game. */
        LEAVING,
        /** Out of sight; the manager removes it. */
        GONE
    }

    /** Width of an ordinary goose's collision box. */
    public static final float WIDTH = 22f;

    /** Height of an ordinary goose's collision box. */
    public static final float HEIGHT = 18f;

    /** Distance between centres at which the goose stops and starts its warning. */
    public static final float REACH = 34f;

    /** How long the warning lasts, so the player has time to step away. */
    public static final float WIND_UP_SECONDS = 1.2f;

    /** The snatch only reaches the player if they are still this close when the warning ends. */
    public static final float SNATCH_RANGE = 52f;

    /** How long the goose backs off and waits after a snatch attempt. */
    public static final float RECOVER_SECONDS = 2.5f;

    /** How long a full goose takes to fly out of sight. */
    public static final float FLY_AWAY_SECONDS = 2f;

    /** Flying speed of a full goose, in world units per second. */
    public static final float FLY_SPEED = 180f;

    /** How long a goose flaps over an obstacle it is stuck behind. */
    public static final float HOP_SECONDS = 0.7f;

    /** How often the goose checks whether it is stuck. */
    private static final float STUCK_CHECK_SECONDS = 0.8f;

    /** Moving less than this share of the intended distance counts as stuck. */
    private static final float STUCK_SHARE = 0.3f;

    /** Longest single step, so a slow frame cannot carry a goose through a thin wall. */
    private static final float MAX_STEP = 4f;

    /** How long the "Yum!" bubble stays after a feed. */
    private static final float FED_FLASH_SECONDS = 0.8f;

    private final GooseType type;
    private final Rectangle bounds;
    private final Rectangle world;
    private final CollisionChecker obstacles;

    private State state = State.APPROACH;
    private float stateTime = 0f;

    /** Share of the hunger bar that is full, from 0 to 1. */
    private float fill = 0f;

    private float hopLeft = 0f;
    private float stuckTimer = 0f;
    private float intendedDistance = 0f;
    private float movedDistance = 0f;
    private float fedFlash = 0f;
    private boolean facingLeft = false;
    private boolean moving = false;
    private float flyX = 0f;
    private float flyY = 1f;
    private boolean leavingAnnounced = false;

    /**
     * Creates a hungry goose.
     *
     * @param type      the kind of goose
     * @param centerX   world x of its centre
     * @param centerY   world y of its centre
     * @param world     the map area; the goose never walks out of it
     * @param obstacles what the goose walks around (it flaps over it when stuck)
     */
    public Goose(GooseType type, float centerX, float centerY, Rectangle world, CollisionChecker obstacles) {
        this.type = type;
        float w = WIDTH * type.getScale();
        float h = HEIGHT * type.getScale();
        this.bounds = new Rectangle(centerX - w / 2f, centerY - h / 2f, w, h);
        this.world = new Rectangle(world);
        this.obstacles = obstacles;
    }

    /**
     * Fills the hunger bar. An ordinary goose is full after a total of 1; a goose with a
     * bigger appetite needs more. A full goose starts flying away. A goose that was warning
     * eats the food instead of snatching, and backs off. Food given to a goose that is
     * already leaving is ignored.
     *
     * @param amount the share of an ordinary goose's bar to fill (0.5 means half)
     */
    @Override
    public void feed(float amount) {
        if (!isHungry() || amount <= 0f) {
            return;
        }
        fill = Math.min(1f, fill + amount / type.getAppetite());
        fedFlash = FED_FLASH_SECONDS;
        if (fill >= 0.999f) {
            fill = 1f;
            startLeaving();
        } else if (state == State.WIND_UP) {
            enter(State.RECOVER);
        }
    }

    private void startLeaving() {
        enter(State.LEAVING);
        // fly up and away, on the side the goose is facing
        flyX = facingLeft ? -0.6f : 0.6f;
        flyY = 0.8f;
    }

    private void enter(State next) {
        state = next;
        stateTime = 0f;
    }

    /**
     * Moves the goose on by one frame.
     *
     * @param delta           seconds since the last frame
     * @param targetX         world x of the player's centre
     * @param targetY         world y of the player's centre
     * @param speedMultiplier 1 normally, more while a hidden event speeds the geese up
     * @return true if a snatch reached the player in this frame
     */
    public boolean update(float delta, float targetX, float targetY, float speedMultiplier) {
        if (delta <= 0f || state == State.GONE) {
            return false;
        }
        stateTime += delta;
        fedFlash = Math.max(0f, fedFlash - delta);
        hopLeft = Math.max(0f, hopLeft - delta);
        moving = false;

        float dx = targetX - getCenterX();
        float dy = targetY - getCenterY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        float speed = type.getSpeed() * speedMultiplier;

        switch (state) {
            case APPROACH:
                if (distance <= REACH) {
                    enter(State.WIND_UP);
                    facingLeft = dx < 0f;
                } else {
                    walk(dx, dy, distance, speed * delta, delta);
                }
                return false;
            case WIND_UP:
                facingLeft = dx < 0f;
                if (stateTime >= WIND_UP_SECONDS) {
                    enter(State.RECOVER);
                    return distance <= SNATCH_RANGE;
                }
                return false;
            case RECOVER:
                if (stateTime < RECOVER_SECONDS / 2f) {
                    walk(-dx, -dy, distance, speed * 0.5f * delta, delta);
                }
                if (stateTime >= RECOVER_SECONDS) {
                    enter(State.APPROACH);
                }
                return false;
            case LEAVING:
                bounds.x += flyX * FLY_SPEED * delta;
                bounds.y += flyY * FLY_SPEED * delta;
                moving = true;
                if (stateTime >= FLY_AWAY_SECONDS) {
                    enter(State.GONE);
                }
                return false;
            default:
                return false;
        }
    }

    /** Walks a distance in a direction, in small steps, and flaps over obstacles when stuck. */
    private void walk(float dirX, float dirY, float length, float distance, float delta) {
        if (length < 0.001f || distance <= 0f) {
            return;
        }
        moving = true;
        if (Math.abs(dirX) > 0.1f * length) {
            facingLeft = dirX < 0f;
        }
        float startX = bounds.x;
        float startY = bounds.y;
        int steps = Math.max(1, (int) Math.ceil(distance / MAX_STEP));
        float step = distance / length / steps;
        for (int i = 0; i < steps; i++) {
            moveAxis(dirX * step, 0f);
            moveAxis(0f, dirY * step);
        }
        float moved = (float) Math.hypot(bounds.x - startX, bounds.y - startY);
        checkStuck(distance, moved, delta);
    }

    /** Counts how far the goose meant to go and how far it got; starts a hop if it is stuck. */
    private void checkStuck(float intended, float moved, float delta) {
        intendedDistance += intended;
        movedDistance += moved;
        stuckTimer += delta;
        if (stuckTimer >= STUCK_CHECK_SECONDS) {
            if (hopLeft <= 0f && movedDistance < STUCK_SHARE * intendedDistance) {
                hopLeft = HOP_SECONDS;
            }
            stuckTimer = 0f;
            intendedDistance = 0f;
            movedDistance = 0f;
        }
    }

    /**
     * Tries one step on one axis; undoes it if it ends in an obstacle, unless the goose is
     * hopping or was already inside one (then any step is allowed, so it can get out).
     */
    private void moveAxis(float dx, float dy) {
        float oldX = bounds.x;
        float oldY = bounds.y;
        boolean wasBlocked = obstacles.collidesWith(bounds);
        bounds.x = clamp(bounds.x + dx, world.x, world.x + world.width - bounds.width);
        bounds.y = clamp(bounds.y + dy, world.y, world.y + world.height - bounds.height);
        if (hopLeft <= 0f && !wasBlocked && obstacles.collidesWith(bounds)) {
            bounds.x = oldX;
            bounds.y = oldY;
        }
        if (hopLeft > 0f && hopLeft < 0.1f && obstacles.collidesWith(bounds)) {
            hopLeft = 0.1f; // never land on a building: keep flapping until clear
        }
    }

    /**
     * Pushes the goose a little, used to keep geese from standing on top of each other.
     * The push is skipped if it would put the goose in an obstacle or off the map.
     *
     * @param dx push along x, in world units
     * @param dy push along y, in world units
     */
    public void nudge(float dx, float dy) {
        if (!isHungry()) {
            return;
        }
        moveAxis(dx, 0f);
        moveAxis(0f, dy);
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Tells whether the goose still wants food. Only hungry geese can be fed, block the
     * player and count towards "geese left".
     *
     * @return true unless it is leaving or gone
     */
    public boolean isHungry() {
        return state == State.APPROACH || state == State.WIND_UP || state == State.RECOVER;
    }

    /**
     * Tells whether the goose is giving its warning before a snatch.
     *
     * @return true while winding up
     */
    public boolean isAlerting() {
        return state == State.WIND_UP;
    }

    /**
     * Returns how far the warning has gone, for drawing.
     *
     * @return 0 at the start of the warning to 1 at the snatch; 0 when not warning
     */
    public float getAlertProgress() {
        return isAlerting() ? Math.min(1f, stateTime / WIND_UP_SECONDS) : 0f;
    }

    /**
     * Tells whether the goose was fed a moment ago, so the screen can show "Yum!".
     *
     * @return true for a short time after each feed
     */
    public boolean wasJustFed() {
        return fedFlash > 0f;
    }

    /**
     * Returns how see-through to draw the goose: it fades out while flying away.
     *
     * @return 1 for solid, 0 for invisible
     */
    public float getAlpha() {
        if (state == State.GONE) {
            return 0f;
        }
        if (state == State.LEAVING) {
            return Math.max(0f, 1f - stateTime / FLY_AWAY_SECONDS);
        }
        return 1f;
    }

    /**
     * Tells whether the goose should be drawn with its wings up.
     *
     * @return true while hopping over an obstacle or flying away
     */
    public boolean isFlapping() {
        return hopLeft > 0f || state == State.LEAVING;
    }

    /**
     * Tells whether the manager has already reported that this goose is full.
     *
     * @return true once reported
     */
    boolean isLeavingAnnounced() {
        return leavingAnnounced;
    }

    /** Records that the manager has reported that this goose is full. */
    void markLeavingAnnounced() {
        leavingAnnounced = true;
    }

    /**
     * Returns the kind of goose.
     *
     * @return the type
     */
    public GooseType getType() {
        return type;
    }

    /**
     * Returns what the goose is doing.
     *
     * @return the state
     */
    public State getState() {
        return state;
    }

    /**
     * Returns how full the hunger bar is.
     *
     * @return 0 (empty) to 1 (full)
     */
    public float getFill() {
        return fill;
    }

    /**
     * Returns the collision box. Do not change it.
     *
     * @return the bounds, bottom-left corner first
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Returns the world x of the goose's centre.
     *
     * @return centre x
     */
    public float getCenterX() {
        return bounds.x + bounds.width / 2f;
    }

    /**
     * Returns the world y of the goose's centre.
     *
     * @return centre y
     */
    public float getCenterY() {
        return bounds.y + bounds.height / 2f;
    }

    /**
     * Tells whether the goose faces left, so the sprite can be flipped.
     *
     * @return true if facing left
     */
    public boolean isFacingLeft() {
        return facingLeft;
    }

    /**
     * Tells whether the goose moved in the last update, for the walking animation.
     *
     * @return true if it moved
     */
    public boolean isMoving() {
        return moving;
    }
}
