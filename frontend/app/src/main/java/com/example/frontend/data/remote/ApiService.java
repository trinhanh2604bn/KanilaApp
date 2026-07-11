package com.example.frontend.data.remote;

import com.example.frontend.model.Product;
import com.example.frontend.data.model.auth.LoginRequest;
import com.example.frontend.data.model.auth.RegisterRequest;
import com.example.frontend.data.model.auth.VerifyOtpRequest;
import com.example.frontend.data.model.auth.AuthResponse;
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
import com.example.frontend.data.model.wishlist.BulkDeleteRequest;
import com.example.frontend.data.model.wishlist.WishlistActionRequest;
import com.example.frontend.data.model.wishlist.WishlistActionResponse;
import com.example.frontend.data.model.wishlist.WishlistItemResponse;
import com.example.frontend.data.model.common.PaginatedData;
import com.example.frontend.data.model.product.ProductDetailResponse;
import com.example.frontend.feature.chatbot.data.request.ChatbotMessageRequest;
import com.example.frontend.feature.chatbot.data.response.ChatbotMessageResponse;
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
    @POST("api/chatbot/message")
    Call<ChatbotMessageResponse> sendChatbotMessage(@Body ChatbotMessageRequest request);

    @GET("api/chatbot/sessions/{sessionId}/messages")
    Call<com.example.frontend.feature.chatbot.data.response.ChatbotSessionHistoryResponse> getChatbotHistory(@Path("sessionId") String sessionId);

    @POST("api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest credentials);

    @POST("api/auth/forgot-password")
    Call<ApiResponse<AuthResponse>> forgotPassword(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("api/auth/verify-otp")
    Call<ApiResponse<AuthResponse>> verifyOtp(@Body VerifyOtpRequest request);

    @POST("api/auth/reset-password")
    Call<ApiResponse<Void>> resetPassword(@Body com.example.frontend.data.model.auth.ResetPasswordRequest request);

    @GET("api/auth/me")
    Call<ApiResponse<Object>> getMe();

    @GET("api/products")
    Call<ApiResponse<List<Product>>> getProducts(@Query("q") String query, @Query("category") String categoryId, @Query("brand") String brandId);

    @GET("api/mobile/products/{id}/detail")
    Call<ApiResponse<ProductDetailResponse>> getProductDetail(@Path("id") String id);

    @GET("api/products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") String id);

    @GET("api/products/slug/{slug}")
    Call<ApiResponse<Product>> getProductBySlug(@Path("slug") String slug);

    @GET("api/product-media/product/{productId}")
    Call<ApiResponse<List<ProductMediaDto>>> getProductMedia(@Path("productId") String productId);

    @GET("api/product-variants/product/{productId}")
    Call<ApiResponse<List<ProductVariantDto>>> getProductVariants(@Path("productId") String productId);

    @GET("api/products/{id}/similar")
    Call<ApiResponse<PaginatedData<Product>>> getSimilarProducts(@Path("id") String id, @Query("limit") Integer limit);

    @GET("api/brands")
    Call<ApiResponse<List<com.example.frontend.model.Brand>>> getBrands();

    @GET("api/categories")
    Call<ApiResponse<List<com.example.frontend.model.Category>>> getCategories();

    @GET("api/catalog")
    Call<ApiResponse<Object>> getCatalogBundle();

    @GET("api/recommendations/me/homepage")
    Call<ApiResponse<List<Product>>> getHomepageRecommendations();

    @GET("api/carts/me")
    Call<ApiResponse<CartDto>> getMyCart();

    @POST("api/carts/me/merge-guest")
    Call<ApiResponse<CartDto>> mergeGuestCart();

    @GET("api/carts/guest/me")
    Call<ApiResponse<CartDto>> getGuestCart();

    @POST("api/carts/me/items")
    Call<ApiResponse<CartDto>> addToCart(@Body AddToCartRequest itemRequest);

    @POST("api/carts/guest/items")
    Call<ApiResponse<CartDto>> addToGuestCart(@Body AddToCartRequest itemRequest);

    @PATCH("api/carts/me/items/{itemId}/quantity")
    Call<ApiResponse<CartDto>> updateCartItemQuantity(@Path("itemId") String itemId, @Body UpdateCartItemRequest updateRequest);

    @PATCH("api/carts/guest/items/{itemId}/quantity")
    Call<ApiResponse<CartDto>> updateGuestCartItemQuantity(@Path("itemId") String itemId, @Body UpdateCartItemRequest updateRequest);

    @PATCH("api/carts/me/items/{itemId}/selection")
    Call<ApiResponse<CartDto>> toggleCartItemSelection(@Path("itemId") String itemId, @Body UpdateCartItemRequest updateRequest);

    @PATCH("api/carts/guest/items/{itemId}/selection")
    Call<ApiResponse<CartDto>> toggleGuestCartItemSelection(@Path("itemId") String itemId, @Body UpdateCartItemRequest updateRequest);

    @PATCH("api/carts/me/selection")
    Call<ApiResponse<CartDto>> toggleAllSelection(@Body UpdateCartItemRequest updateRequest);

    @PATCH("api/carts/guest/selection")
    Call<ApiResponse<CartDto>> toggleGuestAllSelection(@Body UpdateCartItemRequest updateRequest);

    @DELETE("api/carts/me/items/{itemId}")
    Call<ApiResponse<CartDto>> removeFromCart(@Path("itemId") String itemId);

    @DELETE("api/carts/guest/items/{itemId}")
    Call<ApiResponse<CartDto>> removeGuestFromCart(@Path("itemId") String itemId);

    @GET("api/carts/me/checkout-prepare")
    Call<ApiResponse<CheckoutSessionDto>> prepareCheckout();

    @GET("api/carts/guest/checkout-prepare")
    Call<ApiResponse<CheckoutSessionDto>> prepareGuestCheckout();

    @POST("api/checkout-sessions/me")
    Call<ApiResponse<CheckoutSessionDto>> createCheckoutSession(@Body Object request);

    @GET("api/checkout-sessions/me/{id}")
    Call<ApiResponse<CheckoutSessionDto>> getCheckoutSession(@Path("id") String sessionId);

    @PATCH("api/checkout-sessions/{id}")
    Call<ApiResponse<CheckoutSessionDto>> updateCheckoutSession(@Path("id") String sessionId, @Body java.util.Map<String, Object> body);

    @GET("api/checkout-sessions/guest/me/{id}")
    Call<ApiResponse<CheckoutSessionDto>> getGuestCheckoutSession(@Path("id") String sessionId);

    @PATCH("api/checkout-sessions/guest/{id}")
    Call<ApiResponse<CheckoutSessionDto>> updateGuestCheckoutSession(@Path("id") String sessionId, @Body java.util.Map<String, Object> body);

    @GET("api/shipping-methods")
    Call<ApiResponse<List<com.example.frontend.data.model.shipping.ShippingMethodDto>>> getShippingMethods();

    @GET("api/payment-methods")
    Call<ApiResponse<List<com.example.frontend.data.model.payment.PaymentMethodDto>>> getPaymentMethods();

    @POST("api/checkout-sessions/me/buy-now")
    Call<ApiResponse<CheckoutSessionDto>> createBuyNowSession(@Body AddToCartRequest request);

    @POST("api/checkout-sessions/me/{id}/place-order")
    Call<ApiResponse<OrderDto>> placeOrder(@Path("id") String sessionId, @Body Object request);

    @POST("api/checkout-sessions/guest/{id}/place-order")
    Call<ApiResponse<OrderDto>> placeGuestOrder(@Path("id") String sessionId, @Body Object request);

    @POST("api/orders/mock-checkout")
    Call<ApiResponse<com.example.frontend.data.model.order.MockOrderResponse>> createMockOrder(@Body java.util.Map<String, Object> request);

    @GET("api/orders/code/{orderCode}")
    Call<ApiResponse<com.example.frontend.data.model.order.OrderDetailDto>> getOrderByCode(@Path("orderCode") String orderCode);

    @GET("api/beauty-references")
    Call<ApiResponse<List<BeautyReferenceDto>>> getBeautyReferences();

    @GET("api/customers/{customer_id}/beauty-profile")
    Call<ApiResponse<CustomerBeautyProfileDto>> getBeautyProfile(@Path("customer_id") String customerId);

    @PATCH("api/customers/{customer_id}/beauty-profile")
    Call<ApiResponse<CustomerBeautyProfileDto>> updateBeautyProfile(@Path("customer_id") String customerId, @Body Object profileData);

    @GET("api/accounts/profile-hub")
    Call<ApiResponse<ProfileHubDto>> getProfileHub();

    @PATCH("api/accounts/profile")
    Call<ApiResponse<ProfileHubDto.AccountInfo>> patchMyProfile(@Body java.util.Map<String, Object> profileData);

    @GET("api/accounts/addresses")
    Call<ApiResponse<List<AddressDto>>> getMyAddresses();

    @POST("api/accounts/addresses")
    Call<ApiResponse<AddressDto>> addAddress(@Body Object addressData);

    @PATCH("api/accounts/addresses/{id}")
    Call<ApiResponse<AddressDto>> updateAddress(@Path("id") String id, @Body Object addressData);

    @PATCH("api/accounts/addresses/{id}/default")
    Call<ApiResponse<AddressDto>> setDefaultAddress(@Path("id") String id);

    @DELETE("api/accounts/addresses/{id}")
    Call<ApiResponse<Object>> deleteAddress(@Path("id") String id);

    @GET("api/orders/me")
    Call<ApiResponse<List<com.example.frontend.data.model.order.OrderSummaryDto>>> getMyOrders(@Query("status") String status, @Query("page") Integer page);

    @GET("api/orders/me")
    Call<ApiResponse<List<OrderDto>>> getMyOrders();

    @GET("api/orders/me/{id}")
    Call<ApiResponse<OrderDto>> getMyOrderById(@Path("id") String id);

    @GET("api/orders/me/{id}")
    Call<ApiResponse<com.example.frontend.data.model.order.OrderDetailDto>> getMyOrderDetail(@Path("id") String id);

    @GET("api/orders/me/{id}/review-items")
    Call<ApiResponse<com.example.frontend.data.model.order.ReviewOrderItemsDto>> getOrderReviewItems(@Path("id") String orderId);

    @GET("api/reviews/write-eligibility/{orderItemId}")
    Call<ApiResponse<com.example.frontend.data.model.review.ReviewEligibilityDto>> getReviewWriteEligibility(@Path("orderItemId") String orderItemId);

    @POST("api/reviews/submit")
    Call<ApiResponse<Object>> submitReview(@Body com.example.frontend.data.model.review.SubmitReviewRequest request);

    @PATCH("api/orders/{id}/cancel")
    Call<ApiResponse<com.example.frontend.data.model.order.OrderSummaryDto>> cancelMyOrder(@Path("id") String id, @Body java.util.Map<String, String> body);

    @GET("api/coupons/me")
    Call<ApiResponse<List<CouponDto>>> getMyCoupons();

    @GET("api/coupons/available")
    Call<ApiResponse<List<CouponDto>>> getAvailableCoupons();

    @POST("api/coupons/apply")
    Call<ApiResponse<com.example.frontend.data.model.coupon.ApplyCouponResponse>> applyCoupon(@Body com.example.frontend.data.model.coupon.ApplyCouponRequest request);

    @GET("api/wishlists/me/items")
    Call<ApiResponse<PaginatedData<WishlistItemResponse>>> getMyWishlistItems(@Query("sort") String sort);

    @GET("api/wishlists/me/status")
    Call<ApiResponse<java.util.Map<String, Boolean>>> getWishlistStatus(@Query("productIds") String productIds);

    @POST("api/wishlists")
    Call<ApiResponse<WishlistActionResponse>> addToWishlist(@Body WishlistActionRequest request);

    @DELETE("api/wishlists/{productId}")
    Call<ApiResponse<Void>> removeFromWishlist(@Path("productId") String productId);

    @DELETE("api/wishlists/me/items")
    Call<ApiResponse<Object>> clearWishlist();

    @POST("api/wishlists/me/items/bulk-delete")
    Call<ApiResponse<Object>> bulkDeleteWishlistItems(@Body BulkDeleteRequest request);

    @GET("api/orders/me/summary")
    Call<ApiResponse<Object>> getMyOrderSummary();

    @GET("api/loyalty/me")
    Call<ApiResponse<Object>> getMyLoyaltyAccount();

    @POST("api/coupons/save/{couponId}")
    Call<ApiResponse<Object>> saveCoupon(@Path("couponId") String couponId);

    @GET("api/accounts")
    Call<ApiResponse<List<Object>>> getAccounts();
}
