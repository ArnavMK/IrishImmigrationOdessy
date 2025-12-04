package com.ise.officeescape.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the room graph/structure of the game.
 * The current room is tracked by the Player, not here.
 * This class is responsible for creating and linking rooms.
 */
public class RoomManager {
    private List<Room> rooms;
    private Room startRoom;

    public RoomManager() {
        initialiseRooms();
    }

    private void initialiseRooms() {
        rooms = new ArrayList<>();

        Room outside = new Room("outside", "Outside the Irish Immigration Office");
        Room queue = new Room("queue", "The Queue Room - A test of patience");
        Room security = new Room("security", "Security Check - Metal detector and suspicious stares");
        Room ticket = new Room("ticket", "Ticket Machine Room - Take a number and cry");
        Room interview = new Room("interview", "Interview Room - The final bureaucratic boss fight");
        Room exit = new Room("exit", "Exit - Freedom! You escaped the office.");

        // Set up room connections
        // Outside
        outside.setExit(Direction.forward, ticket);

        // Queue Room
        queue.setExit(Direction.backwards, ticket);
        queue.setExit(Direction.forward, security);

        // Security Room
        security.setExit(Direction.backwards, queue);
        security.setExit(Direction.forward, interview);

        // Ticket Room
        ticket.setExit(Direction.backwards, outside);
        ticket.setExit(Direction.forward, queue);

        // Interview Room
        interview.setExit(Direction.backwards, security);
        interview.setExit(Direction.forward, exit);

        // Exit Room
        exit.setExit(Direction.backwards, interview);

        // Store all rooms for reference
        rooms.add(outside);
        rooms.add(ticket);
        rooms.add(queue);
        rooms.add(security);
        rooms.add(interview);
        rooms.add(exit);

        startRoom = outside;
    }

    /**
     * Gets the starting room where the player begins the game.
     */
    public Room getStartRoom() {
        return startRoom;
    }

    /**
     * Gets all rooms in the game (for reference/debugging).
     */
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms); // Return a copy to prevent external modification
    }

    /**
     * Gets the total number of rooms in the game.
     */
    public int getRoomCount() {
        return rooms.size();
    }
}

