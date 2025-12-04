package com.ise.officeescape.view;

import com.ise.officeescape.model.Inventory;
import com.ise.officeescape.model.Item;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Map;

/**
 * Inventory view showing both player and room inventories side by side.
 */
public class InventoryView extends StackPane {
    
    private VBox playerInventoryPanel;
    private VBox roomInventoryPanel;
    private Button closeButton;
    private boolean isVisible = false;
    
    // References to current inventories
    private Inventory playerInventory;
    private Inventory roomInventory;
    
    // Callback for when items are moved (to update the view)
    // Passes: item moved, fromRoomInventory (true if from room, false if from player)
    private java.util.function.BiConsumer<Item, Boolean> onInventoryChanged;
    
    public InventoryView() {
        initializeUI();
    }
    
    /**
     * Sets the callback to be called when inventory changes.
     * @param callback A function that receives (item, fromRoomInventory) where fromRoomInventory is true if item came from room inventory
     */
    public void setOnInventoryChanged(java.util.function.BiConsumer<Item, Boolean> callback) {
        this.onInventoryChanged = callback;
    }
    
    /**
     * Legacy method for Runnable callback (for backwards compatibility).
     */
    public void setOnInventoryChanged(Runnable callback) {
        this.onInventoryChanged = (item, fromRoom) -> callback.run();
    }
    
    private void initializeUI() {
        // Semi-transparent dark overlay
        Rectangle overlay = new Rectangle();
        overlay.setFill(Color.rgb(0, 0, 0, 0.7));
        overlay.widthProperty().bind(widthProperty());
        overlay.heightProperty().bind(heightProperty());
        getChildren().add(overlay);
        
        // Main container
        HBox mainContainer = new HBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setMaxWidth(800);
        mainContainer.setMaxHeight(600);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setStyle(
            "-fx-background-color: rgba(43, 43, 43, 0.95); " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: #555555; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 15;"
        );
        
        // Left panel - Room Inventory
        roomInventoryPanel = createInventoryPanel("Room Inventory", true);
        
        // Right panel - Player Inventory
        playerInventoryPanel = createInventoryPanel("Your Inventory", false);
        
        mainContainer.getChildren().addAll(roomInventoryPanel, playerInventoryPanel);
        
        // Close button
        closeButton = new Button("✕");
        closeButton.setStyle(
            "-fx-background-color: rgba(244, 67, 54, 0.8); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-padding: 8 12; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5; " +
            "-fx-min-width: 35; " +
            "-fx-min-height: 35;"
        );
        closeButton.setOnAction(e -> hide());
        
        // Position close button at top-right of main container
        StackPane containerWithClose = new StackPane();
        containerWithClose.getChildren().add(mainContainer);
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10, 0, 0));
        containerWithClose.getChildren().add(closeButton);
        
        getChildren().add(containerWithClose);
        
        // Initially hidden
        setVisible(false);
    }
    
    private VBox createInventoryPanel(String title, boolean isRoomInventory) {
        VBox panel = new VBox(10);
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setPadding(new Insets(20));
        panel.setMinWidth(350);
        panel.setMaxWidth(350);
        panel.setStyle(
            "-fx-background-color: rgba(30, 30, 30, 0.9); " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #4CAF50; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );
        
        // Title
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-text-fill: #4CAF50; " +
            "-fx-font-weight: bold;"
        );
        
        // Scrollable content area
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        VBox itemsContainer = new VBox(8);
        itemsContainer.setPadding(new Insets(10));
        itemsContainer.setId(isRoomInventory ? "roomItemsContainer" : "playerItemsContainer");
        scrollPane.setContent(itemsContainer);
        
        panel.getChildren().addAll(titleLabel, scrollPane);
        
        return panel;
    }
    
    /**
     * Updates the inventory display with current player and room inventories.
     */
    public void updateInventories(Inventory playerInventory, Inventory roomInventory) {
        this.playerInventory = playerInventory;
        this.roomInventory = roomInventory;
        updateInventoryPanel(playerInventoryPanel, playerInventory, "playerItemsContainer", false);
        updateInventoryPanel(roomInventoryPanel, roomInventory, "roomItemsContainer", true);
    }
    
    private void updateInventoryPanel(VBox panel, Inventory inventory, String containerId, boolean isRoomInventory) {
        // Find the items container
        ScrollPane scrollPane = (ScrollPane) panel.getChildren().get(1);
        VBox itemsContainer = (VBox) scrollPane.getContent();
        
        // Clear existing items
        itemsContainer.getChildren().clear();
        
        if (inventory == null || inventory.getInvetoryMap().isEmpty()) {
            Label emptyLabel = new Label("(Empty)");
            emptyLabel.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-text-fill: #888888; " +
                "-fx-font-style: italic;"
            );
            itemsContainer.getChildren().add(emptyLabel);
        } else {
            // Add items
            Map<String, Item> items = inventory.getInvetoryMap();
            for (Item item : items.values()) {
                VBox itemBox = createItemDisplay(item, isRoomInventory);
                itemsContainer.getChildren().add(itemBox);
            }
        }
    }
    
    private VBox createItemDisplay(Item item, boolean isRoomInventory) {
        VBox itemBox = new VBox(8);
        itemBox.setPadding(new Insets(10));
        itemBox.setAlignment(Pos.TOP_CENTER);
        itemBox.setStyle(
            "-fx-background-color: rgba(50, 50, 50, 0.8); " +
            "-fx-background-radius: 5; " +
            "-fx-border-color: #666666; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 5; " +
            "-fx-cursor: hand;"
        );
        
        // Add hover effect
        itemBox.setOnMouseEntered(e -> {
            itemBox.setStyle(
                "-fx-background-color: rgba(70, 70, 70, 0.9); " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: #4CAF50; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 5; " +
                "-fx-cursor: hand;"
            );
        });
        
        itemBox.setOnMouseExited(e -> {
            itemBox.setStyle(
                "-fx-background-color: rgba(50, 50, 50, 0.8); " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: #666666; " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 5; " +
                "-fx-cursor: hand;"
            );
        });
        
        // Click handler - move item between inventories
        itemBox.setOnMouseClicked(e -> {
            moveItem(item, isRoomInventory);
        });
        
        // Try to load item image
        ImageView itemImageView = new ImageView();
        try {
            String imagePath = "/com/ise/officeescape/assets/" + item.getName() + ".png";
            Image itemImage = new Image(getClass().getResourceAsStream(imagePath));
            itemImageView.setImage(itemImage);
            itemImageView.setPreserveRatio(true);
            itemImageView.setFitWidth(80);
            itemImageView.setFitHeight(80);
            itemImageView.setSmooth(true);
        } catch (Exception ex) {
            // Image not found - use placeholder or hide image view
            itemImageView.setVisible(false);
        }
        
        Label nameLabel = new Label(item.getName());
        nameLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-font-weight: bold;"
        );
        
        Label descLabel = new Label(item.getDescription());
        descLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-text-fill: #cccccc; " +
            "-fx-wrap-text: true;"
        );
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(300);
        descLabel.setAlignment(Pos.CENTER);
        
        itemBox.getChildren().addAll(itemImageView, nameLabel, descLabel);
        return itemBox;
    }
    
    /**
     * Moves an item from one inventory to the other.
     */
    private void moveItem(Item item, boolean fromRoomInventory) {
        if (fromRoomInventory) {
            // Moving from room to player
            if (roomInventory != null && roomInventory.hasItem(item)) {
                roomInventory.removeItem(item.getName());
                if (playerInventory != null) {
                    playerInventory.addItem(item);
                }
            }
        } else {
            // Moving from player to room
            if (playerInventory != null && playerInventory.hasItem(item)) {
                playerInventory.removeItem(item.getName());
                if (roomInventory != null) {
                    roomInventory.addItem(item);
                }
            }
        }
        
        // Refresh the display
        updateInventories(playerInventory, roomInventory);
        
        // Notify callback if set
        if (onInventoryChanged != null) {
            onInventoryChanged.accept(item, fromRoomInventory);
        }
    }
    
    public void show() {
        setVisible(true);
        isVisible = true;
    }
    
    public void hide() {
        setVisible(false);
        isVisible = false;
    }
    
    public boolean isInventoryVisible() {
        return isVisible;
    }
    
    public void toggle() {
        if (isVisible) {
            hide();
        } else {
            show();
        }
    }
}

