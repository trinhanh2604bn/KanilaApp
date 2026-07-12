package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.review.ReviewDto;
import com.example.frontend.data.model.review.ReviewEligibilityDto;
import com.example.frontend.data.model.review.ReviewSummaryDto;
import com.example.frontend.data.model.review.SubmitReviewRequest;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRepository {
    private final ApiService apiService;

    public ReviewRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getReviewsByProductId(String productId, Map<String, String> query, MutableLiveData<NetworkResult<List<ReviewDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getReviewsByProductId(productId, query).enqueue(new Callback<ApiResponse<List<ReviewDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ReviewDto>>> call, Response<ApiResponse<List<ReviewDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to get reviews";
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ReviewDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getRandomReviewPreview(String productId, int limit, MutableLiveData<NetworkResult<List<ReviewDto>>> result) {
        java.util.Map<String, String> query = new java.util.HashMap<>();
        query.put("limit", String.valueOf(limit));
        query.put("sort", "random");
        getReviewsByProductId(productId, query, result);
    }

    public void getReviewSummary(String productId, MutableLiveData<NetworkResult<ReviewSummaryDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getReviewSummaryByProductId(productId).enqueue(new Callback<ApiResponse<ReviewSummaryDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ReviewSummaryDto>> call, Response<ApiResponse<ReviewSummaryDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to get summary";
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ReviewSummaryDto>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
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

    public void toggleReviewVote(String reviewId, MutableLiveData<NetworkResult<com.example.frontend.data.model.review.ReviewVoteResponse>> result) {
        result.setValue(NetworkResult.loading());
        apiService.toggleReviewVote(reviewId).enqueue(new Callback<ApiResponse<com.example.frontend.data.model.review.ReviewVoteResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.example.frontend.data.model.review.ReviewVoteResponse>> call, Response<ApiResponse<com.example.frontend.data.model.review.ReviewVoteResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    result.setValue(NetworkResult.success(response.body().getData()));
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Failed to vote";
                    result.setValue(NetworkResult.error(errorMsg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.example.frontend.data.model.review.ReviewVoteResponse>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
