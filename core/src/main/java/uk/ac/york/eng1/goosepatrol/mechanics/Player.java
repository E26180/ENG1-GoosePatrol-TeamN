package uk.ac.york.eng1.goosepatrol.mechanics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class Player {




    PolygonSprite playerSprite;
    Texture playerTexture;
    TextureRegion playerRectangle;
    PolygonRegion playerRegion;
    private final int x = 10, y = 10;
    private final int width = 18, height = 32;
    private float rotation = 0;
    private float originX, originY;
    private float[] vertices;
    private short[] triangles;
    private Rectangle bounds = new Rectangle();
    private Color color = new Color(1f, 1f, 1f, 1f);
    PolygonSpriteBatch playerBatch = new PolygonSpriteBatch(100);



    public Player() {
        class buildPolygon {

            static final float[] VERTEX_COORDS = {3, 0, 3, 8, 0, 8, 0, 20, 3, 20, 3, 31, 14, 31,
                14, 20, 15, 20, 15, 8, 14, 8, 14, 3, 15, 3, 15, 0};
            static final short[] TRIANGLE_COORDS = {0, 11, 13, 11, 12, 13, 0, 1, 11, 1, 11, 10, 1, 2, 3,
                1, 3, 4, 1, 4, 9, 4, 8, 9, 4, 5, 7, 5, 6, 7};

            static float[] getVertices() {
                return VERTEX_COORDS;
            }

            static short[] getTriangleCoords() {
                return TRIANGLE_COORDS;
            }
        }
        vertices = buildPolygon.getVertices();
        triangles = buildPolygon.getTriangleCoords();
        playerTexture = new Texture(Gdx.files.internal("textures/player/student.png"));
        playerRectangle = new TextureRegion(playerTexture, x, y, width, height);
        playerRegion = new PolygonRegion(playerRectangle, vertices, triangles);
        playerSprite = new PolygonSprite(playerRegion) {
            @Override
            public void setColor(Color tint) {
                super.setColor(color);
            }
        };
        bounds = playerSprite.getBoundingRectangle();
        rotation = playerSprite.getRotation();
        originX = playerSprite.getOriginX();
        originY = playerSprite.getOriginY();
    }

    public Player(PolygonSprite sprite) {
        playerSprite = sprite;
    }

    public void rotate(float degrees) {
        rotation += degrees;
        playerSprite.rotate(degrees);
    }

    public void setOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
        playerSprite.setOrigin(originX, originY);
    }

    public void setColorRed() {
        color.set(0xff0000ff);
        playerSprite.setColor(color);
    }

    public void setColorBlack() {
        color.set(0,0,0,1);
        playerSprite.setColor(color);
    }

    public PolygonSprite getPlayerSprite() {
        return playerSprite;
    }

    public PolygonRegion getPlayerRegion() {
        return playerRegion;
    }

    public TextureRegion getPlayerRectangle() {
        return playerRectangle;
    }

    public Texture getPlayerTexture() {
        return playerTexture;
    }

    public Rectangle getBoundingRectangle() {
        bounds = playerSprite.getBoundingRectangle();
        return bounds;
    }

    public Color getColor() {
        color = playerSprite.getColor();
        return color;
    }

    public void draw() {
        playerSprite.draw(playerBatch);
    }


}
