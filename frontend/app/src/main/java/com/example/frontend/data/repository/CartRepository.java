package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.model.cart.CartDto;
import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.model.cart.UpdateCartItemRequest;
import com.example.frontend.data.remote.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {
    private final ApiService apiService;
    private final TokenManager tokenManager;

    public CartRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
        this.tokenManager = TokenManager.getInstance(context);
    }

    public void getCart(MutableLiveData<NetworkResult<CartDto>> result) {
        // If not logged in and no guest session yet, return empty cart immediately
        // to avoid 400 Bad Request from server (it doesn't know "me" yet)
        if (!tokenManager.isLoggedIn() && !tokenManager.hasGuestSession()) {
            result.setValue(NetworkResult.success(CartDto.createEmptyGuestCart()));
            return;
        }

        result.setValue(NetworkResult.loading());
        
        Call<ApiResponse<CartDto>> call;
        if (tokenManager.isLoggedIn()) {
            call = apiService.getMyCart();
        } else {
            call = apiService.getGuestCart();
        }

        call.enqueue(new Callback<ApiResponse<CartDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartDto>> call, Response<ApiResponse<CartDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    String errorMsg = "Failed to load cart";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " (" + response.code() + ")";
                        }
                    } catch (Exception ignored) {}
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartDto>> call, Throwable t) {
                result.setValue(NetworkResult.error("Network error: " + t.getMessage()));
            }
        });
    }

    public void mergeGuestCart(MutableLiveData<NetworkResult<CartDto>> result) {
        if (!tokenManager.isLoggedIn()) return;
        
        apiService.mergeGuestCart().enqueue(new Callback<ApiResponse<CartDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartDto>> call, Response<ApiResponse<CartDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Merge failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void updateItemQuantity(String itemId, int quantity, MutableLiveData<NetworkResult<CartDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.updateCartItemQuantity(itemId, new UpdateCartItemRequest(quantity, null)).enqueue(new Callback<ApiResponse<CartDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartDto>> call, Response<ApiResponse<CartDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    String errorMsg = "Failed to update quantity";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " (" + response.code() + ")";
                        }
                    } catch (Exception ignored) {}
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartDto>> call, Throwable t) {
                result.setValue(NetworkResult.error("Network error: " + t.getMessage()));
            }
        });
    }

    public void toggleItemSelection(String itemId, boolean selected, MutableLiveData<NetworkResult<CartDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.toggleCartItemSelection(itemId, new UpdateCartItemRequest(null, selected)).enqueue(new Callback<ApiResponse<CartDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartDto>> call, Response<ApiResponse<CartDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to toggle selection"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void removeItem(String itemId, MutableLiveData<NetworkResult<CartDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.removeFromCart(itemId).enqueue(new Callback<ApiResponse<CartDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartDto>> call, Response<ApiResponse<CartDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to remove item"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void addToCart(AddToCartRequest request, MutableLiveData<NetworkResult<CartDto>> result) {
        result.setValue(NetworkResult.loading());

        Call<ApiResponse<CartDto>> call;
        if (tokenManager.isLoggedIn()) {
            call = apiService.addToCart(request);
        } else {
            call = apiService.addToGuestCart(request);
        }

        call.enqueue(new Callback<ApiResponse<CartDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartDto>> call, Response<ApiResponse<CartDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        CartDto cart = apiResponse.getData();
                        if (cart != null && cart.getGuestSessionId() != null) {
                            tokenManager.saveGuestSession(cart.getGuestSessionId());
                        }
                        result.setValue(NetworkResult.success(cart));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    String errorMsg = "Failed to add to cart";
                    try {
                        if (response.errorBody() != null) {
                            String errorJson = response.errorBody().string();
                            // Optional: Parse errorJson if it follows ApiResponse format
                            errorMsg += ": " + response.code();
                        }
                    } catch (Exception ignored) {}
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
