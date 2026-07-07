package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.model.wishlist.BulkDeleteRequest;
import com.example.frontend.data.model.wishlist.WishlistActionRequest;
import com.example.frontend.data.model.wishlist.WishlistActionResponse;
import com.example.frontend.data.model.wishlist.WishlistItemResponse;
import com.example.frontend.data.model.common.PaginatedData;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistRepository {
    private final ApiService apiService;

    public WishlistRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getMyWishlistItems(String sort, MutableLiveData<NetworkResult<List<WishlistItemResponse>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getMyWishlistItems(sort).enqueue(new Callback<ApiResponse<PaginatedData<WishlistItemResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginatedData<WishlistItemResponse>>> call, Response<ApiResponse<PaginatedData<WishlistItemResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PaginatedData<WishlistItemResponse>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        PaginatedData<WishlistItemResponse> paginatedData = apiResponse.getData();
                        List<WishlistItemResponse> items = paginatedData != null ? paginatedData.getItems() : null;
                        if (items != null) {
                            result.setValue(NetworkResult.success(items));
                        } else {
                            result.setValue(NetworkResult.empty());
                        }
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load wishlist items"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginatedData<WishlistItemResponse>>> call, Throwable t) {
                String message = t.getLocalizedMessage() != null ? t.getLocalizedMessage() : "Network error";
                result.setValue(NetworkResult.error(message));
            }
        });
    }

    public void getWishlistStatus(String productIds, MutableLiveData<NetworkResult<java.util.Map<String, Boolean>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getWishlistStatus(productIds).enqueue(new Callback<ApiResponse<java.util.Map<String, Boolean>>>() {
            @Override
            public void onResponse(Call<ApiResponse<java.util.Map<String, Boolean>>> call, Response<ApiResponse<java.util.Map<String, Boolean>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<java.util.Map<String, Boolean>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to get wishlist status"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<java.util.Map<String, Boolean>>> call, Throwable t) {
                String message = t.getLocalizedMessage() != null ? t.getLocalizedMessage() : "Network error";
                result.setValue(NetworkResult.error(message));
            }
        });
    }

    public void addToWishlist(String productId, MutableLiveData<NetworkResult<WishlistActionResponse>> result) {
        result.setValue(NetworkResult.loading());
        apiService.addToWishlist(new WishlistActionRequest(productId)).enqueue(new Callback<ApiResponse<WishlistActionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<WishlistActionResponse>> call, Response<ApiResponse<WishlistActionResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<WishlistActionResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to add to wishlist"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<WishlistActionResponse>> call, Throwable t) {
                String message = t.getLocalizedMessage() != null ? t.getLocalizedMessage() : "Network error";
                result.setValue(NetworkResult.error(message));
            }
        });
    }

    public void removeFromWishlist(String productId, MutableLiveData<NetworkResult<Void>> result) {
        result.setValue(NetworkResult.loading());
        apiService.removeFromWishlist(productId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    result.setValue(NetworkResult.success(null));
                } else {
                    result.setValue(NetworkResult.error("Failed to remove from wishlist"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                String message = t.getLocalizedMessage() != null ? t.getLocalizedMessage() : "Network error";
                result.setValue(NetworkResult.error(message));
            }
        });
    }

    public void bulkDeleteWishlistItems(List<String> itemIds, MutableLiveData<NetworkResult<Object>> result) {
        result.setValue(NetworkResult.loading());
        apiService.bulkDeleteWishlistItems(new BulkDeleteRequest(itemIds)).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to bulk delete"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                String message = t.getLocalizedMessage() != null ? t.getLocalizedMessage() : "Network error";
                result.setValue(NetworkResult.error(message));
            }
        });
    }

    public void clearWishlist(MutableLiveData<NetworkResult<Object>> result) {
        result.setValue(NetworkResult.loading());
        apiService.clearWishlist().enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to clear wishlist"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                String message = t.getLocalizedMessage() != null ? t.getLocalizedMessage() : "Network error";
                result.setValue(NetworkResult.error(message));
            }
        });
    }
}
