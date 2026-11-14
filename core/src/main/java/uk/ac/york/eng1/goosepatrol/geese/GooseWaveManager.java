package uk.ac.york.eng1.goosepatrol.geese;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import uk.ac.york.eng1.goosepatrol.CollisionManager;

public class GooseWaveManager
{

    private List<Geese> geese;
    private int waveCount;
    private final int IntialGeese = 5;

    public GooseWaveManager()
    {
        geese = new ArrayList<>();
        waveCount = 0;
    }

    public void NewWave()
    {
        waveCount++;
        int GeeseSwarm = IntialGeese * waveCount;
        for (int i = 0; i < GeeseSwarm; i++)
        {
            Geese newGoose = new Geese(this);
            geese.add(newGoose);
        }
    }

    public void updateGeese(PolygonSprite player, CollisionManager collisionManager)
    {
        for (Geese goose : geese)
        {
            goose.GooseMove(player, collisionManager);
        }
    }

    public void removeGoose(Geese goose)
    {
        geese.remove(goose);
    }

    public List<Geese> getGeese()
    {
        return geese;
    }

    public int getWaveCount()
    {
        return waveCount;
    }
}
