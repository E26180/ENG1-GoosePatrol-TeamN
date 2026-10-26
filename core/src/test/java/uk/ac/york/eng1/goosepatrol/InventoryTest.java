package uk.ac.york.eng1.goosepatrol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.Input;
import org.junit.jupiter.api.Test;

/**
 * Tests for the inventory: selection by number, using items, counter and text.
 */
class InventoryTest {

    private Inventory twoItems() {
        Inventory inventory = new Inventory();
        inventory.addItem(new OatsItem());
        inventory.addItem(new BreadPicnicItem());
        return inventory;
    }

    @Test
    void firstItemIsSelectedAtTheStart() {
        assertEquals(1, twoItems().getSelectedNumber());
    }

    @Test
    void selectingByNumberChangesTheSelectedItem() {
        Inventory inventory = twoItems();
        assertTrue(inventory.select(2));
        assertEquals(2, inventory.getSelectedNumber());
    }

    @Test
    void numbersWithoutAnItemAreIgnored() {
        Inventory inventory = twoItems();
        assertFalse(inventory.select(3));
        assertFalse(inventory.select(0));
        assertFalse(inventory.select(-1));
        assertEquals(1, inventory.getSelectedNumber());
    }

    @Test
    void usingTheSelectedItemSpendsOneUseAndCountsIt() {
        Inventory inventory = twoItems();
        assertTrue(inventory.useSelected(10f, 20f));
        assertEquals(9, inventory.getItems().get(0).getUsesLeft());
        assertEquals(1, inventory.getItemsUsed());
    }

    @Test
    void failedUseIsNotCounted() {
        Inventory inventory = twoItems();
        inventory.select(2);
        assertTrue(inventory.useSelected(0f, 0f));
        assertFalse(inventory.useSelected(0f, 0f));
        assertEquals(1, inventory.getItemsUsed());
    }

    @Test
    void emptyInventoryCannotUseAnything() {
        Inventory inventory = new Inventory();
        assertFalse(inventory.useSelected(0f, 0f));
        assertEquals(0, inventory.getItemsUsed());
    }

    @Test
    void updateCountsCooldownsDown() {
        Inventory inventory = twoItems();
        inventory.select(2);
        inventory.useSelected(0f, 0f);
        inventory.update(20f);
        assertTrue(inventory.useSelected(0f, 0f));
        assertEquals(2, inventory.getItemsUsed());
    }

    @Test
    void inventoryHoldsAtMostNineItems() {
        Inventory inventory = new Inventory();
        for (int i = 0; i < Inventory.MAX_SLOTS; i++) {
            assertTrue(inventory.addItem(new OatsItem()));
        }
        assertFalse(inventory.addItem(new OatsItem()));
        assertEquals(9, inventory.getItems().size());
    }

    @Test
    void slotTextShowsNumberNameAndUses() {
        Inventory inventory = twoItems();
        assertEquals("1: Bag of Oats 10/10", inventory.slotText(1));
        inventory.useSelected(0f, 0f);
        assertEquals("1: Bag of Oats 9/10", inventory.slotText(1));
    }

    @Test
    void slotTextShowsTheWaitTimeDuringACooldown() {
        Inventory inventory = twoItems();
        inventory.select(2);
        inventory.useSelected(0f, 0f);
        assertEquals("2: Bread Picnic 4/5 (wait 20s)", inventory.slotText(2));
        inventory.update(8.5f);
        assertEquals("2: Bread Picnic 4/5 (wait 12s)", inventory.slotText(2));
    }

    @Test
    void slotTextIsEmptyForMissingSlots() {
        assertEquals("", twoItems().slotText(5));
    }

    @Test
    void numberKeysMapToSlotNumbers() {
        assertEquals(1, InventoryInput.slotForKey(Input.Keys.NUM_1));
        assertEquals(9, InventoryInput.slotForKey(Input.Keys.NUM_9));
        assertEquals(0, InventoryInput.slotForKey(Input.Keys.A));
    }
}
