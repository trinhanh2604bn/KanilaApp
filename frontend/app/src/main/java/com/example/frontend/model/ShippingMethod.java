package com.example.frontend.model;

public class ShippingMethod {
    private String id;
    private String name;
    private String estimate;
    private String description;
    private String price;
    private int iconRes;
    private String tag;
    private boolean isSelected;

    public ShippingMethod(String id, String name, String estimate, String description, String price, int iconRes) {
        this(id, name, estimate, description, price, iconRes, null);
    }

    public ShippingMethod(String id, String name, String estimate, String description, String price, int iconRes, String tag) {
        this.id = id;
        this.name = name;
        this.estimate = estimate;
        this.description = description;
        this.price = price;
        this.iconRes = iconRes;
        this.tag = tag;
        this.isSelected = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEstimate() { return estimate; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public int getIconRes() { return iconRes; }
    public String getTag() { return tag; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
