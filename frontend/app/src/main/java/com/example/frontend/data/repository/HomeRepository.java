package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.model.Product;
import com.example.frontend.data.model.common.PaginatedData;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {
    private final ApiService apiService;

    public HomeRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getProducts(String query, MutableLiveData<NetworkResult<List<Product>>> result) {
        result.setValue(NetworkResult.loading());
        // Ensure query is not passed as "null" string if it's null
        String q = (query != null && !query.isEmpty()) ? query : null;
        apiService.getProducts(q, null, null).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<Product> items = apiResponse.getData();
                        if (items == null || items.isEmpty()) {
                            result.setValue(NetworkResult.empty());
                        } else {
                            result.setValue(NetworkResult.success(items));
                        }
                    } else {
                        String errorMsg = apiResponse.getError() != null ? apiResponse.getError() : apiResponse.getMessage();
                        result.setValue(NetworkResult.error(errorMsg != null ? errorMsg : "Unknown error"));
                    }
                } else if (response.code() == 401) {
                    result.setValue(NetworkResult.unauthorized());
                } else {
                    result.setValue(NetworkResult.error("Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                String message = t.getLocalizedMessage() != null ? t.getLocalizedMessage() : "Network error";
                result.setValue(NetworkResult.error(message));
            }
        });
    }

    public void getHomepageRecommendations(MutableLiveData<NetworkResult<List<Product>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getHomepageRecommendations().enqueue(new Callback<ApiResponse<PaginatedData<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<PaginatedData<Product>>> call, Response<ApiResponse<PaginatedData<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PaginatedData<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        PaginatedData<Product> paginatedData = apiResponse.getData();
                        List<Product> items = paginatedData != null ? paginatedData.getItems() : null;
                        if (items == null || items.isEmpty()) {
                            result.setValue(NetworkResult.empty());
                        } else {
                            result.setValue(NetworkResult.success(items));
                        }
                    } else {
                        String errorMsg = apiResponse.getError() != null ? apiResponse.getError() : apiResponse.getMessage();
                        result.setValue(NetworkResult.error(errorMsg != null ? errorMsg : "Unknown error"));
                    }
                } else if (response.code() == 401) {
                    result.setValue(NetworkResult.unauthorized());
                } else {
                    result.setValue(NetworkResult.error("Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PaginatedData<Product>>> call, Throwable t) {
                String message = t.getLocalizedMessage() != null ? t.getLocalizedMessage() : "Network error";
                result.setValue(NetworkResult.error(message));
            }
        });
    }
}
