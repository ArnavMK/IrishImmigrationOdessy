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
        if (room == null) {
            return ASSETS_PATH + "default.png";
        }
        
        String description = room.getDescription().toLowerCase();
        
        // Map room descriptions to image filenames
        if (description.contains("outside")) {
            return ASSETS_PATH + "outside.png";
        } else if (description.contains("queue")) {
            return ASSETS_PATH + "queue.png";
        } else if (description.contains("security")) {
            return ASSETS_PATH + "security.png";
        } else if (description.contains("ticket")) {
            return ASSETS_PATH + "ticket.png";
        } else if (description.contains("document")) {
            return ASSETS_PATH + "documents.png";
        } else if (description.contains("interview")) {
            return ASSETS_PATH + "interview.png";
        } else if (description.contains("approval")) {
            return ASSETS_PATH + "approval.png";
        } else if (description.contains("exit")) {
            return ASSETS_PATH + "exit.png";
        }
        
        // Default fallback
        return ASSETS_PATH + "default.png";
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
