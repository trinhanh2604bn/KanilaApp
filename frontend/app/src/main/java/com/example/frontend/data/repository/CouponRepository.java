package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.model.coupon.CouponDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponRepository {
    private final ApiService apiService;

    public CouponRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getMyCoupons(MutableLiveData<NetworkResult<List<CouponDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getMyCoupons().enqueue(new Callback<ApiResponse<List<CouponDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CouponDto>>> call, Response<ApiResponse<List<CouponDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<CouponDto>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load coupons"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CouponDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getAvailableCoupons(MutableLiveData<NetworkResult<List<CouponDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getAvailableCoupons().enqueue(new Callback<ApiResponse<List<CouponDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CouponDto>>> call, Response<ApiResponse<List<CouponDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<CouponDto>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load available coupons"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CouponDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
