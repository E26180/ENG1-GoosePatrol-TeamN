package uk.ac.york.eng1.goosepatrol;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the item logic: uses, cooldown and the effect hook.
 */
class ItemTest {

    /** A tiny item that counts how often its effect was applied. */
    private static class CountingItem extends Item {
        int effectCount = 0;

        CountingItem(int uses, float cooldownSeconds) {
            super("Test item", uses, cooldownSeconds);
        }

        @Override
        protected void applyEffect() {
            effectCount++;
        }
    }

    @Test
    void newItemCanBeUsed() {
        CountingItem item = new CountingItem(3, 0f);
        assertTrue(item.canUse());
        assertEquals(3, item.getUsesLeft());
    }

    @Test
    void useSpendsOneUseAndAppliesTheEffectOnce() {
        CountingItem item = new CountingItem(3, 0f);
        item.use();
        assertEquals(2, item.getUsesLeft());
        assertEquals(1, item.effectCount);
    }

    @Test
    void itemCannotBeUsedWhenNoUsesAreLeft() {
        CountingItem item = new CountingItem(1, 0f);
        item.use();
        assertFalse(item.canUse());
        item.use();
        assertEquals(0, item.getUsesLeft());
        assertEquals(1, item.effectCount);
    }

    @Test
    void cooldownBlocksUseUntilItHasRunOut() {
        CountingItem item = new CountingItem(5, 10f);
        item.use();
        assertFalse(item.canUse());
        item.update(9f);
        assertFalse(item.canUse());
        item.update(1f);
        assertTrue(item.canUse());
    }

    @Test
    void cooldownNeverGoesBelowZero() {
        CountingItem item = new CountingItem(5, 2f);
        item.use();
        item.update(100f);
        assertEquals(0f, item.getCooldownLeft());
    }

    @Test
    void useDuringCooldownDoesNothing() {
        CountingItem item = new CountingItem(5, 10f);
        item.use();
        item.use();
        assertEquals(4, item.getUsesLeft());
        assertEquals(1, item.effectCount);
    }

    @Test
    void oatsHaveTenUsesAndNoCooldown() {
        OatsItem oats = new OatsItem();
        assertEquals(10, oats.getUsesLeft());
        oats.use();
        assertTrue(oats.canUse());
    }

    @Test
    void breadPicnicHasATwentySecondCooldown() {
        BreadPicnicItem picnic = new BreadPicnicItem();
        picnic.use();
        assertFalse(picnic.canUse());
        picnic.update(19f);
        assertFalse(picnic.canUse());
        picnic.update(1f);
        assertTrue(picnic.canUse());
    }
}
