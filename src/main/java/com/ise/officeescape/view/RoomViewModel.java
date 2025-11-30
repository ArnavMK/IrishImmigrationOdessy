package com.ise.officeescape.view;

import com.ise.officeescape.model.Room;
import java.util.ArrayList;
import java.util.List;

/**
 * View model for rendering a room.
 * Contains all the data the view needs to render a room (background, hotspots, animations).
 */
public class RoomViewModel {
    private Room room;
    private String backgroundImagePath;
    private List<HotspotViewModel> hotspots;
    private List<AnimationViewModel> animations;

    public RoomViewModel(Room room, String backgroundImagePath) {
        this.room = room;
        this.backgroundImagePath = backgroundImagePath;
        this.hotspots = new ArrayList<>();
        this.animations = new ArrayList<>();
    }

    public Room getRoom() {
        return room;
    }

    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    public List<HotspotViewModel> getHotspots() {
        return hotspots;
    }

    public void addHotspot(HotspotViewModel hotspot) {
        this.hotspots.add(hotspot);
    }

    public List<AnimationViewModel> getAnimations() {
        return animations;
    }

    public void addAnimation(AnimationViewModel animation) {
        this.animations.add(animation);
    }
}

