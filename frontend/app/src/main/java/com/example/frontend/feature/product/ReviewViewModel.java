package com.example.frontend.feature.product;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.review.ReviewDto;
import com.example.frontend.data.model.review.ReviewSummaryDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.ReviewRepository;
import java.util.List;
import java.util.Map;

public class ReviewViewModel extends AndroidViewModel {
    private final ReviewRepository repository;
    private final MutableLiveData<NetworkResult<List<ReviewDto>>> reviewsResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<ReviewSummaryDto>> summaryResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<com.example.frontend.data.model.review.ReviewVoteResponse>> voteResult = new MutableLiveData<>();

    public ReviewViewModel(@NonNull Application application) {
        super(application);
        repository = new ReviewRepository(application);
    }

    public LiveData<NetworkResult<List<ReviewDto>>> getReviewsResult() {
        return reviewsResult;
    }

    public LiveData<NetworkResult<ReviewSummaryDto>> getSummaryResult() {
        return summaryResult;
    }

    public LiveData<NetworkResult<com.example.frontend.data.model.review.ReviewVoteResponse>> getVoteResult() {
        return voteResult;
    }

    public void loadReviews(String productId, Map<String, String> query) {
        repository.getReviewsByProductId(productId, query, reviewsResult);
    }

    public void loadReviewSummary(String productId) {
        repository.getReviewSummary(productId, summaryResult);
    }

    public void toggleReviewVote(String reviewId) {
        repository.toggleReviewVote(reviewId, voteResult);
    }
}
