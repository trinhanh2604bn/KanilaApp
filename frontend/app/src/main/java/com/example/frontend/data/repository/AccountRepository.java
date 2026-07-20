package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.data.model.account.ProfileHubDto;
import com.example.frontend.data.model.address.AddressDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountRepository {
    private final ApiService apiService;
    private final TokenManager tokenManager;

    public AccountRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
        this.tokenManager = TokenManager.getInstance(context);
    }

    public void getProfileHub(MutableLiveData<NetworkResult<ProfileHubDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getProfileHub().enqueue(new Callback<ApiResponse<ProfileHubDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProfileHubDto>> call, Response<ApiResponse<ProfileHubDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProfileHubDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        ProfileHubDto data = apiResponse.getData();
                        // Persist customerId so Fragments can use the real ID instead of "me"
                        if (data != null && data.getProfile() != null
                                && data.getProfile().getCustomerId() != null) {
                            tokenManager.saveCustomerId(data.getProfile().getCustomerId());
                        }
                        result.setValue(NetworkResult.success(data));
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

    public void getAccountAddresses(MutableLiveData<NetworkResult<List<AddressDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getAccountAddresses().enqueue(new Callback<ApiResponse<List<AddressDto>>>() {
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
                    result.setValue(NetworkResult.error("Failed to load account addresses"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<AddressDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void addAccountAddress(java.util.Map<String, Object> body, MutableLiveData<NetworkResult<AddressDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.addAccountAddress(body).enqueue(new Callback<ApiResponse<AddressDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<AddressDto>> call, Response<ApiResponse<AddressDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AddressDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to add account address"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AddressDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void updateAccountAddress(String id, java.util.Map<String, Object> body,
                                     MutableLiveData<NetworkResult<AddressDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.updateAccountAddress(id, body).enqueue(new Callback<ApiResponse<AddressDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<AddressDto>> call, Response<ApiResponse<AddressDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.setValue(NetworkResult.error("Cập nhật thất bại"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AddressDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void deleteAccountAddress(String id, MutableLiveData<NetworkResult<Object>> result) {
        result.setValue(NetworkResult.loading());
        apiService.deleteAccountAddress(id).enqueue(new Callback<ApiResponse<Object>>() {
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
                    result.setValue(NetworkResult.error("Failed to delete account address"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void setDefaultAccountAddress(String id, MutableLiveData<NetworkResult<AddressDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.setDefaultAccountAddress(id).enqueue(new Callback<ApiResponse<AddressDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<AddressDto>> call, Response<ApiResponse<AddressDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AddressDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to set default account address"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AddressDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
