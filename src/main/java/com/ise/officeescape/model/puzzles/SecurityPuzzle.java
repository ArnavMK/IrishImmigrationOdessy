package com.ise.officeescape.model.puzzles;

import com.ise.officeescape.model.InteractionResult;
import com.ise.officeescape.model.Player;
import com.ise.officeescape.model.Puzzle;

/**
 * Puzzle for throwing items through the metal detector when the guard looks away.
 * Player must time their throws correctly - only when the guard is not looking.
 */
public class SecurityPuzzle extends Puzzle {
    
    private static final int REQUIRED_SUCCESSFUL_THROWS = 3; // Need to successfully throw 3 items
    
    private boolean guardLookingAway = false; // Is the guard currently looking away?
    private int successfulThrows = 0; // Number of successful throws
    private boolean caught = false; // Has the player been caught?
    
    public SecurityPuzzle() {
        super("securityPuzzle", "Throw Items Past the Guard");
    }
    
    /**
     * Gets whether the guard is currently looking away.
     */
    public boolean isGuardLookingAway() {
        return guardLookingAway;
    }
    
    /**
     * Sets whether the guard is looking away (called by view when guard state changes).
     */
    public void setGuardLookingAway(boolean lookingAway) {
        this.guardLookingAway = lookingAway;
    }
    
    /**
     * Gets the number of successful throws.
     */
    public int getSuccessfulThrows() {
        return successfulThrows;
    }
    
    /**
     * Gets the required number of successful throws.
     */
    public int getRequiredThrows() {
        return REQUIRED_SUCCESSFUL_THROWS;
    }
    
    /**
     * Checks if the player has been caught.
     */
    public boolean isCaught() {
        return caught;
    }
    
    /**
     * Resets the puzzle state.
     */
    public void reset() {
        successfulThrows = 0;
        caught = false;
        guardLookingAway = false;
    }
    
    @Override
    public InteractionResult interact(String action, Object context) {
        if (action.equals("throwItem")) {
            // Player attempts to throw an item through the detector
            if (caught) {
                return InteractionResult.message("You've been caught! Try again.");
            }
            
            if (guardLookingAway) {
                // Guard is looking away - success!
                successfulThrows++;
                if (successfulThrows >= REQUIRED_SUCCESSFUL_THROWS) {
                    // All items thrown successfully - puzzle solved!
                    solved = true;
                    return InteractionResult.puzzleSolved("securityPuzzle")
                        .addDialogue("You successfully threw all your items through! The guard didn't notice.");
                } else {
                    return InteractionResult.message("Success! " + successfulThrows + "/" + REQUIRED_SUCCESSFUL_THROWS + " items thrown.");
                }
            } else {
                // Guard is looking - caught!
                caught = true;
                return InteractionResult.message("CAUGHT! The guard saw you. Try again when they look away.");
            }
        }
        
        if (action.equals("reset")) {
            // Reset the puzzle
            reset();
            return InteractionResult.message("Puzzle reset.");
        }
        
        return InteractionResult.message("Invalid action.");
    }
    
    @Override
    public boolean canStart(Player player) {
        // Can always start the security puzzle
        return true;
    }
}

