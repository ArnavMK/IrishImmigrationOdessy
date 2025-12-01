package com.ise.officeescape.view.puzzles;

import com.ise.officeescape.model.InteractionResult;
import com.ise.officeescape.model.Puzzle;
import com.ise.officeescape.view.PuzzleView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.animation.FadeTransition;

import java.util.ArrayList;
import java.util.List;

/**
 * Puzzle view for the ticket machine quiz.
 * 
 * CONCEPT: The ticket machine requires you to complete a bureaucratic quiz
 * about Irish immigration rules. The questions are frustratingly pedantic and
 * some answers are intentionally confusing. You need to get all questions right
 * to get your ticket number.
 */
public class TicketMachinePuzzleView extends PuzzleView {
    
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private Label questionLabel;
    private VBox optionsContainer;
    private Label feedbackLabel;
    private Label progressLabel;
    private Button nextButton;
    private VBox quizContent;
    private List<Question> questions;

    // Quiz questions - funny and frustrating!
    private static class Question {
        String question;
        List<String> options;
        int correctIndex;
        String feedback;

        Question(String q, List<String> opts, int correct, String fb) {
            this.question = q;
            this.options = new ArrayList<>(opts);
            this.correctIndex = correct;
            this.feedback = fb;
        }
    }

    public TicketMachinePuzzleView(Puzzle puzzle) {
        super(puzzle);
        initializeQuiz();
    }

    private void initializeQuiz() {
        // Clear the default content and rebuild
        getChildren().clear();
        
        // Load ticket machine background image
        ImageView backgroundImage = new ImageView();
        try {
            Image bgImage = new Image(getClass().getResourceAsStream("/com/ise/officeescape/assets/ticketMachine.png"));
            backgroundImage.setImage(bgImage);
            backgroundImage.setSmooth(true);
            
        } catch (Exception e) {
            System.err.println("Could not load ticket machine image: " + e.getMessage());
        }
        
        if (backgroundImage.getImage() != null) {
            // Use a StackPane to center the image properly
            StackPane imageContainer = new StackPane();
            imageContainer.setAlignment(Pos.CENTER);
            imageContainer.prefWidthProperty().bind(widthProperty());
            imageContainer.prefHeightProperty().bind(heightProperty());
            imageContainer.getChildren().add(backgroundImage);
            getChildren().add(imageContainer);
        }
        
        // Initialize questions
        initializeQuestions();
        
        // Main content container - centered
        StackPane centerContainer = new StackPane();
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.prefWidthProperty().bind(widthProperty());
        centerContainer.prefHeightProperty().bind(heightProperty());
        
        // Progress label (small, top of quiz)
        progressLabel = new Label();
        progressLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #4CAF50; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: rgba(0, 0, 0, 0.7); " +
            "-fx-padding: 5 10; " +
            "-fx-background-radius: 5;"
        );
        updateProgress();
        
        // Quiz content area - smaller, centered
        quizContent = new VBox(10);
        quizContent.setAlignment(Pos.CENTER);
        quizContent.setMaxWidth(450);
        quizContent.setMaxHeight(290);
        quizContent.setPadding(new Insets(15));
        quizContent.setFillWidth(true);
        quizContent.setStyle(
            "-fx-background-color: rgba(22, 33, 62, 0.95); " +
            "-fx-background-radius: 12; " +
            "-fx-border-color: #0f3460; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 12;"
        );
        
        // Question label
        questionLabel = new Label();
        questionLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-font-weight: bold; " +
            "-fx-wrap-text: true; " +
            "-fx-text-alignment: center;"
        );
        questionLabel.setWrapText(true);
        questionLabel.setMaxWidth(400);
        questionLabel.setPrefWidth(400);
        questionLabel.setMinHeight(Region.USE_PREF_SIZE);
        questionLabel.setMaxHeight(Double.MAX_VALUE);
        
        // Options container
        optionsContainer = new VBox(6);
        optionsContainer.setAlignment(Pos.CENTER);
        optionsContainer.setPadding(new Insets(8));
        
        // Feedback label
        feedbackLabel = new Label();
        feedbackLabel.setStyle(
            "-fx-font-size: 12px; " +
            "-fx-text-fill: #ffcc00; " +
            "-fx-font-weight: bold;"
        );
        feedbackLabel.setWrapText(true);
        feedbackLabel.setMaxWidth(400);
        feedbackLabel.setVisible(false);
        
        // Next button (initially hidden)
        nextButton = new Button("Next");
        nextButton.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 12px; " +
            "-fx-padding: 6 20; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5;"
        );
        nextButton.setVisible(false);
        nextButton.setOnAction(e -> nextQuestion());
        
        // Close button (small, top-right)
        Button closeButton = new Button("✕");
        closeButton.setStyle(
            "-fx-background-color: rgba(244, 67, 54, 0.8); " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-padding: 5 10; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 5; " +
            "-fx-min-width: 30; " +
            "-fx-min-height: 30;"
        );
        closeButton.setOnAction(e -> {
            OnPuzzleClosed.invoke(this, new OnPuzzleClosedEventArgs());
        });
        
        quizContent.getChildren().addAll(
            questionLabel,
            optionsContainer,
            feedbackLabel,
            nextButton
        );
        
        // Position progress label at top of quiz content
        StackPane quizWithProgress = new StackPane();
        quizWithProgress.getChildren().add(quizContent);
        StackPane.setAlignment(progressLabel, Pos.TOP_CENTER);
        StackPane.setMargin(progressLabel, new Insets(10, 0, 0, 0));
        quizWithProgress.getChildren().add(progressLabel);
        
        // Move quiz box 20px left and 150px up
        quizWithProgress.setTranslateX(-30);
        quizWithProgress.setTranslateY(-200);
        
        // Position close button at top-right
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10, 0, 0));
        centerContainer.getChildren().addAll(quizWithProgress, closeButton);
        
        getChildren().add(centerContainer);
        
        // Display first question
        displayQuestion(0);
    }
    
    private void initializeQuestions() {
        questions = new ArrayList<>();
        
        // Question 1: Frustratingly pedantic
        questions.add(new Question(
            "What does 2 + 2 euqals to?",
            List.of(
                "3.99999",
                "4.000001",
                "root 16",
                "just wing it"
            ),
            2, // B is correct (most frustrating answer)
            "Correct! becuase root 16 is a perfect 4."
        ));
        
        // Question 2: Confusing bureaucracy
        questions.add(new Question(
            "How would fix your laptop, as a professional engineer?",
            List.of(
                "Check the CPU",
                "You cant fix it becuase you skipped All Mark lectures",
                "Turn it off and on",
                "Beg alison for another laptop."
            ),
            2, // C is correct
            "Correct! Turning it on off always works"
        ));
        
        // Question 3: Trick question
        questions.add(new Question(
            "Who was responsible for the bugs in this project?",
            List.of(
                "you",
                "Most definatley you",
                "All you buddy",
                "Java"
            ),
            3, // C is correct (most annoying)
            "Correct! Blame the language as always!"
        ));
        
        // Question 4: Absurd requirement
        questions.add(new Question(
            "Your teamate comes to you with a bug what do you say?",
            List.of(
                "Sure lemme take a look",
                "UUhh it works on my machine",
                "You should go to someone smarter than me for this",
                "..."
            ),
            1, // Only black
            "Correct! Apparently your machine is special"
        ));
        
        // Question 5: Final question
        questions.add(new Question(
            "What is the most important tool for programmers?",
            List.of(
                "Keyboard",
                "caffine",
                "a laptop"
            ),
            1, // All of the above
            "Correct! Welcome to Irish bureaucracy. You've passed the quiz!"
        ));
    }
    
    private void displayQuestion(int index) {
        if (index >= questions.size()) {
            completeQuiz();
            return;
        }
        
        Question q = questions.get(index);
        currentQuestionIndex = index;
        
        // Update question text
        questionLabel.setText((index + 1) + ". " + q.question);
        
        // Clear previous options
        optionsContainer.getChildren().clear();
        feedbackLabel.setVisible(false);
        nextButton.setVisible(false);
        
        // Create option buttons (in original order, not shuffled)
        for (int i = 0; i < q.options.size(); i++) {
            final String option = q.options.get(i);
            final boolean isCorrect = (i == q.correctIndex);
            
            Button optionButton = new Button(option);
            optionButton.setStyle(
                "-fx-background-color: #0f3460; " +
                "-fx-text-fill: #ffffff; " +
                "-fx-font-size: 12px; " +
                "-fx-padding: 8 16; " +
                "-fx-cursor: hand; " +
                "-fx-background-radius: 5; " +
                "-fx-min-width: 380; " +
                "-fx-alignment: center-left;"
            );
            
            optionButton.setOnAction(e -> handleAnswer(isCorrect, q.feedback));
            
            // Hover effect
            optionButton.setOnMouseEntered(m -> {
                optionButton.setStyle(
                    "-fx-background-color: #1a4d7a; " +
                    "-fx-text-fill: #ffffff; " +
                    "-fx-font-size: 12px; " +
                    "-fx-padding: 8 16; " +
                    "-fx-cursor: hand; " +
                    "-fx-background-radius: 5; " +
                    "-fx-min-width: 380; " +
                    "-fx-alignment: center-left;"
                );
            });
            
            optionButton.setOnMouseExited(m -> {
                optionButton.setStyle(
                    "-fx-background-color: #0f3460; " +
                    "-fx-text-fill: #ffffff; " +
                    "-fx-font-size: 12px; " +
                    "-fx-padding: 8 16; " +
                    "-fx-cursor: hand; " +
                    "-fx-background-radius: 5; " +
                    "-fx-min-width: 380; " +
                    "-fx-alignment: center-left;"
                );
            });
            
            optionsContainer.getChildren().add(optionButton);
        }
        
        updateProgress();
    }
    
    private void handleAnswer(boolean isCorrect, String feedback) {
        // Disable all option buttons
        for (var child : optionsContainer.getChildren()) {
            if (child instanceof Button) {
                Button btn = (Button) child;
                btn.setDisable(true);
                
                // Color code the buttons
                if (btn.getText().equals(questions.get(currentQuestionIndex).options.get(questions.get(currentQuestionIndex).correctIndex))) {
                    // Correct answer - green
                    btn.setStyle(
                        "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-font-size: 12px; " +
                        "-fx-padding: 8 16; " +
                        "-fx-background-radius: 5; " +
                        "-fx-min-width: 380; " +
                        "-fx-alignment: center-left;"
                    );
                } else {
                    // Wrong answer - red
                    btn.setStyle(
                        "-fx-background-color: #f44336; " +
                        "-fx-text-fill: #ffffff; " +
                        "-fx-font-size: 12px; " +
                        "-fx-padding: 8 16; " +
                        "-fx-background-radius: 5; " +
                        "-fx-min-width: 380; " +
                        "-fx-alignment: center-left;"
                    );
                }
            }
        }
        
        // Show feedback
        if (isCorrect) {
            correctAnswers++;
            feedbackLabel.setText("✓ " + feedback);
            feedbackLabel.setStyle(
                "-fx-font-size: 12px; " +
                "-fx-text-fill: #4CAF50; " +
                "-fx-font-weight: bold;"
            );
        } else {
            feedbackLabel.setText("✗ Wrong! Try again.");
            feedbackLabel.setStyle(
                "-fx-font-size: 12px; " +
                "-fx-text-fill: #f44336; " +
                "-fx-font-weight: bold;"
            );
        }
        
        feedbackLabel.setVisible(true);
        
        // Show next button
        if (currentQuestionIndex < questions.size() - 1) {
            nextButton.setVisible(true);
        } else {
            // Last question - change button text
            nextButton.setText("Complete Quiz");
            nextButton.setVisible(true);
        }
    }
    
    private void nextQuestion() {
        displayQuestion(currentQuestionIndex + 1);
    }
    
    private void updateProgress() {
        progressLabel.setText((currentQuestionIndex + 1) + "/" + questions.size() + 
                            " | ✓ " + correctAnswers);
    }
    
    private void completeQuiz() {
        questionLabel.setText("Complete!");
        questionLabel.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-text-fill: #4CAF50; " +
            "-fx-font-weight: bold;"
        );
        
        optionsContainer.getChildren().clear();
        feedbackLabel.setVisible(false);
        nextButton.setVisible(false);
        
        // Show results
        String resultText;
        if (correctAnswers == questions.size()) {
            resultText = "Perfect! All " + questions.size() + " correct!\nTicket dispensed...";
            feedbackLabel.setText(resultText);
            feedbackLabel.setStyle(
                "-fx-font-size: 12px; " +
                "-fx-text-fill: #4CAF50; " +
                "-fx-font-weight: bold;"
            );
            feedbackLabel.setVisible(true);
            
            // Solve the puzzle
            InteractionResult result = puzzle.interact("complete", null);
            if (result.getType() == InteractionResult.ResultType.PUZZLE_SOLVED) {
                // Show ticket item overlay
                showTicketItemOverlay();
                
                // Auto-close after a delay (increased to allow overlay to be seen)
                javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), e -> {
                        OnPuzzleSolved.invoke(this, new OnPuzzleSolvedEventArgs(puzzle.getId(), result));
                    })
                );
                timeline.play();
            }
        } else {
            resultText = correctAnswers + "/" + questions.size() + " correct.\nAll correct required. Try again.";
            feedbackLabel.setText(resultText);
            feedbackLabel.setStyle(
                "-fx-font-size: 12px; " +
                "-fx-text-fill: #f44336; " +
                "-fx-font-weight: bold;"
            );
            feedbackLabel.setVisible(true);
            
            // Reset quiz
            Button retryButton = new Button("Retry");
            retryButton.setStyle(
                "-fx-background-color: #ff9800; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 12px; " +
                "-fx-padding: 6 20; " +
                "-fx-cursor: hand; " +
                "-fx-background-radius: 5;"
            );
            retryButton.setOnAction(e -> {
                currentQuestionIndex = 0;
                correctAnswers = 0;
                displayQuestion(0);
            });
            optionsContainer.getChildren().add(retryButton);
        }
    }
    
    /**
     * Shows an overlay displaying the ticket item that was obtained.
     */
    private void showTicketItemOverlay() {
        // Create overlay background (semi-transparent dark)
        Rectangle overlayBackground = new Rectangle();
        overlayBackground.setFill(Color.rgb(0, 0, 0, 0.8));
        overlayBackground.widthProperty().bind(widthProperty());
        overlayBackground.heightProperty().bind(heightProperty());
        
        // Create container for the ticket item display
        VBox ticketContainer = new VBox(20);
        ticketContainer.setAlignment(Pos.CENTER);
        ticketContainer.setPadding(new Insets(30));
        ticketContainer.setMaxWidth(400);
        ticketContainer.setStyle(
            "-fx-background-color: rgba(43, 43, 43, 0.95); " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: #4CAF50; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 15;"
        );
        
        // Load and display ticket item image
        ImageView ticketImageView = new ImageView();
        try {
            Image ticketImage = new Image(getClass().getResourceAsStream("/com/ise/officeescape/assets/ticketItem.png"));
            ticketImageView.setImage(ticketImage);
            ticketImageView.setPreserveRatio(true);
            ticketImageView.setFitWidth(200);
            ticketImageView.setFitHeight(200);
            ticketImageView.setSmooth(true);
        } catch (Exception e) {
            System.err.println("Could not load ticket item image: " + e.getMessage());
        }
        
        // Label showing you got the ticket
        Label ticketLabel = new Label("Ticket Obtained!");
        ticketLabel.setStyle(
            "-fx-font-size: 24px; " +
            "-fx-text-fill: #4CAF50; " +
            "-fx-font-weight: bold;"
        );
        
        ticketContainer.getChildren().addAll(ticketImageView, ticketLabel);
        
        // Create StackPane to center the ticket container
        StackPane overlayPane = new StackPane();
        overlayPane.setAlignment(Pos.CENTER);
        overlayPane.prefWidthProperty().bind(widthProperty());
        overlayPane.prefHeightProperty().bind(heightProperty());
        overlayPane.getChildren().addAll(overlayBackground, ticketContainer);
        
        // Add overlay to the view (on top of everything)
        getChildren().add(overlayPane);
        
        // Animate the overlay appearance (fade in)
        overlayPane.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(
            javafx.util.Duration.millis(500), overlayPane
        );
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
