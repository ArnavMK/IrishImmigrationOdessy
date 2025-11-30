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
        System.out.println("[Room] interact() called for: " + interactableId + " with action: " + action);
        Interactable interactable = interactables.get(interactableId);
        if (interactable == null) {
            System.out.println("[Room] ERROR: Interactable not found: " + interactableId);
            return InteractionResult.message("You can't interact with that.");
        }
        if (!interactable.isEnabled()) {
            System.out.println("[Room] Interactable is disabled: " + interactableId);
            return InteractionResult.message("You can't interact with that.");
        }

        System.out.println("[Room] Interactable found: " + interactableId + ", type: " + interactable.getInteractionType());

        // Default interaction handling
        switch (interactable.getInteractionType()) {
            case "TAKE_TICKET":
                System.out.println("[Room] Handling TAKE_TICKET interaction");
                Puzzle queuePuzzle = puzzles.get("queuePuzzle");
                if (queuePuzzle != null) {
                    System.out.println("[Room] Found queuePuzzle, calling interact()");
                    return queuePuzzle.interact("take_ticket", null);
                } else {
                    System.out.println("[Room] WARNING: queuePuzzle not found");
                }
                break;
            case "CHECK_QUEUE":
                System.out.println("[Room] Handling CHECK_QUEUE interaction");
                Puzzle checkPuzzle = puzzles.get("queuePuzzle");
                if (checkPuzzle != null) {
                    System.out.println("[Room] Found queuePuzzle, calling interact()");
                    return checkPuzzle.interact("check_queue", null);
                } else {
                    System.out.println("[Room] WARNING: queuePuzzle not found");
                }
                break;
            default:
                System.out.println("[Room] Unknown interaction type: " + interactable.getInteractionType());
        }

        return InteractionResult.message("Nothing happens.");
    }
}
