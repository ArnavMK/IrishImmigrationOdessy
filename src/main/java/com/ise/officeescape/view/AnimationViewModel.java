package com.ise.officeescape.view;

/**
 * View model for an animation overlay in a room.
 */
public class AnimationViewModel {
    private String id;
    private String type; // "LoopingSprite", "OneShot", etc.
    private String assetPath;
    private double x;
    private double y;
    private double width;
    private double height;
    private boolean autoPlay;

    public AnimationViewModel(String id, String type, String assetPath, double x, double y, double width, double height) {
        this.id = id;
        this.type = type;
        this.assetPath = assetPath;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.autoPlay = true;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getAssetPath() {
        return assetPath;
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

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }
}

