package com.ise.officeescape.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * DirectionControllerView - handles its own UI and events for direction buttons.
 * Extends VBox so it can be added directly to the StackPane in GameView.
 */
public class DirectionControllerView extends VBox {

    private Button leftButton;
    private Button rightButton;
    private Button forwardButton;
    private Button backwardButton;

    public DirectionControllerView() {
        initializeButtons();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeButtons() {
        leftButton = new Button("←");
        rightButton = new Button("→");
        forwardButton = new Button("↑");
        backwardButton = new Button("↓");
        
        // Make buttons larger and square-ish
        leftButton.setMinSize(50, 50);
        rightButton.setMinSize(50, 50);
        forwardButton.setMinSize(50, 50);
        backwardButton.setMinSize(50, 50);
    }
    
    private void setupLayout() {
        // Horizontal layout for left/right buttons
        HBox horizontalButtons = new HBox(10);
        horizontalButtons.setAlignment(Pos.CENTER);
        horizontalButtons.getChildren().addAll(leftButton, rightButton);
        
        // Vertical layout: forward button, horizontal buttons, backward button
        setAlignment(Pos.BOTTOM_LEFT);
        setSpacing(10);
        getChildren().addAll(forwardButton, horizontalButtons, backwardButton);
    }
    
    private void setupEventHandlers() {
        leftButton.setOnAction(e -> handleLeft());
        rightButton.setOnAction(e -> handleRight());
        forwardButton.setOnAction(e -> handleForward());
        backwardButton.setOnAction(e -> handleBackward());
    }
    
    // Event handlers - you can customize these or add callbacks
    private void handleLeft() {
        System.out.println("Left button clicked");
        // TODO: Add your movement logic here or use a callback
    }
    
    private void handleRight() {
        System.out.println("Right button clicked");
        // TODO: Add your movement logic here or use a callback
    }
    
    private void handleForward() {
        System.out.println("Forward button clicked");
        // TODO: Add your movement logic here or use a callback
    }
    
    private void handleBackward() {
        System.out.println("Backward button clicked");
        // TODO: Add your movement logic here or use a callback
    }
    
    // Optional: Add getters if you need to access buttons from outside
    public Button getLeftButton() {
        return leftButton;
    }
    
    public Button getRightButton() {
        return rightButton;
    }
    
    public Button getForwardButton() {
        return forwardButton;
    }
    
    public Button getBackwardButton() {
        return backwardButton;
    }
}
