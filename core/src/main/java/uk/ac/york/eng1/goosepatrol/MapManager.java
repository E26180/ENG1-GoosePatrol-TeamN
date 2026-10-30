package uk.ac.york.eng1.goosepatrol;

//Import the needed classes for map loading, camera control and map rendering.
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
//The purpose of ManagerMap is handling the loading, rendering and camera management of the map.
public class MapManager {
    //map stores the map data, tiles, layers and objects loaded from the TMX file.
    private TiledMap map;
    //mapRenderer creates and handles the rendering process
    private OrthogonalTiledMapRenderer mapRenderer;
    //camera controls the visible part of the map.
    private OrthographicCamera camera;
    //collisionManager is for detecting collision between the player and objects.
    private CollisionManager collisionManager;


    public MapManager(){
        //Uses libGDX for loading TMX file and covert it to a TiledMap object.
        map = new TmxMapLoader().load("maps/Campus-Goose Patrol.tmx");
        //Creates a renderer for the loaded orthogonal tile map.
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        //Creates a camera that can view the game world without perspective distortion.
        camera = new OrthographicCamera();
        //Configures the camera to view world-unit area.
        camera.setToOrtho(false, 1600, 1280);
        //Updates the internal matrices of the camera.
        camera.update();

        collisionManager = new CollisionManager(map);
    }
//Updates the camera, assigns it to the map renderer and renders the map onto the screen.
    public void render(){
        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();
    }
    //Returns the camera objects so other classes are able to access it.
    public OrthographicCamera getCamera(){
        return camera;
    }
    //Returns the CollisionManager so other classes can access it.
    public CollisionManager getCollisionManager(){
        return collisionManager;
    }
    //Releases the resources when they are no longer needed.
    public void dispose(){
        mapRenderer.dispose();
        map.dispose();
    }


}
