package com.example.frontend.core.auth;

public class AuthRequiredManager {
    private static AuthRequiredManager instance;
    private PendingAuthAction pendingAction;

    private AuthRequiredManager() {}

    public static synchronized AuthRequiredManager getInstance() {
        if (instance == null) {
            instance = new AuthRequiredManager();
        }
        return instance;
    }

    public void setPendingAction(PendingAuthAction action) {
        this.pendingAction = action;
    }

    public PendingAuthAction getPendingAction() {
        return pendingAction;
    }

    public void clearPendingAction() {
        this.pendingAction = null;
    }

    public boolean hasPendingAction() {
        return pendingAction != null;
    }
}
