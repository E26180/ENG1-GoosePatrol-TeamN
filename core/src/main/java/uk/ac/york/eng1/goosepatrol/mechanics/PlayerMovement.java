package uk.ac.york.eng1.goosepatrol.mechanics;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import uk.ac.york.eng1.goosepatrol.geese.Geese;
import uk.ac.york.eng1.goosepatrol.item.Feedable;
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.Item;

import java.util.ArrayList;
import java.util.List;


public class PlayerMovement {

    private float playerVelocity;
    private boolean isBoosted;
    float delta = Gdx.graphics.getDeltaTime();

    PlayerMovement() {
        playerVelocity = 3;
        isBoosted = false;
    }

    PlayerMovement(float initVelocity, boolean initBoost) {
        playerVelocity = initVelocity;
        isBoosted = initBoost;
    }

    public float getPlayerVelocity(){
        return playerVelocity;
    }

    public void setPlayerVelocity(float newVelocity) {
        playerVelocity = newVelocity;
    }

    public boolean getIsBoosted() {
        return isBoosted;
    }

    public void boost(boolean turnOn) {
        if (turnOn) {
            if (!isBoosted) {
                playerVelocity *= 1.5f;
            }
            isBoosted = true;
        } else {
            if (isBoosted) {
                playerVelocity *= (2/3f);
            }
            isBoosted = false;
        }
    }

    public void moveLeft(PolygonSprite playerSprite, float velocity) {
        playerSprite.translateX(-velocity*delta);
    }

    public void moveRight(PolygonSprite playerSprite, float velocity) {
        playerSprite.translateX(velocity*delta);
    }

    public void moveUp(PolygonSprite playerSprite, float velocity) {
        playerSprite.translateY(velocity*delta);
    }

    public void moveDown(PolygonSprite playerSprite, float velocity) {
        playerSprite.translateY(-velocity*delta);
    }

    public void moveNW(PolygonSprite playerSprite, float velocity) {
        playerSprite.translateX((float) (Math.sqrt(2)*velocity*delta));
        playerSprite.translateY((float) (-Math.sqrt(2)*velocity*delta));
    }

    public void moveNE(PolygonSprite playerSprite, float velocity) {
        playerSprite.translateX((float) (Math.sqrt(2)*velocity*delta));
        playerSprite.translateY((float) (Math.sqrt(2)*velocity*delta));
    }

    public void moveSE(PolygonSprite playerSprite, float velocity) {
        playerSprite.translateX((float) (-Math.sqrt(2)*velocity*delta));
        playerSprite.translateY((float) (Math.sqrt(2)*velocity*delta));
    }

    public void moveSW(PolygonSprite playerSprite, float velocity) {
        playerSprite.translateX((float) (-Math.sqrt(2)*velocity*delta));
        playerSprite.translateY((float) (-Math.sqrt(2)*velocity*delta));
    }

    public boolean quickUse(Item item, List<Feedable> geese) {
        if (item.canUse()) {
            item.use(0,0, geese);
        }
        return false;
    }

    public void pauseKey(Screen gameScreen) {
        gameScreen.pause();
    }

    protected boolean dropItem(Item item, Inventory equippedItems) {
        List<Item> itemList = equippedItems.getItems();
        List<Feedable> geeseList = new ArrayList<>();
        if (itemList.contains(item)) {
            int itemSlot = itemList.indexOf(item);
            equippedItems = new Inventory(geeseList);
            for (int i=0; i<itemList.size(); i++) {
                if (i!=itemSlot) {
                    equippedItems.addItem(itemList.get(i));
                    i++;
                } else {
                    equippedItems.addItem(null);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    protected void scare(PolygonSprite playerSprite, Sprite[] geese) {
        Circle scarecircle = new Circle(playerSprite.getX(), playerSprite.getY(), 2f);
        for (Sprite goose : geese) {
            if (scarecircle.contains(goose.getX(), goose.getY())
            || scarecircle.contains(goose.getX(), goose.getY()+goose.getHeight())
            || scarecircle.contains(goose.getX()+goose.getWidth(), goose.getY()+goose.getHeight())
            || scarecircle.contains(goose.getX()+goose.getWidth(), goose.getY())) {
                for (int i = 0; i < Gdx.graphics.getFramesPerSecond(); ) {
                    goose.setOrigin(scarecircle.x , scarecircle.y);
                    float scaredDirection = -goose.getRotation();
                    goose.translate(MathUtils.cos(scaredDirection)*delta,
                        MathUtils.sin(scaredDirection)*delta);
                }
            }
        }
    }


}
