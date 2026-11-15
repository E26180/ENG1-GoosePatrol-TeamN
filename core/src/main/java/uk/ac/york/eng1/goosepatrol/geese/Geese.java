package uk.ac.york.eng1.goosepatrol.geese;

import com.badlogic.gdx.audio.Sound;
import uk.ac.york.eng1.goosepatrol.item.Feedable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.math.Rectangle;
import uk.ac.york.eng1.goosepatrol.CollisionManager;

/**
 * Base class for geese
 */

public class Geese implements Feedable
{
    private float GooseX;
    private float GooseY;
    private float Fullness = 0;
    private final float DefaultGooseSpeed = 15;
    private float GooseSpeed = DefaultGooseSpeed;
    private float GooseWidth = 5f;
    private float GooseHeight = 5f;
    private final float MaxFullness = 10;
    private boolean hasFlownAway = false;

    public Geese() {
        int bound = (int) (Math.random() * 2);
        int side = (int) (Math.random() * 2);
        switch (bound) {
            case 0:
                this.GooseX = side == 0 ? 10f : 630f;
                this.GooseY = (int) (Math.random() * 460) + 11;
                break;
            case 1:
                this.GooseX = (int) (Math.random() * 620) + 11;
                this.GooseY = side == 0 ? 10f : 470f;
        }
    }

    protected void playSound(String path) {
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
        playSound("audio/honk.wav");
        GooseSpeed = DefaultGooseSpeed;
    }

    public void GooseFlyAway()
    {
        this.hasFlownAway = true;
        this.Fullness = this.MaxFullness;
        playSound("audio/flap.wav");
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

        playSound("audio/eat.wav");

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
