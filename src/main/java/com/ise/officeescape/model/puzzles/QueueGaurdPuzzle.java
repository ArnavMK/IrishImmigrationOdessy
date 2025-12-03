package com.ise.officeescape.model.puzzles;

import com.ise.officeescape.model.InteractionResult;
import com.ise.officeescape.model.Player;
import com.ise.officeescape.model.Puzzle;

import java.util.ArrayList;
import java.util.List;

/**
 * Puzzle for talking to the guard in the queue room.
 * Allows player to bribe the guard with a popsicle to skip the line.
 */
public class QueueGaurdPuzzle extends Puzzle {
    
    // Dialogue state
    private int currentNodeIndex = 0;
    private boolean guardBribed = false;
    
    /**
     * Get current node index (for debugging/reset purposes).
     */
    public int getCurrentNodeIndex() {
        return currentNodeIndex;
    }
    
    // Dialogue nodes
    private List<DialogueNode> dialogueNodes;
    
    /**
     * Represents a dialogue node with NPC text and player options.
     */
    public static class DialogueNode {
        public final String npcText;
        public final List<DialogueOption> options;
        public final boolean isTerminal; // Ends conversation
        
        public DialogueNode(String npcText, List<DialogueOption> options, boolean isTerminal) {
            this.npcText = npcText;
            this.options = new ArrayList<>(options);
            this.isTerminal = isTerminal;
        }
    }
    
    /**
     * Represents a dialogue option the player can choose.
     */
    public static class DialogueOption {
        public final String playerText;
        public final String condition; // "always", "hasPopsicle", etc.
        public final int nextNodeIndex;
        public final String resultAction; // "bribe", "end", etc.
        
        public DialogueOption(String playerText, String condition, int nextNodeIndex, String resultAction) {
            this.playerText = playerText;
            this.condition = condition;
            this.nextNodeIndex = nextNodeIndex;
            this.resultAction = resultAction;
        }
    }

    public QueueGaurdPuzzle() {
        super("queueGaurdPuzzle", "Talk your way through the guard");
        initializeDialogue();
    }
    
    /**
     * Initialize the dialogue tree.
     */
    private void initializeDialogue() {
        dialogueNodes = new ArrayList<>();
        
        // Node 0: Initial greeting (MAIN MENU)
        List<DialogueOption> options0 = new ArrayList<>();
        options0.add(new DialogueOption("What's this line for?", "always", 1, "none")); // Branch A
        options0.add(new DialogueOption("Can I skip the line?", "always", 2, "none")); // Branch B
        options0.add(new DialogueOption("Why do you look so sweaty?", "always", 3, "none")); // Branch C
        options0.add(new DialogueOption("Never mind…", "always", 4, "none")); // End
        dialogueNodes.add(new DialogueNode(
            "Hold it. This queue is for the officially patient only.\n\nYou don't look patient… you look agenda-ful. What do you want?",
            options0,
            false
        ));
        
        // Node 1: Branch A - "What's this line for?"
        List<DialogueOption> options1 = new ArrayList<>();
        options1.add(new DialogueOption("That makes no sense.", "always", 5, "none"));
        options1.add(new DialogueOption("Can I get an invitation?", "always", 2, "none")); // Jump to Branch B
        options1.add(new DialogueOption("Okay…", "always", 4, "none")); // End
        dialogueNodes.add(new DialogueNode(
            "This is the line for the line that leads to the real line.\n\nVery exclusive. Invitation only.",
            options1,
            false
        ));
        
        // Node 2: Branch B - "Can I skip the line?" (default response)
        List<DialogueOption> options2 = new ArrayList<>();
        options2.add(new DialogueOption("Please?", "always", 6, "none"));
        options2.add(new DialogueOption("What do you want?", "always", 7, "none"));
        options2.add(new DialogueOption("I have something for you…", "hasPopsicle", 10, "none")); // Popsicle bribe branch
        options2.add(new DialogueOption("Okay, fine.", "always", 4, "none")); // End
        dialogueNodes.add(new DialogueNode(
            "I could get fired for letting you skip the line.\n\nOr worse… I'd get promoted and have to work harder.",
            options2,
            false
        ));
        
        // Node 3: Branch C - "Why do you look so sweaty?"
        List<DialogueOption> options3 = new ArrayList<>();
        options3.add(new DialogueOption("Do you need help?", "always", 8, "none"));
        options3.add(new DialogueOption("That sounds like a skill issue.", "always", 4, "none")); // End
        options3.add(new DialogueOption("Never mind…", "always", 4, "none")); // End
        dialogueNodes.add(new DialogueNode(
            "This uniform is 90% polyester and 10% despair.\n\nIt traps heat AND emotions.",
            options3,
            false
        ));
        
        // Node 4: End - "Never mind" / "Okay"
        dialogueNodes.add(new DialogueNode(
            "",
            new ArrayList<>(),
            true
        ));
        
        // Node 5: "That makes no sense" response
        dialogueNodes.add(new DialogueNode(
            "Sir/Madam/Engineering Student…\n\nNothing in this office makes sense. That's the point.",
            options0, // Return to main menu
            false
        ));
        
        // Node 6: "Please?" response
        dialogueNodes.add(new DialogueNode(
            "Aww.\n\nYou begged so politely.\n\nStill no.",
            options2, // Return to Branch B options
            false
        ));
        
        // Node 7: "What do you want?" response
        List<DialogueOption> options7 = new ArrayList<>();
        options7.add(new DialogueOption("Are you asking for a popsicle?", "always", 9, "none"));
        options7.add(new DialogueOption("I don't have that.", "always", 11, "none")); // No popsicle path
        options7.add(new DialogueOption("I have one!", "hasPopsicle", 10, "none")); // Popsicle bribe branch
        dialogueNodes.add(new DialogueNode(
            "Well… I could maybe turn around for exactly three seconds.\n\nBut only if I receive…\n\nsomething cold…\n\nsomething fruity…\n\nsomething that melts under pressure…\n\nlike…\n\na popsicle.\n\nNot that I'm being specific.",
            options7,
            false
        ));
        
        // Node 8: "Do you need help?" (Branch C)
        dialogueNodes.add(new DialogueNode(
            "Yes.\n\nGive me a popsicle and I'll forget you asked.",
            options0, // Return to main menu
            false
        ));
        
        // Node 9: "Are you asking for a popsicle?"
        dialogueNodes.add(new DialogueNode(
            "No.\n\nI'm legally not allowed to ask for a bribe.\n\nBut if a popsicle fell into my pocket…\n\nI would not complain.",
            options2, // Return to Branch B options
            false
        ));
        
        // Node 10: POPSICLE BRIBE BRANCH - "I have a popsicle" / "I have something for you..."
        List<DialogueOption> options10 = new ArrayList<>();
        options10.add(new DialogueOption("Yes. It's yours.", "always", 12, "bribe")); // Success!
        options10.add(new DialogueOption("No, it's mine.", "always", 13, "none"));
        options10.add(new DialogueOption("Maybe I'm willing to trade…", "always", 14, "none"));
        dialogueNodes.add(new DialogueNode(
            "No… way…\n\nIs that a… Blue Raspberry Ultra Freeze™ popsicle?",
            options10,
            false
        ));
        
        // Node 11: NO POPSICLE PATH - "I don't have that"
        List<DialogueOption> options11 = new ArrayList<>();
        options11.add(new DialogueOption("Where do I get one?", "always", 15, "none"));
        options11.add(new DialogueOption("Okay, sorry.", "always", 4, "none")); // End
        dialogueNodes.add(new DialogueNode(
            "Wait. You don't have one?\n\nThen why are you even TALKING to me?!\n\nYou're wasting government time!",
            options11,
            false
        ));
        
        // Node 12: Bribe accepted - "Yes. It's yours."
        dialogueNodes.add(new DialogueNode(
            "Oh sweet frozen justice…\n\nTake the shortcut.\n\nGo.\n\nBefore I change my mind… or finish this popsicle.",
            new ArrayList<>(),
            true
        ));
        
        // Node 13: "No, it's mine."
        dialogueNodes.add(new DialogueNode(
            "…Why would you SAY that to me?\n\nGo stand at the back.\n\nThink about your choices.",
            new ArrayList<>(),
            true
        ));
        
        // Node 14: "Maybe I'm willing to trade…"
        dialogueNodes.add(new DialogueNode(
            "Buddy.\n\nI have nothing.\n\nI earn 5 euro an hour.\n\nJust give me the popsicle.",
            options10, // Return to bribe options
            false
        ));
        
        // Node 15: "Where do I get one?"
        dialogueNodes.add(new DialogueNode(
            "There's a vending machine in the break room.\n\nIt only takes coins and tears.\n\nMostly tears.",
            new ArrayList<>(),
            true
        ));
    }
    
    /**
     * Get the current dialogue node.
     */
    public DialogueNode getCurrentNode() {
        if (currentNodeIndex >= 0 && currentNodeIndex < dialogueNodes.size()) {
            return dialogueNodes.get(currentNodeIndex);
        }
        return null;
    }
    
    /**
     * Get available options for the current node, filtered by conditions.
     */
    public List<DialogueOption> getAvailableOptions(Player player) {
        DialogueNode node = getCurrentNode();
        if (node == null) {
            return new ArrayList<>();
        }
        
        List<DialogueOption> available = new ArrayList<>();
        for (DialogueOption option : node.options) {
            if (checkCondition(option.condition, player)) {
                available.add(option);
            }
        }
        return available;
    }
    
    /**
     * Check if a condition is met.
     */
    private boolean checkCondition(String condition, Player player) {
        if (condition == null || condition.equals("always")) {
            return true;
        }
        if (condition.equals("hasPopsicle")) {
            return player != null && player.getInventory().hasItem("popsicle");
        }
        return false;
    }
    
    /**
     * Check if current node is terminal (ends conversation).
     */
    public boolean isCurrentNodeTerminal() {
        DialogueNode node = getCurrentNode();
        return node != null && node.isTerminal;
    }

    @Override
    public InteractionResult interact(String action, Object context) {
        if (action.equals("selectOption")) {
            // Handle option selection
            if (context instanceof Integer) {
                int optionIndex = (Integer) context;
                return handleOptionSelection(optionIndex);
            }
        }
        
        if (action.equals("next")) {
            // Move to next node (for non-terminal nodes)
            if (!isCurrentNodeTerminal()) {
                // This would be used if we want to auto-advance
                return InteractionResult.message("Select an option");
            }
        }
        
        if (action.equals("close")) {
            // Close dialogue
            return InteractionResult.message("Conversation ended");
        }
        
        return InteractionResult.message("Talk to the guard.");
    }
    
    /**
     * Handle selection of a dialogue option.
     */
    private InteractionResult handleOptionSelection(int optionIndex) {
        DialogueNode currentNode = getCurrentNode();
        if (currentNode == null || optionIndex < 0 || optionIndex >= currentNode.options.size()) {
            return InteractionResult.message("Invalid option");
        }
        
        DialogueOption selectedOption = currentNode.options.get(optionIndex);
        
        // Process result action
        if (selectedOption.resultAction.equals("bribe")) {
            guardBribed = true;
            solved = true;
            // Return result that will remove popsicle and allow player to proceed
            return InteractionResult.puzzleSolved("queueGaurdPuzzle")
                .addDialogue("The guard accepts your bribe!")
                .addViewUpdate("enableHotspot:nextRoom"); // Could enable next room or move player
        }
        
        // Move to next node
        if (selectedOption.nextNodeIndex >= 0 && selectedOption.nextNodeIndex < dialogueNodes.size()) {
            currentNodeIndex = selectedOption.nextNodeIndex;
            DialogueNode nextNode = getCurrentNode();
            if (nextNode != null && nextNode.isTerminal) {
                // Terminal node - conversation ends
                return InteractionResult.message(nextNode.npcText);
            }
            return InteractionResult.message("continue:" + nextNode.npcText);
        }
        
        return InteractionResult.message("Conversation continues...");
    }
    
    /**
     * Reset dialogue to start.
     * Resets the dialogue position so the conversation can be replayed.
     * This is called when the puzzle view is opened.
     */
    public void resetDialogue() {
        currentNodeIndex = 0;
        // Note: We don't reset guardBribed or solved here
        // If the puzzle was already solved, it stays solved
        // But the dialogue can be replayed from the beginning
    }
    
    public boolean isGuardBribed() {
        return guardBribed;
    }

    @Override
    public boolean canStart(Player player) {
        // Guard puzzle can always be started (no prerequisites)
        return true;
    }
}
