package com.example.frontend.model;

public class HomeBannerItem {
    private String id;
    private String eyebrow;
    private String title;
    private String subtitle;
    private String buttonText;
    private String backgroundImageUrl;
    private int backgroundDrawableRes;
    private String deepLinkType;
    private String deepLinkValue;
    private boolean active;
    private int displayOrder;

    public HomeBannerItem(String id, String eyebrow, String title, String subtitle, String buttonText, String backgroundImageUrl, int backgroundDrawableRes, String deepLinkType, String deepLinkValue, boolean active, int displayOrder) {
        this.id = id;
        this.eyebrow = eyebrow;
        this.title = title;
        this.subtitle = subtitle;
        this.buttonText = buttonText;
        this.backgroundImageUrl = backgroundImageUrl;
        this.backgroundDrawableRes = backgroundDrawableRes;
        this.deepLinkType = deepLinkType;
        this.deepLinkValue = deepLinkValue;
        this.active = active;
        this.displayOrder = displayOrder;
    }

    // Getters
    public String getId() { return id; }
    public String getEyebrow() { return eyebrow; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getButtonText() { return buttonText; }
    public String getBackgroundImageUrl() { return backgroundImageUrl; }
    public int getBackgroundDrawableRes() { return backgroundDrawableRes; }
    public String getDeepLinkType() { return deepLinkType; }
    public String getDeepLinkValue() { return deepLinkValue; }
    public boolean isActive() { return active; }
    public int getDisplayOrder() { return displayOrder; }
}
