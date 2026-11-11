package uk.ac.york.eng1.goosepatrol;

import uk.ac.york.eng1.goosepatrol.item.Feedable;
import uk.ac.york.eng1.goosepatrol.item.GooseLocator;
import java.util.List;

/**
 * Base class for geese
 */

public class Geese implements Feedable, GooseLocator
{
    private float GooseX;
    private float GooseY;
    private float GooseHunger;
    private float GooseSpeed;

    public float[] GetGoosePos()
    {
        return new float[] {GooseX, GooseY};
    }

    public float GetGooseHunger()
    {
        return GooseHunger;
    }

    public void SetGooseSpeed(float imp)
    {
        GooseSpeed = imp;
    }

    public void GooseFed()
    {

    }

    public void GooseFlyAway()
    {

    }

    public void GooseDistracted()
    {

    }

    @Override
    public void feed(float amount)
    {
        GooseHunger -= amount;
    }

    @Override
    public Feedable gooseAt(float imp1, float imp2)
    {
        return null;
    }

    @Override
    public List<? extends Feedable> geeseNear(float imp1, float imp2, float imp3)
    {
        return null;
    }


}
