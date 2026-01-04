package com.bank.ui.gui.errors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;

public class NotificationBus {
    public static final String EVT_NOTIFICATION = "notification";

    private static NotificationBus instance;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private Notification last;

    private NotificationBus() {}

    public static NotificationBus getInstance() {
        if (instance == null) instance = new NotificationBus();
        return instance;
    }

    public void addListener(PropertyChangeListener l) { pcs.addPropertyChangeListener(l); }
    public void removeListener(PropertyChangeListener l) { pcs.removePropertyChangeListener(l); }

    public Notification getLast() { return last; }

    public void publish(String title, String message) {
        Notification old = this.last;
        this.last = new Notification(title, message, LocalDateTime.now());
        pcs.firePropertyChange(EVT_NOTIFICATION, old, this.last);
    }

    public record Notification(String title, String message, LocalDateTime at) {}
}
