package uk.ac.york.eng1.goosepatrol.geese;

import uk.ac.york.eng1.goosepatrol.item.Feedable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.math.Rectangle;
import java.util.Random;
import uk.ac.york.eng1.goosepatrol.CollisionManager;

/**
 * Base class for geese
 */

public class Geese implements Feedable
{
    private float GooseX;
    private float GooseY;
    private float Fullness = 0;
    private float GooseSpeed = 5;
    private final float DefaultGooseSpeed = 5;
    private float GooseWidth = 1f;
    private float GooseHeight = 1f;
    private final float MaxFullness = 10;
    private boolean hasFlownAway = false;

    private GooseWaveManager waveManager;

    public Geese(GooseWaveManager manager)
    {
        waveManager = manager;
    }

    public void SetGooseSpeed(float imp)
    {
        GooseSpeed *= imp;
    }

    public void ResetGooseSpeed() { GooseSpeed = DefaultGooseSpeed; }

    public void GooseFlyAway()
    {
        this.hasFlownAway = true;
        this.Fullness = this.MaxFullness;
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
        Fullness += 2.5;
        if (Fullness >= MaxFullness)
        {
            GooseFlyAway();
        }
    }

    public void GooseMove(PolygonSprite player, CollisionManager collisionManager)
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
        if (!collisionManager.collidesWith(gooseBounds))
        {
            GooseX += moveX;
        }
        gooseBounds.setPosition(GooseX, GooseY + moveY);
        if (!collisionManager.collidesWith(gooseBounds))
        {
            GooseY += moveY;
        }
    }

    public boolean getFlownAway() {
        return this.hasFlownAway;
    }
}
