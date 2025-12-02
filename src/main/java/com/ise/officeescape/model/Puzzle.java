package com.ise.officeescape.model;

import com.ise.officeescape.eventSystem.*;

/**
 * Base class for puzzles in rooms.
 * Puzzles define game logic that players must solve.
 */
public abstract class Puzzle {
    protected String id;
    protected String description;
    protected boolean solved;
    
    // Event for when puzzle is solved
    public Event<OnPuzzleSolvedEventArgs> OnPuzzleSolved = new Event<>();
    public static class OnPuzzleSolvedEventArgs extends EventArgs {
        public final String puzzleId;
        public final InteractionResult result;
        public OnPuzzleSolvedEventArgs(String puzzleId, InteractionResult result) {
            this.puzzleId = puzzleId;
            this.result = result;
        }
    }
    
    // Event for when puzzle view is closed/cancelled
    public Event<OnPuzzleClosedEventArgs> OnPuzzleClosed = new Event<>();
    public static class OnPuzzleClosedEventArgs extends EventArgs {
        public OnPuzzleClosedEventArgs() {}
    }

    public Puzzle(String id, String description) {
        this.id = id;
        this.description = description;
        this.solved = false;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    /**
     * Handle an interaction with this puzzle.
     * @param action The action performed (e.g., "press_button", "enter_code")
     * @param context Additional context if needed
     * @return The result of the interaction
     */
    public abstract InteractionResult interact(String action, Object context);

    /**
     * Checks if this puzzle can be started.
     * @param player The player attempting to start the puzzle
     * @return true if the puzzle can be started, false otherwise
     */
    public abstract boolean canStart(Player player);
        
}

