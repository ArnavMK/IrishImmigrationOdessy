package com.ise.officeescape.service;

import com.ise.officeescape.model.*;
import com.ise.officeescape.model.puzzles.TicketPuzzle;
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
     * Sets up the ticket room with puzzles and hotspots.
     * Hotspots are the single source of truth - Interactables are created automatically.
     */
    private void setupTicketRoom(Room room, RoomViewModel viewModel) {
        System.out.println("[RoomDataService] Setting up ticket room with hotspots and puzzles");

        // Add puzzle first (needed for interactions)
        Puzzle ticketPuzzle = new TicketPuzzle();
        room.addPuzzle(ticketPuzzle);
        System.out.println("[RoomDataService] Added puzzle: ticketPuzzle");

        // Add some items to room inventory
        room.getInventory().addItem(new Item("bananaPeel", "A slippery banana peel, watch your step!"));
        room.getInventory().addItem(new Item("popsicle", "A refreshing popsicle, perfect for a hot day"));
        System.out.println("[RoomDataService] Added items to room inventory");

        // Define hotspots - Interactables are created automatically!
        // Just use this one method and it handles both view and model
        addInteractables(room, viewModel,
            "ticketMachine",
            600, 400,  // x, y position on screen
            150, 200,  // width, height
            "Ticket Machine - Click to get your number",
            "TAKE_TICKET"
        );
    }

    /**
     * Automatically creates an Interactable model object from a HotspotViewModel.
     * This keeps hotspots as the single source of truth.
     */
    private void createInteractableFromHotspot(Room room, HotspotViewModel hotspot) {
        Interactable interactable = new Interactable(hotspot.getId(), hotspot.getInteractionType());
        interactable.setDescription(hotspot.getHoverText());
        room.addInteractable(interactable);
        System.out.println("[RoomDataService] Auto-created Interactable from hotspot: " + hotspot.getId());
    }

    /**
     * Convenience method: Creates a hotspot and automatically creates its Interactable.
     * This is the recommended way to add interactive elements to rooms.
     * 
     * @param room The room to add the interactable to
     * @param viewModel The view model to add the hotspot to
     * @param id Unique identifier
     * @param x X position on screen
     * @param y Y position on screen
     * @param width Width of clickable area
     * @param height Height of clickable area
     * @param hoverText Text shown on hover
     * @param interactionType Type of interaction (e.g., "TAKE_TICKET", "OPEN_DOOR")
     */
    private void addInteractables(Room room, RoomViewModel viewModel, 
        String id, double x, double y, double width, double height, 
        String hoverText, String interactionType) {
        HotspotViewModel hotspot = new HotspotViewModel(id, x, y, width, height, hoverText, interactionType);
        viewModel.addHotspot(hotspot);
        createInteractableFromHotspot(room, hotspot);
        System.out.println("[RoomDataService] Added hotspot and interactable: " + id + " at (" + x + ", " + y + ")");
    }
}

