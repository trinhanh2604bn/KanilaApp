package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.review.MyReviewDto;
import com.example.frontend.data.model.review.ReviewDto;
import com.example.frontend.data.model.review.ReviewEligibilityDto;
import com.example.frontend.data.model.review.SubmitReviewRequest;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRepository {
    private final ApiService apiService;

    public ReviewRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getReviewWriteEligibility(String orderItemId, MutableLiveData<NetworkResult<ReviewEligibilityDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getReviewWriteEligibility(orderItemId).enqueue(new Callback<ApiResponse<ReviewEligibilityDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReviewEligibilityDto>> call, Response<ApiResponse<ReviewEligibilityDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to get eligibility";
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReviewEligibilityDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void submitReview(SubmitReviewRequest request, MutableLiveData<NetworkResult<Object>> result) {
        result.setValue(NetworkResult.loading());
        apiService.submitReview(request).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to submit review";
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getMyReviews(MutableLiveData<NetworkResult<List<MyReviewDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getMyReviews().enqueue(new Callback<ApiResponse<List<MyReviewDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MyReviewDto>>> call, Response<ApiResponse<List<MyReviewDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to get reviews";
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MyReviewDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getMyReviewDetail(String reviewId, MutableLiveData<NetworkResult<MyReviewDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getMyReviewDetail(reviewId).enqueue(new Callback<ApiResponse<MyReviewDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<MyReviewDto>> call, Response<ApiResponse<MyReviewDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to get review detail";
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<MyReviewDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getReviewsByProductId(String productId, MutableLiveData<NetworkResult<List<ReviewDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getReviewsByProductId(productId).enqueue(new Callback<ApiResponse<List<ReviewDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReviewDto>>> call, Response<ApiResponse<List<ReviewDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to get product reviews";
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReviewDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
