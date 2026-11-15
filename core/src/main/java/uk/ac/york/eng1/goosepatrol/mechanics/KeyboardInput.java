package uk.ac.york.eng1.goosepatrol.mechanics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.*;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.Sprite;
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.Item;

import java.util.ArrayList;

public class KeyboardInput implements ControlsInput {

    enum USER_INPUTS {
        LEFT_UP, A_W, RIGHT_UP, D_W, RIGHT_DOWN, D_S, LEFT_DOWN, A_S}
    private PlayerMovement controls;

    public KeyboardInput() {
        controls = new PlayerMovement();
    }

    public KeyboardInput(PlayerMovement customControls) {
        controls = customControls;
    }

    public PlayerMovement getControls() {
        return controls;
    }

    public void setControls(PlayerMovement newControls) {
        controls = newControls;
    }

    @Override
    public void playerInput(int key, PolygonSprite player, Inventory inventory) {
        String keyButton = Keys.toString(key);
        float movementSpeed = controls.getPlayerVelocity();
        Item selectedItem = inventory.getSelected();
        switch (key) {
            case Keys.LEFT, Keys.A:
                if (Gdx.input.isKeyPressed(Keys.UP)){
                    controls.moveNW(player, movementSpeed);
                } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                    controls.moveSW(player, movementSpeed);
                } else {
                    controls.moveLeft(player, movementSpeed);
                } break;
            case Keys.RIGHT, Keys.D:
                if (Gdx.input.isKeyPressed(Keys.UP)){
                    controls.moveNE(player, movementSpeed);
                } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                    controls.moveSE(player, movementSpeed);
                } else {
                    controls.moveRight(player, movementSpeed);
                } break;
            case Keys.UP, Keys.W:
                if (Gdx.input.isKeyPressed(Keys.LEFT)){
                    controls.moveNW(player, movementSpeed);
                } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                    controls.moveNE(player, movementSpeed);
                } else {
                    controls.moveUp(player, movementSpeed);
                } break;
            case Keys.DOWN, Keys.S:
                if (Gdx.input.isKeyPressed(Keys.LEFT)){
                    controls.moveSW(player, movementSpeed);
                } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                    controls.moveSE(player, movementSpeed);
                } else {
                    controls.moveDown(player, movementSpeed);
                } break;
            case Keys.F:
                controls.quickUse(selectedItem, new ArrayList<>());
                break;
            case Keys.C:
                controls.scare(player, new Sprite[2]);
            case Keys.T:
                boolean anySelected = controls.dropItem(selectedItem, inventory);
        }
    }

    @Override
    public boolean processInput(int key, PolygonSprite player, Inventory inventory) {
        if (Gdx.input.isKeyPressed(key)) {
            playerInput(key, player, inventory);
            return true;
        } else { return false;}
    }

    public boolean cancelInput(int key, PolygonSprite player) {
        if (Gdx.input.isKeyJustPressed(key)) {
            player.setPosition(player.getX(), player.getY());
            return true;
        } else { return false;}
    }







}
