package com.example.frontend.data.remote;

import com.example.frontend.model.Product;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/auth/login")
    Call<ApiResponse<Object>> login(@Body Object credentials);

    @POST("api/auth/register")
    Call<ApiResponse<Object>> register(@Body Object userData);

    @GET("api/products")
    Call<ApiResponse<List<Product>>> getProducts(@Query("q") String query);

    @GET("api/products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") String id);

    @GET("api/recommendations/me/homepage")
    Call<ApiResponse<List<Product>>> getHomepageRecommendations();

    @GET("api/carts/me")
    Call<ApiResponse<Object>> getMyCart();

    @GET("api/carts/guest/me")
    Call<ApiResponse<Object>> getGuestCart();

    @GET("api/wishlists/me/items")
    Call<ApiResponse<List<Object>>> getMyWishlistItems();

    @GET("api/coupons/me")
    Call<ApiResponse<List<Object>>> getMyCoupons();

    @GET("api/coupons/available")
    Call<ApiResponse<List<Object>>> getAvailableCoupons();

    @GET("api/orders/me/summary")
    Call<ApiResponse<Object>> getMyOrderSummary();

    @GET("api/loyalty-accounts/me")
    Call<ApiResponse<Object>> getMyLoyaltyAccount();

    @GET("api/accounts")
    Call<ApiResponse<List<Object>>> getAccounts();
}
