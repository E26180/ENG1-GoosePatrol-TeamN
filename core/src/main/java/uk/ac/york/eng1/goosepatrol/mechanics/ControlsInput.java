package uk.ac.york.eng1.goosepatrol.mechanics;

import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import uk.ac.york.eng1.goosepatrol.item.Inventory;

public interface ControlsInput {

    void playerInput(int key, PolygonSprite player, Inventory inventory);

    boolean processInput(int key, PolygonSprite player, Inventory inventory);

}
