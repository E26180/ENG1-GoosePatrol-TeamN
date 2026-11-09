package uk.ac.york.eng1.goosepatrol;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;


/**
 * Holds the solid areas of the map (buildings and obstacles), read from the map's
 * "Collision" object layer, and tells whether a rectangle overlaps any of them (QR_COLLISION).
 */
public class CollisionManager {
    private final Array<Rectangle> collisionAreas = new Array<>();
    /**
     * Reads the collision rectangles from the loaded Tiled map.
     *
     * @param map the loaded map
     */
    public CollisionManager(TiledMap map){
        //Searches the map's layers and retrieves the related layer.
        MapLayer layer = map.getLayers().get("Collision");
        //Checks whether the collision layer exists
        if(layer == null){
            //Displays a warning message.
            System.out.println("Warning: collision layer not found.");
            return;
        }
        //Iterates every object stored in the collision layer.
        for(MapObject object: layer.getObjects()){
            //Checks whether the current map object is a rectangle.
            if(object instanceof RectangleMapObject){
                //Retrieves the position of a rectangle.
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                //Adds newly created rectangle to the array.
                collisionAreas.add(new Rectangle(rectangle));
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
    public boolean collidesWith(Rectangle playerBounds){
        //Check every rectangle in the array one by one.
        for(Rectangle area:collisionAreas){
            /*Uses LibGDX for determining whether the player's rectangle
             intersects with the current collision rectangle*/
            if(area.overlaps(playerBounds)){
                // if an overlap is detected, returns true.
                return true;
            }
        }
        //if there is no overlap, returns false
        return false;
    }
    /**
     * Returns a copy of the stored collision areas.
     *
     * @return the rectangles read from the map
     */
    public Array<Rectangle>getCollisionAreas(){
        // a copy, so callers cannot change the map's walls
        return new Array<>(collisionAreas);
    }
}
