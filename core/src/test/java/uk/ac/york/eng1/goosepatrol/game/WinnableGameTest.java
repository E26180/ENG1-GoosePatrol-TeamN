package uk.ac.york.eng1.goosepatrol.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import uk.ac.york.eng1.goosepatrol.goose.Goose;
import uk.ac.york.eng1.goosepatrol.goose.GooseListener;
import uk.ac.york.eng1.goosepatrol.goose.GooseManager;
import uk.ac.york.eng1.goosepatrol.item.BreadItem;
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.OatsItem;
import uk.ac.york.eng1.goosepatrol.player.Player;
import uk.ac.york.eng1.goosepatrol.screen.GameScreen;

/**
 * Plays whole games without graphics to check that the default game can be won in time
 * (QR_WINNABLE, QR_NEWBIE) and runs to the end without an exception (QR_STABLE).
 * The simple player stands still, never finds a hidden event, and clicks at most three
 * times a second, so a real player should do at least as well.
 */
class WinnableGameTest {

    private static final float FRAME = 1f / 60f;
    private static final float CLICK_GAP = 1f / 3f;

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3, 4, 5})
    void aSimplePlayerWinsWithoutHiddenEvents(long seed) {
        Rectangle world = new Rectangle(0f, 0f, 1600f, 1280f);
        Player player = new Player(790f, 627f, world, bounds -> false);
        Inventory inventory = new Inventory();
        OatsItem[] oatsHolder = new OatsItem[1];
        GooseManager geese = new GooseManager(world, bounds -> false, player, new GooseListener() {
            @Override
            public void waveCleared(int number) {
                oatsHolder[0].addUses(GameScreen.OATS_PER_CLEARED_WAVE);
            }
        }, GooseManager.DEFAULT_WAVES, new Random(seed));
        OatsItem oats = new OatsItem(geese);
        oatsHolder[0] = oats;
        BreadItem bread = new BreadItem(geese);
        inventory.addItem(oats);
        inventory.addItem(bread);
        GameSession session = new GameSession(new GameTimer(), geese);
        session.play();

        float sinceClick = CLICK_GAP;
        while (session.isPlaying()) {
            player.update(FRAME, 0f, 0f);
            inventory.update(FRAME);
            geese.update(FRAME, player.getCenterX(), player.getCenterY());
            sinceClick += FRAME;
            if (sinceClick >= CLICK_GAP && click(inventory, geese, player)) {
                sinceClick = 0f;
            }
            session.update(FRAME);
        }

        assertEquals(GameState.WON, session.getState(), "seed " + seed + ": geese left " + geese.geeseLeft()
            + ", oats " + oats.getUsesLeft() + ", bread " + bread.getUsesLeft());
        assertTrue(session.getTimer().getSecondsLeft() > 30f,
            "seed " + seed + ": won with time to spare, " + session.getTimer().format() + " left");
    }

    /**
     * Throws bread at a group of two or more nearby geese, otherwise gives oats to the nearest one;
     * when the oats are gone, bread is thrown at single geese too.
     */
    private static boolean click(Inventory inventory, GooseManager geese, Player player) {
        List<Goose> near = geese.geeseNear(player.getCenterX(), player.getCenterY(), 200f);
        if (near.isEmpty()) {
            return false;
        }
        Goose best = near.get(0);
        int bestGroup = 0;
        for (Goose goose : near) {
            int group = geese.geeseNear(goose.getCenterX(), goose.getCenterY(), BreadItem.RADIUS_PIXELS).size();
            if (group > bestGroup) {
                best = goose;
                bestGroup = group;
            }
        }
        boolean outOfOats = inventory.getItems().get(0).getUsesLeft() == 0;
        if (bestGroup >= 2 || outOfOats) {
            inventory.select(2);
            if (inventory.useSelected(best.getCenterX(), best.getCenterY())) {
                return true;
            }
        }
        Goose nearest = near.get(0);
        for (Goose goose : near) {
            if (distance(goose, player) < distance(nearest, player)) {
                nearest = goose;
            }
        }
        inventory.select(1);
        return inventory.useSelected(nearest.getCenterX(), nearest.getCenterY());
    }

    private static double distance(Goose goose, Player player) {
        return Math.hypot(goose.getCenterX() - player.getCenterX(), goose.getCenterY() - player.getCenterY());
    }
}
