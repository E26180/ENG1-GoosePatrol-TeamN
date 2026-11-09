package uk.ac.york.eng1.goosepatrol.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.Test;

import uk.ac.york.eng1.goosepatrol.item.EnergyDrinkItem;

/** Tests for {@link Player}: movement, walls, map edge and speed boost (FR_CONTROLS, QR_COLLISION, QR_OUT_OF_BOUNDS). */
class PlayerTest {

    private static final Rectangle WORLD = new Rectangle(0f, 0f, 1000f, 1000f);
    private static final CollisionChecker NOTHING = bounds -> false;

    @Test
    void walksAtBaseSpeed() {
        Player player = new Player(100f, 100f, WORLD, NOTHING);
        player.update(1f, 1f, 0f);
        assertEquals(100f + Player.BASE_SPEED, player.getBounds().x, 0.001f);
        assertEquals(100f, player.getBounds().y, 0.001f);
        assertTrue(player.isMoving());
    }

    @Test
    void diagonalIsNotFaster() {
        Player player = new Player(100f, 100f, WORLD, NOTHING);
        player.update(1f, 1f, 1f);
        float dx = player.getBounds().x - 100f;
        float dy = player.getBounds().y - 100f;
        assertEquals(Player.BASE_SPEED, (float) Math.sqrt(dx * dx + dy * dy), 0.01f);
    }

    @Test
    void standsStillWithNoKeys() {
        Player player = new Player(100f, 100f, WORLD, NOTHING);
        player.update(1f, 0f, 0f);
        assertEquals(100f, player.getBounds().x);
        assertFalse(player.isMoving());
    }

    @Test
    void cannotLeaveTheMap() {
        Player player = new Player(5f, 5f, WORLD, NOTHING);
        player.update(1f, -1f, -1f);
        assertEquals(0f, player.getBounds().x);
        assertEquals(0f, player.getBounds().y);
        Player other = new Player(990f, 990f, WORLD, NOTHING);
        other.update(1f, 1f, 1f);
        assertEquals(WORLD.width - Player.WIDTH, other.getBounds().x, 0.001f);
        assertEquals(WORLD.height - Player.HEIGHT, other.getBounds().y, 0.001f);
    }

    @Test
    void stopsAtAWallEvenAfterASlowFrame() {
        Rectangle wall = new Rectangle(200f, 0f, 50f, 1000f);
        Player player = new Player(100f, 100f, WORLD, wall::overlaps);
        player.update(1f, 1f, 0f);
        float right = player.getBounds().x + Player.WIDTH;
        assertTrue(right <= wall.x, "does not go into or through the wall");
        assertTrue(right > wall.x - 5f, "walks right up to it");
    }

    @Test
    void slidesAlongAWallWhenWalkingDiagonally() {
        Rectangle wall = new Rectangle(200f, 0f, 50f, 1000f);
        Player player = new Player(100f, 100f, WORLD, wall::overlaps);
        player.update(1f, 1f, 1f);
        assertTrue(player.getBounds().x + Player.WIDTH <= wall.x);
        assertTrue(player.getBounds().y > 150f, "keeps moving up along the wall");
    }

    @Test
    void energyDrinkBoostsSpeedForAWhile() {
        Player player = new Player(100f, 100f, WORLD, NOTHING);
        new EnergyDrinkItem(player).use(0f, 0f);
        assertEquals(Player.BASE_SPEED * EnergyDrinkItem.SPEED_MULTIPLIER, player.getSpeed(), 0.001f);
        player.update(EnergyDrinkItem.BOOST_SECONDS, 0f, 0f);
        assertEquals(Player.BASE_SPEED, player.getSpeed(), 0.001f);
        assertEquals(0f, player.getBoostLeft());
    }

    @Test
    void ignoresInvalidBoosts() {
        Player player = new Player(100f, 100f, WORLD, NOTHING);
        player.applySpeedBoost(0f, 5f);
        player.applySpeedBoost(2f, -1f);
        assertEquals(Player.BASE_SPEED, player.getSpeed(), 0.001f);
    }

    @Test
    void facesTheWayItLastWalked() {
        Player player = new Player(100f, 100f, WORLD, NOTHING);
        player.update(0.1f, -1f, 0f);
        assertTrue(player.isFacingLeft());
        player.update(0.1f, 0f, 1f);
        assertTrue(player.isFacingLeft(), "walking up keeps the old facing");
        player.update(0.1f, 1f, 0f);
        assertFalse(player.isFacingLeft());
    }

    @Test
    void findFreeSpotSkipsBuildings() {
        Rectangle building = new Rectangle(400f, 400f, 200f, 200f);
        float[] spot = Player.findFreeSpot(WORLD, building::overlaps, 500f, 500f);
        Rectangle placed = new Rectangle(spot[0], spot[1], Player.WIDTH, Player.HEIGHT);
        assertFalse(building.overlaps(placed));
        assertTrue(WORLD.contains(placed));
    }

    @Test
    void findFreeSpotKeepsAFreeWish() {
        float[] spot = Player.findFreeSpot(WORLD, NOTHING, 300f, 200f);
        assertEquals(300f, spot[0]);
        assertEquals(200f, spot[1]);
    }
}
