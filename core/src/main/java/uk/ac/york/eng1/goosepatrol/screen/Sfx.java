package uk.ac.york.eng1.goosepatrol.screen;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Plays the sound effects. Every sound goes with a visual cue as well (QR_ACCESS_CUES),
 * so the game can be played muted or on a computer without sound.
 */
public class Sfx implements Disposable {

    /** The sound effects, each a WAV file in {@code assets/audio}. */
    public enum Cue {
        /** A goose is about to snatch a snack (QR_ALERT). */
        HONK("honk"),
        /** Food was thrown or given. */
        EAT("eat"),
        /** A hidden event was triggered. */
        EVENT("event"),
        /** A new wave of geese starts. */
        WAVE("wave"),
        /** The player won. */
        WIN("win"),
        /** The time ran out. */
        LOSE("lose");

        private final String file;

        Cue(String file) {
            this.file = file;
        }
    }

    /** The same sound is not started again within this many milliseconds, so many geese do not deafen. */
    private static final long MIN_GAP_MS = 150;

    private static final float VOLUME = 0.7f;

    private final Map<Cue, Sound> sounds = new EnumMap<>(Cue.class);
    private final Map<Cue, Long> lastPlayed = new EnumMap<>(Cue.class);
    private boolean muted = false;

    /** Loads the sounds. If there is no audio device the game carries on silently. */
    public Sfx() {
        if (Gdx.audio == null) {
            return;
        }
        for (Cue cue : Cue.values()) {
            try {
                sounds.put(cue, Gdx.audio.newSound(Gdx.files.internal("audio/" + cue.file + ".wav")));
            } catch (GdxRuntimeException e) {
                Gdx.app.log("Sfx", "could not load " + cue.file + ".wav: " + e.getMessage());
            }
        }
    }

    /**
     * Plays a sound, unless the sound is off or the same sound has just started.
     *
     * @param cue the sound to play
     */
    public void play(Cue cue) {
        Sound sound = sounds.get(cue);
        if (muted || sound == null) {
            return;
        }
        long now = TimeUtils.millis();
        Long last = lastPlayed.get(cue);
        if (last != null && now - last < MIN_GAP_MS) {
            return;
        }
        lastPlayed.put(cue, now);
        sound.play(VOLUME);
    }

    /** Turns the sound off, or back on. */
    public void toggleMute() {
        muted = !muted;
    }

    /**
     * Tells whether the sound is off.
     *
     * @return true if muted
     */
    public boolean isMuted() {
        return muted;
    }

    @Override
    public void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        sounds.clear();
    }
}
