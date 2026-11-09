package uk.ac.york.eng1.goosepatrol;

import com.badlogic.gdx.Game;

import uk.ac.york.eng1.goosepatrol.screen.GameScreen;

/**
 * Entry point shared by all platforms. It opens the game screen, which starts
 * paused on the start screen (FR_START_PAUSED).
 */
public class Main extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen(this));
    }
}
