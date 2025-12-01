package com.ise.officeescape.view;

import com.ise.officeescape.eventSystem.*;
import com.ise.officeescape.model.InteractionResult;
import com.ise.officeescape.model.Puzzle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * View for displaying and interacting with puzzles.
 * Overlays on top of the game view when a puzzle is active.
 */
public class PuzzleView extends StackPane {
    
    protected Puzzle puzzle;
    private VBox contentPane;
    
    // Event for when puzzle is solved
    public Event<OnPuzzleSolvedEventArgs> OnPuzzleSolved = new Event<>();
    public class OnPuzzleSolvedEventArgs extends EventArgs {
        public final String puzzleId;
        public final InteractionResult result;
        public OnPuzzleSolvedEventArgs(String puzzleId, InteractionResult result) {
            this.puzzleId = puzzleId;
            this.result = result;
        }
    }
    
    // Event for when puzzle is closed/cancelled
    public Event<OnPuzzleClosedEventArgs> OnPuzzleClosed = new Event<>();
    public class OnPuzzleClosedEventArgs extends EventArgs {
        public OnPuzzleClosedEventArgs() {}
    }

    public PuzzleView(Puzzle puzzle) {
        this.puzzle = puzzle;
        initializeUI();
    }
    
    private void initializeUI() {
        // Semi-transparent dark overlay
        Rectangle overlay = new Rectangle();
        overlay.setFill(Color.rgb(0, 0, 0, 0.7));
        overlay.widthProperty().bind(widthProperty());
        overlay.heightProperty().bind(heightProperty());
        getChildren().add(overlay);
        
        // Content pane in center
        contentPane = new VBox(20);
        contentPane.setAlignment(Pos.CENTER);
        contentPane.setPadding(new Insets(40));
        contentPane.setMaxWidth(600);
        contentPane.setStyle(
            "-fx-background-color: #2b2b2b; " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: #555555; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 15;"
        );
        
        // Puzzle title
        Label titleLabel = new Label(puzzle.getDescription());
        titleLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        
        // Puzzle-specific content (will be customized per puzzle type)
        VBox puzzleContent = createPuzzleContent();
        
        // Close button
        Button closeButton = new Button("Close");
        closeButton.setStyle(
            "-fx-background-color: #555555; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 10 20;"
        );
        closeButton.setOnAction(e -> {
            OnPuzzleClosed.invoke(this, new OnPuzzleClosedEventArgs());
        });
        
        contentPane.getChildren().addAll(titleLabel, puzzleContent, closeButton);
        getChildren().add(contentPane);
    }
    
    /**
     * Creates puzzle-specific content based on puzzle type.
     * Override this in puzzle-specific views if needed.
     */
    private VBox createPuzzleContent() {
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        
        // Default puzzle UI - a button to interact with the puzzle
        Label instructionLabel = new Label("Click the button below to interact with the puzzle:");
        instructionLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #cccccc;");
        instructionLabel.setWrapText(true);
        
        Button interactButton = new Button("Get Ticket");
        interactButton.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-padding: 15 30; " +
            "-fx-cursor: hand;"
        );
        interactButton.setOnAction(e -> {
            handlePuzzleInteraction();
        });
        
        content.getChildren().addAll(instructionLabel, interactButton);
        return content;
    }
    
    /**
     * Handles interaction with the puzzle.
     */
    private void handlePuzzleInteraction() {
        // Interact with the puzzle
        var result = puzzle.interact("interact", null);
        
        if (result.getType() == InteractionResult.ResultType.PUZZLE_SOLVED) {
            OnPuzzleSolved.invoke(this, new OnPuzzleSolvedEventArgs(puzzle.getId(), result));
        } else if (result.getMessage() != null) {
            // Could show message in UI
        }
    }
    
    public Puzzle getPuzzle() {
        return puzzle;
    }
}

