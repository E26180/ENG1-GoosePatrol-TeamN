package uk.ac.york.eng1.goosepatrol.core;

import com.badlogic.gdx.Screen;

import uk.ac.york.eng1.goosepatrol.Main;

/** Win/lose screen. R or ENTER restarts via Main.startNewGame(). */
public class EndScreen implements Screen {

    public EndScreen(Main game, boolean won, float timeRemaining) { }

    @Override public void show() { }

    /** Checks restart key, then draws win or lose message. */
    @Override public void render(float delta) { }

    @Override public void resize(int width, int height) { }

    @Override public void pause() { }

    @Override public void resume() { }

    @Override public void hide() { }

    @Override public void dispose() { }
}
