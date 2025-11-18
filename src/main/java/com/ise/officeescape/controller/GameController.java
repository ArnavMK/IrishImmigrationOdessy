package com.ise.officeescape.controller;

import com.ise.officeescape.model.GameManager;
import com.ise.officeescape.model.Room;
import com.ise.officeescape.view.GameView;

/**
 * Controller that coordinates between the game model and view.
 * Updates the view when the room changes.
 */
public class GameController {
    private final GameManager game;
    private final GameView view;

    public GameController(GameManager game, GameView view) {
        this.game = game;
        this.view = view;

        initializeView();
    }

    /**
     * Initializes the view with the current room's background image.
     * Called once when the game starts.
     */
    private void initializeView() {
        updateRoomView();
    }
    
    /**
     * Updates the view to reflect the current room.
     * Call this whenever the room changes.
     */
    public void updateRoomView() {
        Room currentRoom = game.getCurrentRoom();
        if (currentRoom != null) {
            view.updateRoomBackground(currentRoom);
        }
    }
}

