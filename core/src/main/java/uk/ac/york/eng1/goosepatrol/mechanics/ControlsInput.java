package uk.ac.york.eng1.goosepatrol.mechanics;

import com.badlogic.gdx.graphics.g2d.PolygonSprite;

public interface ControlsInput {

    void playerInput(int key, PolygonSprite player);

    boolean processInput(int key, PolygonSprite player);

}
