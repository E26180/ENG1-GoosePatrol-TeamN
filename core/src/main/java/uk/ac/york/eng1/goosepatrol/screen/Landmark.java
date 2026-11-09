package uk.ac.york.eng1.goosepatrol.screen;

import java.util.List;

/**
 * A named place on the campus map, drawn as a sign so the map reads as the University of
 * York's Campus East (UR_THEME, QR_THEMATIC).
 *
 * @param name the name on the sign
 * @param x    world x of the centre of the sign
 * @param y    world y of the bottom of the sign
 */
public record Landmark(String name, float x, float y) {

    /** The signs on the campus map, placed over the matching buildings and the lake. */
    public static final List<Landmark> CAMPUS_EAST = List.of(
        new Landmark("Computer Science", 653f, 832f),
        new Landmark("Ron Cooke Hub", 1010f, 832f),
        new Landmark("Piazza", 303f, 1132f),
        new Landmark("Constantine College", 1408f, 1145f),
        new Landmark("Campus East Lake", 1300f, 200f));
}
