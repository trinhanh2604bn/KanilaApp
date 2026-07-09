package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.model.checkout.CheckoutSessionDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutRepository {
    private final ApiService apiService;

    public CheckoutRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void prepareCheckout(MutableLiveData<NetworkResult<CheckoutSessionDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.prepareCheckout().enqueue(new Callback<ApiResponse<CheckoutSessionDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CheckoutSessionDto>> call, Response<ApiResponse<CheckoutSessionDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CheckoutSessionDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to prepare checkout"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CheckoutSessionDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void prepareGuestCheckout(MutableLiveData<NetworkResult<CheckoutSessionDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.prepareGuestCheckout().enqueue(new Callback<ApiResponse<CheckoutSessionDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CheckoutSessionDto>> call, Response<ApiResponse<CheckoutSessionDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CheckoutSessionDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to prepare guest checkout"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CheckoutSessionDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void createBuyNowSession(String productId, String variantId, int quantity, MutableLiveData<NetworkResult<CheckoutSessionDto>> result) {
        result.setValue(NetworkResult.loading());
        com.example.frontend.data.model.cart.AddToCartRequest request = new com.example.frontend.data.model.cart.AddToCartRequest(productId, variantId, quantity);
        apiService.createBuyNowSession(request).enqueue(new Callback<ApiResponse<CheckoutSessionDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CheckoutSessionDto>> call, Response<ApiResponse<CheckoutSessionDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CheckoutSessionDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to create buy now session"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CheckoutSessionDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
