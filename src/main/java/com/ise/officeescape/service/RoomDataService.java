package com.ise.officeescape.service;

import com.ise.officeescape.model.*;
import com.ise.officeescape.model.puzzles.QueuePuzzle;
import com.ise.officeescape.view.HotspotViewModel;
import com.ise.officeescape.view.RoomViewModel;

/**
 * Service for loading room data and creating room view models.
 * This is where room definitions (hotspots, puzzles, animations) are configured.
 */
public class RoomDataService {

    /**
     * Loads a room with all its interactables, puzzles, and view data.
     */
    public RoomViewModel loadRoom(Room room) {
        String roomId = room.getName();
        System.out.println("[RoomDataService] Loading room: " + roomId);
        RoomViewModel viewModel = new RoomViewModel(room, getBackgroundPath(roomId));

        // Configure room-specific data
        switch (roomId) {
            case "ticket":
                System.out.println("[RoomDataService] Setting up ticket room with puzzles and interactables");
                setupTicketRoom(room, viewModel);
                break;
            default:
                System.out.println("[RoomDataService] No special setup for room: " + roomId);
            // Add other rooms here as needed
        }

        System.out.println("[RoomDataService] Room loaded with " + viewModel.getHotspots().size() + " hotspots");
        return viewModel;
    }

    private String getBackgroundPath(String roomId) {
        return "/com/ise/officeescape/assets/" + roomId + ".png";
    }

    /**
     * Sets up the queue room with puzzles and interactables.
     */
    private void setupTicketRoom(Room room, RoomViewModel viewModel) {

    }
}

