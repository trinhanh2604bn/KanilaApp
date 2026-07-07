package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.model.order.OrderDto;
import com.example.frontend.data.model.common.PaginatedData;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private final ApiService apiService;

    public OrderRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getMyOrders(MutableLiveData<NetworkResult<List<OrderDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getMyOrders().enqueue(new Callback<ApiResponse<List<OrderDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<OrderDto>>> call, Response<ApiResponse<List<OrderDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<OrderDto>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<OrderDto> items = apiResponse.getData();
                        if (items != null && !items.isEmpty()) {
                            result.setValue(NetworkResult.success(items));
                        } else {
                            result.setValue(NetworkResult.empty());
                        }
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load orders"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<OrderDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
