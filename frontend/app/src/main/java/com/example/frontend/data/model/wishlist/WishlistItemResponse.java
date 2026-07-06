package com.example.frontend.data.model.wishlist;

import com.example.frontend.model.Product;

public class WishlistItemResponse {
    private String wishlistItemId;
    private String wishlistId;
    private String productId;
    private String variantId;
    private String createdAt;
    private Product product;

    public String getWishlistItemId() { return wishlistItemId; }
    public String getWishlistId() { return wishlistId; }
    public String getProductId() { return productId; }
    public String getVariantId() { return variantId; }
    public String getCreatedAt() { return createdAt; }
    public Product getProduct() { return product; }
}
