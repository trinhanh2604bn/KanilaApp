package com.example.frontend.model;

public class HomeShortcutItem {
    private String id;
    private String title;
    private int iconRes;
    private String destinationType;
    private String destinationValue;
    private boolean requiresLogin;
    private boolean showBadge;

    public HomeShortcutItem(String id, String title, int iconRes, String destinationType, String destinationValue, boolean requiresLogin, boolean showBadge) {
        this.id = id;
        this.title = title;
        this.iconRes = iconRes;
        this.destinationType = destinationType;
        this.destinationValue = destinationValue;
        this.requiresLogin = requiresLogin;
        this.showBadge = showBadge;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public String getDestinationValue() {
        return destinationValue;
    }

    public boolean isRequiresLogin() {
        return requiresLogin;
    }

    public boolean isShowBadge() {
        return showBadge;
    }
}
