package uk.ac.york.eng1.goosepatrol.mechanics;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface CharacterBatch {

    public void createCharacter();

    public void deleteCharacter();

    public void faceDown();

    public void faceUp();

    public void faceLeft();

    public void faceRight();


}
