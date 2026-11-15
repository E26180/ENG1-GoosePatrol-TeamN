package uk.ac.york.eng1.goosepatrol.geese;

import java.util.List;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

public class GooseWaveManager
{

    private List<Geese> geese;
    private int waveCount;
    private SpriteBatch batch;
    private Texture gooseTexture;

    public GooseWaveManager(List<Geese> geese, SpriteBatch batch, Texture gooseTexture)
    {
        this.geese = geese;
        this.batch = batch;
        this.gooseTexture = gooseTexture;
        this.waveCount = 0;
    }

    public void NewWave() {

        waveCount++;

        int numberOfGeese = 3 + 2 * waveCount;

        for (int i = 0; i < numberOfGeese; i++)
        {

            Geese newGoose = new Geese();
            newGoose.setBatch(batch);
            newGoose.setTexture(gooseTexture);
            geese.add(newGoose);
        }
    }

    public int getWaveCount()
    {
        return waveCount;
    }

    public List<Geese> getGeese()
    {
        return geese;
    }

    public void removeFlownGeese()
    {
        geese.removeIf(Geese::getFlownAway);
    }


}
