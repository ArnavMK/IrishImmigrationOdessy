package com.ise.officeescape.model;

/**
 * Represents an interactive element in a room (clickable item, NPC, door, etc.)
 */
public class Interactable {
    private String id;
    private String interactionType;
    private boolean enabled;
    private String description;

    public Interactable(String id, String interactionType) {
        this.id = id;
        this.interactionType = interactionType;
        this.enabled = true;
    }

    public String getId() {
        return id;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

