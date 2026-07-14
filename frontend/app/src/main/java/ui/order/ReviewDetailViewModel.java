package ui.order;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.review.MyReviewDto;
import com.example.frontend.data.repository.ReviewRepository;
import com.example.frontend.data.remote.NetworkResult;

public class ReviewDetailViewModel extends AndroidViewModel {
    private final ReviewRepository repository;
    private final MutableLiveData<NetworkResult<MyReviewDto>> reviewDetail = new MutableLiveData<>();

    public ReviewDetailViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ReviewRepository(application);
    }

    public LiveData<NetworkResult<MyReviewDto>> getReviewDetail() {
        return reviewDetail;
    }

    public void loadReviewDetail(String reviewId) {
        repository.getMyReviewDetail(reviewId, reviewDetail);
    }
}
