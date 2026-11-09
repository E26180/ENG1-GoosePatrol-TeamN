package uk.ac.york.eng1.goosepatrol.screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.Item;
import uk.ac.york.eng1.goosepatrol.player.Player;

/**
 * Draws everything on top of the map, in screen pixels: the timer (FR_TIMER), the goals in
 * bold, large text (QR_OBJECTIVES), the counters (FR_COUNTERS), the inventory bar, short
 * messages, and the start, pause, win and loss screens (FR_START_PAUSED, FR_PAUSE_KEY, FR_JUDGEMENT).
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
    private final BitmapFont goalFont = new BitmapFont();
    private final GlyphLayout layout = new GlyphLayout();

    /** Item icons, loaded the first time an item with that icon is shown. */
    private final Map<String, Texture> icons = new HashMap<>();

    private String message = "";
    private float messageLeft = 0f;

    /** Creates the HUD and loads its fonts and icons. */
    public Hud() {
        font.getData().setScale(1.15f);
        bigFont.getData().setScale(2.2f);
        goalFont.getData().setScale(1.35f);
        font.setUseIntegerPositions(true);
        bigFont.setUseIntegerPositions(true);
        goalFont.setUseIntegerPositions(true);
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
     * @param player    the player (for the speed boost or startle)
     * @param geeseFasterFor seconds left of the hidden event that speeds the geese up (0 if none)
     * @param muted     whether the sound is off
     * @param tip       a tutorial tip to show above the inventory, or null for none
     */
    public void render(float delta, GameSession session, WaveStatus waves, Inventory inventory, Player player,
                       float geeseFasterFor, boolean muted, String tip) {
        if (session.isPlaying()) {
            messageLeft = Math.max(0f, messageLeft - delta);
        }
        viewport.apply();
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        shapes.setProjectionMatrix(viewport.getCamera().combined);
        batch.setProjectionMatrix(viewport.getCamera().combined);

        drawStatusPanel(session, waves, inventory, player, geeseFasterFor, muted, h);
        drawInventoryBar(inventory, w);
        if (tip != null && session.isPlaying()) {
            drawTip(tip, w);
        }
        if (messageLeft > 0f) {
            drawMessage(w, h);
        }
        if (!session.isPlaying()) {
            drawOverlay(session, waves, inventory, w, h);
        }
    }

    /** Top-left panel: time, goals, counters and anything that is changing speeds. */
    private void drawStatusPanel(GameSession session, WaveStatus waves, Inventory inventory, Player player,
                                 float geeseFasterFor, boolean muted, float h) {
        List<String> extras = new ArrayList<>();
        List<Color> extraColours = new ArrayList<>();
        float next = waves.secondsToNextWave();
        if (next > 0f) {
            extras.add((waves.currentWave() == 0 ? "Geese arrive in " : "Next wave in ") + (int) Math.ceil(next) + " s");
            extraColours.add(TEXT_DIM);
        }
        if (player.getBoostLeft() > 0f) {
            boolean faster = player.getSpeed() > Player.BASE_SPEED;
            extras.add((faster ? "Speed boost: " : "Startled, slower: ") + (int) Math.ceil(player.getBoostLeft()) + " s");
            extraColours.add(faster ? Color.CYAN : Color.SALMON);
        }
        if (geeseFasterFor > 0f) {
            extras.add("Geese faster: " + (int) Math.ceil(geeseFasterFor) + " s");
            extraColours.add(Color.SALMON);
        }
        if (muted) {
            extras.add("Sound off (M)");
            extraColours.add(TEXT_DIM);
        }

        float panelW = 300f;
        float panelH = 150f + extras.size() * 22f;
        fillRect(PAD, h - PAD - panelH, panelW, panelH, PANEL);

        batch.begin();
        float x = PAD + 12f;
        float y = h - PAD - 10f;
        boolean lowTime = session.getTimer().getSecondsLeft() <= 30f;
        bigFont.setColor(lowTime ? Color.SALMON : Color.WHITE);
        bigFont.draw(batch, "Time " + session.getTimer().format() + (lowTime ? " !" : ""), x, y);
        y -= 46f;
        String wave = waves.currentWave() == 0 ? "Waves to come: " + waves.totalWaves()
            : "Wave " + waves.currentWave() + " of " + waves.totalWaves() + ", " + waves.wavesLeft() + " to come";
        drawBold(goalFont, wave, x, y, Color.WHITE);
        y -= 26f;
        drawBold(goalFont, "Geese left: " + waves.geeseLeft(), x, y, Color.WHITE);
        y -= 28f;
        font.setColor(TEXT_DIM);
        font.draw(batch, "Items used: " + inventory.getItemsUsed() + "    Events: " + session.getEventsTriggered(), x, y);
        for (int i = 0; i < extras.size(); i++) {
            y -= 22f;
            font.setColor(extraColours.get(i));
            font.draw(batch, extras.get(i), x, y);
        }
        batch.end();
    }

    /** Draws text twice, one pixel apart, so it reads as bold (QR_OBJECTIVES). */
    private void drawBold(BitmapFont f, String text, float x, float y, Color colour) {
        f.setColor(colour);
        f.draw(batch, text, x, y);
        f.draw(batch, text, x + 1f, y);
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

    /** A tutorial tip in a light box above the inventory bar, marked "TIP" (UR_NO_EXPERIENCE). */
    private void drawTip(String tip, float w) {
        String text = "TIP  " + tip + "   (T: hide tips)";
        layout.setText(font, text);
        float boxW = layout.width + 30f;
        float boxH = layout.height + 20f;
        float x = (w - boxW) / 2f;
        float y = PAD + 22f + SLOT + 40f;
        fillRect(x, y, boxW, boxH, new Color(1f, 1f, 1f, 0.88f));
        batch.begin();
        font.setColor(Color.BLACK);
        font.draw(batch, text, x + 15f, y + boxH - 10f);
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
    private void drawOverlay(GameSession session, WaveStatus waves, Inventory inventory, float w, float h) {
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
                    "Hungry geese are coming to Campus East. Feed every goose in " + waves.totalWaves() + " waves before the 5 minutes run out!",
                    "1 Bag of Oats: click on a goose.    2 Bread: throw it near a group of geese.",
                    "A full goose flies away happily. A goose that flashes red and honks wants your snack - step away!",
                    "Odd things are hidden around campus: walk up to them to see what they do.",
                    "",
                    "WASD or arrows: walk    1-9: choose an item    Left click: use it    P or Esc: pause    M: sound    T: tips",
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
                    "Every goose is full and happy, and Campus East is calm again, with " + session.getTimer().format() + " to spare.",
                    "Items used: " + inventory.getItemsUsed() + "    Events triggered: " + session.getEventsTriggered(),
                    "",
                    "Press R to play again",
                };
                break;
            default:
                title = "TIME'S UP - YOU LOSE";
                colour = Color.SALMON;
                lines = new String[] {
                    waves.geeseLeft() + (waves.geeseLeft() == 1 ? " goose was" : " geese were") + " still hungry and the geese rule Campus East. Better luck next time!",
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

    /** Returns the icon the item names, loading it once; any new item works without changes here. */
    private Texture iconFor(Item item) {
        String path = item.getIconPath();
        if (path == null) {
            return null;
        }
        return icons.computeIfAbsent(path, Texture::new);
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapes.dispose();
        font.dispose();
        bigFont.dispose();
        goalFont.dispose();
        for (Texture icon : icons.values()) {
            icon.dispose();
        }
    }
}
