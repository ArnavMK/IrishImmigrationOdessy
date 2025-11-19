package com.ise.officeescape.view;

import com.ise.officeescape.model.Room;
import javafx.scene.image.Image;

/**
 * Maps Room objects to their corresponding image assets.
 * This keeps rendering logic completely separate from the model.
 */
public class RoomImageMapper {
    
    private static final String ASSETS_PATH = "/com/ise/officeescape/assets/";
    
    /**
     * Gets the image path for a given room based on its description.
     * The model Room doesn't know about images - this class handles the mapping.
     */
    public String getImagePath(Room room) {
       return ASSETS_PATH + room.getName() + ".png";
    }
    
    /**
     * Loads and returns an Image object for the given room.
     * Returns null if image cannot be loaded (handled gracefully by view).
     */
    public Image loadRoomImage(Room room) {
        String imagePath = getImagePath(room);
        try {
            return new Image(getClass().getResourceAsStream(imagePath));
        } catch (Exception e) {
            System.err.println("Could not load image: " + imagePath);
            return null;
        }
    }
}
