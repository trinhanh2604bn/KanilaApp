package ui.order;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.review.ReviewEligibilityDto;
import com.example.frontend.data.model.review.SubmitReviewRequest;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.ReviewRepository;
import java.util.List;

public class ReviewWriteViewModel extends AndroidViewModel {
    private final ReviewRepository repository;
    private final MutableLiveData<NetworkResult<ReviewEligibilityDto>> eligibility = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<Object>> submitResult = new MutableLiveData<>();

    public ReviewWriteViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ReviewRepository(application);
    }

    public LiveData<NetworkResult<ReviewEligibilityDto>> getEligibility() {
        return eligibility;
    }

    public LiveData<NetworkResult<Object>> getSubmitResult() {
        return submitResult;
    }

    public void loadEligibility(String orderItemId) {
        repository.getReviewWriteEligibility(orderItemId, eligibility);
    }

    public void submitReview(String orderItemId, int rating, String reviewTitle, String reviewContent, List<String> reviewTags, List<String> skinTypes, List<String> mediaUrls) {
        SubmitReviewRequest request = new SubmitReviewRequest(orderItemId, rating, reviewTitle, reviewContent, reviewTags, skinTypes, mediaUrls);
        repository.submitReview(request, submitResult);
    }
}
