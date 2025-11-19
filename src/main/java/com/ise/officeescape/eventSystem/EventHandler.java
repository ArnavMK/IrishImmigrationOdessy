package com.ise.officeescape.eventSystem;


public interface EventHandler<T extends EventArgs> {
    void handle(Object sender, T args);
}
