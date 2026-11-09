package uk.ac.york.eng1.goosepatrol;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Holds the solid areas of the map (buildings and obstacles), read from the map's
 * "Collision" object layer, and tells whether a rectangle overlaps any of them (QR_COLLISION).
 */
public class CollisionManager {

    /** Name of the object layer in the Tiled map that marks the solid areas. */
    public static final String LAYER = "Collision";

    private final Array<Rectangle> collisionAreas = new Array<>();

    /**
     * Reads the collision rectangles from the loaded Tiled map.
     *
     * @param map the loaded map
     */
    public CollisionManager(TiledMap map) {
        MapLayer layer = map.getLayers().get(LAYER);
        if (layer == null) {
            // The game still runs, but with no walls; the warning makes a broken map easy to spot.
            System.out.println("Warning: collision layer not found.");
            return;
        }
        // The walls are drawn in Tiled as rectangles; other shapes on the layer are ignored.
        for (MapObject object : layer.getObjects()) {
            if (object instanceof RectangleMapObject) {
                // our own copy, so later changes to the map object cannot move a wall
                collisionAreas.add(new Rectangle(((RectangleMapObject) object).getRectangle()));
            }
        }
        System.out.println("Collision areas loaded:" + collisionAreas.size);
    }

    /**
     * Checks whether a rectangle, normally the player's bounds, overlaps any solid area.
     *
     * @param playerBounds the rectangle to check, in world units
     * @return true if it overlaps a collision area
     */
    public boolean collidesWith(Rectangle playerBounds) {
        for (Rectangle area : collisionAreas) {
            if (area.overlaps(playerBounds)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a copy of the stored collision areas.
     *
     * @return copies of the rectangles read from the map
     */
    public Array<Rectangle> getCollisionAreas() {
        // copies of the rectangles too, so callers cannot move the map's walls
        Array<Rectangle> copy = new Array<>(collisionAreas.size);
        for (Rectangle area : collisionAreas) {
            copy.add(new Rectangle(area));
        }
        return copy;
    }
}
