package uk.ac.york.eng1.goosepatrol.game;

import java.util.Collections;
import java.util.List;

import uk.ac.york.eng1.goosepatrol.item.Feedable;
import uk.ac.york.eng1.goosepatrol.item.GooseLocator;

/**
 * Stand-in for the goose manager until the geese package is ready.
 * It has no geese and never clears, so the game can be played (and only lost
 * when the time runs out) without any goose code. Replace it in
 * {@code GameScreen} with the real goose manager, which implements the same
 * two interfaces.
 */
public class NoGeese implements GooseLocator, WaveStatus {

    @Override
    public Feedable gooseAt(float x, float y) {
        return null;
    }

    @Override
    public List<? extends Feedable> geeseNear(float x, float y, float radius) {
        return Collections.emptyList();
    }

    @Override
    public void update(float delta, float playerX, float playerY) {
        // no geese to move
    }

    @Override
    public int geeseLeft() {
        return 0;
    }

    @Override
    public int wavesLeft() {
        return 0;
    }

    @Override
    public boolean allWavesCleared() {
        return false;
    }
}
