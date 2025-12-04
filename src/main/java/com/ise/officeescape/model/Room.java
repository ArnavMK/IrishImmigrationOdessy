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
    private Inventory inventory; // Room's inventory

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        exits = new HashMap<>();
        interactables = new HashMap<>();
        puzzles = new HashMap<>();
        inventory = new Inventory();
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
                    // Note: canStart() check moved to GameController.showPuzzleView()
                    // to avoid needing Player reference here
                    return InteractionResult.puzzleTriggered("ticketPuzzle");
                }
                break;
            case "TALK_TO_GUARD":
                // Find the guard puzzle and trigger it
                Puzzle queueGaurdPuzzle = puzzles.get("queueGaurdPuzzle");
                if (queueGaurdPuzzle != null) {
                    // Note: canStart() check moved to GameController.showPuzzleView()
                    // to avoid needing Player reference here
                    return InteractionResult.puzzleTriggered("queueGaurdPuzzle");
                }
                break;
            case "SECURITY_CHECK":
                // Find the security puzzle and trigger it
                Puzzle securityPuzzle = puzzles.get("securityPuzzle");
                if (securityPuzzle != null) {
                    return InteractionResult.puzzleTriggered("securityPuzzle");
                }
                break;
            case "START_INTERVIEW":
                // Find the interview puzzle and trigger it
                Puzzle interviewPuzzle = puzzles.get("interviewPuzzle");
                if (interviewPuzzle != null) {
                    return InteractionResult.puzzleTriggered("interviewPuzzle");
                }
                break;
        }

        return InteractionResult.message("Nothing happens.");
    }
    
    // Inventory management
    public Inventory getInventory() {
        return inventory;
    }
    
    /**
     * Checks if a player can enter this room.
     * Override this method or add conditions based on room name to implement entry restrictions.
     * 
     * @param player The player attempting to enter
     * @param roomManager The room manager to access other rooms (for checking puzzle states)
     * @return true if the player can enter, false otherwise
     */
    public boolean canEnter(Player player, RoomManager roomManager) {
        // Default: all rooms are accessible
        // Add room-specific conditions here
        if (name.equals("queue")) {
            // Queue room requires a ticket
            return player.getInventory().hasItem("ticketItem");
        }
        if (name.equals("security")) {
            // Security room requires the guard puzzle to be solved
            Room queueRoom = roomManager.getAllRooms().stream()
                .filter(r -> r.getName().equals("queue"))
                .findFirst()
                .orElse(null);
            if (queueRoom != null) {
                Puzzle guardPuzzle = queueRoom.getPuzzle("queueGaurdPuzzle");
                if (guardPuzzle != null) {
                    return guardPuzzle.isSolved();
                }
            }
            return false;
        }
        if (name.equals("interview")) {
            // Interview room requires both the guard puzzle and security puzzle to be solved
            // Check guard puzzle in queue room
            Room queueRoom = roomManager.getAllRooms().stream()
                .filter(r -> r.getName().equals("queue"))
                .findFirst()
                .orElse(null);
            boolean guardSolved = false;
            if (queueRoom != null) {
                Puzzle guardPuzzle = queueRoom.getPuzzle("queueGaurdPuzzle");
                if (guardPuzzle != null) {
                    guardSolved = guardPuzzle.isSolved();
                }
            }
            
            // Check security puzzle in security room
            Room securityRoom = roomManager.getAllRooms().stream()
                .filter(r -> r.getName().equals("security"))
                .findFirst()
                .orElse(null);
            boolean securitySolved = false;
            if (securityRoom != null) {
                Puzzle securityPuzzle = securityRoom.getPuzzle("securityPuzzle");
                if (securityPuzzle != null) {
                    securitySolved = securityPuzzle.isSolved();
                }
            }
            
            // Both puzzles must be solved
            return guardSolved && securitySolved;
        }
        return true;
    }
    
    /**
     * Gets the message to display when a player cannot enter this room.
     * 
     * @param player The player attempting to enter
     * @param roomManager The room manager to access other rooms
     * @return A message explaining why entry is blocked, or null if entry is allowed
     */
    public String getEntryBlockedMessage(Player player, RoomManager roomManager) {
        if (!canEnter(player, roomManager)) {
            if (name.equals("queue")) {
                return "You need a ticket to enter the queue room.";
            }
            if (name.equals("security")) {
                return "You need to bribe the guard to skip the line first.";
            }
            if (name.equals("interview")) {
                // Check which puzzle is missing
                Room queueRoom = roomManager.getAllRooms().stream()
                    .filter(r -> r.getName().equals("queue"))
                    .findFirst()
                    .orElse(null);
                boolean guardSolved = false;
                if (queueRoom != null) {
                    Puzzle guardPuzzle = queueRoom.getPuzzle("queueGaurdPuzzle");
                    if (guardPuzzle != null) {
                        guardSolved = guardPuzzle.isSolved();
                    }
                }
                
                Room securityRoom = roomManager.getAllRooms().stream()
                    .filter(r -> r.getName().equals("security"))
                    .findFirst()
                    .orElse(null);
                boolean securitySolved = false;
                if (securityRoom != null) {
                    Puzzle securityPuzzle = securityRoom.getPuzzle("securityPuzzle");
                    if (securityPuzzle != null) {
                        securitySolved = securityPuzzle.isSolved();
                    }
                }
                
                if (!guardSolved && !securitySolved) {
                    return "You need to bribe the guard and pass through security first.";
                } else if (!guardSolved) {
                    return "You need to bribe the guard to skip the line first.";
                } else if (!securitySolved) {
                    return "You need to pass through security first.";
                }
                return "You cannot enter this room.";
            }
            return "You cannot enter this room.";
        }
        return null;
    }
}
