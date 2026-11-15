package uk.ac.york.eng1.goosepatrol.core;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.OrthographicCamera;
import uk.ac.york.eng1.goosepatrol.MapManager;
import uk.ac.york.eng1.goosepatrol.HUD;
import uk.ac.york.eng1.goosepatrol.geese.Geese;
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.ItemInput;
import uk.ac.york.eng1.goosepatrol.hidden_event.HiddenEvent;
import uk.ac.york.eng1.goosepatrol.geese.GooseWaveManager;
import uk.ac.york.eng1.goosepatrol.hidden_event.HEItems;
import uk.ac.york.eng1.goosepatrol.hidden_event.HEGooseSpeed;
import uk.ac.york.eng1.goosepatrol.mechanics.Player;
import uk.ac.york.eng1.goosepatrol.mechanics.KeyboardInput;

/**
 * Gameplay screen. Holds everything belonging to one round
 * (MapManager, geese, Inventory, ItemInput, GameTimer), so restart
 * is just "create a new GameScreen".
 */
public class GameScreen implements Screen {

    private static final int TOTAL_WAVES = 3;
    private final CoreMain game;
    private final GameTimer timer;
    private final MapManager mapManager;
    private final HUD hud;
    private final List<Geese> geese = new ArrayList<>();
    private final List<HiddenEvent> hiddenEvents = new ArrayList<>();
    private final Inventory bag;
    private final ItemInput input;
    private GameState state = GameState.PAUSED; // starts paused
    private final Player player;
    private final Texture playerTexture;
    private final GooseWaveManager waveManager;
    private final HEItems itemEvent1;
    private final HEItems itemEvent2;
    private final HEItems itemEvent3;
    private final HEGooseSpeed speedEvent1;
    private final HEGooseSpeed speedEvent2;
    private final Texture gooseTexture;
    private final KeyboardInput keyboard = new KeyboardInput();

    /** Creates a new round in the paused state.
     *
     * @param game the game managing screens and shared rendering resources */
    public GameScreen(CoreMain game) {
        this.game = game;
        this.timer = new GameTimer();
        this.mapManager = new MapManager();
        this.playerTexture = new Texture(
            Gdx.files.internal("textures/player/student.png")
        );

        TextureRegion playerFrame = new TextureRegion(
            this.playerTexture, 0, 0, 32, 32
        );

        float[] vertices = {0, 0, 32, 0, 32, 32, 0, 32};
        short[] triangles = {0, 1, 2, 2, 3, 0};

        this.player = new Player(
            new PolygonRegion(playerFrame, vertices, triangles)
        );
        this.player.setPosition(800f, 500f);
        this.mapManager.getCamera().zoom = 0.5f;
        this.hud = new HUD();
        this.bag = new Inventory(this.geese);
        this.input = new ItemInput(this.bag, this.mapManager.getCamera());
        //goose texture
        gooseTexture = new Texture(Gdx.files.internal("textures/geese/goose.png"));
        waveManager = new GooseWaveManager(geese, game.getBatch(), gooseTexture);
        // Create the first goose wave for this round.
        this.waveManager.NewWave();
        //Create and register the two hidden events for this round.
        this.itemEvent1 = new HEItems(400f, 300f);
        this.itemEvent2 = new HEItems(250f, 1700f);
        this.itemEvent3 = new HEItems(1800f, 1200f);
        this.speedEvent1 = new HEGooseSpeed(1200f, 800f);
        this.speedEvent2 = new HEGooseSpeed(900f, 400f);

        this.hiddenEvents.add(this.itemEvent1);
        this.hiddenEvents.add(this.itemEvent2);
        this.hiddenEvents.add(this.itemEvent3);
        this.hiddenEvents.add(this.speedEvent1);
        this.hiddenEvents.add(this.speedEvent2);


    }

    // --- Screen lifecycle ---

    @Override public void show() { }

    /** Pause input -> if RUNNING: item input, update, end checks -> draw. */
    @Override public void render(float delta) {
        if (state == GameState.WON || state == GameState.LOST) {
            return;
        }

        handlePauseInput();

        if (state == GameState.RUNNING) {
            float oldX = player.getX();
            float oldY = player.getY();

            keyboard.update(player, bag);

            player.setPosition(Math.max(0f, Math.min(player.getX(), 1600f - player.getWidth())),Math.max(0f, Math.min(player.getY(), 1280f - player.getHeight())));

            if (mapManager.getCollisionManager().collidesWith(player.getBoundingRectangle())) {
                player.setPosition(oldX, oldY);
            }

            input.update();
            update(delta);
            updateHiddenEvents(player);

            if (checkEndConditions()) {
                // The end check will switch screens, so stop drawing this one.
                return;
            }
        }

        draw();
    }

    @Override public void resize(int width, int height) {
        if (width > 0 && height > 0) {
            hud.resize(width, height);
        }
    }

    /** Window lost focus: pause the game. */
    @Override public void pause() {
        pauseGame();
    }

    /** Stay paused; player resumes manually. */
    @Override public void resume() {
        // Wait for the player's resume input
    }

    @Override public void hide() { }

    /** Releases the map and HUD resources owned by this screen. */
    @Override public void dispose() {
        mapManager.dispose();
        hud.dispose();

        if (gooseTexture != null)
        {
            gooseTexture.dispose();
        }
        player.dispose();
        playerTexture.dispose();
    }

    // --- Pause / resume ---

    /** SPACE (start/resume), P and ESC (toggle). */
    private void handlePauseInput() {

        // React once per press so holding a key will not repeatedly toggle pause.
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && state == GameState.PAUSED) {
            resumeGame();
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (state == GameState.RUNNING) {
                pauseGame();
            } else if (state == GameState.PAUSED) {
                resumeGame();
            }
        }
    }

    /** RUNNING -> PAUSED. */
    public void pauseGame() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
            timer.isPaused = true;
            // Preserve the remaining duration of the speed event.
            speedEvent1.onPause();
            speedEvent2.onPause();
        }
    }

    /** PAUSED -> RUNNING. */
    public void resumeGame() {
        if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            timer.isPaused = false;
            // Resume the speed event from its remaining duration.
            speedEvent1.onResume();
            speedEvent2.onResume();
        }
    }

    // --- Update ---

    /** Updates the timer, checks the speed effect and starts waves when needed. */
    private void update(float delta)
    {
        timer.update(delta);
        waveManager.removeFlownGeese();
        updateHiddenEvents(this.player);

        if (timer.isFinished()) {
            return;
        }

        // Update the triggered speed event and restore speed after it expires.
        if (speedEvent1.getIsActivated()) {
            speedEvent1.checkActive(geese);
        }

        if (speedEvent2.getIsActivated()) {
            speedEvent2.checkActive(geese);
        }

        // Start another wave only after the current one is cleared.
        if (geese.isEmpty() && waveManager.getWaveCount() < TOTAL_WAVES) {
            waveManager.NewWave();
        }
    }

    /** Checks win then lose; switches screen if the round ended. */
    private boolean checkEndConditions() {
        if (hasWon()) {
            state = GameState.WON;
            timer.isPaused = true;

            // Avoid displaying a negative time after the final timer update.
            game.showEndScreen(true, Math.max(0f, timer.getRemaining()));

            return true;
        }

        if (timer.isFinished()) {
            state = GameState.LOST;
            timer.isPaused = true;

            game.showEndScreen(false, 0f);

            return true;
        }

        // It will let render() continue drawing this screen while the round is running.
        return false;
    }

    /** Returns whether all three waves were cleared before time expired. */
    private boolean hasWon() {
        return !timer.isFinished() && waveManager.getWaveCount() >= TOTAL_WAVES && geese.isEmpty();
    }

    /** Triggers each hidden event once when the player enters its range.
     *
     * @param player the player sprite used for position checks
     */
    private void updateHiddenEvents(PolygonSprite player) {
        if (player == null || state != GameState.RUNNING || timer.isFinished()) {
            return;
        }

        // Refill the inventory when the player reaches the item event.
        if (!itemEvent1.getIsActivated() && itemEvent1.isPlayerInActiveRange(player)) {
            itemEvent1.trigger(bag);
        } else if (!itemEvent2.getIsActivated() && itemEvent2.isPlayerInActiveRange(player)) {
            itemEvent2.trigger(bag);
        } else if (!itemEvent3.getIsActivated() && itemEvent3.isPlayerInActiveRange(player)) {
            itemEvent3.trigger(bag);
        }

        // Activate the temporary speed effect when the player reaches it.
        if (!speedEvent1.getIsActivated() && speedEvent1.isPlayerInActiveRange(player)) {
            speedEvent1.trigger(geese);
        } else if (!speedEvent1.getIsActivated() && speedEvent1.isPlayerInActiveRange(player)) {
            speedEvent1.trigger(geese);
        }
    }

    /** Returns the number of events activated in this round. */
    private int getTriggeredEventCount() {
        int count = 0;

        for (HiddenEvent event : hiddenEvents) {
            if (event.getIsActivated()) {
                count++;
            }
        }

        return count;
    }

    // --- Drawing ---

    /** Map, timer HUD, pause overlay, inventory HUD. */
    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        OrthographicCamera camera = mapManager.getCamera();

        float halfWidth = camera.viewportWidth * camera.zoom / 2f;
        float halfHeight = camera.viewportHeight * camera.zoom / 2f;

        float playerCenterX = player.getX() + player.getWidth() / 2f;
        float playerCenterY = player.getY() + player.getHeight() / 2f;

        camera.position.set(
            Math.max(halfWidth, Math.min(playerCenterX, 1600f - halfWidth)),Math.max(halfHeight, Math.min(playerCenterY, 1280f - halfHeight)), 0f);

        mapManager.render();
        player.draw(mapManager.getCamera().combined);

        hud.draw(game.getBatch(), timer.getRemaining(), state == GameState.PAUSED, bag.getUsed(), getTriggeredEventCount());

        SpriteBatch batch = game.getBatch();
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        // Show the current wave and remaining geese in the top-right corner.
        batch.begin();

        for (Geese goose : geese)
        {
            goose.draw();
        }
        game.getFont(24).draw(batch, "Wave: " + waveManager.getWaveCount() + "/" + TOTAL_WAVES, 20, height - 20, Math.max(1f, width - 40), Align.right, false);
        game.getFont(24).draw(batch, "Geese left: " + geese.size(), 20, height - 55, Math.max(1f, width - 40), Align.right, false);

        batch.end();
    }
}
