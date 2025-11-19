package com.ise.officeescape.eventSystem;

import java.util.ArrayList;
import java.util.List;

public class Event<T extends EventArgs> {

    private final List<EventHandler<T>> listeners = new ArrayList<>();

    public void addListener(EventHandler<T> listener) {
        listeners.add(listener);
    }

    public void removeListener(EventHandler<T> listener) {
        listeners.remove(listener);
    }

    public void invoke(Object sender, T args) {
        for (EventHandler<T> listener : listeners) {
            listener.handle(sender, args);
        }
    }
}

