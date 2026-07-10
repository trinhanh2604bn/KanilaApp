package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.model.account.ProfileHubDto;
import com.example.frontend.data.model.address.AddressDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountRepository {
    private final ApiService apiService;

    public AccountRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getProfileHub(MutableLiveData<NetworkResult<ProfileHubDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getProfileHub().enqueue(new Callback<ApiResponse<ProfileHubDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileHubDto>> call, Response<ApiResponse<ProfileHubDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProfileHubDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load profile hub"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProfileHubDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getAddresses(MutableLiveData<NetworkResult<List<AddressDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getMyAddresses().enqueue(new Callback<ApiResponse<List<AddressDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<AddressDto>>> call, Response<ApiResponse<List<AddressDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<AddressDto>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load addresses"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<AddressDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void updateProfile(java.util.Map<String, Object> data, MutableLiveData<NetworkResult<ProfileHubDto.AccountInfo>> result) {
        result.setValue(NetworkResult.loading());
        apiService.patchMyProfile(data).enqueue(new Callback<ApiResponse<ProfileHubDto.AccountInfo>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileHubDto.AccountInfo>> call, Response<ApiResponse<ProfileHubDto.AccountInfo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProfileHubDto.AccountInfo> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to update profile"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProfileHubDto.AccountInfo>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
