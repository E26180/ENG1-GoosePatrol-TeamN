package uk.ac.york.eng1.goosepatrol.core;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;
import uk.ac.york.eng1.goosepatrol.MapManager;
import uk.ac.york.eng1.goosepatrol.HUD;
import uk.ac.york.eng1.goosepatrol.geese.Geese;
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.ItemInput;
import uk.ac.york.eng1.goosepatrol.hidden_event.HiddenEvent;
import uk.ac.york.eng1.goosepatrol.geese.GooseWaveManager;



/**
 * Gameplay screen. Holds everything belonging to one round
 * (MapManager, geese, Inventory, ItemInput, GameTimer), so restart
 * is just "create a new GameScreen".
 */
public class GameScreen implements Screen {

    private final CoreMain game;
    private final GameTimer timer;
    private final MapManager mapManager;
    private final HUD hud;
    private final List<Geese> geese = new ArrayList<>();
    private final List<HiddenEvent> hiddenEvents = new ArrayList<>();
    private final Inventory bag;
    private final ItemInput input;
    private GameState state = GameState.PAUSED; // starts paused
    private final GooseWaveManager waveManager;

    public GameScreen(CoreMain game) {
        this.game = game;
        this.timer = new GameTimer();
        this.mapManager = new MapManager();
        this.hud = new HUD();
        this.bag = new Inventory(this.geese);
        this.input = new ItemInput(this.bag, this.mapManager.getCamera());
        this.waveManager = new GooseWaveManager(this.geese);

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
            input.update();
            update(delta);

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
        }
    }

    /** PAUSED -> RUNNING. */
    public void resumeGame() {
        if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
            timer.isPaused = false;
        }
    }

    // --- Update ---

    /** Per-frame logic, RUNNING only: timer, player, geese, hidden events. */
    private void update(float delta)
    {
        timer.update(delta);

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

    /** TODO: real win condition. */
    private boolean hasWon() { return false; }

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

        mapManager.render();

        hud.draw(game.getBatch(), timer.getRemaining(), state == GameState.PAUSED, bag.getUsed(), getTriggeredEventCount());
    }
}
