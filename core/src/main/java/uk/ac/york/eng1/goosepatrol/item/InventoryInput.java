package uk.ac.york.eng1.goosepatrol.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Lets the player pick an inventory item with the number keys 1 to 9.
 * Call {@link #update()} once per frame.
 */
public class InventoryInput {

    private final Inventory inventory;

    /**
     * Creates the input handler.
     *
     * @param inventory the inventory to control
     */
    public InventoryInput(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Turns a key code into a slot number.
     *
     * @param keycode a libGDX key code
     * @return 1 to 9 for the keys NUM_1 to NUM_9, or 0 for any other key
     */
    public static int slotForKey(int keycode) {
        if (keycode >= Input.Keys.NUM_1 && keycode <= Input.Keys.NUM_9) {
            return keycode - Input.Keys.NUM_1 + 1;
        }
        return 0;
    }

    /** Checks the number keys and selects the matching item. */
    public void update() {
        for (int key = Input.Keys.NUM_1; key <= Input.Keys.NUM_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                inventory.select(slotForKey(key));
            }
        }
    }
}
