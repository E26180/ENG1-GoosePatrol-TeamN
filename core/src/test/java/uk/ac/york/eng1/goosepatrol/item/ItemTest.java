package uk.ac.york.eng1.goosepatrol.item;

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
        boolean effectWorks = true;

        CountingItem(int uses, float cooldownSeconds) {
            super("Test item", uses, cooldownSeconds);
        }

        @Override
        protected boolean applyEffect(float x, float y) {
            effectCount++;
            return effectWorks;
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
        item.use(0f, 0f);
        assertEquals(2, item.getUsesLeft());
        assertEquals(1, item.effectCount);
    }

    @Test
    void itemCannotBeUsedWhenNoUsesAreLeft() {
        CountingItem item = new CountingItem(1, 0f);
        item.use(0f, 0f);
        assertFalse(item.canUse());
        item.use(0f, 0f);
        assertEquals(0, item.getUsesLeft());
        assertEquals(1, item.effectCount);
    }

    @Test
    void cooldownBlocksUseUntilItHasRunOut() {
        CountingItem item = new CountingItem(5, 10f);
        item.use(0f, 0f);
        assertFalse(item.canUse());
        item.update(9f);
        assertFalse(item.canUse());
        item.update(1f);
        assertTrue(item.canUse());
    }

    @Test
    void cooldownNeverGoesBelowZero() {
        CountingItem item = new CountingItem(5, 2f);
        item.use(0f, 0f);
        item.update(100f);
        assertEquals(0f, item.getCooldownLeft());
    }

    @Test
    void useDuringCooldownDoesNothing() {
        CountingItem item = new CountingItem(5, 10f);
        item.use(0f, 0f);
        item.use(0f, 0f);
        assertEquals(4, item.getUsesLeft());
        assertEquals(1, item.effectCount);
    }

    @Test
    void useThatFindsNothingToActOnIsNotSpent() {
        CountingItem item = new CountingItem(3, 5f);
        item.effectWorks = false;
        assertFalse(item.use(0f, 0f));
        assertEquals(3, item.getUsesLeft());
        assertTrue(item.canUse());
    }

    @Test
    void addUsesGivesMoreUses() {
        CountingItem item = new CountingItem(3, 0f);
        item.addUses(10);
        assertEquals(13, item.getUsesLeft());
        assertEquals(13, item.getMaxUses());
    }

    @Test
    void addUsesIgnoresZeroAndNegativeNumbers() {
        CountingItem item = new CountingItem(3, 0f);
        item.addUses(0);
        item.addUses(-5);
        assertEquals(3, item.getUsesLeft());
    }

    @Test
    void oatsStartFullAndHaveNoCooldown() {
        FakeGeese world = new FakeGeese();
        world.add(0f, 0f);
        OatsItem oats = new OatsItem(world);
        assertEquals(OatsItem.START_USES, oats.getUsesLeft());
        oats.use(0f, 0f);
        assertTrue(oats.canUse());
    }

    @Test
    void oatsFillHalfOfTheClickedGoosesHunger() {
        FakeGeese world = new FakeGeese();
        FakeGeese.FakeGoose clicked = world.add(100f, 100f);
        FakeGeese.FakeGoose other = world.add(500f, 500f);
        new OatsItem(world).use(102f, 99f);
        assertEquals(OatsItem.HUNGER_FILLED, clicked.hunger);
        assertEquals(0f, other.hunger);
    }

    @Test
    void oatsClickThatMissesEveryGooseIsNotSpent() {
        FakeGeese world = new FakeGeese();
        world.add(100f, 100f);
        OatsItem oats = new OatsItem(world);
        assertFalse(oats.use(300f, 300f));
        assertEquals(OatsItem.START_USES, oats.getUsesLeft());
    }

    @Test
    void breadFeedsEveryGooseInTheRadiusButNotThoseOutside() {
        FakeGeese world = new FakeGeese();
        FakeGeese.FakeGoose near1 = world.add(100f, 100f);
        FakeGeese.FakeGoose near2 = world.add(110f, 115f);
        FakeGeese.FakeGoose far = world.add(100f + BreadItem.RADIUS_PIXELS + 50f, 100f);
        BreadItem bread = new BreadItem(world);
        assertTrue(bread.use(100f, 100f));
        assertEquals(BreadItem.HUNGER_FILLED, near1.hunger);
        assertEquals(BreadItem.HUNGER_FILLED, near2.hunger);
        assertEquals(0f, far.hunger);
    }

    @Test
    void breadHasAShortCooldownAndItsStartingUses() {
        BreadItem bread = new BreadItem(new FakeGeese());
        assertEquals(BreadItem.START_USES, bread.getUsesLeft());
        bread.use(0f, 0f);
        assertFalse(bread.canUse());
        bread.update(BreadItem.COOLDOWN_SECONDS);
        assertTrue(bread.canUse());
    }

    @Test
    void aHiddenEventCanGiveTenMoreBread() {
        BreadItem bread = new BreadItem(new FakeGeese());
        bread.addUses(10);
        assertEquals(BreadItem.START_USES + 10, bread.getUsesLeft());
    }

    @Test
    void breadFeedsAtMostTenGeeseAtOnce() {
        FakeGeese world = new FakeGeese();
        for (int i = 0; i < 13; i++) {
            world.add(100f, 100f);
        }
        new BreadItem(world).use(100f, 100f);
        long fed = world.geese.stream().filter(g -> g.hunger > 0f).count();
        assertEquals(BreadItem.MAX_GEESE, fed, "QR_AVOID_EXTREME: at most 10 geese per throw");
    }

    @Test
    void everyItemNamesAnIconThatExists() {
        FakeGeese world = new FakeGeese();
        Item[] items = {new OatsItem(world), new BreadItem(world), new EnergyDrinkItem((m, s) -> { })};
        for (Item item : items) {
            assertTrue(new java.io.File("../assets/" + item.getIconPath()).isFile(), item.getName() + " icon");
        }
    }

    @Test
    void foodItemsShowARingAndTheDrinkShowsAMessage() {
        FakeGeese world = new FakeGeese();
        assertTrue(new OatsItem(world).isFood());
        assertEquals(BreadItem.RADIUS_PIXELS, new BreadItem(world).getSplashRadius());
        Item drink = new EnergyDrinkItem((m, s) -> { });
        assertFalse(drink.isFood());
        assertTrue(drink.getUseMessage().contains("faster"));
    }
}
