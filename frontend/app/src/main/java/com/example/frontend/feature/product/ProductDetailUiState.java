package com.example.frontend.feature.product;

import com.example.frontend.data.model.product.ProductDetailResponse;
import com.example.frontend.data.model.product.ProductVariantDto;
import com.example.frontend.data.model.product.ProductMediaDto;
import com.example.frontend.data.model.product.SkinMatchDto;
import com.example.frontend.data.model.product.ReviewInsightDto;
import com.example.frontend.model.Product;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

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
<<<<<<< HEAD
    
    // New detailed data from specific endpoints
    public SkinMatchDto detailedSkinMatch = null;
    public ReviewInsightDto reviewInsight = null;

=======
    public List<com.example.frontend.data.model.review.ReviewDto> reviewPreviewList = new ArrayList<>();
>>>>>>> origin/main
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

        if (data == null) {
            return state;
        }

        state.product = data.getProduct();

        List<ProductMediaDto> cleanMedia = new ArrayList<>();

        List<ProductMediaDto> backendMedia = data.getMedia();
        Log.d("ProductDetailUiState", "backend media size = " + (backendMedia == null ? "null" : backendMedia.size()));

        if (backendMedia != null) {
            for (ProductMediaDto media : backendMedia) {
                if (media == null) continue;

                String url = media.getUrl();
                Log.d("ProductDetailUiState", "backend media url = " + url);

                if (isValidImageUrl(url)) {
                    cleanMedia.add(media);
                } else {
                    Log.d("ProductDetailUiState", "skip invalid media url = " + url);
                }
            }
        }

        if (cleanMedia.isEmpty() && state.product != null) {
            String productImageUrl = state.product.getImageUrl();
            Log.d("ProductDetailUiState", "product imageUrl fallback = " + productImageUrl);

            if (isValidImageUrl(productImageUrl)) {
                cleanMedia.add(createImageMedia(productImageUrl));
            }
        }

        state.mediaList = cleanMedia;

        Log.d("ProductDetailUiState", "final media size = " + state.mediaList.size());
        if (!state.mediaList.isEmpty()) {
            Log.d("ProductDetailUiState", "final first media url = " + state.mediaList.get(0).getUrl());
        }

        state.variants = data.getVariants();
        state.inventory = data.getInventory();
        state.isWishlisted = data.isWishlisted();
        state.skinMatch = data.getSkinMatch();
        state.reviewSummary = data.getReviewSummary();
        state.relatedProducts = data.getRelatedProducts() != null
                ? data.getRelatedProducts().getItems()
                : new ArrayList<>();

        return state;
    }

    private static boolean isValidImageUrl(String url) {
        if (url == null) return false;

        String normalized = url.trim();
        if (normalized.isEmpty()) return false;
        if ("null".equalsIgnoreCase(normalized)) return false;

        // Seed/mock URLs in database. They return 404 and must not be used.
        if (normalized.contains("example.com")) return false;

        return normalized.startsWith("http://") || normalized.startsWith("https://");
    }

    private static ProductMediaDto createImageMedia(String url) {
        ProductMediaDto media = new ProductMediaDto();
        media.setUrl(url);
        media.setMediaType("image");
        media.setDisplayOrder(0);
        return media;
    }
}
