package com.netflix.hollow.api.common;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ListenerSupport {

    protected final CopyOnWriteArrayList<EventListener> eventListeners;

    public ListenerSupport() {
        eventListeners = new CopyOnWriteArrayList<>();
    }

    public ListenerSupport(List<? extends EventListener> listeners) {
        eventListeners = new CopyOnWriteArrayList<>(listeners);
    }

    public ListenerSupport(ListenerSupport that) {
        eventListeners = new CopyOnWriteArrayList<>(that.eventListeners);
    }

    public void addListener(EventListener listener) {
        eventListeners.addIfAbsent(listener);
    }

    public void removeListener(EventListener listener) {
        eventListeners.remove(listener);
    }

}
