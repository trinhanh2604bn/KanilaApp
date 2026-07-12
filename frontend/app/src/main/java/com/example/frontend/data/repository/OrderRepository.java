package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.model.order.OrderDto;
import com.example.frontend.data.model.order.OrderDetailDto;
import com.example.frontend.data.model.order.OrderSummaryDto;
import com.example.frontend.data.model.common.PaginatedData;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.example.frontend.data.model.returnrefund.ReturnRefundRequestDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {
    private final ApiService apiService;

    public OrderRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getOrderDetail(String orderId, MutableLiveData<NetworkResult<OrderDetailDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getMyOrderDetail(orderId).enqueue(new Callback<ApiResponse<OrderDetailDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderDetailDto>> call, Response<ApiResponse<OrderDetailDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<OrderDetailDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load order details"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderDetailDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void reorderOrder(String orderId, MutableLiveData<NetworkResult<OrderSummaryDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.reorderMyOrder(orderId).enqueue(new Callback<ApiResponse<OrderSummaryDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderSummaryDto>> call, Response<ApiResponse<OrderSummaryDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<OrderSummaryDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to reorder"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderSummaryDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getOrderReviewItems(String orderId, MutableLiveData<NetworkResult<com.example.frontend.data.model.order.ReviewOrderItemsDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getOrderReviewItems(orderId).enqueue(new Callback<ApiResponse<com.example.frontend.data.model.order.ReviewOrderItemsDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.example.frontend.data.model.order.ReviewOrderItemsDto>> call, Response<ApiResponse<com.example.frontend.data.model.order.ReviewOrderItemsDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<com.example.frontend.data.model.order.ReviewOrderItemsDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load review items"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.example.frontend.data.model.order.ReviewOrderItemsDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getOrderByCode(String orderCode, MutableLiveData<NetworkResult<OrderDetailDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getOrderByCode(orderCode).enqueue(new Callback<ApiResponse<OrderDetailDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderDetailDto>> call, Response<ApiResponse<OrderDetailDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<OrderDetailDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load order by code"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderDetailDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void cancelOrder(String orderId, String reason, MutableLiveData<NetworkResult<OrderSummaryDto>> result) {
        result.setValue(NetworkResult.loading());
        Map<String, String> body = new HashMap<>();
        body.put("reason", reason);
        apiService.cancelMyOrder(orderId, body).enqueue(new Callback<ApiResponse<OrderSummaryDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderSummaryDto>> call, Response<ApiResponse<OrderSummaryDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<OrderSummaryDto> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to cancel order"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<OrderSummaryDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void submitReturnRefund(String orderId, ReturnRefundRequestDto request, MutableLiveData<NetworkResult<Object>> result) {
        result.setValue(NetworkResult.loading());
        apiService.submitReturnRefund(orderId, request).enqueue(new Callback<ApiResponse<Object>>() {
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
                    result.setValue(NetworkResult.error("Failed to submit return request"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void cancelReturnRequest(String orderId, MutableLiveData<NetworkResult<Object>> result) {
        result.setValue(NetworkResult.loading());
        apiService.cancelReturnRequest(orderId).enqueue(new Callback<ApiResponse<Object>>() {
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
                    result.setValue(NetworkResult.error("Failed to cancel return request"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getMyOrders(String status, Integer page, MutableLiveData<NetworkResult<List<OrderSummaryDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getMyOrders(status, page).enqueue(new Callback<ApiResponse<List<OrderSummaryDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<OrderSummaryDto>>> call, Response<ApiResponse<List<OrderSummaryDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<OrderSummaryDto>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<OrderSummaryDto> items = apiResponse.getData();
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
            public void onFailure(Call<ApiResponse<List<OrderSummaryDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
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
