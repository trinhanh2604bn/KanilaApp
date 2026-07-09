package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.payment.PaymentMethodDto;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentRepository {
    private final ApiService apiService;

    public PaymentRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getPaymentMethods(MutableLiveData<NetworkResult<List<PaymentMethodDto>>> resultLiveData) {
        resultLiveData.postValue(NetworkResult.loading());
        apiService.getPaymentMethods().enqueue(new Callback<ApiResponse<List<PaymentMethodDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PaymentMethodDto>>> call, Response<ApiResponse<List<PaymentMethodDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        resultLiveData.postValue(NetworkResult.success(response.body().getData()));
                    } else {
                        resultLiveData.postValue(NetworkResult.error(response.body().getMessage()));
                    }
                } else {
                    resultLiveData.postValue(NetworkResult.error("Lỗi kết nối server"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PaymentMethodDto>>> call, Throwable t) {
                resultLiveData.postValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
