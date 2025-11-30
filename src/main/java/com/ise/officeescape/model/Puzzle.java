package com.ise.officeescape.model;

/**
 * Base class for puzzles in rooms.
 * Puzzles define game logic that players must solve.
 */
public abstract class Puzzle {
    protected String id;
    protected String description;
    protected boolean solved;

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
}

