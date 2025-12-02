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
    private PuzzleView currentPuzzleView; // Current puzzle overlay (if any)
    private InventoryView inventoryView; // Inventory overlay
    private Button inventoryButton; // Button to open inventory

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
        // Make it fill the entire view so hotspots scale properly
        hotspotsLayer = new Pane();
        hotspotsLayer.setPickOnBounds(false); // Only click on actual buttons, not empty space
        hotspotsLayer.prefWidthProperty().bind(widthProperty());
        hotspotsLayer.prefHeightProperty().bind(heightProperty());
        hotspotsLayer.setMouseTransparent(false); // Ensure it can receive mouse events

        // Debug: Print layer info
        System.out.println("[GameView] HotspotsLayer created and configured");

        // Add layers in order: background, direction controller, hotspots
        // Hotspots added last so they're on top and clickable
        getChildren().add(backgroundImage);

        // Create and add direction controller view (before hotspots so hotspots are on top)
        directionControllerView = new DirectionControllerView();
        StackPane.setAlignment(directionControllerView, javafx.geometry.Pos.BOTTOM_LEFT);
        StackPane.setMargin(directionControllerView, new Insets(0, 0, 20, 20));
        getChildren().add(directionControllerView);

        // Create inventory button (bottom right, aligned with direction buttons)
        inventoryButton = new Button("i");
        inventoryButton.setStyle(
            "-fx-background-color: rgba(76, 175, 80, 0.9); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 20px; " +
            "-fx-padding: 10 15; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5; " +
            "-fx-min-width: 50; " +
            "-fx-min-height: 50;"
        );
        inventoryButton.setOnAction(e -> toggleInventory());
        StackPane.setAlignment(inventoryButton, javafx.geometry.Pos.BOTTOM_RIGHT);
        StackPane.setMargin(inventoryButton, new Insets(0, 20, 20, 0));
        getChildren().add(inventoryButton);

        // Create inventory view (initially hidden)
        inventoryView = new InventoryView();
        getChildren().add(inventoryView);

        // Add hotspots layer LAST so it's on top and receives clicks
        getChildren().add(hotspotsLayer);
    }

    public DirectionControllerView getDirectionControllerView() {
        return directionControllerView;
    }

    /**
     * Shows a room with all its hotspots and interactions.
     */
    public void showRoom(RoomViewModel viewModel) {
        System.out.println("[GameView] showRoom() called for room: " + viewModel.getRoom().getName());
        System.out.println("[GameView] Current window size: " + getWidth() + " x " + getHeight());

        // Load and set background image
        Image roomImage = roomImageMapper.loadRoomImage(viewModel.getRoom());
        if (roomImage != null) {
            backgroundImage.setImage(roomImage);
            System.out.println("[GameView] Background image loaded: " + roomImage.getWidth() + " x " + roomImage.getHeight());
        } else {
            System.out.println("[GameView] WARNING: Background image failed to load");
        }

        // Clear existing hotspots
        clearHotspots();
        System.out.println("[GameView] Cleared existing hotspots");

        // Add hotspots (clickable areas) - they will scale with window size
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
        System.out.println("[GameView] HotspotsLayer children count: " + hotspotsLayer.getChildren().size());
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
     * Hotspots use absolute positioning - they will scale proportionally with window resize.
     */
    private void addHotspot(HotspotViewModel hotspot) {
        System.out.println("[GameView] addHotspot() - Creating button for: " + hotspot.getId());
        System.out.println("[GameView]   Position: (" + hotspot.getX() + ", " + hotspot.getY() + ")");
        System.out.println("[GameView]   Size: " + hotspot.getWidth() + " x " + hotspot.getHeight());
        System.out.println("[GameView]   Current window size: " + getWidth() + " x " + getHeight());

        // Create transparent button for the hotspot
        Button hotspotButton = new Button();

        // Store original coordinates for scaling
        double originalX = hotspot.getX();
        double originalY = hotspot.getY();
        double originalWidth = hotspot.getWidth();
        double originalHeight = hotspot.getHeight();

        // For now, use absolute positioning (will fix scaling later if needed)
        hotspotButton.setLayoutX(originalX);
        hotspotButton.setLayoutY(originalY);
        hotspotButton.setPrefSize(originalWidth, originalHeight);
        hotspotButton.setMinSize(originalWidth, originalHeight);
        hotspotButton.setMaxSize(originalWidth, originalHeight);

        // Ensure button can receive mouse events
        hotspotButton.setMouseTransparent(false);
        hotspotButton.setDisable(false);

        System.out.println("[GameView]   Button created at: (" + hotspotButton.getLayoutX() + ", " + hotspotButton.getLayoutY() + ")");
        System.out.println("[GameView]   Button size: " + hotspotButton.getWidth() + " x " + hotspotButton.getHeight());

        // Make it visible with border for debugging
        hotspotButton.setStyle(
            "-fx-background-color: rgba(255, 0, 0, 0.1); " +  // Light red background for visibility
            "-fx-border-color: #FF0000; " +  // Red border
            "-fx-border-width: 2; " +
            "-fx-border-style: solid; " +
            "-fx-cursor: hand;"
        );

        // Add hover effect (brighter highlight)
        hotspotButton.setOnMouseEntered(e -> {
            hotspotButton.setStyle(
                "-fx-background-color: rgba(255, 255, 0, 0.2); " +  // Yellow highlight on hover
                "-fx-border-color: #FFFF00; " +  // Yellow border on hover
                "-fx-border-width: 3; " +
                "-fx-border-style: solid; " +
                "-fx-cursor: hand;"
            );
        });

        hotspotButton.setOnMouseExited(e -> {
            hotspotButton.setStyle(
                "-fx-background-color: rgba(255, 0, 0, 0.1); " +  // Back to red
                "-fx-border-color: #FF0000; " +
                "-fx-border-width: 2; " +
                "-fx-border-style: solid; " +
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
            OnHotspotClicked.invoke(this, new OnHotspotClickedEventArgs(hotspot.getId()));
        });

        hotspotButton.setDisable(!hotspot.isEnabled());

        System.out.println("[GameView]   Adding button to hotspotsLayer. Layer children count: " + hotspotsLayer.getChildren().size());
        hotspotsLayer.getChildren().add(hotspotButton);
        hotspotButtons.put(hotspot.getId(), hotspotButton);
        System.out.println("[GameView]   Button added. New layer children count: " + hotspotsLayer.getChildren().size());
        System.out.println("[GameView]   Hotspot button enabled: " + !hotspotButton.isDisabled());
        System.out.println("[GameView]   Hotspot button mouse transparent: " + hotspotButton.isMouseTransparent());
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
     * Shows a puzzle view overlay.
     */
    public void showPuzzleView(PuzzleView puzzleView) {
        // Remove existing puzzle view if any
        if (currentPuzzleView != null) {
            getChildren().remove(currentPuzzleView);
        }

        // Add new puzzle view on top
        currentPuzzleView = puzzleView;
        getChildren().add(puzzleView);
    }

    /**
     * Hides the current puzzle view.
     */
    public void hidePuzzleView() {
        if (currentPuzzleView != null) {
            getChildren().remove(currentPuzzleView);
            currentPuzzleView = null;
        }
    }

    /**
     * Applies view updates from interaction results (animations, enabling/disabling hotspots, etc.).
     */
    public void applyViewUpdates(java.util.List<String> updates) {
        for (String update : updates) {
            if (update.startsWith("enableHotspot:")) {
                String hotspotId = update.substring("enableHotspot:".length());
                updateHotspot(hotspotId, true);
            } else if (update.startsWith("disableHotspot:")) {
                String hotspotId = update.substring("disableHotspot:".length());
                updateHotspot(hotspotId, false);
            } else if (update.startsWith("playAnimation:")) {
                // TODO: Implement animation playback
                String animationId = update.substring("playAnimation:".length());
            }
        }
    }

    /**
     * Toggles the inventory view visibility.
     */
    public void toggleInventory() {
        inventoryView.toggle();
    }

    /**
     * Updates the inventory view with current player and room inventories.
     */
    public void updateInventory(com.ise.officeescape.model.Inventory playerInventory, 
         com.ise.officeescape.model.Inventory roomInventory) {
        inventoryView.updateInventories(playerInventory, roomInventory);
        // Set callback to refresh when items are moved (will be called from controller)
    }
    
    /**
     * Sets the callback for when inventory items are moved.
     * This allows the controller to refresh the inventory display.
     */
    public void setInventoryChangeCallback(Runnable callback) {
        inventoryView.setOnInventoryChanged(callback);
    }

    /**
     * Gets the inventory view.
     */
    public InventoryView getInventoryView() {
        return inventoryView;
    }
}

