package uk.ac.york.eng1.goosepatrol;

/**
 * Something with a hunger bar that food can fill, normally a goose.
 * The Goose class implements this, so the items do not depend on it.
 */
public interface Feedable {

    /**
     * Fills the hunger bar.
     *
     * @param amount the share of the whole bar to fill (0.5 means half of the bar)
     */
    void feed(float amount);
}
