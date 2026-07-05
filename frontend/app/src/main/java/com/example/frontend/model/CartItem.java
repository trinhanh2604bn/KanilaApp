package com.example.frontend.model;

public class CartItem {
    private Product product;
    private String variant;
    private int quantity;
    private boolean isSelected;
    private boolean isWishlisted;

    public CartItem(Product product, String variant, int quantity, boolean isSelected) {
        this.product = product;
        this.variant = variant;
        this.quantity = quantity;
        this.isSelected = isSelected;
        this.isWishlisted = false;
    }

    public Product getProduct() { return product; }
    public String getVariant() { return variant; }
    public void setVariant(String variant) { this.variant = variant; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
    public boolean isWishlisted() { return isWishlisted; }
    public void setWishlisted(boolean wishlisted) { isWishlisted = wishlisted; }
}
