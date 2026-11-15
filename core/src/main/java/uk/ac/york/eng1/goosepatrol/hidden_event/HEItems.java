package uk.ac.york.eng1.goosepatrol.hidden_event;

import uk.ac.york.eng1.goosepatrol.item.Bread;
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.Oats;


/**
 * Source code for the positive hidden event
 *      This fills up the bread and oats in the inventory by 10 and 5 respectively
 *      Extends HiddenEvent class which contains get methods, activate() and player distance
 *          methods
 */

public class HEItems extends HiddenEvent {

    /**
     * Constructor that sets the event activation coordinates
     *
     * @param posX X-coordinate of event
     * @param posY Y-coordinate of event
     */

    public HEItems(float posX, float posY) {
        super(posX, posY);
    }

    /**
     * Method that gets run upon event activation
     *
     * @param bag Current player inventory
     */
    public void trigger(Inventory bag) {

        playSound();

        //Adds items to the bag
        bag.addItem(new Bread(5, 15));
        bag.addItem(new Oats(10, 30));

        //Runs the activation method in the parent class
        activate();
    }
}
