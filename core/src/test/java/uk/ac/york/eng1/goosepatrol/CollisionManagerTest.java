package uk.ac.york.eng1.goosepatrol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.Test;

class CollisionManagerTest {

    private static CollisionManager oneWall() {
        TiledMap map = new TiledMap();
        MapLayer layer = new MapLayer();
        layer.setName(CollisionManager.LAYER);
        layer.getObjects().add(new RectangleMapObject(100f, 100f, 50f, 50f));
        map.getLayers().add(layer);
        return new CollisionManager(map);
    }

    @Test
    void aRectangleOverTheWallCollides() {
        CollisionManager walls = oneWall();
        assertTrue(walls.collidesWith(new Rectangle(120f, 120f, 10f, 10f)));
        assertFalse(walls.collidesWith(new Rectangle(0f, 0f, 10f, 10f)));
    }

    @Test
    void changingTheReturnedAreasDoesNotMoveTheWalls() {
        CollisionManager walls = oneWall();
        walls.getCollisionAreas().first().setPosition(1000f, 1000f);
        walls.getCollisionAreas().clear();
        assertEquals(1, walls.getCollisionAreas().size);
        assertTrue(walls.collidesWith(new Rectangle(120f, 120f, 10f, 10f)));
    }

    @Test
    void aMapWithoutACollisionLayerHasNoWalls() {
        CollisionManager walls = new CollisionManager(new TiledMap());
        assertFalse(walls.collidesWith(new Rectangle(120f, 120f, 10f, 10f)));
    }
}
