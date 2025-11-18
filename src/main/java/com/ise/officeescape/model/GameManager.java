package com.ise.officeescape.model;

public class GameManager {
    private Player player;
    private RoomManager roomManager;

    public GameManager() {
        this.roomManager = new RoomManager();
        this.player = new Player("Player");
    }

    /**
     * Gets the current room the player is in.
     * Used by the view to display the correct background image.
     */
    public Room getCurrentRoom() {
        return player.getCurrentRoom();
    }

    public String handleCommand(String cmd) {
        // TODO: Parse command and update game state
        // For now, just return a message
        return "Command executed: " + cmd;
    }
    
    public Player getPlayer() {
        return player;
    }
}

