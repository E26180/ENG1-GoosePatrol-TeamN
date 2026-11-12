package uk.ac.york.eng1.goosepatrol.hidden_event;

import uk.ac.york.eng1.goosepatrol.geese.Geese;

import java.time.Instant;
import java.util.List;

public class HEGooseSpeed extends HiddenEvent {

    private float activationTime = 0f;
    private List<Geese> existingGeese;

    public HEGooseSpeed(float posX, float posY, List<Geese> geese) {
        super(posX, posY);
    }

    public void showFlag() {
        //TODO: Setup the flag to show an event is nearby.  Dont show which event or exactly where
    }

    public void trigger(List<Geese> geeseList) {
        this.existingGeese = geeseList;
        for (Geese goose : existingGeese) {
            goose.SetGooseSpeed(7.5f);
        }
        this.activationTime = Instant.now().getEpochSecond() + 10;
        activate();
    }

    public boolean checkActive() {
        if (Instant.now().getEpochSecond() > this.activationTime) {
            for (Geese goose : existingGeese) {
                goose.SetGooseSpeed(5f);
            }
            return false;
        } else {
            return true;
        }
    }

}
