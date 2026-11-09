package uk.ac.york.eng1.goosepatrol;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;


public class CollisionManager {
    private final Array<Rectangle> collisionAreas = new Array<>();
    //Constructor that receives the loaded Tiled map.
    public CollisionManager(TiledMap map){
        //Searches the map's layers and retrieves the related layer.
        MapLayer layer = map.getLayers().get("collision");
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
    //Receives the player's bounding rectangle and check if it intersects with any collision rectangle
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
    //Provides access to the stored collision.
    public Array<Rectangle>getCollisionAreas(){
        return collisionAreas;
    }
}
