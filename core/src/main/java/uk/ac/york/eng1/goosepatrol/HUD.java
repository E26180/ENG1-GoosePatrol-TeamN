package uk.ac.york.eng1.goosepatrol;
//GDX- Needed to know the size of the window
import com.badlogic.gdx.Gdx;
//OrthographicCamera- 2D camera for HUD
import com.badlogic.gdx.graphics.OrthographicCamera;
//BitmapFont- used to draw a text on the screen
import com.badlogic.gdx.graphics.g2d.BitmapFont;
//SpriteBatch- the tool that does all the drawing
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//Color- used to change the text colour
import com.badlogic.gdx.graphics.Color;
//ShapeRenderer- used to draw simple shapes like the feed bars
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/** Draws the heads-up display (timer, counters and warnings) on top of the game */
public class HUD {
    //Font used for all HUD text
    //final - this field is assigned a value only once, and then it does not change anymore
    private final BitmapFont font;

    //Separate camera for the HUD, so the text doesn't move with the game camera
    private final OrthographicCamera camera;

    //Draws the feed bars above the goose
    private final ShapeRenderer shapeRenderer;

    /** Creates the HUD and loads the default font and creates the shape renderer*/
    public HUD() {
        //libGDX built-in font, so we don't need a font file
        font = new BitmapFont();
        //Creates the HUD camera
        camera = new OrthographicCamera();
        //Make the camera show exactly the window (0, 0) is the bottom-left corner
        //false = the y-axis goes up, which is normal in libGDX
        camera.setToOrtho(false, Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
        //Tool for drawing simple shapes, used for the goose feed bars
        shapeRenderer= new ShapeRenderer();
    }

    /**
     * Draws the HUD on top of the game
     * @param batch the sprite batch used for drawing
     * @param secondsLeft the remaining time in seconds
     * @param paused true if the game is paused
     * @param itemsUsed how many items the player has used
     * @param eventsTriggered how many hidden events the player has triggered
     */
    public void draw(SpriteBatch batch, float secondsLeft, boolean paused, int itemsUsed, int eventsTriggered){
        //Apply any changes to the camera before using it
        camera.update();
        //Tell the batch to draw in HUD (screen) coordinates, not game world coordinates
        batch.setProjectionMatrix(camera.combined);
        //Small gap from the left edge
        float x= 10;
        //Full height minus a small gap
        float y= camera.viewportHeight - 10;
        //Start drawing (every begin() needs an end())
        batch.begin();
        //Show a warning when 30 sec or less are left
        if ((int) secondsLeft <= 30){
            //Warning colour for the timer
            font.setColor(Color.RED);
            //Draw the timer in red
            font.draw(batch, "Time: " + formatTime(secondsLeft), x, y);
            //Show a text warning, so the warning does not rely on colour alone
            font.draw(batch, "Hurry!", x+120, y);
        } else {
            //Normal timer in white
            font.draw(batch, "Time: " + formatTime(secondsLeft), x, y);
        }
        //Reset the colour, so the rest of the HUD text is white again
        font.setColor(Color.WHITE);
        //Number of items the player has used, one line below the timer
        font.draw(batch, "Items used: " + itemsUsed, x, y- 20);
        //Number of hidden events triggered, one more line below
        font.draw(batch, "Events triggered: "+ eventsTriggered, x, y- 40);

        //Show a clear text message when the game is paused
        if (paused) {
            //Roughly the middle of the screen (moved left a bit, so the word is centred)
            float pausedX = camera.viewportWidth / 2 - 30;
            //Middle of the screen vertically
            float pausedY = camera.viewportHeight / 2;
            //Draw the pause message
            font.draw(batch, "PAUSED", pausedX, pausedY);
        }
        //finish drawing and send everything to the screen
        batch.end();
    }

    /**
     * Updates the HUD camera when the window changes size
     * @param width the new window width in pixels
     * @param height the new window height in pixels
     */
    public void resize(int width, int height) {
        //Set the camera up again with the new window size
        camera.setToOrtho(false, width, height);
    }

    /**
     * Draws a feed bar above the goose, showing how full the goose is
     * @param worldCamera the game camera, so the bar moves with the map
     * @param gooseX the x position of the goose in the world
     * @param gooseY the y position of the goose in the world
     * @param feedLevel how full the goose is, from 0 (empty) to 1 (full)
     */
    public void drawFeedBar(OrthographicCamera worldCamera, float gooseX, float gooseY, float feedLevel){
        //Size of the bar in world units
        float barWidth= 80;
        float barHeight= 12;
        //Put the bar a little above the goose
        float barY= gooseY+ 40;

        //Use the GAME camera, so the bar stays above the goose when the map moves
        shapeRenderer.setProjectionMatrix(worldCamera.combined);
        //Start drawing filled shapes (every begin() needs an end())
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        //Grey background: shows the full length of the bar
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(gooseX, barY, barWidth, barHeight);

        //Green part: its length shows how full the goose is (not only the colour)
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(gooseX, barY, barWidth* feedLevel, barHeight);

        //Finish drawing the shapes
        shapeRenderer.end();
    }

    /**
     * Converts seconds into a mm:ss string for the timer
     * @param secondsLeft the remaining time in seconds
     * @return the time as mm:ss "01:35"
     */
    private String formatTime(float secondsLeft){
        //Remove the decimal part: 95.8 becomes 95
        int totalSeconds = (int) secondsLeft;
        //Never show negative time, use 0 instead
        totalSeconds = Math.max(0, totalSeconds);
        //Whole minutes: 95/60= 1
        int minutes = totalSeconds / 60;
        //Seconds left over after the minutes: 95%60= 35
        int seconds = totalSeconds % 60;
        //Build the text with 2 digits each: "01:35"
        return String.format("%02d:%02d", minutes, seconds);
    }

    /** Frees the memory used by the font and the shape renderer. Call this when the game closes */
    public void dispose() {
        //the font uses video memory, so it must be freed by hand
        font.dispose();
        //the shape renderer also uses video memory
        shapeRenderer.dispose();
    }

}
