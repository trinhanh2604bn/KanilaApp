package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.model.Brand;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BrandRepository {
    private final ApiService apiService;

    public BrandRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public LiveData<NetworkResult<List<Brand>>> getBrands() {
        MutableLiveData<NetworkResult<List<Brand>>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getBrands().enqueue(new Callback<ApiResponse<List<Brand>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Brand>>> call, Response<ApiResponse<List<Brand>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Brand>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Không thể tải danh sách thương hiệu"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Brand>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });

        return result;
    }
}
