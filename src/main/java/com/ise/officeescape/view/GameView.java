package com.ise.officeescape.view;


import com.ise.officeescape.model.Room;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * View that displays the current room's background image.
 * Simple and clean - just shows the room image.
 */
public class GameView extends StackPane {

    private ImageView backgroundImage;
    private RoomImageMapper roomImageMapper;
    private DirectionControllerView directionControllerView;

    public GameView() {
        this.roomImageMapper = new RoomImageMapper();
        initializeUI();
    }
    
    private void initializeUI() {
        // Background image view - fills the entire view
        backgroundImage = new ImageView();
        backgroundImage.setPreserveRatio(true);
        backgroundImage.setSmooth(true);
        
        // Make background image fill available space
        backgroundImage.fitWidthProperty().bind(widthProperty());
        backgroundImage.fitHeightProperty().bind(heightProperty());
    
        // Add background image first (will be behind other elements)
        getChildren().add(backgroundImage);
        
        // Create and add direction controller view
        // StackPane layers children in order - last added is on top
        directionControllerView = new DirectionControllerView();
        
        // Position at bottom left with padding (not touching corners)
        StackPane.setAlignment(directionControllerView, javafx.geometry.Pos.BOTTOM_LEFT);
        StackPane.setMargin(directionControllerView, new Insets(0, 0, 20, 20)); // bottom, right, top, left padding
        
        getChildren().add(directionControllerView);
    }
    
    public DirectionControllerView getDirectionControllerView() {
        return directionControllerView;
    }

    /**
     * Updates the background image when the player enters a new room.
     * This method is called by the controller when room changes.
     */
    public void updateRoomBackground(Room room) {

        // load the next room image
        Image roomImage = roomImageMapper.loadRoomImage(room);
        if (roomImage != null) {
            backgroundImage.setImage(roomImage);
        }
        
        // update button states
        if (room != null) {
            directionControllerView.updateButtonState(room.getAllExits());
        }
    }
}

