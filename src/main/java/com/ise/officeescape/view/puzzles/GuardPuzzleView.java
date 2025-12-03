package com.ise.officeescape.view.puzzles;

import com.ise.officeescape.controller.GameController;
import com.ise.officeescape.model.InteractionResult;
import com.ise.officeescape.model.Player;
import com.ise.officeescape.model.Puzzle;
import com.ise.officeescape.model.puzzles.QueueGaurdPuzzle;
import com.ise.officeescape.view.PuzzleView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.FadeTransition;

import java.util.List;

/**
 * Puzzle view for talking to the guard.
 * Displays dialogue in a translucent overlay with terminal-style UI.
 */
public class GuardPuzzleView extends PuzzleView {
    
    private QueueGaurdPuzzle guardPuzzle;
    private Label npcDialogueLabel;
    private VBox optionsContainer;
    private Button closeButton;
    private VBox dialogueContent;
    
    public GuardPuzzleView(Puzzle puzzle) {
        super(puzzle);
        if (puzzle instanceof QueueGaurdPuzzle) {
            this.guardPuzzle = (QueueGaurdPuzzle) puzzle;
        } else {
            throw new IllegalArgumentException("GuardPuzzleView requires a QueueGaurdPuzzle instance");
        }
        // Reset dialogue to beginning when view is opened
        guardPuzzle.resetDialogue();
        initializeDialogue();
    }
    
    private void initializeDialogue() {
        // Semi-transparent dark overlay background
        Rectangle overlayBackground = new Rectangle();
        overlayBackground.setFill(Color.rgb(0, 0, 0, 0.7));
        overlayBackground.widthProperty().bind(widthProperty());
        overlayBackground.heightProperty().bind(heightProperty());
        getChildren().add(overlayBackground);
        
        // Main dialogue container - centered
        StackPane centerContainer = new StackPane();
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.prefWidthProperty().bind(widthProperty());
        centerContainer.prefHeightProperty().bind(heightProperty());
        
        // Dialogue content box
        dialogueContent = new VBox(20);
        dialogueContent.setAlignment(Pos.CENTER);
        dialogueContent.setMaxWidth(600);
        dialogueContent.setMaxHeight(500);
        dialogueContent.setPadding(new Insets(30));
        dialogueContent.setStyle(
            "-fx-background-color: rgba(30, 30, 30, 0.95); " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: #4CAF50; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 15;"
        );
        
        // NPC name label
        Label npcNameLabel = new Label("Guard");
        npcNameLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-text-fill: #4CAF50; " +
            "-fx-font-weight: bold;"
        );
        
        // NPC dialogue text
        npcDialogueLabel = new Label();
        npcDialogueLabel.setWrapText(true);
        npcDialogueLabel.setMaxWidth(550);
        npcDialogueLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-padding: 15; " +
            "-fx-background-color: rgba(0, 0, 0, 0.5); " +
            "-fx-background-radius: 10;"
        );
        
        // Options container
        optionsContainer = new VBox(10);
        optionsContainer.setAlignment(Pos.CENTER);
        optionsContainer.setMaxWidth(550);
        
        // Close button (initially hidden, shown on terminal nodes)
        closeButton = new Button("Close");
        closeButton.setStyle(
            "-fx-background-color: rgba(244, 67, 54, 0.8); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8 20; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5;"
        );
        closeButton.setOnAction(e -> {
            puzzle.OnPuzzleClosed.invoke(puzzle, new Puzzle.OnPuzzleClosedEventArgs());
        });
        closeButton.setVisible(false);
        
        dialogueContent.getChildren().addAll(
            npcNameLabel,
            npcDialogueLabel,
            optionsContainer,
            closeButton
        );
        
        centerContainer.getChildren().add(dialogueContent);
        getChildren().add(centerContainer);
        
        // Display initial dialogue
        displayCurrentDialogue();
    }
    
    /**
     * Displays the current dialogue node.
     */
    private void displayCurrentDialogue() {
        QueueGaurdPuzzle.DialogueNode currentNode = guardPuzzle.getCurrentNode();
        if (currentNode == null) {
            return;
        }
        
        // Display NPC text
        npcDialogueLabel.setText(currentNode.npcText);
        
        // Clear previous options
        optionsContainer.getChildren().clear();
        
        // Get player to check conditions
        Player player = null;
        if (GameController.instance != null) {
            player = GameController.instance.getPlayer();
        }
        
        // Get available options (filtered by conditions)
        List<QueueGaurdPuzzle.DialogueOption> availableOptions = guardPuzzle.getAvailableOptions(player);
        
        if (currentNode.isTerminal) {
            // Terminal node - show close button
            closeButton.setVisible(true);
            optionsContainer.setVisible(false);
        } else {
            // Non-terminal node - show options
            closeButton.setVisible(false);
            optionsContainer.setVisible(true);
            
            // Create option buttons
            for (int i = 0; i < availableOptions.size(); i++) {
                QueueGaurdPuzzle.DialogueOption option = availableOptions.get(i);
                final int optionIndex = i;
                
                Button optionButton = new Button(option.playerText);
                optionButton.setStyle(
                    "-fx-background-color: rgba(15, 52, 96, 0.9); " +
                    "-fx-text-fill: #ffffff; " +
                    "-fx-font-size: 14px; " +
                    "-fx-padding: 10 20; " +
                    "-fx-cursor: hand; " +
                    "-fx-background-radius: 5; " +
                    "-fx-min-width: 500; " +
                    "-fx-alignment: center-left;"
                );
                
                // Hover effect
                optionButton.setOnMouseEntered(e -> {
                    optionButton.setStyle(
                        "-fx-background-color: rgba(26, 77, 122, 0.9); " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5; " +
                        "-fx-min-width: 500; " +
                        "-fx-alignment: center-left;"
                    );
                });
                
                optionButton.setOnMouseExited(e -> {
                    optionButton.setStyle(
                        "-fx-background-color: rgba(15, 52, 96, 0.9); " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-font-size: 14px; " +
                        "-fx-padding: 10 20; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 5; " +
                        "-fx-min-width: 500; " +
                        "-fx-alignment: center-left;"
                    );
                });
                
                // Click handler
                optionButton.setOnAction(e -> handleOptionSelection(optionIndex));
                
                optionsContainer.getChildren().add(optionButton);
            }
        }
    }
    
    /**
     * Handles selection of a dialogue option.
     */
    private void handleOptionSelection(int availableOptionIndex) {
        // Get the current node
        QueueGaurdPuzzle.DialogueNode currentNode = guardPuzzle.getCurrentNode();
        if (currentNode == null) {
            return;
        }
        
        // Get player to check conditions
        Player player = null;
        if (GameController.instance != null) {
            player = GameController.instance.getPlayer();
        }
        
        // Get available options (filtered by conditions)
        List<QueueGaurdPuzzle.DialogueOption> availableOptions = guardPuzzle.getAvailableOptions(player);
        if (availableOptionIndex < 0 || availableOptionIndex >= availableOptions.size()) {
            return;
        }
        
        // Get the selected option from available options
        QueueGaurdPuzzle.DialogueOption selectedOption = availableOptions.get(availableOptionIndex);
        
        // Find the index of this option in the full options list
        int actualIndex = currentNode.options.indexOf(selectedOption);
        if (actualIndex < 0) {
            return;
        }
        
        // Call puzzle to handle the option (pass the index in the full options list)
        InteractionResult result = guardPuzzle.interact("selectOption", actualIndex);
        
        // Check if puzzle was solved (bribe accepted)
        if (result.getType() == InteractionResult.ResultType.PUZZLE_SOLVED) {
            // Move to next node (terminal node with success message)
            displayCurrentDialogue();
            
            // Show success overlay
            showSuccessOverlay();
            
            // Trigger solved event after showing the overlay
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), e -> {
                    puzzle.OnPuzzleSolved.invoke(puzzle, new Puzzle.OnPuzzleSolvedEventArgs(puzzle.getId(), result));
                })
            );
            timeline.play();
            return;
        }
        
        // Check result message to see if we should advance
        String message = result.getMessage();
        if (message != null && message.startsWith("continue:")) {
            // Move to next node
            displayCurrentDialogue();
        } else if (message != null && !message.equals("Invalid option")) {
            // Terminal node or end of conversation
            displayCurrentDialogue();
        }
    }
    
    /**
     * Shows an overlay displaying the success message after bribing the guard.
     */
    private void showSuccessOverlay() {
        // Create overlay background (fully transparent to show dialogue behind)
        Rectangle overlayBackground = new Rectangle();
        overlayBackground.setFill(Color.TRANSPARENT);
        overlayBackground.widthProperty().bind(widthProperty());
        overlayBackground.heightProperty().bind(heightProperty());
        
        // Create container for the success message
        VBox successContainer = new VBox(20);
        successContainer.setAlignment(Pos.CENTER);
        successContainer.setPadding(new Insets(30));
        successContainer.setMaxWidth(500);
        successContainer.setStyle(
            "-fx-background-color: rgba(43, 43, 43, 0.95); " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: #4CAF50; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 15;"
        );
        
        // Success message label
        Label successLabel = new Label("You skipped the line\nyou cheeky little gremlin.");
        successLabel.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-text-fill: #4CAF50; " +
            "-fx-font-weight: bold; " +
            "-fx-text-alignment: center;"
        );
        successLabel.setAlignment(Pos.CENTER);
        
        successContainer.getChildren().add(successLabel);
        
        // Create StackPane to center the success container
        StackPane overlayPane = new StackPane();
        overlayPane.setAlignment(Pos.CENTER);
        overlayPane.prefWidthProperty().bind(widthProperty());
        overlayPane.prefHeightProperty().bind(heightProperty());
        overlayPane.getChildren().addAll(overlayBackground, successContainer);
        
        // Add overlay to the view (on top of everything)
        getChildren().add(overlayPane);
        
        // Animate the overlay appearance (fade in)
        overlayPane.setOpacity(0);
        javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
            javafx.util.Duration.millis(500), overlayPane
        );
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}

