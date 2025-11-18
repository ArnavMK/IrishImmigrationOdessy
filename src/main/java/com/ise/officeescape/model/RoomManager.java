package com.ise.officeescape.model;

import java.util.ArrayList;

public class RoomManager {
    public static RoomManager instance;

    private ArrayList<Room> rooms;
    private Room startRoom;
    private int currentRoomIndex = 0;

    public RoomManager() {
        instance = this;
        initialiseRooms();
    }

    private void initialiseRooms() {
        rooms = new ArrayList<>();

        Room outside = new Room("outside", "Outside the Irish Immigration Office");
        Room queue = new Room("queue","The Queue Room - A test of patience");
        Room security = new Room("security", "Security Check - Metal detector and suspicious stares");
        Room ticket = new Room("ticket", "Ticket Machine Room - Take a number and cry");
        Room documents = new Room("documents", "Document Verification Room - Missing photocopy simulator");
        Room interview = new Room("interview", "Interview Room - The final bureaucratic boss fight");
        Room approval = new Room("approval", "Approval Room - The land of the sacred green stamp");
        Room exit = new Room("exit", "Exit - Freedom! You escaped the office.");

        // Outside
        outside.setExit(Direction.forward, queue);

        // Queue Room
        queue.setExit(Direction.backwards, outside);
        queue.setExit(Direction.forward, security);

        // Security Room
        security.setExit(Direction.backwards, queue);
        security.setExit(Direction.forward, ticket);

        // Ticket Room
        ticket.setExit(Direction.backwards, security);
        ticket.setExit(Direction.forward, documents);

        // Document Room
        documents.setExit(Direction.backwards, ticket);
        documents.setExit(Direction.forward, interview);

        // Interview Room
        interview.setExit(Direction.backwards, documents);
        interview.setExit(Direction.forward, approval);

        // Approval Room
        approval.setExit(Direction.backwards, interview);
        approval.setExit(Direction.forward, exit);

        // Exit Room
        exit.setExit(Direction.backwards, approval);

        // Add all rooms to list (ordered)
        rooms.add(outside);
        rooms.add(queue);
        rooms.add(security);
        rooms.add(ticket);
        rooms.add(documents);
        rooms.add(interview);
        rooms.add(approval);
        rooms.add(exit);

        startRoom = outside;
    }

    public Room getStartRoom() {
        return startRoom;
    }

    public Room getNextRoom() {
        currentRoomIndex ++;
        return rooms.get(currentRoomIndex);
    }

    public int getRoomCount() {
        return rooms.size();
    }
}

