package com.ise.officeescape.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;
import java.util.List;
import java.util.Map;

public class Room {
    private String description;
    private String name;
    private Map<Direction, Room> exits; // Map direction to neighboring Room
    private Map<String, Interactable> interactables; // Map interactable ID to Interactable
    private Map<String, Puzzle> puzzles; // Map puzzle ID to Puzzle

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        exits = new HashMap<>();
        interactables = new HashMap<>();
        puzzles = new HashMap<>();
    }

    public String getDescription() {
        return description;
    }
    
    public String getName() {
        return name;
    } 
    
    public void setExit(Direction direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    public Room getExit(Direction direction) {
        return exits.get(direction);
    }

    public String getExitString() {
        StringBuilder sb = new StringBuilder();
        for (Direction direction : exits.keySet()) {
            sb.append(direction.toString()).append(" ");
        }
        return sb.toString().trim();
    }
     
    public Set<Direction> getAllExits() {
        return exits.keySet();
    }
    
    public String getLongDescription() {
        return "You are " + description + ".\nExits: " + getExitString();
    }

    // Interactable management
    public void addInteractable(Interactable interactable) {
        interactables.put(interactable.getId(), interactable);
    }

    public Interactable getInteractable(String id) {
        return interactables.get(id);
    }

    public List<Interactable> getAllInteractables() {
        return new ArrayList<>(interactables.values());
    }

    // Puzzle management
    public void addPuzzle(Puzzle puzzle) {
        puzzles.put(puzzle.getId(), puzzle);
    }

    public Puzzle getPuzzle(String id) {
        return puzzles.get(id);
    }

    public List<Puzzle> getAllPuzzles() {
        return new ArrayList<>(puzzles.values());
    }

    /**
     * Handle interaction with an interactable in this room.
     */
    public InteractionResult interact(String interactableId, String action) {
        Interactable interactable = interactables.get(interactableId);
        if (interactable == null) {
            return InteractionResult.message("You can't interact with that.");
        }
        if (!interactable.isEnabled()) {
            return InteractionResult.message("You can't interact with that.");
        }

        // Default interaction handling
        switch (interactable.getInteractionType()) {
            case "TAKE_TICKET":
                // Find the ticket puzzle and trigger it
                Puzzle ticketPuzzle = puzzles.get("ticketPuzzle");
                if (ticketPuzzle != null) {
                    // Return PUZZLE_TRIGGERED to show the puzzle view
                    return InteractionResult.puzzleTriggered("ticketPuzzle");
                }
                break;
        }

        return InteractionResult.message("Nothing happens.");
    }
}
