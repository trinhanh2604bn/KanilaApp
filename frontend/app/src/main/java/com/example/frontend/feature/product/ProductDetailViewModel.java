package com.example.frontend.feature.product;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.product.ProductDetailResponse;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.model.cart.CartDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.data.repository.CartRepository;
import com.example.frontend.data.repository.WishlistRepository;
import com.example.frontend.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailViewModel extends AndroidViewModel {
    private static final String TAG = "ProductDetailVM";
    private static final String PREF_RECENTLY_VIEWED = "recently_viewed";
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final WishlistRepository wishlistRepository;
    private final com.example.frontend.data.repository.ReviewRepository reviewRepository;
    private final com.example.frontend.data.repository.CheckoutRepository checkoutRepository;
    private final MutableLiveData<ProductDetailUiState> uiState = new MutableLiveData<>(new ProductDetailUiState());
    private final MutableLiveData<NetworkResult<CartDto>> addToCartResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<com.example.frontend.data.model.checkout.CheckoutSessionDto>> buyNowResult = new MutableLiveData<>();
    private List<Product> recentlyViewedList = new ArrayList<>();
    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public ProductDetailViewModel(@NonNull Application application) {
        super(application);
        this.productRepository = new ProductRepository(application);
        this.cartRepository = new CartRepository(application);
        this.wishlistRepository = new WishlistRepository(application);
        this.reviewRepository = new com.example.frontend.data.repository.ReviewRepository(application);
        this.checkoutRepository = new com.example.frontend.data.repository.CheckoutRepository(application);
        this.sharedPreferences = application.getSharedPreferences("kanila_prefs", Context.MODE_PRIVATE);
        loadRecentlyViewedFromPrefs();
    }

    public LiveData<ProductDetailUiState> getUiState() {
        return uiState;
    }

    public LiveData<NetworkResult<CartDto>> getAddToCartResult() {
        return addToCartResult;
    }

    public LiveData<NetworkResult<com.example.frontend.data.model.checkout.CheckoutSessionDto>> getBuyNowResult() {
        return buyNowResult;
    }

    private void loadRecentlyViewedFromPrefs() {
        String json = sharedPreferences.getString(PREF_RECENTLY_VIEWED, null);
        if (json != null) {
            recentlyViewedList = gson.fromJson(json, new TypeToken<List<Product>>() {}.getType());
        }
    }

    private void saveRecentlyViewedToPrefs() {
        String json = gson.toJson(recentlyViewedList);
        sharedPreferences.edit().putString(PREF_RECENTLY_VIEWED, json).apply();
    }

    public void loadProductDetails(String productId) {
        Log.d(TAG, "Loading productId = " + productId);
        if (productId == null || !productId.matches("^[a-fA-F0-9]{24}$")) {
            uiState.setValue(ProductDetailUiState.error("Product ID không hợp lệ: " + productId));
            return;
        }

        uiState.setValue(ProductDetailUiState.loading());
        productRepository.getProductDetail(productId).observeForever(result -> {
            if (result == null) {
                uiState.setValue(ProductDetailUiState.error("Lỗi hệ thống: result is null"));
                return;
            }
            switch (result.status) {
                case SUCCESS:
                    if (result.data != null && result.data.getProduct() != null) {
                        ProductDetailUiState successState = ProductDetailUiState.success(result.data);
                        successState.recentlyViewed = updateRecentlyViewed(result.data.getProduct());
                        uiState.setValue(successState);
                        loadReviewPreview(productId);
                    } else {
                        uiState.setValue(ProductDetailUiState.error("Không tìm thấy thông tin sản phẩm"));
                    }
                    break;
                case ERROR:
                    uiState.setValue(ProductDetailUiState.error(result.message != null ? result.message : "Lỗi tải chi tiết sản phẩm"));
                    break;
                case EMPTY:
                    uiState.setValue(ProductDetailUiState.error("Sản phẩm không tồn tại"));
                    break;
                case LOADING:
                    // Keep loading state
                    break;
                case NO_INTERNET:
                    ProductDetailUiState errorState = ProductDetailUiState.error("Không có kết nối Internet");
                    errorState.noInternet = true;
                    uiState.setValue(errorState);
                    break;
            }
        });
    }

    private List<Product> updateRecentlyViewed(Product currentProduct) {
        if (currentProduct != null) {
            recentlyViewedList.removeIf(p -> p.getId().equals(currentProduct.getId()));
            recentlyViewedList.add(0, currentProduct);
            if (recentlyViewedList.size() > 20) recentlyViewedList.remove(20);
            saveRecentlyViewedToPrefs();
        }
        List<Product> displayList = new ArrayList<>(recentlyViewedList);
        if (currentProduct != null) displayList.removeIf(p -> p.getId().equals(currentProduct.getId()));
        return displayList;
    }

    public void addToCart(String productId, String variantId, int quantity) {
        AddToCartRequest request = new AddToCartRequest(productId, variantId, quantity);
        cartRepository.addToCart(request, addToCartResult);
    }

    public void buyNow(String productId, String variantId, int quantity) {
        checkoutRepository.createBuyNowSession(productId, variantId, quantity, buyNowResult);
    }

    public void toggleWishlist() {
        ProductDetailUiState current = uiState.getValue();
        if (current == null || current.product == null) return;

        boolean wasWishlisted = current.isWishlisted;
        // Optimistic update
        current.isWishlisted = !wasWishlisted;
        uiState.setValue(current);

        if (wasWishlisted) {
            wishlistRepository.removeFromWishlist(current.product.getId(), new MutableLiveData<>());
        } else {
            wishlistRepository.addToWishlist(current.product.getId(), new MutableLiveData<>());
        }
    }

    public void loadReviewPreview(String productId) {
        MutableLiveData<NetworkResult<List<com.example.frontend.data.model.review.ReviewDto>>> result = new MutableLiveData<>();
        reviewRepository.getRandomReviewPreview(productId, 2, result);
        result.observeForever(networkResult -> {
            if (networkResult.status == NetworkResult.Status.SUCCESS) {
                ProductDetailUiState currentState = uiState.getValue();
                if (currentState != null) {
                    currentState.reviewPreviewList = networkResult.data;
                    uiState.setValue(currentState);
                }
            }
        });
    }

    public void toggleReviewLike(com.example.frontend.data.model.review.ReviewDto review) {
        reviewRepository.toggleReviewVote(review.getId(), new MutableLiveData<>());
        // Local update for immediate UI response
        boolean newLiked = !review.isLikedByMe();
        int newCount = review.getHelpfulCount() + (newLiked ? 1 : -1);
        review.setLikedByMe(newLiked);
        review.setHelpfulCount(newCount);
        uiState.setValue(uiState.getValue());
    }
}