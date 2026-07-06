package com.example.frontend.data.model.wishlist;

public class WishlistActionRequest {
    private String productId;
    private String variantId;

    public WishlistActionRequest(String productId) {
        this.productId = productId;
    }

    public WishlistActionRequest(String productId, String variantId) {
        this.productId = productId;
        this.variantId = variantId;
    }

    public String getProductId() { return productId; }
    public String getVariantId() { return variantId; }
}
