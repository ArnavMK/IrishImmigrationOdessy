package com.ise.officeescape.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Result of an interaction with a room element.
 * Contains information about what happened and what the view should update.
 */
public class InteractionResult {
    public enum ResultType {
        MESSAGE,
        PUZZLE_TRIGGERED,
        PUZZLE_SOLVED,
        ITEM_OBTAINED,
        NONE
    }

    private ResultType type;
    private String message;
    private String puzzleId;
    private String itemId;
    private Item item; // The actual item object (when ITEM_OBTAINED)
    private String nextRoomId;
    private List<String> dialogueLines;

    public InteractionResult(ResultType type) {
        this.type = type;
        this.dialogueLines = new ArrayList<>();
    }

    public static InteractionResult message(String message) {
        InteractionResult result = new InteractionResult(ResultType.MESSAGE);
        result.message = message;
        return result;
    }

    public static InteractionResult puzzleTriggered(String puzzleId) {
        InteractionResult result = new InteractionResult(ResultType.PUZZLE_TRIGGERED);
        result.puzzleId = puzzleId;
        return result;
    }

    public static InteractionResult puzzleSolved(String puzzleId) {
        InteractionResult result = new InteractionResult(ResultType.PUZZLE_SOLVED);
        result.puzzleId = puzzleId;
        return result;
    }

    public static InteractionResult itemObtained(String itemId) {
        InteractionResult result = new InteractionResult(ResultType.ITEM_OBTAINED);
        result.itemId = itemId;
        return result;
    }
    
    public static InteractionResult itemObtained(Item item) {
        InteractionResult result = new InteractionResult(ResultType.ITEM_OBTAINED);
        result.item = item;
        result.itemId = item != null ? item.getName() : null;
        return result;
    }

    public static InteractionResult none() {
        return new InteractionResult(ResultType.NONE);
    }

    // Getters
    public ResultType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String getPuzzleId() {
        return puzzleId;
    }

    public String getItemId() {
        return itemId;
    }
    
    public Item getItem() {
        return item;
    }

    public String getNextRoomId() {
        return nextRoomId;
    }

    public List<String> getDialogueLines() {
        return dialogueLines;
    }

    public InteractionResult addDialogue(String line) {
        this.dialogueLines.add(line);
        return this;
    }
    
    public InteractionResult addItem(Item item) {
        this.item = item;
        this.itemId = item != null ? item.getName() : null;
        // Change type to ITEM_OBTAINED if not already set
        if (this.type != ResultType.ITEM_OBTAINED && this.type != ResultType.PUZZLE_SOLVED) {
            this.type = ResultType.ITEM_OBTAINED;
        }
        return this;
    }
    
    /**
     * Sets an item on this result (for use when combining PUZZLE_SOLVED with ITEM_OBTAINED).
     */
    public InteractionResult setItem(Item item) {
        this.item = item;
        this.itemId = item != null ? item.getName() : null;
        return this;
    }
}

