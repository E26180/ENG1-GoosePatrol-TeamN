package uk.ac.york.eng1.goosepatrol.hidden_event;

import uk.ac.york.eng1.goosepatrol.item.Bread;
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.Oats;

public class HEItems extends HiddenEvent {

    public HEItems(float posX, float posY) {
        super(posX, posY);
    }


    public void showFlag() {
        //TODO: Setup the flag to show an event is nearby.  Dont show what event or exactly where
    }

    public void trigger(Inventory bag) {
        bag.addItem(new Bread(5, 15));
        bag.addItem(new Oats(10, 30));
        activate();
    }
}
