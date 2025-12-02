package com.ise.officeescape.view;

import com.ise.officeescape.model.Puzzle;
import javafx.scene.layout.StackPane;

/**
 * Abstract base class for displaying and interacting with puzzles.
 * Overlays on top of the game view when a puzzle is active.
 * Each puzzle type should have its own concrete implementation.
 */
public abstract class PuzzleView extends StackPane {
    
    protected Puzzle puzzle;

    public PuzzleView(Puzzle puzzle) {
        this.puzzle = puzzle;
    }
    
    public Puzzle getPuzzle() {
        return puzzle;
    }
}

