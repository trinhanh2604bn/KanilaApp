package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.model.address.AddressDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressRepository {
    private final ApiService apiService;

    public AddressRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getCustomerAddresses(MutableLiveData<NetworkResult<List<AddressDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getMyAddresses().enqueue(new Callback<ApiResponse<List<AddressDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<AddressDto>>> call, Response<ApiResponse<List<AddressDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<AddressDto>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<AddressDto> data = apiResponse.getData();
                        if (data == null || data.isEmpty()) {
                            result.setValue(NetworkResult.empty());
                        } else {
                            result.setValue(NetworkResult.success(data));
                        }
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Unable to load addresses"));
                    }
                } else if (response.code() == 401) {
                    result.setValue(NetworkResult.unauthorized());
                } else {
                    String message = "Failed to load addresses";
                    if (response.errorBody() != null) {
                        try {
                            message = response.message();
                        } catch (Exception ignored) {
                            // Keep the default message.
                        }
                    }
                    result.setValue(NetworkResult.error(message));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<AddressDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void updateAddress(String id, Object addressData, MutableLiveData<NetworkResult<AddressDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.updateAddress(id, addressData).enqueue(new Callback<ApiResponse<AddressDto>>() {
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
                    result.setValue(NetworkResult.error("Failed to update address"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AddressDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void addAddress(Object addressData, MutableLiveData<NetworkResult<AddressDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.addAddress(addressData).enqueue(new Callback<ApiResponse<AddressDto>>() {
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
                    result.setValue(NetworkResult.error("Failed to add address"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AddressDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void setDefaultAddress(String id, MutableLiveData<NetworkResult<AddressDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.setDefaultAddress(id).enqueue(new Callback<ApiResponse<AddressDto>>() {
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
                    result.setValue(NetworkResult.error("Failed to set default address"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AddressDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
