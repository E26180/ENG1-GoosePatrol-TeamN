package uk.ac.york.eng1.goosepatrol;

/**
 * Base class for the geese
 */
public class Geese {
    
    private float GooseSpeed;
    private int GooseHunger;
    private int GooseX;
    private int GooseY;

    public void GooseFed()
    {
        if (GooseHunger < 1)
        {
            GooseFlyAway();
        }
    }

    public void GooseFlyAway()
    {

    }

    public void GooseDistracted()
    {

    }

    public void GooseSlow()
    {

    }

    

}
