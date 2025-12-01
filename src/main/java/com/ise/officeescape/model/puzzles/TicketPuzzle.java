package com.ise.officeescape.model.puzzles;

import com.ise.officeescape.model.InteractionResult;
import com.ise.officeescape.model.Puzzle;

/**
 * Puzzle for getting a ticket from the ticket machine.
 */
public class TicketPuzzle extends Puzzle {
    private boolean ticketObtained;
    private int ticketNumber;

    public TicketPuzzle() {
        super("ticketPuzzle", "Get Your Ticket Number");
        this.ticketObtained = false;
        this.ticketNumber = (int)(Math.random() * 100) + 1;
    }

    @Override
    public InteractionResult interact(String action, Object context) {
        if (action.equals("complete")) {
            // Puzzle was completed by dragging the lever multiple times
            if (!ticketObtained) {
                ticketObtained = true;
                solved = true;
                return InteractionResult.puzzleSolved("ticketPuzzle")
                    .addViewUpdate("enableHotspot:securityDoor")
                    .addDialogue("You got ticket number " + ticketNumber + "!");
            } else {
                return InteractionResult.message("You already have a ticket.");
            }
        }
        
        if (action.equals("interact") || action.equals("get_ticket")) {
            if (!ticketObtained) {
                ticketObtained = true;
                solved = true;
                return InteractionResult.puzzleSolved("ticketPuzzle")
                    .addViewUpdate("enableHotspot:securityDoor")
                    .addDialogue("You got ticket number " + ticketNumber + "!");
            } else {
                return InteractionResult.message("You already have a ticket.");
            }
        }

        return InteractionResult.message("Click the button to get your ticket.");
    }

    public boolean isTicketObtained() {
        return ticketObtained;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }
}
