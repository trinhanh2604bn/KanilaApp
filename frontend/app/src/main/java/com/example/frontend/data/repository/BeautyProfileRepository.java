package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.model.beauty.BeautyReferenceDto;
import com.example.frontend.data.model.beauty.CustomerBeautyProfileDto;
import com.example.frontend.data.remote.TokenManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeautyProfileRepository {
    private final ApiService apiService;
    private final TokenManager tokenManager;

    public BeautyProfileRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
        this.tokenManager = TokenManager.getInstance(context);
    }

    public void getBeautyProfile(String customerId, MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getBeautyProfile(customerId).enqueue(new Callback<ApiResponse<CustomerBeautyProfileDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CustomerBeautyProfileDto>> call, Response<ApiResponse<CustomerBeautyProfileDto>> response) {
                handleProfileResponse(response, result);
            }

            @Override
            public void onFailure(Call<ApiResponse<CustomerBeautyProfileDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getMyBeautyProfile(MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getMyBeautyProfile().enqueue(new Callback<ApiResponse<CustomerBeautyProfileDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CustomerBeautyProfileDto>> call, Response<ApiResponse<CustomerBeautyProfileDto>> response) {
                handleProfileResponse(response, result);
            }

            @Override
            public void onFailure(Call<ApiResponse<CustomerBeautyProfileDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    private void handleProfileResponse(Response<ApiResponse<CustomerBeautyProfileDto>> response, MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> result) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<CustomerBeautyProfileDto> apiResponse = response.body();
            if (apiResponse.isSuccess()) {
                CustomerBeautyProfileDto data = apiResponse.getData();
                if (data != null && data.getCustomerId() != null) {
                    tokenManager.saveCustomerId(data.getCustomerId());
                }
                result.setValue(NetworkResult.success(data));
            } else {
                result.setValue(NetworkResult.error(apiResponse.getMessage()));
            }
        } else {
            result.setValue(NetworkResult.error("Failed to load beauty profile (" + response.code() + ")"));
        }
    }

    public void updateMyBeautyProfile(com.example.frontend.feature.beauty.UpdateBeautyProfileRequest request, MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.updateMyBeautyProfile(request).enqueue(new Callback<ApiResponse<CustomerBeautyProfileDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CustomerBeautyProfileDto>> call, Response<ApiResponse<CustomerBeautyProfileDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccess(response.body(), result);
                } else if (response.code() == 404) {
                    createMyBeautyProfile(request, result);
                } else {
                    handleError(response, result);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CustomerBeautyProfileDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void createMyBeautyProfile(com.example.frontend.feature.beauty.UpdateBeautyProfileRequest request, MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> result) {
        apiService.createMyBeautyProfile(request).enqueue(new Callback<ApiResponse<CustomerBeautyProfileDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CustomerBeautyProfileDto>> call, Response<ApiResponse<CustomerBeautyProfileDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccess(response.body(), result);
                } else {
                    handleError(response, result);
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

    public void getBeautyReferencesByGroup(String group, MutableLiveData<NetworkResult<List<BeautyReferenceDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getBeautyReferencesByGroup(group).enqueue(new Callback<ApiResponse<List<BeautyReferenceDto>>>() {
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
                    result.setValue(NetworkResult.error("Failed to load beauty references for group: " + group));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BeautyReferenceDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void updateBeautyProfile(String customerId, com.example.frontend.feature.beauty.UpdateBeautyProfileRequest request, MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.updateBeautyProfile(customerId, request).enqueue(new Callback<ApiResponse<CustomerBeautyProfileDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CustomerBeautyProfileDto>> call, Response<ApiResponse<CustomerBeautyProfileDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccess(response.body(), result);
                } else if (response.code() == 404) {
                    // Nếu không tìm thấy profile, thử tạo mới bằng POST
                    createBeautyProfile(customerId, request, result);
                } else {
                    handleError(response, result);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CustomerBeautyProfileDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void createBeautyProfile(String customerId, com.example.frontend.feature.beauty.UpdateBeautyProfileRequest request, MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> result) {
        apiService.createBeautyProfile(customerId, request).enqueue(new Callback<ApiResponse<CustomerBeautyProfileDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<CustomerBeautyProfileDto>> call, Response<ApiResponse<CustomerBeautyProfileDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleSuccess(response.body(), result);
                } else {
                    handleError(response, result);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CustomerBeautyProfileDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    private void handleSuccess(ApiResponse<CustomerBeautyProfileDto> apiResponse, MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> result) {
        if (apiResponse.isSuccess()) {
            CustomerBeautyProfileDto data = apiResponse.getData();
            if (data != null && data.getCustomerId() != null) {
                tokenManager.saveCustomerId(data.getCustomerId());
            }
            result.setValue(NetworkResult.success(data));
        } else {
            result.setValue(NetworkResult.error(apiResponse.getMessage()));
        }
    }

    private void handleError(Response<?> response, MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> result) {
        String errorMsg = "Lỗi hệ thống (" + response.code() + ")";
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                if (errorJson.contains("message")) {
                    errorMsg = errorJson; // Trả về nguyên văn JSON lỗi để Fragment hiển thị
                }
            }
        } catch (Exception ignored) {}
        result.setValue(NetworkResult.error(errorMsg));
    }
}
