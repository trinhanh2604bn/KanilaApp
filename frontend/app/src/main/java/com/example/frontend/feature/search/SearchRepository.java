package com.example.frontend.feature.search;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.frontend.data.model.search.SearchResponse;
import com.example.frontend.data.model.search.SearchSuggestionResponse;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.SearchApi;
import com.example.frontend.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRepository {

    private final SearchApi searchApi;

    public SearchRepository(Context context) {
        searchApi = ApiClient.getClient(context).create(SearchApi.class);
    }

    // ─── Product search with full makeup filters ───────────────────────────────

    public Call<ApiResponse<SearchResponse>> searchProducts(
            String query,
            int page,
            int limit,
            String sort,
            String brandIds,
            String categoryIds,
            String minPrice,
            String maxPrice,
            String minRating,
            String finishTypes,
            String coverageLevels,
            String colorFamilies,
            String shadeCodes,
            Boolean inStock,
            Boolean onSale,
            Boolean arSupported,
            Boolean waterproof,
            Boolean longWear,
            MutableLiveData<NetworkResult<SearchResponse>> resultLiveData
    ) {
        Call<ApiResponse<SearchResponse>> call = searchApi.searchProducts(
                query, page, limit, sort,
                brandIds, categoryIds,
                minPrice, maxPrice, minRating,
                finishTypes, coverageLevels, colorFamilies,
                shadeCodes, inStock, onSale, arSupported,
                waterproof, longWear
        );

        call.enqueue(new Callback<ApiResponse<SearchResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SearchResponse>> call, Response<ApiResponse<SearchResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    SearchResponse data = response.body().getData();
                    if (data == null || data.items == null || data.items.isEmpty()) {
                        resultLiveData.setValue(NetworkResult.empty());
                    } else {
                        resultLiveData.setValue(NetworkResult.success(data));
                    }
                } else {
                    resultLiveData.setValue(NetworkResult.error("Không thể tải kết quả tìm kiếm"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SearchResponse>> call, Throwable t) {
                if (!call.isCanceled()) {
                    resultLiveData.setValue(NetworkResult.error("Lỗi kết nối mạng: " + t.getMessage()));
                }
            }
        });

        return call;
    }

    // ─── Suggestions ──────────────────────────────────────────────────────────

    public Call<ApiResponse<SearchSuggestionResponse>> getSuggestions(
            String query,
            int limit,
            MutableLiveData<NetworkResult<SearchSuggestionResponse>> resultLiveData
    ) {
        Call<ApiResponse<SearchSuggestionResponse>> call = searchApi.getSuggestions(query, limit);
        call.enqueue(new Callback<ApiResponse<SearchSuggestionResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SearchSuggestionResponse>> call, Response<ApiResponse<SearchSuggestionResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    resultLiveData.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    resultLiveData.setValue(NetworkResult.error("Không thể tải gợi ý"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SearchSuggestionResponse>> call, Throwable t) {
                if (!call.isCanceled()) {
                    resultLiveData.setValue(NetworkResult.error("Lỗi kết nối mạng"));
                }
            }
        });
        return call;
    }

    // ─── Scan (barcode / QR / SKU) ────────────────────────────────────────────

    public Call<ApiResponse<SearchResponse>> scanSearch(
            String value,
            MutableLiveData<NetworkResult<SearchResponse>> resultLiveData
    ) {
        Call<ApiResponse<SearchResponse>> call = searchApi.scanSearch(value);
        call.enqueue(new Callback<ApiResponse<SearchResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SearchResponse>> call, Response<ApiResponse<SearchResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    SearchResponse data = response.body().getData();
                    if (data == null || data.items == null || data.items.isEmpty()) {
                        resultLiveData.setValue(NetworkResult.empty());
                    } else {
                        resultLiveData.setValue(NetworkResult.success(data));
                    }
                } else {
                    resultLiveData.setValue(NetworkResult.error("Không tìm thấy sản phẩm"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SearchResponse>> call, Throwable t) {
                if (!call.isCanceled()) {
                    resultLiveData.setValue(NetworkResult.error("Lỗi kết nối mạng"));
                }
            }
        });
        return call;
    }

    // ─── Discovery ────────────────────────────────────────────────────────────

    public Call<ApiResponse<SearchResponse>> getDiscovery(
            MutableLiveData<NetworkResult<List<Product>>> resultLiveData
    ) {
        Call<ApiResponse<SearchResponse>> call = searchApi.getDiscovery();
        call.enqueue(new Callback<ApiResponse<SearchResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SearchResponse>> call, Response<ApiResponse<SearchResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    SearchResponse data = response.body().getData();
                    if (data != null && data.items != null && !data.items.isEmpty()) {
                        resultLiveData.setValue(NetworkResult.success(data.items));
                    } else {
                        resultLiveData.setValue(NetworkResult.empty());
                    }
                } else {
                    // Silent failure for discovery — just show empty
                    resultLiveData.setValue(NetworkResult.empty());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SearchResponse>> call, Throwable t) {
                if (!call.isCanceled()) {
                    resultLiveData.setValue(NetworkResult.empty());
                }
            }
        });
        return call;
    }
}
