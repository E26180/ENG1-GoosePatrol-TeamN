package uk.ac.york.eng1.goosepatrol.screen;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import uk.ac.york.eng1.goosepatrol.hidden_event.HEGooseSpeed;
import uk.ac.york.eng1.goosepatrol.hidden_event.HEItems;
import uk.ac.york.eng1.goosepatrol.Main;
import uk.ac.york.eng1.goosepatrol.MapManager;
import uk.ac.york.eng1.goosepatrol.game.EventSpot;
import uk.ac.york.eng1.goosepatrol.game.GameSession;
import uk.ac.york.eng1.goosepatrol.game.GameState;
import uk.ac.york.eng1.goosepatrol.game.GameTimer;
import uk.ac.york.eng1.goosepatrol.game.NoGeese;
import uk.ac.york.eng1.goosepatrol.game.WaveStatus;
import uk.ac.york.eng1.goosepatrol.item.BreadItem;
import uk.ac.york.eng1.goosepatrol.item.EnergyDrinkItem;
import uk.ac.york.eng1.goosepatrol.item.GooseLocator;
import uk.ac.york.eng1.goosepatrol.item.Inventory;
import uk.ac.york.eng1.goosepatrol.item.InventoryInput;
import uk.ac.york.eng1.goosepatrol.item.Item;
import uk.ac.york.eng1.goosepatrol.item.OatsItem;
import uk.ac.york.eng1.goosepatrol.player.Player;

/**
 * The screen of one game. It puts the map, the player, the items, the hidden events
 * and the geese together, reads the keyboard and mouse, and draws the world and the HUD.
 * The rules themselves live in {@link GameSession}, {@link Player} and the item classes.
 */
public class GameScreen extends ScreenAdapter {

    /** Width of the part of the map shown on screen, in world units. */
    public static final float VIEW_WIDTH = 640f;

    /** Height of the part of the map shown on screen, in world units. */
    public static final float VIEW_HEIGHT = 480f;

    /** Seconds per frame of the walking animation. */
    private static final float WALK_FRAME_SECONDS = 0.18f;

    /** Extra bread the item hidden event gives. */
    private static final int EVENT_EXTRA_BREAD = 10;

    private final Main game;
    private final MapManager mapManager;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final SpriteBatch batch = new SpriteBatch();
    private final Texture playerSheet = new Texture("textures/player/student.png");
    private final TextureRegion[] walkFrames;
    private final Hud hud = new Hud();

    /**
     * The geese. {@link NoGeese} stands in until the goose manager exists;
     * the goose manager should implement both {@link GooseLocator} and {@link WaveStatus}.
     */
    private final NoGeese geese = new NoGeese();

    private final Player player;
    private final Inventory inventory = new Inventory();
    private final InventoryInput inventoryInput = new InventoryInput(inventory);
    private final GameSession session = new GameSession(new GameTimer(), geese);
    private final List<EventSpot> events = new ArrayList<>();
    private final Vector2 mouse = new Vector2();
    private final BreadItem bread;
    private float walkTime = 0f;

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
        float[] start = Player.findFreeSpot(world, mapManager.getCollisionManager()::collidesWith,
            world.width / 2f, world.height / 2f);
        player = new Player(start[0], start[1], world, mapManager.getCollisionManager()::collidesWith);

        // FR_START_INVENTORY: one single-target and one area item.
        inventory.addItem(new OatsItem(geese));
        bread = new BreadItem(geese);
        inventory.addItem(bread);

        TextureRegion[][] split = TextureRegion.split(playerSheet, 32, 32);
        walkFrames = split[0];

        placeEvents(world);
    }

    /** Puts the two hidden events on free spots of the map (FR_EVENTS, FR_EVENT_EFFECT). */
    private void placeEvents(Rectangle world) {
        float[] helpSpot = Player.findFreeSpot(world, mapManager.getCollisionManager()::collidesWith,
            world.width * 0.25f, world.height * 0.7f);
        events.add(new EventSpot(new HEItems(helpSpot[0], helpSpot[1]), () -> {
            bread.addUses(EVENT_EXTRA_BREAD);
            inventory.addItem(new EnergyDrinkItem(player));
            hud.showMessage("Hidden event: a food stash! +" + EVENT_EXTRA_BREAD
                + " bread and " + EnergyDrinkItem.DEFAULT_USES + " energy drinks (key 3)");
        }));

        float[] hinderSpot = Player.findFreeSpot(world, mapManager.getCollisionManager()::collidesWith,
            world.width * 0.75f, world.height * 0.3f);
        events.add(new EventSpot(new HEGooseSpeed(hinderSpot[0], hinderSpot[1]), () ->
            // The goose manager reads HEGooseSpeed.checkActive() to speed its geese up.
            hud.showMessage("Hidden event: the geese smell food and get faster for 10 seconds!")));
    }

    @Override
    public void render(float delta) {
        if (handleInput()) {
            // A new game replaced this screen, which is now disposed.
            game.setScreen(new GameScreen(game));
            return;
        }
        update(delta);
        draw(delta);
    }

    /**
     * Reads the keys that change the game state, the item keys and the mouse.
     *
     * @return true if the player asked for a new game
     */
    private boolean handleInput() {
        GameState state = session.getState();
        if (state == GameState.READY && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            session.play();
        } else if (state == GameState.PAUSED && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            session.play();
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

    private void useSelectedItem(float x, float y) {
        Item item = inventory.getItems().get(inventory.getSelectedNumber() - 1);
        if (inventory.useSelected(x, y)) {
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
            }
        }
        session.update(delta);
    }

    /** Draws the map around the player, the player, then the HUD. */
    private void draw(float delta) {
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        followPlayer();
        viewport.apply();
        mapManager.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        TextureRegion frame = walkFrames[(int) (walkTime / WALK_FRAME_SECONDS) % walkFrames.length];
        Rectangle b = player.getBounds();
        float drawX = player.getCenterX() - 16f;
        float drawY = b.y - 3f;
        if (player.isFacingLeft()) {
            batch.draw(frame, drawX + 32f, drawY, -32f, 32f);
        } else {
            batch.draw(frame, drawX, drawY, 32f, 32f);
        }
        batch.end();

        hud.render(delta, session, geese, inventory, player);
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
        batch.dispose();
        playerSheet.dispose();
        hud.dispose();
        mapManager.dispose();
    }
}
