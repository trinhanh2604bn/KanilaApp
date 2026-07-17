package com.example.frontend.data.remote;

import com.example.frontend.data.model.search.SearchResponse;
import com.example.frontend.data.model.search.SearchSuggestionResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchApi {

    /**
     * Main product search with full makeup filter support.
     * GET /api/search/products
     */
    @GET("api/search/products")
    Call<ApiResponse<SearchResponse>> searchProducts(
            @Query("q")               String query,
            @Query("page")            int page,
            @Query("limit")           int limit,
            @Query("sort")            String sort,
            @Query("brand_ids")       String brandIds,
            @Query("category_ids")    String categoryIds,
            @Query("min_price")       String minPrice,
            @Query("max_price")       String maxPrice,
            @Query("min_rating")      String minRating,
            @Query("finish_types")    String finishTypes,
            @Query("coverage_levels") String coverageLevels,
            @Query("color_families")  String colorFamilies,
            @Query("shade_codes")     String shadeCodes,
            @Query("in_stock")        Boolean inStock,
            @Query("on_sale")         Boolean onSale,
            @Query("ar_supported")    Boolean arSupported,
            @Query("waterproof")      Boolean waterproof,
            @Query("long_wear")       Boolean longWear
    );

    /**
     * Autocomplete suggestions (debounced in ViewModel).
     * GET /api/search/suggestions
     */
    @GET("api/search/suggestions")
    Call<ApiResponse<SearchSuggestionResponse>> getSuggestions(
            @Query("q")     String query,
            @Query("limit") int limit
    );

    /**
     * Barcode / QR / SKU scan search.
     * GET /api/search/scan
     */
    @GET("api/search/scan")
    Call<ApiResponse<SearchResponse>> scanSearch(
            @Query("value") String value
    );

    /**
     * Discovery products for empty search screen.
     * GET /api/search/discovery
     */
    @GET("api/search/discovery")
    Call<ApiResponse<SearchResponse>> getDiscovery();

    /**
     * Record search event analytics.
     * POST /api/search/event
     */
    @retrofit2.http.POST("api/search/event")
    Call<ApiResponse<Void>> recordEvent(@retrofit2.http.Body com.example.frontend.data.model.search.SearchEventRequest request);
}
