package com.ise.officeescape.model.puzzles;

import com.ise.officeescape.model.InteractionResult;
import com.ise.officeescape.model.Player;
import com.ise.officeescape.model.Puzzle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Puzzle for the immigration interview.
 * Rapid-fire questions that check if you have all required documents.
 */
public class InterviewPuzzle extends Puzzle {
    
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private List<InterviewQuestion> questions;
    private Player player; // Store player reference for document checks
    
    // Required documents for the interview
    private static final List<String> REQUIRED_DOCUMENTS = Arrays.asList(
        "passport",
        "visaApplication",
        "stolenDocument", // Can be used instead of photoId
        "birthCertificate",
        "employmentLetter"
    );
    
    // Interview question data structure
    public static class InterviewQuestion {
        public final String question;
        public final List<String> options;
        public final int correctIndex;
        public final String documentCheck; // Document name to check, or null
        public final String feedback;
        public final int timeLimit; // Time in seconds to answer

        public InterviewQuestion(String q, List<String> opts, int correct, String docCheck, String fb, int timeLimit) {
            this.question = q;
            this.options = new ArrayList<>(opts);
            this.correctIndex = correct;
            this.documentCheck = docCheck;
            this.feedback = fb;
            this.timeLimit = timeLimit;
        }
    }

    public InterviewPuzzle() {
        super("interviewPuzzle", "Immigration Interview");
        initializeQuestions();
    }
    
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean canStart(Player player) {
        // Can always start the interview, but you might fail if you don't have documents
        return true;
    }

    @Override
    public InteractionResult interact(String action, Object context) {
        if (action.equals("answer")) {
            // This method is not used directly - handleAnswer is called from the view
            // with both answer index and document name
            return InteractionResult.none();
        } else if (action.equals("complete")) {
            return completeInterview();
        } else if (action.equals("start")) {
            return InteractionResult.puzzleTriggered(getId());
        }
        return InteractionResult.none();
    }

    private void initializeQuestions() {
        questions = new ArrayList<>();
        
        // Question 1: Document check - Passport
        questions.add(new InterviewQuestion(
            "First question: Do you have your passport?",
            Arrays.asList("Yes, I have it", "No, I forgot it", "What's a passport?", "I have a driver's license"),
            0,
            "passport",
            "Good, you have your passport.",
            5
        ));
        
        // Question 2: Document check - Visa Application
        questions.add(new InterviewQuestion(
            "Do you have your completed visa application form?",
            Arrays.asList("Yes, right here", "I filled it out in pencil", "What form?", "I have a napkin with notes"),
            0,
            "visaApplication",
            "Excellent, the form is required.",
            5
        ));
        
        // Question 3: Document check - Birth Certificate
        questions.add(new InterviewQuestion(
            "Can you show me your birth certificate?",
            Arrays.asList("Yes, here it is", "I was born yesterday", "My mom has it", "I have a certificate of existence"),
            0,
            "birthCertificate",
            "Good, birth certificate verified.",
            4
        ));
        
        // Question 4: Stupid question - What is bureaucracy?
        questions.add(new InterviewQuestion(
            "What is bureaucracy?",
            Arrays.asList("A form of government", "A type of pasta", "The art of waiting", "I don't know"),
            0,
            null,
            "Interesting perspective.",
            5
        ));
        
        // Question 5: Document check - Employment Letter
        questions.add(new InterviewQuestion(
            "Can you provide proof of employment?",
            Arrays.asList("Yes, here's my employment letter", "I'm self-employed", "I work for exposure", "I have a LinkedIn profile"),
            0,
            "employmentLetter",
            "Employment verified.",
            5
        ));
        
        // Question 6: Stupid question - How many forms?
        questions.add(new InterviewQuestion(
            "How many forms have you filled out today?",
            Arrays.asList("Too many", "Not enough", "Exactly 42", "I lost count"),
            0,
            null,
            "That sounds about right.",
            4
        ));
        
        // Question 7: Stupid question - Why Ireland?
        questions.add(new InterviewQuestion(
            "Why Ireland specifically?",
            Arrays.asList("The people are friendly", "I like the weather", "I heard the bureaucracy is fun", "I got lost"),
            0,
            null,
            "Fair enough.",
            5
        ));
        
        // Question 8: Document check - Stolen Document
        questions.add(new InterviewQuestion(
            "Do you have your identification document?",
            Arrays.asList("Yes, I have it", "I have a selfie", "My face is my ID", "I have a drawing of myself"),
            0,
            "stolenDocument",
            "Identity verified.",
            4
        ));
        
        // Question 9: Stupid question - Why do you want to immigrate?
        questions.add(new InterviewQuestion(
            "Why do you want to immigrate to Ireland?",
            Arrays.asList("For better opportunities", "The weather", "To escape this interview", "I got lost"),
            0,
            null,
            "That's a reasonable answer.",
            6
        ));
        
        // Question 10: Stupid question - What is patience?
        questions.add(new InterviewQuestion(
            "On a scale of 1-10, how patient are you?",
            Arrays.asList("10 - Very patient", "5 - Moderately patient", "1 - Not patient at all", "What is patience?"),
            0,
            null,
            "Good answer.",
            4
        ));
        
        // Question 11: Stupid question - Capital of Ireland
        questions.add(new InterviewQuestion(
            "What is the capital of Ireland?",
            Arrays.asList("Dublin", "Cork", "Galway", "Belfast"),
            0,
            null,
            "Correct!",
            5
        ));
        
        // Question 12: Final check - Do you have all documents?
        questions.add(new InterviewQuestion(
            "Final question: Do you have ALL required documents with you right now?",
            Arrays.asList("Yes, I have everything", "I think so", "Maybe", "I hope so"),
            0,
            null,
            "Let me verify...",
            5
        ));
    }

    public InterviewQuestion getCurrentQuestion() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            return questions.get(currentQuestionIndex);
        }
        return null;
    }

    public InteractionResult handleAnswer(int selectedIndex, String selectedDocumentName) {
        InterviewQuestion currentQuestion = getCurrentQuestion();
        if (currentQuestion == null) {
            return InteractionResult.none();
        }

        // Check if answer is correct
        boolean isCorrect = (selectedIndex == currentQuestion.correctIndex);
        
        // If question requires a document check, verify the correct document was selected
        if (currentQuestion.documentCheck != null) {
            if (selectedDocumentName == null || !selectedDocumentName.equals(currentQuestion.documentCheck)) {
                isCorrect = false;
                System.out.println("[InterviewPuzzle] Document check failed - expected: " + currentQuestion.documentCheck + ", got: " + selectedDocumentName);
            } else {
                System.out.println("[InterviewPuzzle] Document check passed - correct document selected: " + selectedDocumentName);
            }
        }

        if (isCorrect) {
            correctAnswers++;
        }

        // Move to next question
        currentQuestionIndex++;

        // Check if interview is complete
        if (currentQuestionIndex >= questions.size()) {
            return completeInterview();
        }

        return InteractionResult.message(isCorrect ? currentQuestion.feedback : "That's not quite right. Let's continue.");
    }

    private InteractionResult completeInterview() {
        // Check if player has all required documents
        boolean hasAllDocuments = true;
        if (player != null) {
            for (String doc : REQUIRED_DOCUMENTS) {
                if (!player.getInventory().hasItem(doc)) {
                    hasAllDocuments = false;
                    break;
                }
            }
        }

        // Need all documents AND most questions correct (at least 7 out of 9)
        boolean passed = hasAllDocuments && correctAnswers >= 7;

        if (passed) {
            setSolved(true);
            return InteractionResult.puzzleSolved(getId());
        } else {
            // Reset interview to try again
            resetInterview();
            InteractionResult result = InteractionResult.message(
                "I'm sorry, but you're missing some required documents or didn't answer correctly. " +
                "Please gather all necessary documents and try again."
            );
            return result;
        }
    }

    public void resetInterview() {
        currentQuestionIndex = 0;
        correctAnswers = 0;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public int getTotalQuestions() {
        return questions.size();
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public List<String> getRequiredDocuments() {
        return new ArrayList<>(REQUIRED_DOCUMENTS);
    }
}

