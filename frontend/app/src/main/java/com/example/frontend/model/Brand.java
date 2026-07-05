package com.example.frontend.model;

public class Brand {
    private String name;
    private int logoRes;
    private boolean favorite;
    private String region;

    public Brand(String name, int logoRes, boolean favorite, String region) {
        this.name = name;
        this.logoRes = logoRes;
        this.favorite = favorite;
        this.region = region;
    }

    public String getName() {
        return name;
    }

    public int getLogoRes() {
        return logoRes;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getRegion() {
        return region;
    }
}
