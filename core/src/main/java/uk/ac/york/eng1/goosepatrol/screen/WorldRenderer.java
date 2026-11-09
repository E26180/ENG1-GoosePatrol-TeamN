package uk.ac.york.eng1.goosepatrol.screen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

import uk.ac.york.eng1.goosepatrol.game.EventSpot;
import uk.ac.york.eng1.goosepatrol.goose.Goose;
import uk.ac.york.eng1.goosepatrol.hidden_event.IHiddenEvent;
import uk.ac.york.eng1.goosepatrol.player.Player;

/**
 * Draws everything that sits on the map, in world units: the hidden-event boxes, the player,
 * the geese with their hunger bars, the "HONK!" warning (QR_ALERT), "Yum!" after a feed,
 * where food landed, and arrows at the edge of the screen towards geese out of sight.
 * Every cue uses a shape or text as well as colour (QR_ACCESS_CUES).
 */
public class WorldRenderer implements Disposable {

    /** Seconds per frame of the walking animations. */
    private static final float WALK_FRAME_SECONDS = 0.18f;

    /** How long the ring where food landed stays visible. */
    private static final float SPLASH_SECONDS = 0.5f;

    private static final Color OUTLINE = new Color(0.1f, 0.1f, 0.1f, 1f);
    private static final Color BAR_EMPTY = new Color(0.25f, 0.25f, 0.25f, 0.9f);
    private static final Color BAR_FULL = new Color(0.98f, 0.78f, 0.2f, 1f);
    private static final Color BOX = new Color(0.62f, 0.42f, 0.22f, 1f);
    private static final Color ARROW = new Color(1f, 0.62f, 0.15f, 1f);

    private final SpriteBatch batch = new SpriteBatch();
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final BitmapFont font = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();
    private final Texture playerSheet = new Texture("textures/player/student.png");
    private final Texture gooseSheet = new Texture("textures/geese/goose.png");
    private final TextureRegion[] playerFrames;
    private final TextureRegion[] gooseFrames;
    private final List<float[]> splashes = new ArrayList<>();
    private float time = 0f;

    /** Loads the sprites and the font. */
    public WorldRenderer() {
        playerFrames = TextureRegion.split(playerSheet, 32, 32)[0];
        gooseFrames = TextureRegion.split(gooseSheet, 32, 32)[0];
        font.getData().setScale(0.7f);
        font.setUseIntegerPositions(false);
    }

    /**
     * Shows a fading ring where food landed, so the player sees what their click did.
     *
     * @param x      world x of the centre
     * @param y      world y of the centre
     * @param radius radius of the ring
     */
    public void addSplash(float x, float y, float radius) {
        splashes.add(new float[] {x, y, radius, 0f});
    }

    /**
     * Moves the animations on. Called only while the game runs, so they stop during a pause.
     *
     * @param delta seconds since the last frame
     */
    public void update(float delta) {
        time += delta;
        Iterator<float[]> it = splashes.iterator();
        while (it.hasNext()) {
            float[] s = it.next();
            s[3] += delta;
            if (s[3] >= SPLASH_SECONDS) {
                it.remove();
            }
        }
    }

    /**
     * Draws the world on top of the map.
     *
     * @param camera   the map camera
     * @param player   the player
     * @param walkTime how long the player has been walking, for the animation
     * @param geese    the geese on the map
     * @param events   the hidden events
     */
    public void render(OrthographicCamera camera, Player player, float walkTime, List<Goose> geese, List<EventSpot> events) {
        shapes.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        drawEventBoxes(player, events);
        drawSplashes();
        drawSprites(player, walkTime, geese);
        drawGooseInfo(geese);
        drawOffScreenArrows(camera, geese);
    }

    /** A wooden box with a "?" for every event not found yet; it bobs when the player is near. */
    private void drawEventBoxes(Player player, List<EventSpot> events) {
        beginShapes(ShapeRenderer.ShapeType.Filled);
        for (EventSpot spot : events) {
            if (spot.isTriggered()) {
                continue;
            }
            float[] p = boxPosition(spot, player);
            shapes.setColor(OUTLINE);
            shapes.rect(p[0] - 15f, p[1] - 13f, 30f, 26f);
            shapes.setColor(BOX);
            shapes.rect(p[0] - 14f, p[1] - 12f, 28f, 24f);
            shapes.setColor(OUTLINE);
            shapes.rect(p[0] - 14f, p[1] - 1f, 28f, 2f);
        }
        shapes.end();
        batch.begin();
        for (EventSpot spot : events) {
            if (spot.isTriggered()) {
                continue;
            }
            float[] p = boxPosition(spot, player);
            font.getData().setScale(1.1f);
            drawCentred("?", p[0], p[1] + 8f, Color.WHITE, 1f);
            font.getData().setScale(0.7f);
        }
        batch.end();
    }

    private float[] boxPosition(EventSpot spot, Player player) {
        IHiddenEvent event = spot.getEvent();
        float[] pos = event.getPos();
        boolean near = event.isPlayerInFlagRange(player.getPosition());
        float bob = near ? (float) Math.sin(time * 8f) * 3f : 0f;
        return new float[] {pos[0], pos[1] + bob};
    }

    private void drawSplashes() {
        beginShapes(ShapeRenderer.ShapeType.Line);
        for (float[] s : splashes) {
            float progress = s[3] / SPLASH_SECONDS;
            shapes.setColor(1f, 0.9f, 0.5f, 1f - progress);
            shapes.circle(s[0], s[1], s[2] * (0.6f + 0.4f * progress), 32);
        }
        shapes.end();
    }

    /** Draws the player and the geese, back to front, so lower sprites overlap higher ones. */
    private void drawSprites(Player player, float walkTime, List<Goose> geese) {
        List<Goose> sorted = new ArrayList<>(geese);
        sorted.sort((a, b) -> Float.compare(b.getBounds().y, a.getBounds().y));
        batch.begin();
        boolean playerDrawn = false;
        for (Goose goose : sorted) {
            if (!playerDrawn && goose.getBounds().y < player.getBounds().y) {
                drawPlayer(player, walkTime);
                playerDrawn = true;
            }
            drawGoose(goose);
        }
        if (!playerDrawn) {
            drawPlayer(player, walkTime);
        }
        batch.setColor(Color.WHITE);
        batch.end();
    }

    private void drawPlayer(Player player, float walkTime) {
        TextureRegion frame = playerFrames[(int) (walkTime / WALK_FRAME_SECONDS) % playerFrames.length];
        float x = player.getCenterX() - 16f;
        float y = player.getBounds().y - 3f;
        boolean startled = player.getSpeed() < Player.BASE_SPEED;
        // a startled player shakes a little
        if (startled) {
            x += (float) Math.sin(time * 40f);
        }
        batch.setColor(Color.WHITE);
        if (player.isFacingLeft()) {
            batch.draw(frame, x + 32f, y, -32f, 32f);
        } else {
            batch.draw(frame, x, y, 32f, 32f);
        }
    }

    private void drawGoose(Goose goose) {
        float scale = goose.getType().getScale();
        TextureRegion frame;
        if (goose.isFlapping()) {
            frame = gooseFrames[2];
        } else if (goose.isAlerting()) {
            frame = gooseFrames[3];
        } else if (goose.isMoving()) {
            frame = gooseFrames[(int) (time / WALK_FRAME_SECONDS) % 2];
        } else {
            frame = gooseFrames[0];
        }
        boolean flash = goose.isAlerting() && ((int) (time * 8f)) % 2 == 0;
        if (flash) {
            batch.setColor(1f, 0.35f, 0.35f, goose.getAlpha());
        } else {
            batch.setColor(1f, 1f, 1f, goose.getAlpha());
        }
        float size = 32f * scale;
        float x = goose.getCenterX() - size / 2f;
        float y = goose.getBounds().y - 3f * scale;
        if (goose.isFacingLeft()) {
            batch.draw(frame, x + size, y, -size, size);
        } else {
            batch.draw(frame, x, y, size, size);
        }
    }

    /** Hunger bars, the warning bubble, "Yum!" and the head goose's name. */
    private void drawGooseInfo(List<Goose> geese) {
        beginShapes(ShapeRenderer.ShapeType.Filled);
        for (Goose goose : geese) {
            if (!goose.isHungry()) {
                continue;
            }
            float scale = goose.getType().getScale();
            float w = 28f * scale;
            float x = goose.getCenterX() - w / 2f;
            float y = spriteTop(goose) + 2f;
            shapes.setColor(OUTLINE);
            shapes.rect(x - 1f, y - 1f, w + 2f, 6f);
            shapes.setColor(BAR_EMPTY);
            shapes.rect(x, y, w, 4f);
            shapes.setColor(BAR_FULL);
            shapes.rect(x, y, w * goose.getFill(), 4f);
            if (goose.isAlerting()) {
                bubble(goose.getCenterX(), y + 10f, 46f, 16f, Color.WHITE);
            } else if (goose.wasJustFed()) {
                bubble(goose.getCenterX(), y + 10f, 34f, 16f, Color.WHITE);
            }
        }
        for (Goose goose : geese) {
            if (goose.getState() == Goose.State.LEAVING && goose.getAlpha() > 0.4f) {
                bubble(goose.getCenterX(), spriteTop(goose) + 4f, 34f, 16f, Color.WHITE);
            }
        }
        shapes.end();

        batch.begin();
        for (Goose goose : geese) {
            float top = spriteTop(goose);
            if (goose.isAlerting()) {
                drawCentred("HONK!", goose.getCenterX(), top + 24f, Color.RED, 1f);
                drawCentred("!", goose.getCenterX() + 30f, top + 24f, Color.RED, 1f);
            } else if (goose.isHungry() && goose.wasJustFed()) {
                drawCentred("Yum!", goose.getCenterX(), top + 24f, Color.BLACK, 1f);
            } else if (goose.getState() == Goose.State.LEAVING && goose.getAlpha() > 0.4f) {
                drawCentred("Full!", goose.getCenterX(), top + 16f, Color.BLACK, 1f);
            }
            if (goose.isHungry() && goose.getType().getAppetite() > 1f) {
                drawCentred(goose.getType().getDisplayName(), goose.getCenterX(), goose.getBounds().y - 6f, Color.WHITE, 1f);
            }
        }
        batch.end();
    }

    private float spriteTop(Goose goose) {
        float scale = goose.getType().getScale();
        return goose.getBounds().y - 3f * scale + 32f * scale;
    }

    private void bubble(float cx, float y, float w, float h, Color fill) {
        shapes.setColor(OUTLINE);
        shapes.rect(cx - w / 2f - 1f, y - 1f, w + 2f, h + 2f);
        shapes.triangle(cx - 4f, y, cx + 4f, y, cx, y - 5f);
        shapes.setColor(fill);
        shapes.rect(cx - w / 2f, y, w, h);
    }

    /** Arrows at the edge of the view pointing to hungry geese that are out of sight. */
    private void drawOffScreenArrows(OrthographicCamera camera, List<Goose> geese) {
        float halfW = camera.viewportWidth * camera.zoom / 2f;
        float halfH = camera.viewportHeight * camera.zoom / 2f;
        Rectangle view = new Rectangle(camera.position.x - halfW, camera.position.y - halfH, halfW * 2f, halfH * 2f);
        float inset = 14f;
        beginShapes(ShapeRenderer.ShapeType.Filled);
        for (Goose goose : geese) {
            if (!goose.isHungry() || view.contains(goose.getCenterX(), goose.getCenterY())) {
                continue;
            }
            float dx = goose.getCenterX() - camera.position.x;
            float dy = goose.getCenterY() - camera.position.y;
            float t = Math.min((halfW - inset) / Math.max(Math.abs(dx), 0.001f), (halfH - inset) / Math.max(Math.abs(dy), 0.001f));
            float ax = camera.position.x + dx * t;
            float ay = camera.position.y + dy * t;
            float len = (float) Math.sqrt(dx * dx + dy * dy);
            float ux = dx / len;
            float uy = dy / len;
            arrow(ax, ay, ux, uy, 11f, OUTLINE);
            arrow(ax, ay, ux, uy, 8f, ARROW);
        }
        shapes.end();
    }

    private void arrow(float x, float y, float ux, float uy, float size, Color colour) {
        shapes.setColor(colour);
        float tipX = x + ux * size;
        float tipY = y + uy * size;
        float baseX = x - ux * size * 0.6f;
        float baseY = y - uy * size * 0.6f;
        shapes.triangle(tipX, tipY, baseX - uy * size * 0.7f, baseY + ux * size * 0.7f, baseX + uy * size * 0.7f, baseY - ux * size * 0.7f);
    }

    private void drawCentred(String text, float cx, float y, Color colour, float alpha) {
        font.setColor(colour.r, colour.g, colour.b, alpha);
        layout.setText(font, text);
        font.draw(batch, layout, cx - layout.width / 2f, y);
    }

    private void beginShapes(ShapeRenderer.ShapeType type) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.begin(type);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
        playerSheet.dispose();
        gooseSheet.dispose();
    }
}
