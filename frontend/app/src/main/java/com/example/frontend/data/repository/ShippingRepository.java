package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.shipping.ShippingMethodDto;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShippingRepository {
    private final ApiService apiService;

    public ShippingRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getShippingMethods(MutableLiveData<NetworkResult<List<ShippingMethodDto>>> result) {
        result.postValue(NetworkResult.loading());
        apiService.getShippingMethods().enqueue(new Callback<ApiResponse<List<ShippingMethodDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ShippingMethodDto>>> call, Response<ApiResponse<List<ShippingMethodDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.postValue(NetworkResult.success(response.body().getData()));
                } else {
                    result.postValue(NetworkResult.error("Failed to load shipping methods"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ShippingMethodDto>>> call, Throwable t) {
                result.postValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
