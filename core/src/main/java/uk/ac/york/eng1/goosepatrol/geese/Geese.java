package uk.ac.york.eng1.goosepatrol.geese;

import uk.ac.york.eng1.goosepatrol.item.Feedable;
import java.util.List;

/**
 * Base class for geese
 */

public class Geese implements Feedable
{
    private float GooseX;
    private float GooseY;
    private float GooseHunger;
    private float GooseSpeed;
    private final float MaxGooseHunger = 10;

    public float GetGooseHunger()
    {
        return GooseHunger;
    }

    public void SetGooseSpeed(float imp)
    {
        GooseSpeed = imp;
    }

    public void GooseFlyAway()
    {

    }

    public void GooseDistracted()
    {

    }

    public void feed(float amount)
    {
        GooseHunger -= amount;
    }

    public Feedable gooseAt(float imp1, float imp2)
    {
        return null;
    }

    public List<? extends Feedable> geeseNear(float imp1, float imp2, float imp3)
    {
        return null;
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

    @Override
    public boolean isHungry()
    {
        return GooseHunger != 0;
    }

    @Override
    public void feed()
    {
        GooseHunger += 2.5;
        if (GooseHunger == MaxGooseHunger)
        {
            GooseFlyAway();
        }
    }
}
