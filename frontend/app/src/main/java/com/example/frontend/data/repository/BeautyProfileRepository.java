package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.model.beauty.BeautyReferenceDto;
import com.example.frontend.data.model.beauty.CustomerBeautyProfileDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeautyProfileRepository {
    private final ApiService apiService;

    public BeautyProfileRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getBeautyProfile(String customerId, MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getBeautyProfile(customerId).enqueue(new Callback<ApiResponse<CustomerBeautyProfileDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CustomerBeautyProfileDto>> call, Response<ApiResponse<CustomerBeautyProfileDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CustomerBeautyProfileDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load beauty profile"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CustomerBeautyProfileDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getBeautyReferences(MutableLiveData<NetworkResult<List<BeautyReferenceDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getBeautyReferences().enqueue(new Callback<ApiResponse<List<BeautyReferenceDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BeautyReferenceDto>>> call, Response<ApiResponse<List<BeautyReferenceDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<BeautyReferenceDto>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load beauty references"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BeautyReferenceDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
