package uk.ac.york.eng1.goosepatrol.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import java.util.HashMap;
import java.util.Map;

/**
 * Application-level class: owns shared resources and switches screens.
 */
public class CoreMain extends Game {

    private SpriteBatch batch;
    private final Map<Integer, BitmapFont> fonts = new HashMap<>();

    private BitmapFont createFont(FreeTypeFontGenerator generator, int size) {
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = size;
        return generator.generateFont(parameter);
    }

    /**
     * Creates the shared resources, then starts the first round.
     */
    @Override
    public void create() {
        //Generates the base sprite batch
        this.batch = new SpriteBatch();
        //Generator for fonts
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/font.ttf"));
        //Fonts stored in hashmap with keys 16, 24, 32, 48 corresponding to font sizes
        fonts.put(16, createFont(generator, 16));
        fonts.put(24, createFont(generator, 24));
        fonts.put(32, createFont(generator, 32));
        fonts.put(48, createFont(generator, 48));
        generator.dispose();
        startNewGame();
    }

    // NOTE: no render() override. Game.render() calls the current screen's render(delta).

    /**
     * Releases shared resources.
     */
    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        //Disposes of the sprite batch
        this.batch.dispose();
        //Disposes of each font iteratively (O(n))
        for (BitmapFont font : this.fonts.values()) {
            font.dispose();
        }

    }

    /** Starts a fresh round (also used for restart). Round begins PAUSED. */
    public void startNewGame() {
        //Calls switchScreen method to the starting (pause) screen
        switchScreen(new GameScreen(this));
    }

    /** Called by GameScreen when the round ends. */
    public void showEndScreen(boolean won, float timeRemaining) {
        //Creates an instance of EndScreen and switches the screen to it
        EndScreen endScreen = new EndScreen(this, won, timeRemaining);
        switchScreen(endScreen);
    }

    /** Sets the new screen and disposes the old one. */
    private void switchScreen(com.badlogic.gdx.Screen next) {
        /* Sets the current screen to a local variable
        before switching to the new screen and disposing
        the old one */
        com.badlogic.gdx.Screen currentScreen = getScreen();
        setScreen(next);
        if (currentScreen != null) {
            currentScreen.dispose();
        }
    }

    /**
     * Get method for sprite batch
     * @return Sprite Batch (this.batch)
     */
    public SpriteBatch getBatch() { return this.batch; }

    public BitmapFont getFont(int size) {
        BitmapFont font = this.fonts.get(size);
        if (font == null) {
            throw new IllegalArgumentException("This font size does not exist in the project\n16, 24, 32, 48 only");
        } else {
            return font;
        }
    }

}
