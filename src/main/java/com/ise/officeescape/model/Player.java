package com.ise.officeescape.model;

/**
 * Player
 */
public class Player {
    
    private String name;
    private Room currentRoom;

    public Player(String name) {
        this.name = name;
        this.currentRoom = RoomManager.instance.getStartRoom();
    }
    
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }
    
    public String getName() {
        return name;
    }
}

