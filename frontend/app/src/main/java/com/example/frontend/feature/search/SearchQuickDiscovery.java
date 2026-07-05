package com.example.frontend.feature.search;

public class SearchQuickDiscovery {
    private String title;
    private String description;
    private int imageResource;

    public SearchQuickDiscovery(String title, String description, int imageResource) {
        this.title = title;
        this.description = description;
        this.imageResource = imageResource;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getImageResource() { return imageResource; }
}
