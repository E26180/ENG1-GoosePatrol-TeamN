package uk.ac.york.eng1.goosepatrol.core;

import com.badlogic.gdx.Screen;

import uk.ac.york.eng1.goosepatrol.core.CoreMain;

/** Win/lose screen. R or ENTER restarts via Main.startNewGame(). */
public class EndScreen implements Screen {


    private CoreMain game;
    private boolean won;
    private float timeRemaining;

    public EndScreen(CoreMain game, boolean won, float timeRemaining) {
        this.game = game;
        this.won = won;
        this.timeRemaining = timeRemaining;
    }

    @Override public void show() { }

    /** Checks restart key, then draws win or lose message. */
    @Override public void render(float delta) { }

    @Override public void resize(int width, int height) { }

    @Override public void pause() { }

    @Override public void resume() { }

    @Override public void hide() { }

    @Override public void dispose() { }
}
