package uk.ac.york.eng1.goosepatrol.game;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import java.util.Set;

/**
 * Short tips that teach the game while it is played, so a player who has never played
 * this kind of game knows what to do (UR_NO_EXPERIENCE, QR_NEWBIE). Each tip appears once,
 * when the game first reaches the moment it is about. The player can hide the tips.
 */
public class Tutorial {

    /** The tips, in the order a player usually meets them. */
    public enum Tip {
        /** The game has just started. */
        WALK("Walk with WASD or the arrow keys. The geese are on their way!"),
        /** The first wave has arrived. */
        FEED("Press 1 for the Bag of Oats, then click on a goose to feed it."),
        /** The player has fed a goose for the first time. */
        FED("Yum! Two oats fill a goose, and a full goose flies away happily."),
        /** The first goose has flown away full. */
        BREAD("Press 2 for Bread and throw it near a group of geese to feed them all."),
        /** A goose is warning for the first time. */
        DODGE("A red, honking goose wants your snack: step away, or feed it quickly!"),
        /** The player is near a hidden event for the first time. */
        EVENT("Something odd here... walk right up to it to see what happens.");

        private final String text;

        Tip(String text) {
            this.text = text;
        }

        /**
         * Returns the text shown to the player.
         *
         * @return the tip text
         */
        public String getText() {
            return text;
        }
    }

    /** How long a tip stays if nothing else happens. */
    public static final float TIP_SECONDS = 7f;

    /** A tip stays at least this long before a newer one replaces it. */
    public static final float MIN_SECONDS = 2.5f;

    private final Set<Tip> used = EnumSet.noneOf(Tip.class);
    private final Deque<Tip> waiting = new ArrayDeque<>();
    private Tip current = null;
    private float shownFor = 0f;
    private boolean enabled = true;

    /**
     * Asks for a tip. A tip is only ever shown once per game; asking again does nothing.
     *
     * @param tip the tip for what just happened
     */
    public void trigger(Tip tip) {
        if (used.add(tip)) {
            waiting.add(tip);
            if (current == null) {
                next();
            }
        }
    }

    /**
     * Moves the tips on. Called once per frame while the game runs.
     *
     * @param delta seconds since the last frame
     */
    public void update(float delta) {
        if (current == null) {
            return;
        }
        shownFor += delta;
        boolean expired = shownFor >= TIP_SECONDS;
        boolean replaced = !waiting.isEmpty() && shownFor >= MIN_SECONDS;
        if (expired || replaced) {
            next();
        }
    }

    private void next() {
        current = waiting.poll();
        shownFor = 0f;
    }

    /** Hides the tips, or shows them again. */
    public void toggle() {
        enabled = !enabled;
    }

    /**
     * Returns the tip to show now.
     *
     * @return the tip, or null if there is none or the tips are hidden
     */
    public Tip current() {
        return enabled ? current : null;
    }

    /**
     * Tells whether tips are shown.
     *
     * @return true unless the player hid them
     */
    public boolean isEnabled() {
        return enabled;
    }
}
