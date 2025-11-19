package com.ise.officeescape.controller;

import com.ise.officeescape.eventSystem.*;
import com.ise.officeescape.model.Direction;
import com.ise.officeescape.model.Player;
import com.ise.officeescape.model.Room;
import com.ise.officeescape.model.RoomManager;
import com.ise.officeescape.view.DirectionControllerView.OnDirectionButtonPressedArgs;
import com.ise.officeescape.view.GameView;

public class GameController {
    private final GameView view;
    private final Player player;
    private final RoomManager roomManager;

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
        this.player = new Player("Player", roomManager.getStartRoom());

        initializeView();
        setupEventSubscriptions();
    }

    /**
     * Initializes the view with the current room's background image.
     * Called once when the game starts.
     */
    private void initializeView() {
        updateRoomView();
    }

    /**
     * Sets up event listeners for UI components.
     */
    private void setupEventSubscriptions() {
        view.getDirectionControllerView().OnDirectionButtonPressed.addListener(
            (sender, args) -> onDirectionButtonPressed(sender, args)
        );
    }

    /**
     * Handles direction button press events.
     * Moves the player and updates the view.
     */
    private void onDirectionButtonPressed(Object sender, OnDirectionButtonPressedArgs args) {
        String result = movePlayer(args.direction);
        System.out.println(result);
    }

    /**
     * Gets the current room the player is in.
     * Used by the view to display the correct background image.
     */
    public Room getCurrentRoom() {
        return player.getCurrentRoom();
    }

    /**
     * Attempts to move the player in the specified direction.
     * Returns a message describing the result.
     */
    public String movePlayer(Direction direction) {
        if (player.move(direction)) {
            updateRoomView();
            onRoomChanged.invoke(this, new OnRoomChangedEventArgs(getCurrentRoom()));
            return "You move " + direction + ".";
        } else {
            return "You cannot go " + direction + " from here.";
        }
    }

    /**
     * Updates the view to reflect the current room.
     * Call this whenever the room changes.
     */
    public void updateRoomView() {
        Room currentRoom = getCurrentRoom();
        if (currentRoom != null) {
            view.updateRoomBackground(currentRoom);
        }
    }

    // Getters for accessing game state if needed
    public Player getPlayer() {
        return player;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }
}


