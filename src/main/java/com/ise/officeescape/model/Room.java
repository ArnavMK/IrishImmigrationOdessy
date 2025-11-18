package com.ise.officeescape.model;

import java.util.HashMap;
import java.util.Map;

public class Room {
    private String description;
    private String name;
    private Map<Direction, Room> exits; // Map direction to neighboring Room

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        exits = new HashMap<>();
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

    public String getLongDescription() {
        return "You are " + description + ".\nExits: " + getExitString();
    }
}
