package uk.ac.york.eng1.goosepatrol.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The player's inventory: a numbered list of items, one of which is selected.
 * The player picks an item with the number keys (1 to 9) and uses it where they aim.
 * It also counts how many items have been used, for the on-screen counter.
 */
public class Inventory {

    /** Most items the inventory can hold, so every item has a number key (1 to 9). */
    public static final int MAX_SLOTS = 9;

    /** The items, in slot order. Slot numbers shown to the player start at 1. */
    private final List<Item> items = new ArrayList<>();

    /** Index (starting at 0) of the selected item. */
    private int selected = 0;

    /** How many times an item has been used successfully. */
    private int itemsUsed = 0;

    /**
     * Adds an item in the next free slot.
     *
     * @param item the item to add
     * @return true if it was added, false if the inventory is full
     */
    public boolean addItem(Item item) {
        if (items.size() >= MAX_SLOTS) {
            return false;
        }
        items.add(item);
        return true;
    }

    /**
     * Selects an item by the number the player sees (1 is the first item).
     * Numbers with no item are ignored.
     *
     * @param number slot number, starting at 1
     * @return true if the selection changed to an existing item
     */
    public boolean select(int number) {
        int index = number - 1;
        if (index < 0 || index >= items.size()) {
            return false;
        }
        selected = index;
        return true;
    }

    /**
     * Uses the selected item where the player aimed.
     * Counts the use only if the item was ready.
     *
     * @param x where the player aimed (world x)
     * @param y where the player aimed (world y)
     * @return true if the item was used
     */
    public boolean useSelected(float x, float y) {
        if (items.isEmpty()) {
            return false;
        }
        boolean used = items.get(selected).use(x, y);
        if (used) {
            itemsUsed++;
        }
        return used;
    }

    /**
     * Counts the cooldowns of all items down. Called once per frame.
     *
     * @param delta seconds since the last frame
     */
    public void update(float delta) {
        for (Item item : items) {
            item.update(delta);
        }
    }

    /**
     * Returns the text to show for one slot, e.g. "1: Bag of Oats 7/10" or
     * "2: Bread Picnic 5/5 (wait 12s)". The HUD can draw this text directly.
     *
     * @param number slot number, starting at 1
     * @return the text, or an empty string if the slot has no item
     */
    public String slotText(int number) {
        int index = number - 1;
        if (index < 0 || index >= items.size()) {
            return "";
        }
        Item item = items.get(index);
        String text = number + ": " + item.getName() + " " + item.getUsesLeft() + "/" + item.getMaxUses();
        if (item.getCooldownLeft() > 0f) {
            text += " (wait " + (int) Math.ceil(item.getCooldownLeft()) + "s)";
        }
        return text;
    }

    /**
     * Returns the items, for drawing. The list cannot be changed.
     *
     * @return the items in slot order
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Returns the number of the selected item, starting at 1.
     *
     * @return selected slot number
     */
    public int getSelectedNumber() {
        return selected + 1;
    }

    /**
     * Returns how many items have been used so far (for the counter).
     *
     * @return the number of successful uses
     */
    public int getItemsUsed() {
        return itemsUsed;
    }
}
