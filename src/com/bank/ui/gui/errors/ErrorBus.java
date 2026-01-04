package com.bank.ui.gui.errors;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ErrorBus {
    private static final ErrorBus instance = new ErrorBus();
    public static ErrorBus getInstance() { return instance; }

    private final List<ErrorListener> listeners = new CopyOnWriteArrayList<>();

    // tracks if the last action produced an error
    private volatile boolean lastActionHadError = false;

    private ErrorBus() {}

    public void addListener(ErrorListener l) { listeners.add(l); }
    public void removeListener(ErrorListener l) { listeners.remove(l); }

    public void publish(String title, String message) {
        lastActionHadError = true; // ✅ mark
        ErrorEvent ev = new ErrorEvent(title, message);
        for (ErrorListener l : listeners) l.onError(ev);
    }

    // ✅ call BEFORE an action
    public void resetLastError() {
        lastActionHadError = false;
    }

    // ✅ call AFTER an action
    public boolean hadError() {
        return lastActionHadError;
    }
}
