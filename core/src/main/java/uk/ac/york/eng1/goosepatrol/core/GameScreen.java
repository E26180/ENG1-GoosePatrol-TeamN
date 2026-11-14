package uk.ac.york.eng1.goosepatrol.core;

import com.badlogic.gdx.Screen;

/**
 * Gameplay screen. Holds everything belonging to one round
 * (MapManager, geese, Inventory, ItemInput, GameTimer), so restart
 * is just "create a new GameScreen".
 */
public class GameScreen implements Screen {

    // Assumes GameState is in the same package; add an import if it isn't.
    private GameState state = GameState.PAUSED;   // starts paused

    public GameScreen(CoreMain game) { }

    // --- Screen lifecycle ---

    @Override public void show() { }

    /** Pause input -> if RUNNING: item input, update, end checks -> draw. */
    @Override public void render(float delta) { }

    @Override public void resize(int width, int height) { }

    /** Window lost focus: pause the game. */
    @Override public void pause() { }

    /** Stay paused; player resumes manually. */
    @Override public void resume() { }

    @Override public void hide() { }

    /** Disposes MapManager only (batch/font belong to CoreMain). */
    @Override public void dispose() { }

    // --- Pause / resume ---

    /** SPACE (start/resume), P and ESC (toggle). */
    private void handlePauseInput() { }

    /** RUNNING -> PAUSED. */
    public void pauseGame() { }

    /** PAUSED -> RUNNING. */
    public void resumeGame() { }

    // --- Update ---

    /** Per-frame logic, RUNNING only: timer, player, geese, hidden events. */
    private void update(float delta) { }

    /** Checks win then lose; switches screen if the round ended. */
    private boolean checkEndConditions() { return false; }

    /** TODO: real win condition. */
    private boolean hasWon() { return false; }

    // --- Drawing ---

    /** Map, timer HUD, pause overlay, inventory HUD. */
    private void draw() { }
}
