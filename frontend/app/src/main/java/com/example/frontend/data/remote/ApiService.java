package com.example.frontend.data.remote;

import com.example.frontend.model.Product;
import com.example.frontend.data.model.auth.LoginRequest;
import com.example.frontend.data.model.auth.LoginResponse;
import com.example.frontend.data.model.cart.CartDto;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.model.cart.UpdateCartItemRequest;
import com.example.frontend.data.model.checkout.CheckoutSessionDto;
import com.example.frontend.data.model.beauty.BeautyReferenceDto;
import com.example.frontend.data.model.beauty.CustomerBeautyProfileDto;
import com.example.frontend.data.model.account.ProfileHubDto;
import com.example.frontend.data.model.order.OrderDto;
import com.example.frontend.data.model.address.AddressDto;
import com.example.frontend.data.model.coupon.CouponDto;
import com.example.frontend.data.model.product.ProductVariantDto;
import com.example.frontend.data.model.product.ProductMediaDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;

public interface ApiService {
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest credentials);

    @POST("auth/register")
    Call<ApiResponse<LoginResponse>> register(@Body Object userData);

    @GET("products")
    Call<ApiResponse<List<Product>>> getProducts(@Query("q") String query, @Query("category") String categoryId, @Query("brand") String brandId);

    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") String id);

    @GET("products/slug/{slug}")
    Call<ApiResponse<Product>> getProductBySlug(@Path("slug") String slug);

    @GET("product-media/product/{productId}")
    Call<ApiResponse<List<ProductMediaDto>>> getProductMedia(@Path("productId") String productId);

    @GET("product-variants/product/{productId}")
    Call<ApiResponse<List<ProductVariantDto>>> getProductVariants(@Path("productId") String productId);

    @GET("api/products/{id}/similar")
    Call<ApiResponse<List<Product>>> getSimilarProducts(@Path("id") String id, @Query("limit") Integer limit);

    @GET("brands")
    Call<ApiResponse<List<com.example.frontend.model.Brand>>> getBrands();

    @GET("categories")
    Call<ApiResponse<List<com.example.frontend.model.Category>>> getCategories();

    @GET("catalog")
    Call<ApiResponse<Object>> getCatalogBundle();

    @GET("recommendations/me/homepage")
    Call<ApiResponse<List<Product>>> getHomepageRecommendations();

    @GET("carts/me")
    Call<ApiResponse<CartDto>> getMyCart();

    @GET("carts/guest/me")
    Call<ApiResponse<CartDto>> getGuestCart();

    @POST("carts/me/items")
    Call<ApiResponse<CartDto>> addToCart(@Body AddToCartRequest itemRequest);

    @PATCH("carts/me/items/{itemId}/quantity")
    Call<ApiResponse<CartDto>> updateCartItemQuantity(@Path("itemId") String itemId, @Body UpdateCartItemRequest updateRequest);

    @PATCH("carts/me/items/{itemId}/selection")
    Call<ApiResponse<CartDto>> toggleCartItemSelection(@Path("itemId") String itemId, @Body UpdateCartItemRequest updateRequest);

    @DELETE("carts/me/items/{itemId}")
    Call<ApiResponse<CartDto>> removeFromCart(@Path("itemId") String itemId);

    @GET("carts/me/checkout-prepare")
    Call<ApiResponse<CheckoutSessionDto>> prepareCheckout();

    @POST("checkout-sessions/me")
    Call<ApiResponse<CheckoutSessionDto>> createCheckoutSession(@Body Object request);

    @POST("checkout-sessions/me/place-order")
    Call<ApiResponse<Object>> placeOrder(@Path("id") String sessionId, @Body Object request);

    @GET("beauty-references")
    Call<ApiResponse<List<BeautyReferenceDto>>> getBeautyReferences();

    @GET("customers/{customer_id}/beauty-profile")
    Call<ApiResponse<CustomerBeautyProfileDto>> getBeautyProfile(@Path("customer_id") String customerId);

    @PATCH("customers/{customer_id}/beauty-profile")
    Call<ApiResponse<CustomerBeautyProfileDto>> updateBeautyProfile(@Path("customer_id") String customerId, @Body Object profileData);

    @GET("accounts/profile-hub")
    Call<ApiResponse<ProfileHubDto>> getProfileHub();

    @GET("accounts/addresses")
    Call<ApiResponse<List<AddressDto>>> getMyAddresses();

    @POST("accounts/addresses")
    Call<ApiResponse<AddressDto>> addAddress(@Body Object addressData);

    @PATCH("accounts/addresses/{id}")
    Call<ApiResponse<AddressDto>> updateAddress(@Path("id") String id, @Body Object addressData);

    @DELETE("accounts/addresses/{id}")
    Call<ApiResponse<Object>> deleteAddress(@Path("id") String id);

    @GET("orders/me")
    Call<ApiResponse<List<OrderDto>>> getMyOrders();

    @GET("orders/me/{id}")
    Call<ApiResponse<OrderDto>> getMyOrderById(@Path("id") String id);

    @GET("coupons/me")
    Call<ApiResponse<List<CouponDto>>> getMyCoupons();

    @GET("coupons/available")
    Call<ApiResponse<List<CouponDto>>> getAvailableCoupons();


    @GET("api/wishlists/me/items")
    Call<ApiResponse<List<com.example.frontend.data.model.wishlist.WishlistItemResponse>>> getMyWishlistItems(@Query("sort") String sort);

    @POST("api/wishlists")
    Call<ApiResponse<com.example.frontend.data.model.wishlist.WishlistActionResponse>> addToWishlist(@Body com.example.frontend.data.model.wishlist.WishlistActionRequest request);

    @DELETE("api/wishlists/{productId}")
    Call<ApiResponse<Void>> removeFromWishlist(@Path("productId") String productId);

    @POST("api/wishlists/me/items/bulk-delete")
    Call<ApiResponse<Object>> bulkDeleteWishlistItems(@Body com.example.frontend.data.model.wishlist.BulkDeleteRequest request);

    @GET("orders/me/summary")
    Call<ApiResponse<Object>> getMyOrderSummary();

    @GET("loyalty-accounts/me")
    Call<ApiResponse<Object>> getMyLoyaltyAccount();

    @GET("accounts")
    Call<ApiResponse<List<Object>>> getAccounts();
}
