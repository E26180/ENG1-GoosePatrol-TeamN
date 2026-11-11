package uk.ac.york.eng1.goosepatrol;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

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

    private final List<Feedable> geese = new ArrayList<>();

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

        this.batch.begin();
        this.batch.draw(this.image, 140, 210);
        this.batch.end();

        this.mapManager.render();
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
    }
}
