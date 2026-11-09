package uk.ac.york.eng1.goosepatrol.goose;

/**
 * One wave of geese (FR_WAVES): how many of each kind arrive together.
 *
 * @param geese     number of ordinary geese
 * @param headGeese number of head geese, which arrive last
 */
public record Wave(int geese, int headGeese) {

    /**
     * Checks the counts.
     *
     * @param geese     number of ordinary geese
     * @param headGeese number of head geese
     * @throws IllegalArgumentException if a count is negative or the wave is empty
     */
    public Wave {
        if (geese < 0 || headGeese < 0 || geese + headGeese == 0) {
            throw new IllegalArgumentException("a wave needs at least one goose and no negative counts");
        }
    }

    /**
     * Returns how many full hunger bars the whole wave needs, for balancing (QR_WINNABLE).
     *
     * @return the total appetite of the wave
     */
    public float totalAppetite() {
        return geese * GooseType.NORMAL.getAppetite() + headGeese * GooseType.HEAD.getAppetite();
    }
}
