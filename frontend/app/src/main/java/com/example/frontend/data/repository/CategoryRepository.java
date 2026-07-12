package com.example.frontend.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.frontend.data.model.category.CategoryDto;
import com.example.frontend.data.remote.ApiClient;
import android.content.Context;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {

    private final ApiService apiService;

    public CategoryRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public LiveData<NetworkResult<List<CategoryDto>>> getRootCategories() {
        MutableLiveData<NetworkResult<List<CategoryDto>>> result = new MutableLiveData<>();

        apiService.getRootCategories().enqueue(new Callback<ApiResponse<List<CategoryDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryDto>>> call,
                                   Response<ApiResponse<List<CategoryDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryDto> data = response.body().getData();

                    if (data == null || data.isEmpty()) {
                        result.setValue(NetworkResult.empty());
                    } else {
                        result.setValue(NetworkResult.success(data));
                    }
                } else {
                    result.setValue(NetworkResult.error("Không thể tải danh mục"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<List<CategoryDto>>> getChildCategories(String parentId) {
        MutableLiveData<NetworkResult<List<CategoryDto>>> result = new MutableLiveData<>();

        apiService.getChildCategories(parentId).enqueue(new Callback<ApiResponse<List<CategoryDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryDto>>> call,
                                   Response<ApiResponse<List<CategoryDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryDto> data = response.body().getData();

                    if (data == null || data.isEmpty()) {
                        result.setValue(NetworkResult.empty());
                    } else {
                        result.setValue(NetworkResult.success(data));
                    }
                } else {
                    result.setValue(NetworkResult.error("Không thể tải danh mục con"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });

        return result;
    }
}