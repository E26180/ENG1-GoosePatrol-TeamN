package uk.ac.york.eng1.goosepatrol.mechanics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Player extends PolygonSprite {

    Texture playerTexture;
    TextureRegion playerRectangle;
    PolygonRegion playerRegion;
    private float x = 10, y = 10;
    private float width, height;
    private float scaleX = 1f, scaleY = 1f;
    private float rotation;
    private float originX, originY;
    private float[] vertices;
    private short[] traingles;
    private boolean dirty;
    private Rectangle bounds = new Rectangle();
    private final Color color = new Color(1f, 1f, 1f, 1f);
    PolygonSpriteBatch playerBatch = new PolygonSpriteBatch(100);


    public Player(PolygonRegion region) {
        super(region);
    }

    public Player(PolygonSprite sprite) {
        super(sprite);
    }

    @Override
    public void set(PolygonSprite sprite) {
        super.set(sprite);
    }

    public void draw() {}


}
