package uk.ac.york.eng1.goosepatrol;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;
    private MapManager mapManager;

    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        mapManager = new MapManager();

    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
        mapManager.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
        if(mapManager != null){
            mapManager.dispose();
        }
    }
}
