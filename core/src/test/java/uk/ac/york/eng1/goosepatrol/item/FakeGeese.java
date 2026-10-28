package uk.ac.york.eng1.goosepatrol.item;

import java.util.ArrayList;
import java.util.List;

/** A fake goose world for the item tests: geese at positions with a simple hunger value. */
class FakeGeese implements GooseLocator {

    /** A goose at a position that remembers how much it was fed. */
    static class FakeGoose implements Feedable {
        final float x;
        final float y;
        float hunger = 0f;

        FakeGoose(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void feed(float amount) {
            hunger += amount;
        }
    }

    final List<FakeGoose> geese = new ArrayList<>();

    FakeGoose add(float x, float y) {
        FakeGoose goose = new FakeGoose(x, y);
        geese.add(goose);
        return goose;
    }

    @Override
    public Feedable gooseAt(float x, float y) {
        for (FakeGoose goose : geese) {
            if (Math.hypot(goose.x - x, goose.y - y) <= 10f) {
                return goose;
            }
        }
        return null;
    }

    @Override
    public List<? extends Feedable> geeseNear(float x, float y, float radius) {
        List<FakeGoose> near = new ArrayList<>();
        for (FakeGoose goose : geese) {
            if (Math.hypot(goose.x - x, goose.y - y) <= radius) {
                near.add(goose);
            }
        }
        return near;
    }
}
