package uk.ac.york.eng1.goosepatrol;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * Loads the campus map made in Tiled (FR_CAMPUS_MAP), draws it around the camera,
 * and owns the {@link CollisionManager} that knows where the walls are.
 */
public class MapManager {

    /** The map file made in Tiled. */
    public static final String MAP_FILE = "maps/Campus-Goose Patrol.tmx";

    /** Width and height of the world area the camera shows, in world units (pixels). */
    private static final float VIEW_WIDTH = 1600f;
    private static final float VIEW_HEIGHT = 1280f;

    private final TiledMap map;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final OrthographicCamera camera;
    private final CollisionManager collisionManager;

    /**
     * Extra space drawn around the screen, as big as the largest tile. Buildings are single
     * tiles much bigger than one map cell, and the renderer skips a tile whose cell is off
     * screen, so without this margin a building vanished while part of it was still visible.
     */
    private final float drawMargin;

    /** Loads the map file, creates the renderer and camera, and reads the collision layer. */
    public MapManager() {
        map = new TmxMapLoader().load(MAP_FILE);
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        // y points up, as in the rest of the game
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);
        camera.update();

        collisionManager = new CollisionManager(map);
        drawMargin = largestTileSize();
    }

    /**
     * Finds the width or height of the largest tile image in the map's tilesets.
     *
     * @return the largest side, in pixels
     */
    private float largestTileSize() {
        float largest = 0f;
        for (TiledMapTileSet tileSet : map.getTileSets()) {
            for (TiledMapTile tile : tileSet) {
                largest = Math.max(largest, tile.getTextureRegion().getRegionWidth());
                largest = Math.max(largest, tile.getTextureRegion().getRegionHeight());
            }
        }
        return largest;
    }

    /** Updates the camera, assigns it to the map renderer and renders the map onto the screen. */
    public void render() {
        camera.update();
        float viewWidth = camera.viewportWidth * camera.zoom;
        float viewHeight = camera.viewportHeight * camera.zoom;
        mapRenderer.setView(camera.combined,
            camera.position.x - viewWidth / 2f - drawMargin,
            camera.position.y - viewHeight / 2f - drawMargin,
            viewWidth + 2f * drawMargin,
            viewHeight + 2f * drawMargin);
        mapRenderer.render();
    }

    /**
     * Returns the camera, so other classes can move it and draw with it.
     *
     * @return the map camera
     */
    public OrthographicCamera getCamera() {
        return camera;
    }

    /**
     * Returns the width of the whole map, in world units (pixels).
     *
     * @return map width
     */
    public float getMapWidth() {
        return map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class);
    }

    /**
     * Returns the height of the whole map, in world units (pixels).
     *
     * @return map height
     */
    public float getMapHeight() {
        return map.getProperties().get("height", Integer.class) * map.getProperties().get("tileheight", Integer.class);
    }

    /**
     * Returns the collision areas of the map.
     *
     * @return the collision manager
     */
    public CollisionManager getCollisionManager() {
        return collisionManager;
    }

    /** Releases the resources when they are no longer needed. */
    public void dispose() {
        mapRenderer.dispose();
        map.dispose();
    }
}
