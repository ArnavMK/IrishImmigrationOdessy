package com.ise.officeescape.view.puzzles;

import com.ise.officeescape.model.InteractionResult;
import com.ise.officeescape.model.Item;
import com.ise.officeescape.model.Puzzle;
import com.ise.officeescape.model.puzzles.InterviewPuzzle;
import com.ise.officeescape.view.PuzzleView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.util.Map;

/**
 * Puzzle view for the immigration interview.
 * Questions that check if you have required documents - for document questions,
 * you must select both the correct answer and click the correct document from your inventory.
 */
public class InterviewPuzzleView extends PuzzleView {
    
    private InterviewPuzzle interviewPuzzle;
    private Label questionLabel;
    private VBox optionsContainer;
    private Label feedbackLabel;
    private Label progressLabel;
    private Button nextButton;
    private VBox interviewContent;
    
    // Inventory display
    private VBox inventoryPanel;
    private ScrollPane inventoryScrollPane;
    private VBox inventoryItemsContainer;
    private Label documentHintLabel;
    
    // Selection tracking
    private Integer selectedAnswerIndex = null;
    private String selectedDocumentName = null;
    private boolean waitingForDocument = false;
    
    public InterviewPuzzleView(Puzzle puzzle) {
        super(puzzle);
        if (puzzle instanceof InterviewPuzzle) {
            this.interviewPuzzle = (InterviewPuzzle) puzzle;
        } else {
            throw new IllegalArgumentException("InterviewPuzzleView requires an InterviewPuzzle instance");
        }
        interviewPuzzle.resetInterview();
        initializeInterview();
    }

    private void initializeInterview() {
        // Create translucent overlay background
        Rectangle overlayBackground = new Rectangle();
        overlayBackground.setFill(Color.rgb(0, 0, 0, 0.8));
        overlayBackground.widthProperty().bind(widthProperty());
        overlayBackground.heightProperty().bind(heightProperty());
        
        // Main horizontal container - interview on left, inventory on right
        HBox mainContainer = new HBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(20));
        mainContainer.prefWidthProperty().bind(widthProperty());
        mainContainer.prefHeightProperty().bind(heightProperty());
        
        // Interview content panel (left side)
        interviewContent = new VBox(20);
        interviewContent.setAlignment(Pos.CENTER);
        interviewContent.setPadding(new Insets(40));
        interviewContent.setPrefWidth(700);
        interviewContent.setStyle(
            "-fx-background-color: rgba(30, 30, 40, 0.95); " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: #4a90e2; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 15;"
        );
        
        // Title
        Label titleLabel = new Label("Immigration Interview");
        titleLabel.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-text-fill: #4a90e2; " +
            "-fx-font-weight: bold;"
        );
        
        // Progress label
        progressLabel = new Label();
        progressLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #90EE90; " +
            "-fx-font-weight: bold;"
        );
        
        // Question label
        questionLabel = new Label();
        questionLabel.setStyle(
            "-fx-font-size: 18px; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-font-weight: bold; " +
            "-fx-wrap-text: true;"
        );
        questionLabel.setAlignment(Pos.CENTER);
        questionLabel.setMaxWidth(600);
        
        // Document hint label (for document questions)
        documentHintLabel = new Label();
        documentHintLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #ffd700; " +
            "-fx-font-weight: bold; " +
            "-fx-wrap-text: true;"
        );
        documentHintLabel.setAlignment(Pos.CENTER);
        documentHintLabel.setMaxWidth(600);
        documentHintLabel.setVisible(false);
        
        // Options container
        optionsContainer = new VBox(10);
        optionsContainer.setAlignment(Pos.CENTER);
        optionsContainer.setPadding(new Insets(20, 0, 20, 0));
        
        // Feedback label
        feedbackLabel = new Label();
        feedbackLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #ffd700; " +
            "-fx-wrap-text: true;"
        );
        feedbackLabel.setAlignment(Pos.CENTER);
        feedbackLabel.setMaxWidth(600);
        feedbackLabel.setVisible(false);
        
        // Next button (initially hidden)
        nextButton = new Button("Next Question");
        nextButton.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-background-color: #4a90e2; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10 20; " +
            "-fx-background-radius: 5;"
        );
        nextButton.setVisible(false);
        nextButton.setOnAction(e -> {
            feedbackLabel.setVisible(false);
            nextButton.setVisible(false);
            documentHintLabel.setVisible(false);
            selectedAnswerIndex = null;
            selectedDocumentName = null;
            waitingForDocument = false;
            displayCurrentQuestion();
        });
        
        // Close button
        Button closeButton = new Button("x");
        closeButton.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-background-color: #ff4444; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 5 10; " +
            "-fx-background-radius: 3;"
        );
        closeButton.setOnAction(e -> {
            puzzle.OnPuzzleClosed.invoke(puzzle, new Puzzle.OnPuzzleClosedEventArgs());
        });
        
        // Arrange close button in top-right
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        topBar.getChildren().add(closeButton);
        
        interviewContent.getChildren().addAll(
            topBar,
            titleLabel,
            progressLabel,
            questionLabel,
            documentHintLabel,
            optionsContainer,
            feedbackLabel,
            nextButton
        );
        
        // Inventory panel (right side)
        createInventoryPanel();
        
        mainContainer.getChildren().addAll(interviewContent, inventoryPanel);
        
        StackPane centerContainer = new StackPane();
        centerContainer.getChildren().add(overlayBackground);
        centerContainer.getChildren().add(mainContainer);
        getChildren().add(centerContainer);
        
        // Display first question
        displayCurrentQuestion();
    }
    
    private void createInventoryPanel() {
        inventoryPanel = new VBox(10);
        inventoryPanel.setAlignment(Pos.TOP_CENTER);
        inventoryPanel.setPadding(new Insets(20));
        inventoryPanel.setPrefWidth(250);
        inventoryPanel.setStyle(
            "-fx-background-color: rgba(30, 30, 40, 0.95); " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: #4a90e2; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 15;"
        );
        
        Label inventoryTitle = new Label("Your Documents");
        inventoryTitle.setStyle(
            "-fx-font-size: 20px; " +
            "-fx-text-fill: #4a90e2; " +
            "-fx-font-weight: bold;"
        );
        
        inventoryItemsContainer = new VBox(10);
        inventoryItemsContainer.setAlignment(Pos.TOP_CENTER);
        inventoryItemsContainer.setPadding(new Insets(10));
        
        inventoryScrollPane = new ScrollPane(inventoryItemsContainer);
        inventoryScrollPane.setFitToWidth(true);
        inventoryScrollPane.setPrefHeight(500);
        inventoryScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        inventoryPanel.getChildren().addAll(inventoryTitle, inventoryScrollPane);
        
        updateInventoryDisplay();
    }
    
    private void updateInventoryDisplay() {
        inventoryItemsContainer.getChildren().clear();
        
        if (interviewPuzzle.getPlayer() == null || interviewPuzzle.getPlayer().getInventory() == null) {
            Label emptyLabel = new Label("No items");
            emptyLabel.setStyle("-fx-text-fill: #888888;");
            inventoryItemsContainer.getChildren().add(emptyLabel);
            return;
        }
        
        Map<String, Item> items = interviewPuzzle.getPlayer().getInventory().getInvetoryMap();
        if (items.isEmpty()) {
            Label emptyLabel = new Label("(Empty)");
            emptyLabel.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-text-fill: #888888; " +
                "-fx-font-style: italic;"
            );
            inventoryItemsContainer.getChildren().add(emptyLabel);
        } else {
            for (Item item : items.values()) {
                VBox itemBox = createInventoryItemDisplay(item);
                inventoryItemsContainer.getChildren().add(itemBox);
            }
        }
    }
    
    private VBox createInventoryItemDisplay(Item item) {
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
        
        // Highlight if this is the required document and we're waiting for it
        InterviewPuzzle.InterviewQuestion currentQuestion = interviewPuzzle.getCurrentQuestion();
        boolean isRequiredDoc = currentQuestion != null && 
                               currentQuestion.documentCheck != null && 
                               currentQuestion.documentCheck.equals(item.getName());
        boolean isSelected = item.getName().equals(selectedDocumentName);
        
        if (isSelected) {
            itemBox.setStyle(
                "-fx-background-color: rgba(76, 175, 80, 0.9); " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: #4CAF50; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 5; " +
                "-fx-cursor: hand;"
            );
        } else if (isRequiredDoc && waitingForDocument) {
            itemBox.setStyle(
                "-fx-background-color: rgba(255, 193, 7, 0.8); " +
                "-fx-background-radius: 5; " +
                "-fx-border-color: #FFC107; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 5; " +
                "-fx-cursor: hand;"
            );
        }
        
        itemBox.setOnMouseEntered(e -> {
            if (!isSelected) {
                itemBox.setStyle(
                    "-fx-background-color: rgba(70, 70, 70, 0.9); " +
                    "-fx-background-radius: 5; " +
                    "-fx-border-color: #4CAF50; " +
                    "-fx-border-width: 2; " +
                    "-fx-border-radius: 5; " +
                    "-fx-cursor: hand;"
                );
            }
        });
        
        itemBox.setOnMouseExited(e -> {
            if (!isSelected) {
                if (isRequiredDoc && waitingForDocument) {
                    itemBox.setStyle(
                        "-fx-background-color: rgba(255, 193, 7, 0.8); " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-color: #FFC107; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 5; " +
                        "-fx-cursor: hand;"
                    );
                } else {
                    itemBox.setStyle(
                        "-fx-background-color: rgba(50, 50, 50, 0.8); " +
                        "-fx-background-radius: 5; " +
                        "-fx-border-color: #666666; " +
                        "-fx-border-width: 1; " +
                        "-fx-border-radius: 5; " +
                        "-fx-cursor: hand;"
                    );
                }
            }
        });
        
        // Click handler - select document
        itemBox.setOnMouseClicked(e -> {
            if (waitingForDocument) {
                selectedDocumentName = item.getName();
                updateInventoryDisplay(); // Refresh to show selection
                checkIfAnswerComplete();
            }
        });
        
        // Try to load item image
        ImageView itemImageView = new ImageView();
        try {
            String imagePath = "/com/ise/officeescape/assets/" + item.getName() + ".png";
            Image itemImage = new Image(getClass().getResourceAsStream(imagePath));
            itemImageView.setImage(itemImage);
            itemImageView.setPreserveRatio(true);
            itemImageView.setFitWidth(100);
            itemImageView.setFitHeight(100);
            itemImageView.setSmooth(true);
        } catch (Exception ex) {
            itemImageView.setVisible(false);
        }
        
        Label nameLabel = new Label(item.getName());
        nameLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-font-weight: bold;"
        );
        
        itemBox.getChildren().addAll(itemImageView, nameLabel);
        return itemBox;
    }
    
    private void displayCurrentQuestion() {
        InterviewPuzzle.InterviewQuestion currentQuestion = interviewPuzzle.getCurrentQuestion();
        
        if (currentQuestion == null) {
            completeInterview();
            return;
        }
        
        // Update progress
        progressLabel.setText(
            "Question " + (interviewPuzzle.getCurrentQuestionIndex() + 1) + 
            " of " + interviewPuzzle.getTotalQuestions() + 
            " | Correct: " + interviewPuzzle.getCorrectAnswers()
        );
        
        // Update question text
        questionLabel.setText(currentQuestion.question);
        
        // Reset selection state
        selectedAnswerIndex = null;
        selectedDocumentName = null;
        waitingForDocument = false;
        documentHintLabel.setVisible(false);
        
        // Clear previous options
        optionsContainer.getChildren().clear();
        
        // Update inventory display
        updateInventoryDisplay();
        
        // Create option buttons
        for (int i = 0; i < currentQuestion.options.size(); i++) {
            final int optionIndex = i;
            Button optionButton = new Button(currentQuestion.options.get(i));
            optionButton.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-background-color: #555555; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 12 20; " +
                "-fx-background-radius: 5; " +
                "-fx-pref-width: 500;"
            );
            optionButton.setOnMouseEntered(e -> {
                optionButton.setStyle(
                    "-fx-font-size: 14px; " +
                    "-fx-background-color: #666666; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 12 20; " +
                    "-fx-background-radius: 5; " +
                    "-fx-pref-width: 500;"
                );
            });
            optionButton.setOnMouseExited(e -> {
                optionButton.setStyle(
                    "-fx-font-size: 14px; " +
                    "-fx-background-color: #555555; " +
                    "-fx-text-fill: white; " +
                    "-fx-padding: 12 20; " +
                    "-fx-background-radius: 5; " +
                    "-fx-pref-width: 500;"
                );
            });
            optionButton.setOnAction(e -> handleAnswer(optionIndex));
            optionsContainer.getChildren().add(optionButton);
        }
    }
    
    private void handleAnswer(int selectedIndex) {
        InterviewPuzzle.InterviewQuestion currentQuestion = interviewPuzzle.getCurrentQuestion();
        if (currentQuestion == null) {
            return;
        }
        
        selectedAnswerIndex = selectedIndex;
        
        // If this is a document question, wait for document selection
        if (currentQuestion.documentCheck != null) {
            waitingForDocument = true;
            documentHintLabel.setText("Now click the document: " + currentQuestion.documentCheck);
            documentHintLabel.setVisible(true);
            updateInventoryDisplay(); // Refresh to highlight required document
            return;
        }
        
        // For non-document questions, process immediately
        processCompleteAnswer();
    }
    
    private void checkIfAnswerComplete() {
        InterviewPuzzle.InterviewQuestion currentQuestion = interviewPuzzle.getCurrentQuestion();
        if (currentQuestion == null || selectedAnswerIndex == null) {
            return;
        }
        
        // For document questions, need both answer and document
        if (currentQuestion.documentCheck != null) {
            if (selectedDocumentName == null) {
                return; // Still waiting for document
            }
        }
        
        // We have everything we need, process the answer
        processCompleteAnswer();
    }
    
    private void processCompleteAnswer() {
        InterviewPuzzle.InterviewQuestion currentQuestion = interviewPuzzle.getCurrentQuestion();
        if (currentQuestion == null || selectedAnswerIndex == null) {
            return;
        }
        
        // Disable all option buttons
        for (var child : optionsContainer.getChildren()) {
            if (child instanceof Button) {
                ((Button) child).setDisable(true);
            }
        }
        
        // Check if answer is correct
        boolean isCorrect = (selectedAnswerIndex == currentQuestion.correctIndex);
        
        // If document question, also check document
        if (currentQuestion.documentCheck != null) {
            if (selectedDocumentName == null || !selectedDocumentName.equals(currentQuestion.documentCheck)) {
                isCorrect = false;
            }
        }
        
        // Process through puzzle
        InteractionResult result = interviewPuzzle.handleAnswer(selectedAnswerIndex, selectedDocumentName);
        
        // Get feedback
        String feedback = result.getMessage();
        if (feedback == null || feedback.isEmpty()) {
            feedback = isCorrect ? currentQuestion.feedback : "That's not quite right. Let's continue.";
        }
        feedbackLabel.setText(feedback);
        feedbackLabel.setVisible(true);
        documentHintLabel.setVisible(false);
        
        // Move to next question after a brief delay
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            if (interviewPuzzle.getCurrentQuestionIndex() < interviewPuzzle.getTotalQuestions()) {
                displayCurrentQuestion();
            } else {
                completeInterview();
            }
        });
        pause.play();
    }
    
    private void completeInterview() {
        // Check if interview passed
        boolean passed = interviewPuzzle.isSolved();
        
        questionLabel.setText(passed ? 
            "Congratulations! You passed the interview!" : 
            "I'm sorry, but you're missing some required documents or didn't answer correctly. Please gather all necessary documents and try again."
        );
        
        optionsContainer.getChildren().clear();
        progressLabel.setText(
            "Final Score: " + interviewPuzzle.getCorrectAnswers() + 
            " out of " + interviewPuzzle.getTotalQuestions()
        );
        
        if (passed) {
            // Show success overlay
            showSuccessOverlay();
            
            // Auto-close after delay
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> {
                InteractionResult result = interviewPuzzle.interact("complete", null);
                puzzle.OnPuzzleSolved.invoke(puzzle, new Puzzle.OnPuzzleSolvedEventArgs(puzzle.getId(), result));
            });
            pause.play();
        } else {
            // Show retry button
            Button retryButton = new Button("Retry Interview");
            retryButton.setStyle(
                "-fx-font-size: 16px; " +
                "-fx-background-color: #4a90e2; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10 20; " +
                "-fx-background-radius: 5;"
            );
            retryButton.setOnAction(e -> {
                interviewPuzzle.resetInterview();
                displayCurrentQuestion();
            });
            optionsContainer.getChildren().add(retryButton);
        }
    }
    
    private void showSuccessOverlay() {
        Label successLabel = new Label("You passed the interview!");
        successLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-text-fill: #4CAF50; " +
            "-fx-font-weight: bold;"
        );
        
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), successLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        // Add to center of screen
        StackPane overlay = new StackPane(successLabel);
        overlay.setAlignment(Pos.CENTER);
        getChildren().add(overlay);
    }
}

