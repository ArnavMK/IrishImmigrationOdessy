package com.ise.officeescape.view.puzzles;

import com.ise.officeescape.model.InteractionResult;
import com.ise.officeescape.model.Puzzle;
import com.ise.officeescape.model.puzzles.SecurityPuzzle;
import com.ise.officeescape.view.PuzzleView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

/**
 * Puzzle view for throwing items through the metal detector when the guard looks away.
 * Reaction-based timing puzzle - player must click when guard is not looking.
 */
public class SecurityPuzzleView extends PuzzleView {
    
    private SecurityPuzzle securityPuzzle;
    private Label guardStatusLabel;
    private Label progressLabel;
    private Label statusLabel;
    private Button throwButton;
    private Button resetButton;
    private Button closeButton;
    private VBox mainContainer;
    private Timeline guardAnimation;
    
    public SecurityPuzzleView(Puzzle puzzle) {
        super(puzzle);
        if (puzzle instanceof SecurityPuzzle) {
            this.securityPuzzle = (SecurityPuzzle) puzzle;
        } else {
            throw new IllegalArgumentException("SecurityPuzzleView requires a SecurityPuzzle instance");
        }
        initializeUI();
        startGuardAnimation();
    }
    
    private void initializeUI() {
        // Create translucent overlay background
        Rectangle overlayBackground = new Rectangle();
        overlayBackground.setFill(Color.rgb(0, 0, 0, 0.7));
        overlayBackground.widthProperty().bind(widthProperty());
        overlayBackground.heightProperty().bind(heightProperty());
        
        // Main container for the puzzle UI
        mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setMaxWidth(500);
        mainContainer.setStyle(
            "-fx-background-color: rgba(43, 43, 43, 0.95); " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: #ff9800; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 15;"
        );
        
        // Title
        Label titleLabel = new Label("Security Checkpoint");
        titleLabel.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-text-fill: #ff9800; " +
            "-fx-font-weight: bold;"
        );
        
        // Instructions
        Label instructionLabel = new Label(
            "The guard is watching the metal detector.\n" +
            "Wait for them to look away, then quickly throw your items through!\n" +
            "You need to successfully throw " + securityPuzzle.getRequiredThrows() + " items."
        );
        instructionLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-text-alignment: center;"
        );
        instructionLabel.setAlignment(Pos.CENTER);
        instructionLabel.setWrapText(true);
        
        // Guard status display
        VBox guardContainer = new VBox(10);
        guardContainer.setAlignment(Pos.CENTER);
        
        Label guardLabel = new Label("Guard Status:");
        guardLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-font-weight: bold;"
        );
        
        guardStatusLabel = new Label("LOOKING");
        guardStatusLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-text-fill: #f44336; " +
            "-fx-font-weight: bold;"
        );
        
        guardContainer.getChildren().addAll(guardLabel, guardStatusLabel);
        
        // Progress label
        progressLabel = new Label("Progress: 0/" + securityPuzzle.getRequiredThrows());
        progressLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-text-fill: #4CAF50; " +
            "-fx-font-weight: bold;"
        );
        updateProgress();
        
        // Status label (shows feedback)
        statusLabel = new Label("");
        statusLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #ffcc00; " +
            "-fx-text-alignment: center;"
        );
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setWrapText(true);
        statusLabel.setVisible(false);
        
        // Throw button
        throwButton = new Button("Throw Item Through Detector");
        throwButton.setStyle(
            "-fx-background-color: #ff9800; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 15 30; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        throwButton.setOnAction(e -> handleThrowItem());
        
        // Reset button
        resetButton = new Button("r");
        resetButton.setStyle(
            "-fx-background-color: #666; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8 16; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        resetButton.setOnAction(e -> {
            securityPuzzle.reset();
            updateProgress();
            statusLabel.setVisible(false);
            throwButton.setDisable(false);
        });
        
        // Close button
        closeButton = new Button("x");
        closeButton.setStyle(
            "-fx-background-color: #666; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-padding: 8 16; " +
            "-fx-background-radius: 5; " +
            "-fx-cursor: hand;"
        );
        closeButton.setOnAction(e -> {
            if (guardAnimation != null) {
                guardAnimation.stop();
            }
            this.puzzle.OnPuzzleClosed.invoke(this.puzzle, new Puzzle.OnPuzzleClosedEventArgs());
        });
        
        HBox buttonContainer = new HBox(10);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(throwButton, resetButton, closeButton);
        
        mainContainer.getChildren().addAll(
            titleLabel,
            instructionLabel,
            guardContainer,
            progressLabel,
            statusLabel,
            buttonContainer
        );
        
        // Center the main container
        StackPane centerPane = new StackPane();
        centerPane.setAlignment(Pos.CENTER);
        centerPane.prefWidthProperty().bind(widthProperty());
        centerPane.prefHeightProperty().bind(heightProperty());
        centerPane.getChildren().addAll(overlayBackground, mainContainer);
        
        getChildren().add(centerPane);
    }
    
    /**
     * Starts the guard animation - guard alternates between looking and looking away.
     */
    private void startGuardAnimation() {
        // Guard looks away for 1-2 seconds, then looks back for 2-3 seconds
        guardAnimation = new Timeline(
            new KeyFrame(Duration.seconds(0), e -> {
                // Guard starts looking
                securityPuzzle.setGuardLookingAway(false);
                updateGuardStatus();
            }),
            new KeyFrame(Duration.seconds(2.5), e -> {
                // Guard looks away
                securityPuzzle.setGuardLookingAway(true);
                updateGuardStatus();
            }),
            new KeyFrame(Duration.seconds(3.5), e -> {
                // Guard looks back
                securityPuzzle.setGuardLookingAway(false);
                updateGuardStatus();
            }),
            new KeyFrame(Duration.seconds(6.0), e -> {
                // Guard looks away again
                securityPuzzle.setGuardLookingAway(true);
                updateGuardStatus();
            }),
            new KeyFrame(Duration.seconds(7.5), e -> {
                // Guard looks back
                securityPuzzle.setGuardLookingAway(false);
                updateGuardStatus();
            }),
            new KeyFrame(Duration.seconds(10.0), e -> {
                // Guard looks away again
                securityPuzzle.setGuardLookingAway(true);
                updateGuardStatus();
            }),
            new KeyFrame(Duration.seconds(11.5), e -> {
                // Guard looks back - restart cycle
                securityPuzzle.setGuardLookingAway(false);
                updateGuardStatus();
            })
        );
        guardAnimation.setCycleCount(Timeline.INDEFINITE);
        guardAnimation.play();
    }
    
    /**
     * Updates the guard status display.
     */
    private void updateGuardStatus() {
        if (securityPuzzle.isGuardLookingAway()) {
            guardStatusLabel.setText("LOOKING AWAY");
            guardStatusLabel.setStyle(
                "-fx-font-size: 32px; " +
                "-fx-text-fill: #4CAF50; " +
                "-fx-font-weight: bold;"
            );
        } else {
            guardStatusLabel.setText("LOOKING");
            guardStatusLabel.setStyle(
                "-fx-font-size: 32px; " +
                "-fx-text-fill: #f44336; " +
                "-fx-font-weight: bold;"
            );
        }
    }
    
    /**
     * Updates the progress display.
     */
    private void updateProgress() {
        progressLabel.setText("Progress: " + securityPuzzle.getSuccessfulThrows() + "/" + securityPuzzle.getRequiredThrows());
    }
    
    private void handleThrowItem() {
        // Call puzzle to handle the throw
        InteractionResult result = securityPuzzle.interact("throwItem", null);
        
        if (result.getType() == InteractionResult.ResultType.PUZZLE_SOLVED) {
            // Success! All items thrown
            statusLabel.setText("Success! All items thrown through!");
            statusLabel.setStyle(
                "-fx-font-size: 16px; " +
                "-fx-text-fill: #4CAF50; " +
                "-fx-font-weight: bold; " +
                "-fx-text-alignment: center;"
            );
            statusLabel.setVisible(true);
            throwButton.setDisable(true);
            
            if (guardAnimation != null) {
                guardAnimation.stop();
            }
            
            // Show success overlay
            showSuccessOverlay();
            
            // Trigger solved event after a delay
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> {
                    this.puzzle.OnPuzzleSolved.invoke(this.puzzle, new Puzzle.OnPuzzleSolvedEventArgs(this.puzzle.getId(), result));
                })
            );
            timeline.play();
        } else {
            // Show feedback message
            String message = result.getMessage();
            if (message != null) {
                if (message.contains("CAUGHT")) {
                    statusLabel.setText(message);
                    statusLabel.setStyle(
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #f44336; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-alignment: center;"
                    );
                    throwButton.setDisable(true);
                } else if (message.contains("Success")) {
                    statusLabel.setText(message);
                    statusLabel.setStyle(
                        "-fx-font-size: 14px; " +
                        "-fx-text-fill: #4CAF50; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-alignment: center;"
                    );
                    updateProgress();
                }
                statusLabel.setVisible(true);
            }
        }
    }
    
    /**
     * Shows an overlay displaying the success message.
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
        Label successLabel = new Label("You slipped through security!\nAll your items made it through unnoticed.");
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
        FadeTransition fadeIn = new FadeTransition(
            Duration.millis(500), overlayPane
        );
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
