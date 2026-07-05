package com.example.frontend.model;

public class Product {
    private String id;
    private String brand;
    private String name;
    private String price;
    private String rating;
    private String reviewCount;
    private int imageResource;
    private String badgeText;

    public Product(String id, String brand, String name, String price, String rating, String reviewCount, int imageResource, String badgeText) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.imageResource = imageResource;
        this.badgeText = badgeText;
    }

    public String getId() { return id; }
    public String getBrand() { return brand; }
    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getRating() { return rating; }
    public String getReviewCount() { return reviewCount; }
    public int getImageResource() { return imageResource; }
    public String getBadgeText() { return badgeText; }
}
