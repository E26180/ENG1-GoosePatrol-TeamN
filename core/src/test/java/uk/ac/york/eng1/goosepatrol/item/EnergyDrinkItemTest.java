package uk.ac.york.eng1.goosepatrol.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the energy drink speed boost.
 */
class EnergyDrinkItemTest {

    /** A fake player that records the boosts it receives. */
    private static class FakePlayer implements SpeedBoostTarget {
        int boosts = 0;
        float lastMultiplier = 0f;
        float lastSeconds = 0f;

        @Override
        public void applySpeedBoost(float multiplier, float seconds) {
            boosts++;
            lastMultiplier = multiplier;
            lastSeconds = seconds;
        }
    }

    @Test
    void defaultIsThreeDrinks() {
        EnergyDrinkItem drink = new EnergyDrinkItem(new FakePlayer());
        assertEquals(3, drink.getUsesLeft());
    }

    @Test
    void usingADrinkBoostsThePlayer() {
        FakePlayer player = new FakePlayer();
        EnergyDrinkItem drink = new EnergyDrinkItem(player);
        drink.use(0f, 0f);
        assertEquals(1, player.boosts);
        assertEquals(EnergyDrinkItem.SPEED_MULTIPLIER, player.lastMultiplier);
        assertEquals(EnergyDrinkItem.BOOST_SECONDS, player.lastSeconds);
        assertEquals(2, drink.getUsesLeft());
    }

    @Test
    void secondDrinkHasToWaitForTheCooldown() {
        FakePlayer player = new FakePlayer();
        EnergyDrinkItem drink = new EnergyDrinkItem(player);
        drink.use(0f, 0f);
        drink.use(0f, 0f);
        assertEquals(1, player.boosts);
        assertFalse(drink.canUse());
        drink.update(EnergyDrinkItem.COOLDOWN_SECONDS);
        assertTrue(drink.canUse());
    }

    @Test
    void cooldownIsLongerThanTheBoostSoBoostsDoNotStack() {
        assertTrue(EnergyDrinkItem.COOLDOWN_SECONDS > EnergyDrinkItem.BOOST_SECONDS);
    }

    @Test
    void boostIsFasterButNotExtreme() {
        assertTrue(EnergyDrinkItem.SPEED_MULTIPLIER > 1f);
        assertTrue(EnergyDrinkItem.SPEED_MULTIPLIER <= 2f);
    }
}
