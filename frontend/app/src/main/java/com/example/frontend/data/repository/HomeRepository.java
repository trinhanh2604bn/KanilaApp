package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.model.Product;
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
        apiService.getProducts(query, null, null).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        if (apiResponse.getData() == null || apiResponse.getData().isEmpty()) {
                            result.setValue(NetworkResult.empty());
                        } else {
                            result.setValue(NetworkResult.success(apiResponse.getData()));
                        }
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getError()));
                    }
                } else if (response.code() == 401) {
                    result.setValue(NetworkResult.unauthorized());
                } else {
                    result.setValue(NetworkResult.error("Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getLocalizedMessage()));
            }
        });
    }

    public void getHomepageRecommendations(MutableLiveData<NetworkResult<List<Product>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getHomepageRecommendations().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        if (apiResponse.getData() == null || apiResponse.getData().isEmpty()) {
                            result.setValue(NetworkResult.empty());
                        } else {
                            result.setValue(NetworkResult.success(apiResponse.getData()));
                        }
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getError()));
                    }
                } else if (response.code() == 401) {
                    result.setValue(NetworkResult.unauthorized());
                } else {
                    result.setValue(NetworkResult.error("Error: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getLocalizedMessage()));
            }
        });
    }
}
