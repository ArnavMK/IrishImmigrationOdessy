package com.ise.officeescape.view;

import java.util.Set;

import com.ise.officeescape.eventSystem.*;
import com.ise.officeescape.model.Direction;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

/**
 * DirectionControllerView - handles its own UI and events for direction buttons.
 * Arranged in a D-pad cross/plus shape like a portable PlayStation controller.
 * Extends GridPane so it can be added directly to the StackPane in GameView.
 */
public class DirectionControllerView extends GridPane {

    private Button leftButton;
    private Button rightButton;
    private Button forwardButton;
    private Button backwardButton;

    public Event<OnDirectionButtonPressedArgs> OnDirectionButtonPressed = new Event<>(); 
    public class OnDirectionButtonPressedArgs extends EventArgs {
        public final Direction direction;
        public OnDirectionButtonPressedArgs(Direction direction) {
            this.direction = direction;
        }
    } 

    public DirectionControllerView() {
        initializeButtons();
        setupLayout();
        setupEventHandlers();
    }

    private void setupEventHandlers() {
        leftButton.setOnAction(e -> 
            OnDirectionButtonPressed.invoke(this, new OnDirectionButtonPressedArgs(Direction.left))
        );

        rightButton.setOnAction(e ->
            OnDirectionButtonPressed.invoke(this, new OnDirectionButtonPressedArgs(Direction.right))
        );

        forwardButton.setOnAction(e ->
            OnDirectionButtonPressed.invoke(this, new OnDirectionButtonPressedArgs(Direction.forward))
        );

        backwardButton.setOnAction(e ->
            OnDirectionButtonPressed.invoke(this, new OnDirectionButtonPressedArgs(Direction.backwards))
        );
    }

    /**
     * Updates button states based on available exits.
     * Buttons are enabled if the exit exists, disabled if it doesn't.
     */
    public void updateButtonState(Set<Direction> exits) {
        // Enable button if exit exists, disable if it doesn't
        leftButton.setDisable(!exits.contains(Direction.left));
        rightButton.setDisable(!exits.contains(Direction.right));
        forwardButton.setDisable(!exits.contains(Direction.forward));
        backwardButton.setDisable(!exits.contains(Direction.backwards));
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
        // Arrange buttons in a plus/cross shape (D-pad style)
        // Grid layout: 3x3 grid with buttons forming a cross
        //     ↑
        //   ← + →
        //     ↓

        setAlignment(Pos.BOTTOM_LEFT);
        setHgap(5);
        setVgap(5);

        // Forward button at top center (row 0, col 1)
        add(forwardButton, 1, 0);

        // Left button at middle left (row 1, col 0)
        add(leftButton, 0, 1);

        // Right button at middle right (row 1, col 2)
        add(rightButton, 2, 1);

        // Backward button at bottom center (row 2, col 1)
        add(backwardButton, 1, 2);
    }


    // Event handlers - you can customize these or add callbacks
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

