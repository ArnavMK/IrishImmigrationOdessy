package com.ise.officeescape.view;

import com.ise.officeescape.eventSystem.*;
import com.ise.officeescape.model.Room;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.HashMap;
import java.util.Map;

/**
 * View that displays the current room with background, hotspots, and interactions.
 */
public class GameView extends StackPane {

    private ImageView backgroundImage;
    private RoomImageMapper roomImageMapper;
    private DirectionControllerView directionControllerView;
    private Pane hotspotsLayer; // Layer for clickable hotspots
    
    // Event for hotspot clicks
    public Event<OnHotspotClickedEventArgs> OnHotspotClicked = new Event<>();
    public class OnHotspotClickedEventArgs extends EventArgs {
        public final String hotspotId;
        public OnHotspotClickedEventArgs(String hotspotId) {
            this.hotspotId = hotspotId;
        }
    }
    
    // Map to track hotspot buttons
    private Map<String, Button> hotspotButtons = new HashMap<>();

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
    
        // Hotspots layer - transparent overlay for clickable areas
        hotspotsLayer = new Pane();
        hotspotsLayer.setPickOnBounds(false); // Only click on actual buttons, not empty space
        
        // Add layers in order: background, hotspots, direction controller
        getChildren().add(backgroundImage);
        getChildren().add(hotspotsLayer);
        
        // Create and add direction controller view
        directionControllerView = new DirectionControllerView();
        StackPane.setAlignment(directionControllerView, javafx.geometry.Pos.BOTTOM_LEFT);
        StackPane.setMargin(directionControllerView, new Insets(0, 0, 20, 20));
        getChildren().add(directionControllerView);
    }
    
    public DirectionControllerView getDirectionControllerView() {
        return directionControllerView;
    }

    /**
     * Shows a room with all its hotspots and interactions.
     */
    public void showRoom(RoomViewModel viewModel) {
        System.out.println("[GameView] showRoom() called for room: " + viewModel.getRoom().getName());
        
        // Load and set background image
        Image roomImage = roomImageMapper.loadRoomImage(viewModel.getRoom());
        if (roomImage != null) {
            backgroundImage.setImage(roomImage);
            System.out.println("[GameView] Background image loaded");
        } else {
            System.out.println("[GameView] WARNING: Background image failed to load");
        }
        
        // Clear existing hotspots
        clearHotspots();
        System.out.println("[GameView] Cleared existing hotspots");
        
        // Add hotspots (clickable areas)
        System.out.println("[GameView] Adding " + viewModel.getHotspots().size() + " hotspots");
        for (HotspotViewModel hotspot : viewModel.getHotspots()) {
            addHotspot(hotspot);
            System.out.println("[GameView] Added hotspot: " + hotspot.getId() + " at (" + hotspot.getX() + ", " + hotspot.getY() + ")");
        }
        
        // Update direction button states
        if (viewModel.getRoom() != null) {
            directionControllerView.updateButtonState(viewModel.getRoom().getAllExits());
            System.out.println("[GameView] Updated direction buttons");
        }
        
        System.out.println("[GameView] Room display complete");
    }

    /**
     * Updates just the background (for simple room changes without hotspots).
     */
    public void updateRoomBackground(Room room) {
        Image roomImage = roomImageMapper.loadRoomImage(room);
        if (roomImage != null) {
            backgroundImage.setImage(roomImage);
        }
        
        if (room != null) {
            directionControllerView.updateButtonState(room.getAllExits());
        }
    }

    /**
     * Adds a clickable hotspot to the view.
     */
    private void addHotspot(HotspotViewModel hotspot) {
        // Create transparent button for the hotspot
        Button hotspotButton = new Button();
        hotspotButton.setLayoutX(hotspot.getX());
        hotspotButton.setLayoutY(hotspot.getY());
        hotspotButton.setPrefSize(hotspot.getWidth(), hotspot.getHeight());
        
        // Make it transparent but visible on hover (for debugging/UX)
        hotspotButton.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent; " +
            "-fx-cursor: hand;"
        );
        
        // Add hover effect (subtle highlight)
        hotspotButton.setOnMouseEntered(e -> {
            hotspotButton.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1); " +
                "-fx-border-color: rgba(255, 255, 255, 0.3); " +
                "-fx-border-width: 2; " +
                "-fx-cursor: hand;"
            );
        });
        
        hotspotButton.setOnMouseExited(e -> {
            hotspotButton.setStyle(
                "-fx-background-color: transparent; " +
                "-fx-border-color: transparent; " +
                "-fx-cursor: hand;"
            );
        });
        
        // Tooltip
        if (hotspot.getHoverText() != null) {
            Tooltip tooltip = new Tooltip(hotspot.getHoverText());
            Tooltip.install(hotspotButton, tooltip);
        }
        
        // Click handler
        hotspotButton.setOnAction(e -> {
            System.out.println("[GameView] Hotspot clicked: " + hotspot.getId());
            OnHotspotClicked.invoke(this, new OnHotspotClickedEventArgs(hotspot.getId()));
        });
        
        hotspotButton.setDisable(!hotspot.isEnabled());
        
        hotspotsLayer.getChildren().add(hotspotButton);
        hotspotButtons.put(hotspot.getId(), hotspotButton);
    }

    /**
     * Clears all hotspots from the view.
     */
    private void clearHotspots() {
        hotspotsLayer.getChildren().clear();
        hotspotButtons.clear();
    }

    /**
     * Updates hotspot state (enable/disable).
     */
    public void updateHotspot(String hotspotId, boolean enabled) {
        Button button = hotspotButtons.get(hotspotId);
        if (button != null) {
            button.setDisable(!enabled);
        }
    }

    /**
     * Applies view updates from interaction results (animations, enabling/disabling hotspots, etc.).
     */
    public void applyViewUpdates(java.util.List<String> updates) {
        System.out.println("[GameView] applyViewUpdates() called with " + updates.size() + " updates");
        for (String update : updates) {
            System.out.println("[GameView] Processing update: " + update);
            if (update.startsWith("enableHotspot:")) {
                String hotspotId = update.substring("enableHotspot:".length());
                System.out.println("[GameView] Enabling hotspot: " + hotspotId);
                updateHotspot(hotspotId, true);
            } else if (update.startsWith("disableHotspot:")) {
                String hotspotId = update.substring("disableHotspot:".length());
                System.out.println("[GameView] Disabling hotspot: " + hotspotId);
                updateHotspot(hotspotId, false);
            } else if (update.startsWith("playAnimation:")) {
                // TODO: Implement animation playback
                String animationId = update.substring("playAnimation:".length());
                System.out.println("[GameView] Playing animation: " + animationId);
            }
        }
    }
}

