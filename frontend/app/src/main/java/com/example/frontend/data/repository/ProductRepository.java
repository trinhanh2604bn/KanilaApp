package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.model.Product;
import com.example.frontend.data.model.product.ProductVariantDto;
import com.example.frontend.data.model.product.ProductMediaDto;
import com.example.frontend.data.model.product.ProductDetailResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private final ApiService apiService;

    public ProductRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public LiveData<NetworkResult<ProductDetailResponse>> getProductDetail(String id) {
        MutableLiveData<NetworkResult<ProductDetailResponse>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getProductDetail(id).enqueue(new Callback<ApiResponse<ProductDetailResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductDetailResponse>> call, Response<ApiResponse<ProductDetailResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProductDetailResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Product detail not found"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductDetailResponse>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<List<Product>>> getProducts(String query, String categoryId, String brandId) {
        MutableLiveData<NetworkResult<List<Product>>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getProducts(query, categoryId, brandId).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<Product> data = apiResponse.getData();
                        if (data != null && !data.isEmpty()) {
                            result.setValue(NetworkResult.success(data));
                        } else {
                            result.setValue(NetworkResult.empty());
                        }
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load products"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });

        return result;
    }

    public LiveData<NetworkResult<Product>> getProductById(String id) {
        MutableLiveData<NetworkResult<Product>> result = new MutableLiveData<>();
        result.setValue(NetworkResult.loading());

        apiService.getProductById(id).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Product> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Product not found"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });

        return result;
    }

    public void getProductMedia(String productId, MutableLiveData<NetworkResult<List<ProductMediaDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getProductMedia(productId).enqueue(new Callback<ApiResponse<List<ProductMediaDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductMediaDto>>> call, Response<ApiResponse<List<ProductMediaDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<ProductMediaDto>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load product media"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ProductMediaDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getProductVariants(String productId, MutableLiveData<NetworkResult<List<ProductVariantDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getProductVariants(productId).enqueue(new Callback<ApiResponse<List<ProductVariantDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductVariantDto>>> call, Response<ApiResponse<List<ProductVariantDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<ProductVariantDto>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(apiResponse.getData()));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load product variants"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ProductVariantDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
