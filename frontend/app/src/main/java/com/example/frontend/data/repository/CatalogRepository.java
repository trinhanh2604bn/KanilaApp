package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.model.Brand;
import com.example.frontend.model.Category;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CatalogRepository {
    private final ApiService apiService;

    public CatalogRepository(Context context) {
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
                        List<Brand> data = apiResponse.getData();
                        if (data != null && !data.isEmpty()) {
                            List<Brand> activeBrands = new ArrayList<>();
                            for (Brand b : data) {
                                if (b.isActive() && "active".equals(b.getBrandStatus())) {
                                    activeBrands.add(b);
                                }
                            }
                            if (!activeBrands.isEmpty()) {
                                result.setValue(NetworkResult.success(activeBrands));
                            } else {
                                result.setValue(NetworkResult.empty());
                            }
                        } else {
                            result.setValue(NetworkResult.empty());
                        }
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load brands"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Brand>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<List<Category>>> getCategories() {
        MutableLiveData<NetworkResult<List<Category>>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Category>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<Category> data = apiResponse.getData();
                        if (data != null && !data.isEmpty()) {
                            result.setValue(NetworkResult.success(data));
                        } else {
                            result.setValue(NetworkResult.empty());
                        }
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load categories"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });

        return result;
    }
}
