package uk.ac.york.eng1.goosepatrol.geese;

import java.util.List;

public class GooseWaveManager
{

    private List<Geese> geese;
    private int waveCount;

    public GooseWaveManager(List<Geese> geese)
    {
        this.geese = geese;
        waveCount = 0;
    }

    public void NewWave() {

        waveCount++;

        int numberOfGeese = 3 + 2 * waveCount;

        for (int i = 0; i < numberOfGeese; i++)
        {

            Geese newGoose = new Geese(this);

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

    public void removeFlownGeese() {
        geese.removeIf(Geese::getFlownAway);
    }
}
