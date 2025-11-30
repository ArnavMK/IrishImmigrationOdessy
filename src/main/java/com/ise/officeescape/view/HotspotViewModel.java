package com.ise.officeescape.view;

/**
 * View model for a clickable hotspot in a room.
 * Contains the data needed to render and interact with a hotspot.
 */
public class HotspotViewModel {
    private String id;
    private double x;
    private double y;
    private double width;
    private double height;
    private String hoverText;
    private String interactionType;
    private boolean enabled;

    public HotspotViewModel(String id, double x, double y, double width, double height, String hoverText, String interactionType) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hoverText = hoverText;
        this.interactionType = interactionType;
        this.enabled = true;
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public String getHoverText() {
        return hoverText;
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
}

