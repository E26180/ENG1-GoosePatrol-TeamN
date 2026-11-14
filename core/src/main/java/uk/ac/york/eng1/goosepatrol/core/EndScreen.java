package uk.ac.york.eng1.goosepatrol.core;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import uk.ac.york.eng1.goosepatrol.core.CoreMain;

/** Win/lose screen. R or ENTER restarts via CoreMain.startNewGame(). */
public class EndScreen implements Screen {


    private CoreMain game;
    private boolean won;
    private float timeRemaining;
    private final OrthographicCamera camera = new OrthographicCamera();

    /** Creates a screen displaying the game result.
     *
     * @param game the game managing screens and shared rendering resources
     * @param won whether the player won the game
     * @param timeRemaining the remaining game time in seconds */
    public EndScreen(CoreMain game, boolean won, float timeRemaining) {
        this.game = game;
        this.won = won;
        this.timeRemaining = timeRemaining;
    }

    /** Sets up the camera using the current window dimensions. */
    @Override public void show() {
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /** Checks restart key, then draws win or lose message. */
    @Override public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.startNewGame();

            //Restarting replaces this screen, so stop processing this frame.
            return;
        }

        draw();
    }

    /** Updates the camera to match the window dimensions.
     * Ignores non-positive dimensions.
     *
     * @param width the window width in pixels
     * @param height the window height in pixels */
    @Override public void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }

        //It will match the screen coordinates to the window size.
        camera.setToOrtho(false, width, height);
        camera.update();
    }

    @Override public void pause() { }

    @Override public void resume() { }

    @Override public void hide() { }

    @Override public void dispose() { }

    /** Clears the screen and draws the result and restart instructions. */
    private void draw() {
        ScreenUtils.clear(0.12f, 0.15f, 0.18f, 1f);

        SpriteBatch batch = game.getBatch();
        batch.setProjectionMatrix(camera.combined);

        float width = camera.viewportWidth;
        float height = camera.viewportHeight;
        String result = won ? "YOU WIN!" : "TIME IS UP!";

        batch.begin();

        // Centre the result and restart instructions will be shown in the window.
        game.getFont(48).draw(batch, result, 0, height * 0.65f, width, Align.center, true);
        game.getFont(24).draw(batch, "Press R or Enter to play one more time", 0, height * 0.40f, width, Align.center, true);

        batch.end();
    }
}
