package uk.ac.york.eng1.goosepatrol.item;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests feeding behaviour, item uses and inventory selection.
 */
class InventoryTest {

    /**
     * Provides a feeding target without game or graphics dependencies.
     */
    private static class Goose implements Feedable {
        private final float x;
        private final float y;
        private boolean hungry = true;

        /**
         * Creates a hungry test goose.
         *
         * @param x world x position
         * @param y world y position
         */
        Goose(float x, float y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Returns the horizontal position.
         *
         * @return world x position
         */
        @Override
        public float getX() {
            return this.x;
        }

        /**
         * Returns the vertical position.
         *
         * @return world y position
         */
        @Override
        public float getY() {
            return this.y;
        }

        /**
         * Checks whether the goose needs food.
         *
         * @return true if the goose is hungry
         */
        @Override
        public boolean isHungry() {
            return this.hungry;
        }

        /**
         * Satisfies the goose's hunger.
         */
        @Override
        public void feed() {
            this.hungry = false;
        }
    }

    /**
     * Checks that an exhausted item cannot feed another hungry target.
     */
    @Test
    void emptyItemCannotFeed() {
        Goose first = new Goose(0, 0);
        Goose next = new Goose(5, 0);
        List<Goose> geese = List.of(first, next);
        Oats oats = new Oats(1, 10);

        assertTrue(oats.use(0, 0, geese));
        assertFalse(first.isHungry());
        assertEquals(0, oats.getUses());
        assertFalse(oats.canUse());

        assertFalse(oats.use(5, 0, geese));
        assertTrue(next.isHungry());
        assertEquals(0, oats.getUses());
    }

    /**
     * Checks group feeding, the radius boundary and one use per action.
     */
    @Test
    void breadFeedsGroupOnce() {
        Goose first = new Goose(0, 0);
        Goose edge = new Goose(60, 80);
        Goose outside = new Goose(101, 0);
        Inventory bag = new Inventory(List.of(first, edge, outside));

        assertTrue(bag.select(1));
        int uses = bag.getSelected().getUses();
        assertTrue(bag.use(0, 0));

        assertFalse(first.isHungry());
        assertFalse(edge.isHungry());
        assertTrue(outside.isHungry());
        assertEquals(uses - 1, bag.getSelected().getUses());
        assertEquals(1, bag.getUsed());

        // Satisfied targets must not consume another use.
        assertFalse(bag.use(0, 0));
        assertEquals(uses - 1, bag.getSelected().getUses());
        assertEquals(1, bag.getUsed());
    }

    /**
     * Checks that invalid slots preserve the current selection.
     */
    @Test
    void invalidSlotKeepsSelection() {
        Inventory bag = new Inventory(List.of());
        assertEquals(2, bag.getItems().size());
        assertEquals("Oats", bag.getSelected().getName());
        assertTrue(bag.select(1));
        Item selected = bag.getSelected();

        assertFalse(bag.select(-1));
        assertSame(selected, bag.getSelected());
        assertFalse(bag.select(bag.getItems().size()));
        assertSame(selected, bag.getSelected());
    }

    /**
     * Checks that a missed target preserves uses and the usage counter.
     */
    @Test
    void missKeepsUses() {
        Goose goose = new Goose(500, 500);
        Inventory bag = new Inventory(List.of(goose));
        int uses = bag.getSelected().getUses();

        assertFalse(bag.use(0, 0));
        assertTrue(goose.isHungry());
        assertEquals(uses, bag.getSelected().getUses());
        assertEquals(0, bag.getUsed());
    }

    /**
     * Checks that oats chooses the nearest hungry target only.
     */
    @Test
    void oatsFeedsNearest() {
        Goose far = new Goose(8, 0);
        Goose near = new Goose(2, 0);
        Oats oats = new Oats(2, 10);

        assertTrue(oats.use(0, 0, List.of(far, near)));
        assertFalse(near.isHungry());
        assertTrue(far.isHungry());
        assertEquals(1, oats.getUses());
    }
}
