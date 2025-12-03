package com.ise.officeescape.controller;

import com.ise.officeescape.eventSystem.*;
import com.ise.officeescape.model.Direction;
import com.ise.officeescape.model.InteractionResult;
import com.ise.officeescape.model.Player;
import com.ise.officeescape.model.Puzzle;
import com.ise.officeescape.model.Room;
import com.ise.officeescape.model.RoomManager;
import com.ise.officeescape.service.RoomDataService;
import com.ise.officeescape.view.DirectionControllerView.OnDirectionButtonPressedArgs;
import com.ise.officeescape.view.GameView;
import com.ise.officeescape.view.GameView.OnHotspotClickedEventArgs;
import com.ise.officeescape.view.PuzzleView;
import com.ise.officeescape.view.RoomViewModel;

public class GameController {

    public static GameController instance; 

    private final GameView view;
    private final Player player;
    private final RoomManager roomManager;
    private final RoomDataService roomDataService;

    public Event<OnRoomChangedEventArgs> onRoomChanged;
    public class OnRoomChangedEventArgs extends EventArgs {
        public Room newRoom;
        public OnRoomChangedEventArgs(Room newRoom) {
            this.newRoom = newRoom;
        }
    }

    public GameController(GameView view) {
        this.view = view;
        this.roomManager = new RoomManager();
        this.roomDataService = new RoomDataService();
        this.player = new Player("Player", roomManager.getStartRoom());
        GameController.instance = this;

        initializeView();
        setupEventSubscriptions();
        
        // Set up inventory change callback to refresh inventory view when items are moved
        view.setInventoryChangeCallback(() -> {
            view.updateInventory(player.getInventory(), getCurrentRoom().getInventory());
        });
    }

    /**
     * Initializes the view with the current room.
     * Called once when the game starts.
     */
    private void initializeView() {
        System.out.println("[GameController] initializeView() called");
        System.out.println("[GameController] Starting room: " + getCurrentRoom().getName());
        loadAndShowRoom(getCurrentRoom());
    }

    /**
     * Sets up event listeners for UI components.
     */
    private void setupEventSubscriptions() {
        // Direction button events
        view.getDirectionControllerView().OnDirectionButtonPressed.addListener(
            (sender, args) -> onDirectionButtonPressed(sender, args)
        );
        
        // Hotspot click events
        view.OnHotspotClicked.addListener(
            (sender, args) -> onHotspotClicked(sender, args)
        );
    }

    /**
     * Handles direction button press events.
     * Moves the player and loads the new room.
     */
    private void onDirectionButtonPressed(Object sender, OnDirectionButtonPressedArgs args) {
        String result = movePlayer(args.direction);
        System.out.println(result);
    }

    /**
     * Handles hotspot click events.
     * Processes interactions with room elements.
     */
    private void onHotspotClicked(Object sender, OnHotspotClickedEventArgs args) {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null) {
            return;
        }

        // Interact with the hotspot
        InteractionResult result = currentRoom.interact(args.hotspotId, "click");
        
        // Single print statement for interaction
        System.out.println("Interaction: " + args.hotspotId + " -> " + result.getType());
        
        // Process the result
        handleInteractionResult(result);
    }

    /**
     * Processes the result of an interaction.
     */
    private void handleInteractionResult(InteractionResult result) {
        switch (result.getType()) {
            case MESSAGE:
                if (result.getMessage() != null) {
                    System.out.println(result.getMessage());
                }
                break;
                
            case PUZZLE_SOLVED:
                view.hidePuzzleView();
                view.applyViewUpdates(result.getViewUpdates());
                
                // Handle guard puzzle - remove popsicle when bribed
                if (result.getPuzzleId() != null && result.getPuzzleId().equals("queueGaurdPuzzle")) {
                    if (player.getInventory().hasItem("popsicle")) {
                        player.getInventory().removeItem("popsicle");
                        System.out.println("You gave the popsicle to the guard.");
                        view.updateInventory(player.getInventory(), getCurrentRoom().getInventory());
                    }
                }
                
                if (result.getItem() != null) {
                    player.getInventory().addItem(result.getItem());
                    System.out.println("Item obtained: " + result.getItem().getName());
                    // Update inventory view if it's visible
                    view.updateInventory(player.getInventory(), getCurrentRoom().getInventory());
                }
                break;
                
            case PUZZLE_TRIGGERED:
                // Show puzzle view
                showPuzzleView(result.getPuzzleId());
                break;
                
            case ITEM_OBTAINED:
                // Add item to player inventory
                if (result.getItem() != null) {
                    player.getInventory().addItem(result.getItem());
                    System.out.println("Item obtained: " + result.getItem().getName());
                    // Update inventory view if it's visible
                    view.updateInventory(player.getInventory(), getCurrentRoom().getInventory());
                } else if (result.getItemId() != null) {
                    System.out.println("Item obtained: " + result.getItemId() + " (item object not provided)");
                }
                break;
                
            case DOOR_UNLOCKED:
                System.out.println("Door unlocked to: " + result.getNextRoomId());
                // Could automatically move player
                break;
                
            case DIALOGUE:
                for (String line : result.getDialogueLines()) {
                    System.out.println(line);
                }
                break;
                
            case NONE:
                // No action needed
                break;
        }
        
        // Apply any view updates
        if (!result.getViewUpdates().isEmpty()) {
            view.applyViewUpdates(result.getViewUpdates());
        }
    }

    /**
     * Gets the current room the player is in.
     */
    public Room getCurrentRoom() {
        return player.getCurrentRoom();
    }

    /**
     * Attempts to move the player in the specified direction.
     * Returns a message describing the result.
     */
    public String movePlayer(Direction direction) {
        System.out.println("[GameController] movePlayer() called, direction: " + direction);
        if (player.move(direction)) {
            System.out.println("[GameController] Player moved successfully to: " + getCurrentRoom().getName());
            loadAndShowRoom(getCurrentRoom());
            if (onRoomChanged != null) {
                onRoomChanged.invoke(this, new OnRoomChangedEventArgs(getCurrentRoom()));
            }
            return "You move " + direction + ".";
        } else {
            System.out.println("[GameController] Player cannot move " + direction);
            return "You cannot go " + direction + " from here.";
        }
    }

    /**
     * Loads room data and shows it in the view.
     * This is the main method for room transitions.
     */
    private void loadAndShowRoom(Room room) {
        if (room == null) {
            System.out.println("[GameController] WARNING: loadAndShowRoom called with null room");
            return;
        }
        
        System.out.println("[GameController] loadAndShowRoom() called for room: " + room.getName());
        
        // Load room data (hotspots, puzzles, animations)
        RoomViewModel viewModel = roomDataService.loadRoom(room);
        System.out.println("[GameController] Room data loaded, showing in view");
        
        // Show the room in the view
        view.showRoom(viewModel);
        System.out.println("[GameController] Room display initiated");
        
        // Update inventory view with current inventories
        view.updateInventory(player.getInventory(), room.getInventory());
    }

    /**
     * Updates the view to reflect the current room (simple version, for backwards compatibility).
     */
    public void updateRoomView() {
        loadAndShowRoom(getCurrentRoom());
    }

    // Getters for accessing game state if needed
    public Player getPlayer() {
        return player;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }
    
    /**
     * Shows a puzzle view for the given puzzle ID.
     */
    private void showPuzzleView(String puzzleId) {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null) {
            return;
        }
        
        Puzzle puzzle = currentRoom.getPuzzle(puzzleId);
        if (puzzle == null) {
            return;
        }
        
        // Check if puzzle can be started
        if (!puzzle.canStart(player)) {
            // Puzzle cannot be started - show message to user
            System.out.println("You cannot start this puzzle right now.");
            // Could show a message dialog here if you have UI for that
            return;
        }
        
        // Create puzzle-specific view based on puzzle ID
        PuzzleView puzzleView;
        if (puzzleId.equals("ticketPuzzle")) {
            puzzleView = new com.ise.officeescape.view.puzzles.TicketMachinePuzzleView(puzzle);
        } else if (puzzleId.equals("queueGaurdPuzzle")) {
            puzzleView = new com.ise.officeescape.view.puzzles.GuardPuzzleView(puzzle);
        } else {
            // No view implementation for this puzzle type
            System.err.println("No puzzle view implementation for puzzle: " + puzzleId);
            return;
        }
        
        // Set up puzzle event handlers
        puzzle.OnPuzzleSolved.addListener((sender, args) -> {
            // Use the result that was already computed in the puzzle
            handleInteractionResult(args.result);
        });
        
        puzzle.OnPuzzleClosed.addListener((sender, args) -> {
            view.hidePuzzleView();
        });
        
        view.showPuzzleView(puzzleView);
    }
}


