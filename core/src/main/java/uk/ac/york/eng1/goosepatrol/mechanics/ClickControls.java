package uk.ac.york.eng1.goosepatrol.mechanics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.Item;

public class ClickControls {

    private boolean isBoosted;
    private Inventory inventory;

    ClickControls(Inventory playerItems) {
        isBoosted = false;
        inventory = playerItems;
    }

    public void activateItem(PolygonSprite player, Item item) {
        if (player.getBoundingRectangle().contains(Gdx.input.getX(), Gdx.input.getY())) {
            inventory.use(player.getX() + 0.5f*player.getWidth(),
                player.getY() + 0.5f*player.getHeight());
        }
    }

    public void activateItem(Sprite goose, Item item) {
        if (goose.getBoundingRectangle().contains(Gdx.input.getX(), Gdx.input.getY())) {
            inventory.use(goose.getX() + 0.5f*goose.getWidth(),
                goose.getY() + 0.5f*goose.getHeight());
        }
    }

    public void throwItem(Item item, float x, float y) {
            inventory.use(x,y);
        }


    public boolean openInventory(Window inventMenu, Inventory itemList){

        return false;
    }

    protected boolean scrollItems(Item[] items) {
        return false;
    }

}
