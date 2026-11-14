package uk.ac.york.eng1.goosepatrol.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Game class. Owns shared resources and switches screens.
 * Extends Game so Lwjgl3Launcher's "new Main()" keeps working.
 */
public class CoreMain extends Game {

    private SpriteBatch batch;
    private BitmapFont font;

    /** Creates shared resources, then calls startNewGame(). */
    @Override
    public void create() { }

    /** Starts a fresh round (also used for restart). Round begins PAUSED. */
    public void startNewGame() { }

    /** Called by GameScreen when the round ends. */
    public void showEndScreen(boolean won, float timeRemaining) { }

    /** Sets the new screen and disposes the old one. */
    private void switchScreen(com.badlogic.gdx.Screen next) { }

    public SpriteBatch getBatch() { return batch; }

    public BitmapFont getFont() { return font; }

    /** Disposes current screen, batch and font. */
    @Override
    public void dispose() { }
}
