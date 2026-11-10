package uk.ac.york.eng1.goosepatrol.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

/**
 * Handles inventory selection and item use.
 */
public class ItemInput {

    private final Inventory bag;
    private final Camera cam;
    private final Vector3 pos;

    /**
     * Creates input handling for the inventory.
     *
     * @param bag inventory to control
     * @param cam camera used to draw the map
     */
    public ItemInput(Inventory bag, Camera cam) {
        this.bag = bag;
        this.cam = cam;
        this.pos = new Vector3();
    }

    /**
     * Checks keys 1 to 9 for selection and left clicks for item use.
     */
    public void update() {
        for (int i = 0; i < 9; i++) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1 + i)) {
                this.bag.select(i);
            }
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            this.pos.set(Gdx.input.getX(), Gdx.input.getY(), 0);

            // Convert the screen position to map coordinates.
            this.cam.unproject(this.pos);

            this.bag.use(this.pos.x, this.pos.y);
        }
    }
}
