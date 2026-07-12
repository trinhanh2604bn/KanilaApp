package com.example.frontend.feature.product;

import com.example.frontend.data.model.product.ProductDetailResponse;
import com.example.frontend.data.model.product.ProductVariantDto;
import com.example.frontend.data.model.product.ProductMediaDto;
import com.example.frontend.data.model.product.SkinMatchDto;
import com.example.frontend.data.model.product.ReviewInsightDto;
import com.example.frontend.model.Product;
import java.util.List;

public class ProductDetailUiState {
    public boolean isLoading = false;
    public String error = null;
    public boolean noInternet = false;

    public Product product = null;
    public List<ProductMediaDto> mediaList = null;
    public List<ProductVariantDto> variants = null;
    public ProductVariantDto selectedVariant = null;
    public String selectedVariantName = null;

    public ProductDetailResponse.InventoryDto inventory = null;
    public boolean isWishlisted = false;
    
    // Legacy simple data from product detail response
    public ProductDetailResponse.SkinMatchDto skinMatch = null;
    public ProductDetailResponse.ReviewSummaryDto reviewSummary = null;
    
    // New detailed data from specific endpoints
    public SkinMatchDto detailedSkinMatch = null;
    public ReviewInsightDto reviewInsight = null;

    public List<Product> relatedProducts = null;
    public List<Product> recentlyViewed = null;

    public ProductDetailUiState() {}

    public static ProductDetailUiState loading() {
        ProductDetailUiState state = new ProductDetailUiState();
        state.isLoading = true;
        return state;
    }

    public static ProductDetailUiState error(String message) {
        ProductDetailUiState state = new ProductDetailUiState();
        state.error = message;
        return state;
    }

    public static ProductDetailUiState success(ProductDetailResponse data) {
        ProductDetailUiState state = new ProductDetailUiState();
        state.product = data.getProduct();
        state.mediaList = data.getMedia();
        
        // Media fallback: if mediaList is null/empty but product has imageUrl, create fallback
        if ((state.mediaList == null || state.mediaList.isEmpty()) && state.product != null && state.product.getImageUrl() != null) {
            ProductMediaDto fallback = new ProductMediaDto();
            fallback.setUrl(state.product.getImageUrl());
            fallback.setMediaType("image");
            state.mediaList = new java.util.ArrayList<>();
            state.mediaList.add(fallback);
        }

        state.variants = data.getVariants();
        state.inventory = data.getInventory();
        state.isWishlisted = data.isWishlisted();
        state.skinMatch = data.getSkinMatch();
        state.reviewSummary = data.getReviewSummary();
        state.relatedProducts = data.getRelatedProducts() != null ? data.getRelatedProducts().getItems() : new java.util.ArrayList<>();
        return state;
    }
}
