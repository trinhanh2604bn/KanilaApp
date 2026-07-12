package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.recommendation.RecommendationData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendationRepository {
    private final ApiService apiService;

    public RecommendationRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getHomepageRecommendations(MutableLiveData<NetworkResult<RecommendationData>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getHomepageRecommendations().enqueue(new Callback<ApiResponse<RecommendationData>>() {
            @Override
            public void onResponse(Call<ApiResponse<RecommendationData>> call, Response<ApiResponse<RecommendationData>> response) {
                handleResponse(response, result);
            }

            @Override
            public void onFailure(Call<ApiResponse<RecommendationData>> call, Throwable t) {
                String message = t.getLocalizedMessage() != null ? t.getLocalizedMessage() : "Network error";
                result.setValue(NetworkResult.error(message));
            }
        });
    }

    public void getMyRecommendations(MutableLiveData<NetworkResult<RecommendationData>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getMyRecommendations().enqueue(new Callback<ApiResponse<RecommendationData>>() {
            @Override
            public void onResponse(Call<ApiResponse<RecommendationData>> call, Response<ApiResponse<RecommendationData>> response) {
                handleResponse(response, result);
            }

            @Override
            public void onFailure(Call<ApiResponse<RecommendationData>> call, Throwable t) {
                String message = t.getLocalizedMessage() != null ? t.getLocalizedMessage() : "Network error";
                result.setValue(NetworkResult.error(message));
            }
        });
    }

    private void handleResponse(Response<ApiResponse<RecommendationData>> response, MutableLiveData<NetworkResult<RecommendationData>> result) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<RecommendationData> apiResponse = response.body();
            if (apiResponse.isSuccess()) {
                RecommendationData data = apiResponse.getData();
                if (data == null || data.getProducts() == null || data.getProducts().isEmpty()) {
                    result.setValue(NetworkResult.empty());
                } else {
                    result.setValue(NetworkResult.success(data));
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
}
