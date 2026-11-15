package uk.ac.york.eng1.goosepatrol.mechanics;

import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import uk.ac.york.eng1.goosepatrol.item.Inventory;

public class MouseInput implements ControlsInput {


    @Override
    public void playerInput(int key, PolygonSprite player, Inventory inventory) {

    }

    @Override
    public boolean processInput(int key, PolygonSprite player, Inventory inventory) {
        return false;
    }
}
