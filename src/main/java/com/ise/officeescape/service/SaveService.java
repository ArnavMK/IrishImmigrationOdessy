package com.ise.officeescape.service;

import com.ise.officeescape.model.Inventory;
import com.ise.officeescape.model.Item;
import com.ise.officeescape.model.Room;
import com.ise.officeescape.model.RoomManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for saving and loading game state.
 * Saves to a simple text file in the user's home directory to ensure it works from JAR files.
 */
public class SaveService {
    
    private static final String SAVE_FILE_NAME = "savegame.txt";
    private static final String SAVE_DIR_NAME = ".irishImmigrationOdyssey";
    
    /**
     * Gets the path to the save file.
     * Uses user's home directory to ensure it works when running from a JAR file.
     */
    private String getSaveFilePath() {
        String userHome = System.getProperty("user.home");
        String saveDir = userHome + java.io.File.separator + SAVE_DIR_NAME;
        java.io.File dir = new java.io.File(saveDir);
        if (!dir.exists()) {
            dir.mkdirs(); // Create directory if it doesn't exist
        }
        return saveDir + java.io.File.separator + SAVE_FILE_NAME;
    }
    
    /**
     * Saves the current game state to a file.
     * Format:
     * Line 1: room name
     * Line 2+: item names (one per line)
     * 
     * @param currentRoom The room the player is currently in
     * @param playerInventory The player's inventory
     * @return true if save was successful, false otherwise
     */
    public boolean saveGame(Room currentRoom, Inventory playerInventory) {
        String savePath = getSaveFilePath();
        try (PrintWriter writer = new PrintWriter(new FileWriter(savePath))) {
            // Write room name
            writer.println(currentRoom.getName());
            
            // Write inventory items (one per line)
            if (playerInventory != null) {
                Map<String, Item> items = playerInventory.getInvetoryMap();
                for (Item item : items.values()) {
                    writer.println(item.getName());
                }
            }
            
            System.out.println("[SaveService] Game saved successfully to " + savePath);
            return true;
        } catch (IOException e) {
            System.err.println("[SaveService] Error saving game: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Loads the game state from a file.
     * 
     * @param roomManager The room manager to get rooms by name
     * @return SaveData containing the saved room and inventory items, or null if load failed
     */
    public SaveData loadGame(RoomManager roomManager) {
        String savePath = getSaveFilePath();
        File saveFile = new File(savePath);
        if (!saveFile.exists()) {
            System.out.println("[SaveService] No save file found, starting new game");
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(saveFile))) {
            // Read room name (first line)
            String roomName = reader.readLine();
            if (roomName == null || roomName.trim().isEmpty()) {
                System.err.println("[SaveService] Save file is empty or invalid");
                return null;
            }
            roomName = roomName.trim();
            
            // Find the room
            Room savedRoom = null;
            for (Room room : roomManager.getAllRooms()) {
                if (room.getName().equals(roomName)) {
                    savedRoom = room;
                    break;
                }
            }
            
            if (savedRoom == null) {
                System.err.println("[SaveService] Saved room not found: " + roomName);
                return null;
            }
            
            // Read inventory items (remaining lines)
            List<String> itemNames = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    itemNames.add(line);
                }
            }
            
            System.out.println("[SaveService] Game loaded successfully from " + savePath);
            System.out.println("[SaveService] Room: " + roomName + ", Items: " + itemNames.size());
            
            return new SaveData(savedRoom, itemNames);
        } catch (IOException e) {
            System.err.println("[SaveService] Error loading game: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Checks if a save file exists.
     */
    public boolean saveExists() {
        return new File(getSaveFilePath()).exists();
    }
    
    /**
     * Deletes the save file (for starting a new game).
     */
    public boolean deleteSave() {
        File saveFile = new File(getSaveFilePath());
        if (saveFile.exists()) {
            return saveFile.delete();
        }
        return true;
    }
    
    /**
     * Data class for loaded save data.
     */
    public static class SaveData {
        public final Room room;
        public final List<String> itemNames;
        
        public SaveData(Room room, List<String> itemNames) {
            this.room = room;
            this.itemNames = itemNames;
        }
    }
    
    /**
     * Creates an Item from its name.
     * Uses default descriptions based on item names.
     */
    public static Item createItemFromName(String itemName) {
        // Map item names to their descriptions
        switch (itemName) {
            case "passport":
                return new Item("passport", "Your passport");
            case "visaApplication":
                return new Item("visaApplication", "Visa application form");
            case "stolenDocument":
                return new Item("stolenDocument", "A stolen document - someone dropped it after slipping on the banana peel");
            case "birthCertificate":
                return new Item("birthCertificate", "Birth certificate");
            case "employmentLetter":
                return new Item("employmentLetter", "Employment letter");
            case "ticketItem":
                return new Item("ticketItem", "This is the ticket required for the queue");
            case "bananaPeel":
                return new Item("bananaPeel", "A slippery banana peel, watch your step!");
            case "popsicle":
                return new Item("popsicle", "A refreshing popsicle, perfect for a hot day");
            default:
                // Generic description for unknown items
                return new Item(itemName, "A " + itemName);
        }
    }
}

