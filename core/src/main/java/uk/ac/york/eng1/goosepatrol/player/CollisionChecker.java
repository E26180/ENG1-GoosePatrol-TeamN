package uk.ac.york.eng1.goosepatrol.player;

import com.badlogic.gdx.math.Rectangle;

/**
 * Tells the player whether a space is blocked, for example by a building.
 * The map's {@code CollisionManager::collidesWith} fits this, and tests can pass a lambda.
 */
@FunctionalInterface
public interface CollisionChecker {

    /**
     * Checks a space.
     *
     * @param bounds the space the player would take, in world units
     * @return true if something solid is there
     */
    boolean collidesWith(Rectangle bounds);
}
