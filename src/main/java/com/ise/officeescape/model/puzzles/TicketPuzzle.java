package com.ise.officeescape.model.puzzles;

import com.ise.officeescape.controller.GameController;
import com.ise.officeescape.model.InteractionResult;
import com.ise.officeescape.model.Item;
import com.ise.officeescape.model.Player;
import com.ise.officeescape.model.Puzzle;

import java.util.ArrayList;
import java.util.List;

/**
 * Puzzle for getting a ticket from the ticket machine.
 * Requires completing a quiz with all questions correct.
 */
public class TicketPuzzle extends Puzzle {
    private boolean ticketObtained;
    private int ticketNumber;
    
    // Quiz state
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private List<Question> questions;
    
    // Quiz question data structure
    public static class Question {
        public final String question;
        public final List<String> options;
        public final int correctIndex;
        public final String feedback;

        public Question(String q, List<String> opts, int correct, String fb) {
            this.question = q;
            this.options = new ArrayList<>(opts);
            this.correctIndex = correct;
            this.feedback = fb;
        }
    }

    public TicketPuzzle() {
        super("ticketPuzzle", "Get Your Ticket Number");
        this.ticketObtained = false;
        this.ticketNumber = 12;
        initializeQuestions();
    }

    @Override
    public InteractionResult interact(String action, Object context) {
        if (action.equals("complete")) {
            // Quiz completed - check if all answers were correct
            if (correctAnswers == questions.size() && !ticketObtained) {
                ticketObtained = true;
                solved = true;
                Item ticketItem = new Item("ticketItem", "This is the ticket required for the queue");
                // Return both PUZZLE_SOLVED and ITEM_OBTAINED
                // We'll handle this as a combined result in the controller
                return InteractionResult.puzzleSolved("ticketPuzzle")
                    .addDialogue("You got ticket number " + ticketNumber + "!")
                    .setItem(ticketItem);
            } else if (ticketObtained) {
                return InteractionResult.message("You already have a ticket.");
            } else {
                return InteractionResult.message("You need to answer all questions correctly.");
            }
        }
        
        if (action.equals("answer")) {
            // Handle answer selection
            if (context instanceof Integer) {
                int selectedIndex = (Integer) context;
                return handleAnswer(selectedIndex);
            }
        }
        
        if (action.equals("next")) {
            // Move to next question
            nextQuestion();
            return InteractionResult.message("Next question");
        }
        
        if (action.equals("reset")) {
            // Reset quiz
            resetQuiz();
            return InteractionResult.message("Quiz reset");
        }

        return InteractionResult.message("Click the button to get your ticket.");
    }
    
    /**
     * Initialize quiz questions.
     */
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
            2, // root 16 is correct
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
            2, // Turn it off and on is correct
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
            3, // Java is correct
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
            1, // "UUhh it works on my machine" is correct
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
            1, // caffine is correct
            "Correct! Welcome to Irish bureaucracy. You've passed the quiz!"
        ));
    }
    
    /**
     * Get the current question.
     */
    public Question getCurrentQuestion() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }
    
    /**
     * Get current question index (0-based).
     */
    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
    
    /**
     * Get total number of questions.
     */
    public int getTotalQuestions() {
        return questions.size();
    }
    
    /**
     * Get number of correct answers.
     */
    public int getCorrectAnswers() {
        return correctAnswers;
    }
    
    /**
     * Handle an answer selection.
     */
    public InteractionResult handleAnswer(int selectedIndex) {
        if (currentQuestionIndex >= questions.size()) {
            return InteractionResult.message("Quiz already completed");
        }
        
        Question q = questions.get(currentQuestionIndex);
        boolean isCorrect = (selectedIndex == q.correctIndex);
        
        if (isCorrect) {
            correctAnswers++;
            return InteractionResult.message("correct:" + q.feedback);
        } else {
            return InteractionResult.message("wrong");
        }
    }
    
    /**
     * Move to the next question.
     */
    public void nextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
        }
    }
    
    /**
     * Check if quiz is complete (all questions answered).
     * Quiz is complete when we've answered all questions (currentQuestionIndex is at or past the last question).
     */
    public boolean isQuizComplete() {
        return currentQuestionIndex >= questions.size();
    }
    
    /**
     * Check if all answers were correct.
     * This checks if we've answered all questions correctly.
     * We've answered all questions when we're on the last question (index == size - 1) or beyond.
     */
    public boolean isAllCorrect() {
        // Check if we've answered all questions and got them all correct
        // We've answered all questions if we're on the last question (index == size - 1) or beyond
        // and we got all answers correct
        return correctAnswers == questions.size() && currentQuestionIndex >= questions.size() - 1;
    }
    
    /**
     * Reset the quiz to start over.
     */
    public void resetQuiz() {
        currentQuestionIndex = 0;
        correctAnswers = 0;
    }
    
    /**
     * Check if ticket should be shown (quiz completed successfully).
     */
    public boolean shouldShowTicketOverlay() {
        return isAllCorrect() && ticketObtained;
    }

    public boolean isTicketObtained() {
        return ticketObtained;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

	@Override
	public boolean canStart(Player player) {
        if (player == null) {
            return false;
        }
        return !player.getInventory().hasItem("ticketItem");
	}
}
