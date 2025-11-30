package com.ise.officeescape.controller;

import com.ise.officeescape.eventSystem.*;
import com.ise.officeescape.model.Direction;
import com.ise.officeescape.model.InteractionResult;
import com.ise.officeescape.model.Player;
import com.ise.officeescape.model.Room;
import com.ise.officeescape.model.RoomManager;
import com.ise.officeescape.service.RoomDataService;
import com.ise.officeescape.view.DirectionControllerView.OnDirectionButtonPressedArgs;
import com.ise.officeescape.view.GameView;
import com.ise.officeescape.view.GameView.OnHotspotClickedEventArgs;
import com.ise.officeescape.view.RoomViewModel;

public class GameController {
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

        initializeView();
        setupEventSubscriptions();
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
        System.out.println("[GameController] onHotspotClicked() called for hotspot: " + args.hotspotId);
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null) {
            System.out.println("[GameController] ERROR: Current room is null");
            return;
        }

        System.out.println("[GameController] Interacting with hotspot in room: " + currentRoom.getName());
        // Interact with the hotspot
        InteractionResult result = currentRoom.interact(args.hotspotId, "click");
        System.out.println("[GameController] Interaction result type: " + result.getType());
        
        // Process the result
        handleInteractionResult(result);
    }

    /**
     * Processes the result of an interaction.
     */
    private void handleInteractionResult(InteractionResult result) {
        System.out.println("[GameController] handleInteractionResult() called, type: " + result.getType());
        
        switch (result.getType()) {
            case MESSAGE:
                System.out.println("[GameController] MESSAGE: " + result.getMessage());
                break;
                
            case PUZZLE_SOLVED:
                System.out.println("[GameController] PUZZLE_SOLVED: " + result.getPuzzleId());
                // Apply view updates (animations, enable/disable hotspots)
                view.applyViewUpdates(result.getViewUpdates());
                break;
                
            case PUZZLE_TRIGGERED:
                System.out.println("[GameController] PUZZLE_TRIGGERED: " + result.getPuzzleId());
                break;
                
            case ITEM_OBTAINED:
                System.out.println("[GameController] ITEM_OBTAINED: " + result.getItemId());
                break;
                
            case DOOR_UNLOCKED:
                System.out.println("[GameController] DOOR_UNLOCKED to: " + result.getNextRoomId());
                // Could automatically move player
                break;
                
            case DIALOGUE:
                System.out.println("[GameController] DIALOGUE:");
                for (String line : result.getDialogueLines()) {
                    System.out.println("  - " + line);
                }
                break;
                
            case NONE:
                System.out.println("[GameController] NONE - No action needed");
                break;
        }
        
        // Apply any view updates
        if (!result.getViewUpdates().isEmpty()) {
            System.out.println("[GameController] Applying " + result.getViewUpdates().size() + " view updates");
            view.applyViewUpdates(result.getViewUpdates());
        } else {
            System.out.println("[GameController] No view updates to apply");
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
}


