package uk.ac.york.eng1.goosepatrol.screen;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import uk.ac.york.eng1.goosepatrol.Main;
import uk.ac.york.eng1.goosepatrol.MapManager;
import uk.ac.york.eng1.goosepatrol.game.EventSpot;
import uk.ac.york.eng1.goosepatrol.game.GameSession;
import uk.ac.york.eng1.goosepatrol.game.GameState;
import uk.ac.york.eng1.goosepatrol.game.GameTimer;
import uk.ac.york.eng1.goosepatrol.game.Tutorial;
import uk.ac.york.eng1.goosepatrol.goose.Goose;
import uk.ac.york.eng1.goosepatrol.goose.GooseListener;
import uk.ac.york.eng1.goosepatrol.goose.GooseManager;
import uk.ac.york.eng1.goosepatrol.hidden_event.HEGooseSpeed;
import uk.ac.york.eng1.goosepatrol.hidden_event.HEItems;
import uk.ac.york.eng1.goosepatrol.item.BreadItem;
import uk.ac.york.eng1.goosepatrol.item.EnergyDrinkItem;
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.InventoryInput;
import uk.ac.york.eng1.goosepatrol.item.Item;
import uk.ac.york.eng1.goosepatrol.item.OatsItem;
import uk.ac.york.eng1.goosepatrol.player.CollisionChecker;
import uk.ac.york.eng1.goosepatrol.player.Player;

/**
 * The screen of one game. It puts the map, the player, the items, the hidden events
 * and the geese together, reads the keyboard and mouse, and draws the world and the HUD.
 * The rules themselves live in {@link GameSession}, {@link Player}, {@link GooseManager}
 * and the item classes.
 */
public class GameScreen extends ScreenAdapter {

    /** Width of the part of the map shown on screen, in world units. */
    public static final float VIEW_WIDTH = 640f;

    /** Height of the part of the map shown on screen, in world units. */
    public static final float VIEW_HEIGHT = 480f;

    /** Extra bread the item hidden event gives. */
    public static final int EVENT_EXTRA_BREAD = 10;

    /** Extra oats given each time a wave is cleared, so the game stays winnable (QR_WINNABLE). */
    public static final int OATS_PER_CLEARED_WAVE = 5;

    /** How much faster the geese walk during the goose speed event (QR_AVOID_EXTREME). */
    public static final float EVENT_GOOSE_SPEED = 1.3f;

    /** How long the goose speed event lasts. */
    public static final float EVENT_GOOSE_SECONDS = 10f;

    private final Main game;
    private final MapManager mapManager;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final CollisionChecker map;
    private final Hud hud = new Hud();
    private final WorldRenderer renderer = new WorldRenderer();
    private final Sfx sfx = new Sfx();
    private final Tutorial tutorial = new Tutorial();

    private final Player player;
    private final GooseManager geese;
    private final GameSession session;
    private final Inventory inventory = new Inventory();
    private final InventoryInput inventoryInput = new InventoryInput(inventory);
    private final OatsItem oats;
    private final BreadItem bread;
    private final List<EventSpot> events = new ArrayList<>();
    private final Vector2 mouse = new Vector2();
    private float walkTime = 0f;
    private GameState lastState = GameState.READY;

    /**
     * Creates a new game, waiting on the start screen.
     *
     * @param game the application, used to restart
     */
    public GameScreen(Main game) {
        this.game = game;
        mapManager = new MapManager();
        camera = mapManager.getCamera();
        viewport = new FitViewport(VIEW_WIDTH, VIEW_HEIGHT, camera);

        Rectangle world = new Rectangle(0f, 0f, mapManager.getMapWidth(), mapManager.getMapHeight());
        map = mapManager.getCollisionManager()::collidesWith;
        float[] start = Player.findFreeSpot(world, map, world.width / 2f, world.height / 2f);
        player = new Player(start[0], start[1], world, this::blocksPlayer);
        geese = new GooseManager(world, map, player, new Feedback());
        session = new GameSession(new GameTimer(), geese);

        // FR_START_INVENTORY: one single-target and one area item.
        oats = new OatsItem(geese);
        bread = new BreadItem(geese);
        inventory.addItem(oats);
        inventory.addItem(bread);

        placeEvents(world);
    }

    /**
     * Tells whether the player may not stand in a space: buildings and hungry geese
     * block them (QR_COLLISION, UR_BOUNDS).
     */
    private boolean blocksPlayer(Rectangle bounds) {
        return map.collidesWith(bounds) || (geese != null && geese.collidesWith(bounds));
    }

    /** Turns what happens to the geese into messages and sounds. */
    private class Feedback implements GooseListener {
        @Override
        public void waveStarted(int number, int total) {
            hud.showMessage("Wave " + number + " of " + total + ": hungry geese are waddling in from the lake!");
            sfx.play(Sfx.Cue.WAVE);
            tutorial.trigger(Tutorial.Tip.FEED);
        }

        @Override
        public void gooseAlert(Goose goose) {
            sfx.play(Sfx.Cue.HONK);
            tutorial.trigger(Tutorial.Tip.DODGE);
        }

        @Override
        public void gooseFull(Goose goose) {
            tutorial.trigger(Tutorial.Tip.BREAD);
        }

        @Override
        public void playerSnatched(Goose goose) {
            hud.showMessage("A goose snatched a snack! You are startled: slower for "
                + (int) GooseManager.STARTLE_SECONDS + " s");
        }

        @Override
        public void waveCleared(int number) {
            if (number < geese.totalWaves()) {
                oats.addUses(OATS_PER_CLEARED_WAVE);
                hud.showMessage("Wave " + number + " fed! A friendly student at the Piazza gives you "
                    + OATS_PER_CLEARED_WAVE + " more oats");
            }
        }
    }

    /**
     * Puts the two hidden events on free spots of the map (FR_EVENTS, FR_EVENT_EFFECT):
     * one helps the player, the other speeds the geese up.
     */
    private void placeEvents(Rectangle world) {
        float[] helpSpot = Player.findFreeSpot(world, map, world.width * 0.25f, world.height * 0.7f);
        events.add(new EventSpot(new HEItems(helpSpot[0], helpSpot[1]), () -> {
            bread.addUses(EVENT_EXTRA_BREAD);
            inventory.addItem(new EnergyDrinkItem(player));
            hud.showMessage("Hidden event: a lost backpack full of picnic food! +" + EVENT_EXTRA_BREAD
                + " bread and " + EnergyDrinkItem.DEFAULT_USES + " energy drinks (key 3)");
            sfx.play(Sfx.Cue.EVENT);
        }));

        // on the grass by Campus East Lake, clear of the trees
        float[] hinderSpot = Player.findFreeSpot(world, map, world.width * 0.79f, world.height * 0.32f);
        events.add(new EventSpot(new HEGooseSpeed(hinderSpot[0], hinderSpot[1]), () -> {
            geese.speedUp(EVENT_GOOSE_SPEED, EVENT_GOOSE_SECONDS);
            hud.showMessage("Hidden event: a tall duck (Long Boi's cousin?) tips over a bucket of chips! Geese "
                + Math.round((EVENT_GOOSE_SPEED - 1f) * 100) + "% faster for " + (int) EVENT_GOOSE_SECONDS + " s");
            sfx.play(Sfx.Cue.EVENT);
        }));
    }

    @Override
    public void render(float delta) {
        if (handleInput()) {
            // A new game replaced this screen, which is now disposed.
            game.setScreen(new GameScreen(game));
            return;
        }
        update(delta);
        playResultSound();
        draw(delta);
    }

    /**
     * Reads the keys that change the game state, the item keys and the mouse.
     *
     * @return true if the player asked for a new game
     */
    private boolean handleInput() {
        GameState state = session.getState();
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            tutorial.toggle();
            hud.showMessage(tutorial.isEnabled() ? "Tips on" : "Tips off (T to turn them on)");
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            sfx.toggleMute();
            hud.showMessage(sfx.isMuted() ? "Sound off (M to turn it on)" : "Sound on");
        }
        if ((state == GameState.READY || state == GameState.PAUSED) && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            session.play();
            tutorial.trigger(Tutorial.Tip.WALK);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            session.togglePause();
        } else if (state.isOver() && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            return true;
        }

        if (!session.isPlaying()) {
            return false;
        }
        inventoryInput.update();
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            viewport.unproject(mouse.set(Gdx.input.getX(), Gdx.input.getY()));
            useSelectedItem(mouse.x, mouse.y);
        }
        return false;
    }

    /** Uses the selected item where the player clicked, and says why if it could not be used. */
    private void useSelectedItem(float x, float y) {
        Item item = inventory.getItems().get(inventory.getSelectedNumber() - 1);
        if (inventory.useSelected(x, y)) {
            if (item instanceof BreadItem) {
                renderer.addSplash(x, y, BreadItem.RADIUS_PIXELS);
                sfx.play(Sfx.Cue.EAT);
            } else if (item instanceof OatsItem) {
                renderer.addSplash(x, y, 14f);
                sfx.play(Sfx.Cue.EAT);
                tutorial.trigger(Tutorial.Tip.FED);
            } else if (item instanceof EnergyDrinkItem) {
                hud.showMessage("Energy drink: " + Math.round((EnergyDrinkItem.SPEED_MULTIPLIER - 1f) * 100)
                    + "% faster for " + (int) EnergyDrinkItem.BOOST_SECONDS + " s");
            }
            return;
        }
        if (item.getUsesLeft() == 0) {
            hud.showMessage(item.getName() + " is all used up");
        } else if (item.getCooldownLeft() > 0f) {
            hud.showMessage(item.getName() + " is not ready yet");
        } else {
            hud.showMessage("No goose there - click on a goose to feed it");
        }
    }

    /** Moves everything on by one frame while the game is running. */
    private void update(float delta) {
        if (!session.isPlaying()) {
            return;
        }
        float dirX = 0f;
        float dirY = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) dirX -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) dirX += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) dirY -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) dirY += 1f;
        player.update(delta, dirX, dirY);
        walkTime = player.isMoving() ? walkTime + delta : 0f;

        inventory.update(delta);
        geese.update(delta, player.getCenterX(), player.getCenterY());
        for (EventSpot spot : events) {
            if (spot.update(player.getPosition())) {
                session.eventTriggered();
            } else if (!spot.isTriggered() && spot.getEvent().isPlayerInFlagRange(player.getPosition())) {
                tutorial.trigger(Tutorial.Tip.EVENT);
            }
        }
        tutorial.update(delta);
        renderer.update(delta);
        session.update(delta);
    }

    /** Plays the win or loss sound once, when the game ends. */
    private void playResultSound() {
        GameState state = session.getState();
        if (state != lastState) {
            if (state == GameState.WON) {
                sfx.play(Sfx.Cue.WIN);
            } else if (state == GameState.LOST) {
                sfx.play(Sfx.Cue.LOSE);
            }
            lastState = state;
        }
    }

    /** Draws the map around the player, the world on top of it, then the HUD. */
    private void draw(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        followPlayer();
        viewport.apply();
        mapManager.render();
        renderer.render(camera, player, walkTime, geese.getGeese(), events, Landmark.CAMPUS_EAST);
        Tutorial.Tip tip = tutorial.current();
        hud.render(delta, session, geese, inventory, player, geese.getSpeedUpLeft(), sfx.isMuted(),
            tip == null ? null : tip.getText());
    }

    /** Centres the camera on the player without showing anything outside the map. */
    private void followPlayer() {
        float halfW = VIEW_WIDTH / 2f;
        float halfH = VIEW_HEIGHT / 2f;
        camera.position.x = MathUtils.clamp(player.getCenterX(), halfW, mapManager.getMapWidth() - halfW);
        camera.position.y = MathUtils.clamp(player.getCenterY(), halfH, mapManager.getMapHeight() - halfH);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hud.resize(width, height);
    }

    @Override
    public void pause() {
        // The window lost focus: pause so the timer does not run in the background.
        if (session.isPlaying()) {
            session.togglePause();
        }
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        renderer.dispose();
        hud.dispose();
        sfx.dispose();
        mapManager.dispose();
    }
}
