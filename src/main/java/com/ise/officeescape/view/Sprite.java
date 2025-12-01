package com.ise.officeescape.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * A draggable sprite/image that can be moved around like in Unity.
 * Handles mouse drag interactions.
 */
public class Sprite {
    private ImageView imageView;
    private double startX;
    private double startY;
    private double dragStartX;
    private double dragStartY;
    private boolean draggable;

    public Sprite(String imagePath, double x, double y, double width, double height) {
        this.imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Could not load sprite image: " + imagePath);
        }
        
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        
        this.startX = x;
        this.startY = y;
        this.draggable = false;
    }

    /**
     * Makes this sprite draggable.
     */
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
        if (draggable) {
            imageView.setOnMousePressed(this::handleMousePressed);
            imageView.setOnMouseDragged(this::handleMouseDragged);
            imageView.setOnMouseReleased(this::handleMouseReleased);
            imageView.setStyle("-fx-cursor: hand;");
        } else {
            imageView.setOnMousePressed(null);
            imageView.setOnMouseDragged(null);
            imageView.setOnMouseReleased(null);
            imageView.setStyle("-fx-cursor: default;");
        }
    }

    private void handleMousePressed(MouseEvent e) {
        dragStartX = e.getSceneX() - imageView.getX();
        dragStartY = e.getSceneY() - imageView.getY();
        imageView.setStyle("-fx-cursor: closed-hand;");
    }

    private void handleMouseDragged(MouseEvent e) {
        if (draggable) {
            double newX = e.getSceneX() - dragStartX;
            double newY = e.getSceneY() - dragStartY;
            setPosition(newX, newY);
        }
    }

    private void handleMouseReleased(MouseEvent e) {
        imageView.setStyle("-fx-cursor: hand;");
    }

    /**
     * Sets the position of the sprite.
     */
    public void setPosition(double x, double y) {
        imageView.setX(x);
        imageView.setY(y);
    }

    /**
     * Gets the current X position.
     */
    public double getX() {
        return imageView.getX();
    }

    /**
     * Gets the current Y position.
     */
    public double getY() {
        return imageView.getY();
    }

    /**
     * Gets the width.
     */
    public double getWidth() {
        return imageView.getFitWidth();
    }

    /**
     * Gets the height.
     */
    public double getHeight() {
        return imageView.getFitHeight();
    }

    /**
     * Resets sprite to its starting position.
     */
    public void resetPosition() {
        setPosition(startX, startY);
    }

    /**
     * Gets the ImageView node to add to a Pane.
     */
    public ImageView getNode() {
        return imageView;
    }

    /**
     * Sets constraints on where the sprite can be dragged (bounds).
     */
    public void setDragBounds(double minX, double minY, double maxX, double maxY) {
        imageView.setOnMouseDragged(e -> {
            if (draggable) {
                double newX = Math.max(minX, Math.min(maxX, e.getSceneX() - dragStartX));
                double newY = Math.max(minY, Math.min(maxY, e.getSceneY() - dragStartY));
                setPosition(newX, newY);
            }
        });
    }

    /**
     * Checks if the sprite is at a specific position (within tolerance).
     */
    public boolean isAtPosition(double targetX, double targetY, double tolerance) {
        double dx = Math.abs(imageView.getX() - targetX);
        double dy = Math.abs(imageView.getY() - targetY);
        return dx <= tolerance && dy <= tolerance;
    }
}

