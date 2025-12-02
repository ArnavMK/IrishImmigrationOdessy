package com.ise.officeescape.model;

import java.security.KeyStore.PrivateKeyEntry;

/**
 * Represents the player in the game.
 * The player tracks their current location (room).
 */
public class Player {
    
    private String name;
    private Room currentRoom;
    private Inventory inventory;

    public Player(String name, Room startRoom) {
        this.name = name;
        this.currentRoom = startRoom;
        this.inventory = new Inventory();
    }
    
    public Room getCurrentRoom() {
        return currentRoom;
    }
    
    public void setCurrentRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Cannot move to null room");
        }
        this.currentRoom = room;
    }
    
    public boolean move(Direction direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            setCurrentRoom(nextRoom);
            return true;
        }
        return false;
    }
    
    public String getName() {
        return name;
    }

    public Inventory getInventory() {
        return inventory;
    }
}

