package uk.ac.york.eng1.goosepatrol.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Stores items, the selected slot and successful item uses.
 */
public class Inventory {

    private final List<Item> items;
    private final List<? extends Feedable> geese;

    private int slot;
    private int used;

    /**
     * Creates a starting inventory containing oats and bread.
     *
     * @param geese live list of feeding targets
     * @throws NullPointerException if geese is null
     */
    public Inventory(List<? extends Feedable> geese) {
        this.geese = Objects.requireNonNull(
            geese, "Geese list cannot be null"
        );

        this.items = new ArrayList<>();
        this.slot = 0;
        this.used = 0;

        // Starting quantities and distances can be adjusted here.
        this.items.add(new Oats(10, 32));
        this.items.add(new Bread(5, 100));
    }

    /**
     * Adds an item or combines its uses with an item of the same class.
     *
     * @param item item to add
     * @throws NullPointerException if item is null
     */
    public void addItem(Item item) {
        Objects.requireNonNull(item, "Item cannot be null");

        for (Item stored : this.items) {
            if (stored.getClass() == item.getClass()) {
                stored.addUses(item.getUses());
                return;
            }
        }

        this.items.add(item);
    }

    /**
     * Returns the inventory items.
     *
     * @return an unmodifiable view of the items
     */
    public List<Item> getItems() {
        return Collections.unmodifiableList(this.items);
    }

    /**
     * Returns the selected item.
     *
     * @return selected item
     */
    public Item getSelected() {
        return this.items.get(this.slot);
    }

    /**
     * Selects an inventory slot.
     * Invalid slots leave the current selection unchanged.
     *
     * @param slot requested slot, counting from zero
     * @return true if the slot exists
     */
    public boolean select(int slot) {
        if (slot < 0 || slot >= this.items.size()) {
            return false;
        }

        this.slot = slot;
        return true;
    }

    /**
     * Uses the selected item at a world position.
     *
     * @param x world x position
     * @param y world y position
     * @return true if the selected item was successfully used
     */
    public boolean use(float x, float y) {
        Item item = this.getSelected();

        if (!item.use(x, y, this.geese)) {
            return false;
        }

        // One successful action counts once, including group feeding.
        this.used++;
        return true;
    }

    /**
     * Returns the total number of successful item uses.
     *
     * @return item usage counter
     */
    public int getUsed() {
        return this.used;
    }
}
