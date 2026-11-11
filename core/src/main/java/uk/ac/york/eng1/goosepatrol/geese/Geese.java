package uk.ac.york.eng1.goosepatrol.geese;

import uk.ac.york.eng1.goosepatrol.item.Feedable;

/**
 * Base class for geese
 */

public class Geese implements Feedable
{
    private float GooseX;
    private float GooseY;
    private float GooseHunger;
    private float GooseSpeed = 5;
    private int WaveCount = 1;
    private final float MaxGooseHunger = 10;

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

    public void NewWave()
    {

    }
}
