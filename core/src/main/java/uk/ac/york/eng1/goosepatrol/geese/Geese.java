package uk.ac.york.eng1.goosepatrol.geese;

import com.badlogic.gdx.audio.Sound;
import uk.ac.york.eng1.goosepatrol.item.Feedable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.math.Rectangle;
import uk.ac.york.eng1.goosepatrol.CollisionManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import java.util.List;


/**
 * Base class for geese
 */

public class Geese implements Feedable
{
    private float GooseX;
    private float GooseY;
    private float Fullness = 0;
    private final float DefaultGooseSpeed = 50;
    private float GooseSpeed = DefaultGooseSpeed;
    private float GooseWidth = 28f;
    private float GooseHeight = 32f;
    private final float MaxFullness = 12;
    private boolean hasFlownAway = false;
    private SpriteBatch batch;
    private Texture texture;

    /**
     * Places the goose on clear ground away from the player.
     *
     * @param collisionManager the map collision checker
     * @param player the current player
     * @param geese the current geese
     */
    public void placeSpawn(
        CollisionManager collisionManager, PolygonSprite player, List<Geese> geese) {
        for (int attempt = 0; attempt < 100; attempt++) {
            // Spawn in the central area south of the main building.
            GooseX = 220f + (float) Math.random() * (1180f - GooseWidth);
            GooseY = 380f + (float) Math.random() * (220f - GooseHeight);

            Rectangle spawnBounds = getBoundingRectangle();

            if (!collisionManager.collidesWith(spawnBounds) && !spawnBounds.overlaps(player.getBoundingRectangle()) && !overlapsGoose(spawnBounds, geese)) {
                return;
            }
        }

        throw new IllegalStateException("No clear goose spawn position found");
    }

    public Geese() {
        int bound = (int) (Math.random() * 2);
        int side = (int) (Math.random() * 2);

        switch (bound) {
            case 0:
                this.GooseX = side == 0 ? 10f : 1590f;
                this.GooseY = (int) (Math.random() * 1260) + 11;
                break;
            case 1:
                this.GooseX = (int) (Math.random() * 1580) + 11;
                this.GooseY = side == 0 ? 10f : 1270;
        } // switch ends here

        // Keep the complete goose inside the map when it spawns.
        GooseX = Math.max(0f, Math.min(GooseX, 1600f - GooseWidth));
        GooseY = Math.max(0f, Math.min(GooseY, 1280f - GooseHeight));
    } // constructor ends here

    protected void playSound(String path, float vol) {
        Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
        sound.play(1.0f);
        sound.dispose();
    }

    public void SetGooseSpeed(float imp)
    {
        GooseSpeed *= imp;
    }

    public void ResetGooseSpeed()
    {
        playSound("audio/honk.wav", 1.0f);
        GooseSpeed = DefaultGooseSpeed;
    }

    public void GooseFlyAway()
    {
        this.hasFlownAway = true;
        this.Fullness = this.MaxFullness;
        playSound("audio/flap.wav", 1.0f);
    }

    @Override
    public float getX()
    {
        return GooseX;
    }

    @Override
    public float getY()
    {
        return GooseY;
    }

    /**
     * Returns the goose's collision bounds in map coordinates.
     *
     * @return the goose's bounding rectangle
     */
    public Rectangle getBoundingRectangle() {
        return new Rectangle(GooseX, GooseY, GooseWidth, GooseHeight);
    }

    /**
     * Checks whether bounds overlap another active goose.
     *
     * @param bounds the proposed collision bounds
     * @param geese the current geese
     * @return whether another goose blocks the position
     */
    private boolean overlapsGoose(Rectangle bounds, List<Geese> geese) {
        for (Geese other : geese) {
            if (other != this && !other.getFlownAway()
                && bounds.overlaps(other.getBoundingRectangle())) {
                return true;
            }
        }
        return false;
    }

    /**
     * How full the goose is, used by the HUD feed bar.
     * @return a value from 0 (empty) to 1 (full)
     */
    @Override
    public float getFeedLevel()
    {
        return Fullness / MaxFullness;
    }

    @Override
    public boolean isHungry()
    {
        return this.Fullness < this.MaxFullness;
    }

    @Override
    public void feed()
    {

        playSound("audio/eat.wav", 0.6f);

        Fullness += 4;
        if (Fullness >= MaxFullness)
        {
            GooseFlyAway();
        }
    }

    public void GooseMove(PolygonSprite player, CollisionManager collisionManager, List<Geese> geese)
    {
        if (this.hasFlownAway) {
            return;
        }
        float playerX = player.getX() + player.getWidth() / 2;
        float playerY = player.getY() + player.getHeight() / 2;
        float dx = playerX - (GooseX + GooseWidth / 2);
        float dy = playerY - (GooseY + GooseHeight / 2);
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance == 0)
        {
            return;
        }
        dx /= distance;
        dy /= distance;
        float movement = GooseSpeed * Gdx.graphics.getDeltaTime();
        float moveX = dx * movement;
        float moveY = dy * movement;
        Rectangle gooseBounds = new Rectangle(GooseX, GooseY, GooseWidth, GooseHeight);
        gooseBounds.setPosition(GooseX + moveX, GooseY);
        if (!collisionManager.collidesWith(gooseBounds) && !gooseBounds.overlaps(player.getBoundingRectangle()) && !overlapsGoose(gooseBounds, geese))
        {
            GooseX += moveX;
        }
        gooseBounds.setPosition(GooseX, GooseY + moveY);
        if (!collisionManager.collidesWith(gooseBounds) && !gooseBounds.overlaps(player.getBoundingRectangle()) && !overlapsGoose(gooseBounds, geese))
        {
            GooseY += moveY;
        }
    }

    public boolean getFlownAway() {
        return this.hasFlownAway;
    }

    public SpriteBatch getBatch()
    {
        return batch;
    }

    public void setBatch(SpriteBatch batch)
    {
        this.batch = batch;
    }

    public void setTexture(Texture texture)
    {
        this.texture = texture;
    }

    public void draw()
    {
        if (batch == null || texture == null || hasFlownAway)
        {
            return;
        }
        batch.draw(texture, GooseX, GooseY, GooseWidth, GooseHeight);
    }



}
