package uk.ac.york.eng1.goosepatrol.screen;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import uk.ac.york.eng1.goosepatrol.game.GameSession;
import uk.ac.york.eng1.goosepatrol.game.GameState;
import uk.ac.york.eng1.goosepatrol.game.WaveStatus;
import uk.ac.york.eng1.goosepatrol.item.BreadItem;
import uk.ac.york.eng1.goosepatrol.item.EnergyDrinkItem;
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.Item;
import uk.ac.york.eng1.goosepatrol.item.OatsItem;
import uk.ac.york.eng1.goosepatrol.player.Player;

/**
 * Draws everything on top of the map, in screen pixels: the timer (FR_TIMER), the goals
 * (QR_OBJECTIVES), the counters (FR_COUNTERS), the inventory bar, short messages, and the
 * start, pause, win and loss screens (FR_START_PAUSED, FR_PAUSE_KEY, FR_JUDGEMENT).
 * Every cue uses text as well as colour (QR_ACCESS_CUES).
 */
public class Hud implements Disposable {

    /** Side of one inventory slot, in pixels. */
    private static final float SLOT = 64f;

    /** Gap around panels, in pixels. */
    private static final float PAD = 10f;

    /** How long a message stays on screen, in seconds. */
    private static final float MESSAGE_SECONDS = 4f;

    private static final Color PANEL = new Color(0f, 0f, 0f, 0.6f);
    private static final Color SELECTED = new Color(1f, 0.82f, 0.25f, 1f);
    private static final Color TEXT_DIM = new Color(0.8f, 0.8f, 0.8f, 1f);

    private final ScreenViewport viewport = new ScreenViewport();
    private final SpriteBatch batch = new SpriteBatch();
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final BitmapFont font = new BitmapFont();
    private final BitmapFont bigFont = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    private final Texture oatsIcon = new Texture("textures/items/oats.png");
    private final Texture breadIcon = new Texture("textures/items/bread.png");
    private final Texture drinkIcon = new Texture("textures/items/energy-drink.png");

    private String message = "";
    private float messageLeft = 0f;

    /** Creates the HUD and loads its fonts and icons. */
    public Hud() {
        font.getData().setScale(1.15f);
        bigFont.getData().setScale(2.2f);
        font.setUseIntegerPositions(true);
        bigFont.setUseIntegerPositions(true);
    }

    /**
     * Shows a short message near the top of the screen for a few seconds.
     *
     * @param text the message
     */
    public void showMessage(String text) {
        message = text;
        messageLeft = MESSAGE_SECONDS;
    }

    /**
     * Updates the HUD size after the window changes.
     *
     * @param width  new window width in pixels
     * @param height new window height in pixels
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Draws the HUD for one frame.
     *
     * @param delta     seconds since the last frame
     * @param session   the game rules (timer, state, events counter)
     * @param waves     the geese (for the goals panel)
     * @param inventory the player's items
     * @param player    the player (for the speed boost)
     */
    public void render(float delta, GameSession session, WaveStatus waves, Inventory inventory, Player player) {
        if (session.isPlaying()) {
            messageLeft = Math.max(0f, messageLeft - delta);
        }
        viewport.apply();
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        shapes.setProjectionMatrix(viewport.getCamera().combined);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        drawStatusPanel(session, waves, inventory, player, h);
        drawInventoryBar(inventory, w);
        if (messageLeft > 0f) {
            drawMessage(w, h);
        }
        if (!session.isPlaying()) {
            drawOverlay(session, inventory, w, h);
        }
    }

    /** Top-left panel: time, goals, counters and the speed boost. */
    private void drawStatusPanel(GameSession session, WaveStatus waves, Inventory inventory, Player player, float h) {
        float panelW = 290f;
        float panelH = player.getBoostLeft() > 0f ? 150f : 125f;
        fillRect(PAD, h - PAD - panelH, panelW, panelH, PANEL);

        batch.begin();
        float x = PAD + 12f;
        float y = h - PAD - 10f;
        boolean lowTime = session.getTimer().getSecondsLeft() <= 30f;
        bigFont.setColor(lowTime ? Color.SALMON : Color.WHITE);
        bigFont.draw(batch, "Time " + session.getTimer().format() + (lowTime ? " !" : ""), x, y);
        y -= 48f;
        font.setColor(Color.WHITE);
        font.draw(batch, "Geese left: " + waves.geeseLeft() + "    Waves to come: " + waves.wavesLeft(), x, y);
        y -= 24f;
        font.setColor(TEXT_DIM);
        font.draw(batch, "Items used: " + inventory.getItemsUsed(), x, y);
        y -= 22f;
        font.draw(batch, "Events triggered: " + session.getEventsTriggered(), x, y);
        if (player.getBoostLeft() > 0f) {
            y -= 24f;
            font.setColor(Color.CYAN);
            font.draw(batch, "Speed boost: " + (int) Math.ceil(player.getBoostLeft()) + "s", x, y);
        }
        batch.end();
    }

    /** Bottom bar: one box per item, the selected one with a yellow frame and "SELECTED" above it. */
    private void drawInventoryBar(Inventory inventory, float w) {
        List<Item> items = inventory.getItems();
        float barW = items.size() * (SLOT + PAD) - PAD;
        float startX = (w - barW) / 2f;
        float y = PAD + 22f;

        beginShapes();
        for (int i = 0; i < items.size(); i++) {
            float x = startX + i * (SLOT + PAD);
            boolean selected = inventory.getSelectedNumber() == i + 1;
            shapes.setColor(selected ? SELECTED : Color.DARK_GRAY);
            shapes.rect(x - 3f, y - 3f, SLOT + 6f, SLOT + 6f);
            shapes.setColor(PANEL);
            shapes.rect(x, y, SLOT, SLOT);
            Item item = items.get(i);
            if (item.getCooldownLeft() > 0f || item.getUsesLeft() == 0) {
                shapes.setColor(0f, 0f, 0f, 0.55f);
                shapes.rect(x, y, SLOT, SLOT);
            }
        }
        shapes.end();

        batch.begin();
        for (int i = 0; i < items.size(); i++) {
            float x = startX + i * (SLOT + PAD);
            Item item = items.get(i);
            Texture icon = iconFor(item);
            if (icon != null) {
                batch.setColor(item.canUse() ? Color.WHITE : Color.GRAY);
                batch.draw(icon, x + 14f, y + 18f, SLOT - 28f, SLOT - 28f);
                batch.setColor(Color.WHITE);
            }
            font.setColor(Color.WHITE);
            font.draw(batch, String.valueOf(i + 1), x + 4f, y + SLOT - 4f);
            String uses = item.getUsesLeft() + "/" + item.getMaxUses();
            layout.setText(font, uses);
            font.draw(batch, uses, x + SLOT - layout.width - 4f, y + 18f);
            if (item.getCooldownLeft() > 0f) {
                String wait = "wait " + (int) Math.ceil(item.getCooldownLeft()) + "s";
                layout.setText(font, wait);
                font.setColor(Color.SALMON);
                font.draw(batch, wait, x + (SLOT - layout.width) / 2f, y - 6f);
            }
            if (inventory.getSelectedNumber() == i + 1) {
                // The name sits above the selected slot, so long names never overlap.
                String label = "> " + item.getName() + " <";
                layout.setText(font, label);
                font.setColor(SELECTED);
                font.draw(batch, label, x + (SLOT - layout.width) / 2f, y + SLOT + 22f);
            }
        }
        batch.end();
    }

    private void drawMessage(float w, float h) {
        layout.setText(font, message);
        float boxW = layout.width + 30f;
        float boxH = layout.height + 22f;
        float x = (w - boxW) / 2f;
        float y = h - PAD - boxH;
        fillRect(x, y, boxW, boxH, PANEL);
        batch.begin();
        font.setColor(Color.YELLOW);
        font.draw(batch, message, x + 15f, y + boxH - 11f);
        batch.end();
    }

    /** Darkens the screen and shows the start, pause or result text. */
    private void drawOverlay(GameSession session, Inventory inventory, float w, float h) {
        fillRect(0f, 0f, w, h, new Color(0f, 0f, 0f, 0.65f));
        GameState state = session.getState();
        String title;
        String[] lines;
        Color colour;
        switch (state) {
            case READY:
                title = "GOOSE PATROL";
                colour = Color.WHITE;
                lines = new String[] {
                    "Hungry geese are coming for your food. Feed every goose before the 5 minutes run out!",
                    "",
                    "WASD or arrow keys: walk          1-9: choose an item          Left click: use it",
                    "P or Esc: pause          Find the hidden events around campus",
                    "",
                    "Press SPACE to start",
                };
                break;
            case PAUSED:
                title = "PAUSED";
                colour = Color.WHITE;
                lines = new String[] {"Time left " + session.getTimer().format(), "", "Press P, Esc or SPACE to carry on"};
                break;
            case WON:
                title = "YOU WIN!";
                colour = Color.LIME;
                lines = new String[] {
                    "Every goose is full and happy, with " + session.getTimer().format() + " to spare.",
                    "Items used: " + inventory.getItemsUsed() + "    Events triggered: " + session.getEventsTriggered(),
                    "",
                    "Press R to play again",
                };
                break;
            default:
                title = "TIME'S UP - YOU LOSE";
                colour = Color.SALMON;
                lines = new String[] {
                    "The geese are still hungry. Better luck next time!",
                    "Items used: " + inventory.getItemsUsed() + "    Events triggered: " + session.getEventsTriggered(),
                    "",
                    "Press R to play again",
                };
                break;
        }
        batch.begin();
        layout.setText(bigFont, title);
        float y = h / 2f + 40f + lines.length * 12f;
        bigFont.setColor(colour);
        bigFont.draw(batch, title, (w - layout.width) / 2f, y);
        y -= 60f;
        for (String line : lines) {
            layout.setText(font, line);
            font.setColor(Color.WHITE);
            font.draw(batch, line, (w - layout.width) / 2f, y);
            y -= 26f;
        }
        batch.end();
    }

    /**
     * Starts drawing filled shapes with see-through colours.
     * Blending is switched on every time, because {@link SpriteBatch#end()} switches it off.
     */
    private void beginShapes() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapes.begin(ShapeRenderer.ShapeType.Filled);
    }

    private void fillRect(float x, float y, float width, float height, Color colour) {
        beginShapes();
        shapes.setColor(colour);
        shapes.rect(x, y, width, height);
        shapes.end();
    }

    private Texture iconFor(Item item) {
        if (item instanceof OatsItem) {
            return oatsIcon;
        }
        if (item instanceof BreadItem) {
            return breadIcon;
        }
        if (item instanceof EnergyDrinkItem) {
            return drinkIcon;
        }
        return null;
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
        bigFont.dispose();
        oatsIcon.dispose();
        breadIcon.dispose();
        drinkIcon.dispose();
    }
}
