package uk.ac.york.eng1.goosepatrol;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import uk.ac.york.eng1.goosepatrol.geese.Geese;
import uk.ac.york.eng1.goosepatrol.item.Feedable;
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.ItemInput;

/**
 * Loads game resources, handles input and renders the map.
 */
public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture image;
    private MapManager mapManager;
    private HUD hud;

    //TODO: test geese for the HUD feed bars, replace with the real Geese class later
    //x positions of the test geese in the world
    private final float[] testGooseX= {400, 800, 1200};
    //y positions of the test geese in the world
    private final float[] testGooseY= {300, 640, 900};
    //how full each test goose is, from 0 (empty) to 1 (full)
    private final float[] testFeedLevel= {0.2f, 0.5f, 1f};

    private final List<Geese> geese = new ArrayList<>();

    private Inventory bag;
    private ItemInput input;

    /**
     * Loads the map and creates the starting inventory.
     */
    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.image = new Texture("libgdx.png");
        this.mapManager = new MapManager();
        this.hud = new HUD();

        // Active geese will be registered in the live target list.
        this.bag = new Inventory(this.geese);
        this.input = new ItemInput(
            this.bag,
            this.mapManager.getCamera()
        );
    }

    /**
     * Handles item input and draws the current frame.
     */
    @Override
    public void render() {
        this.input.update();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
        mapManager.render();
        //Draw a feed bar above every test goose
        for (int i = 0; i < testGooseX.length; i++) {
            hud.drawFeedBar(mapManager.getCamera(), testGooseX[i], testGooseY[i], testFeedLevel[i]);
        }
        //TODO: replace 95, false and 1 with the real timer, pause and event counter
        //TODO: call hud.playWave() when a new wave starts, and hud.playWin() / hud.playLose() on the end screens
        hud.draw(batch, 95, false, bag.getUsed(), 0);
    }


    /**
     * Resizes the HUD when the window size changes
     * @param width the new window width in pixels
     * @param height the new window height in pixels
     */
    @Override
    public void resize(int width, int height) {
        hud.resize(width, height);
    }


    /**
     * Releases rendering and map resources.
     */
    @Override
    public void dispose() {
        this.batch.dispose();
        this.image.dispose();

        if (this.mapManager != null) {
            this.mapManager.dispose();
        }
        hud.dispose();
    }
}
