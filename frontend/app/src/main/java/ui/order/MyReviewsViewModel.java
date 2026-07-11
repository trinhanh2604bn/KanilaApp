package ui.order;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.review.MyReviewDto;
import com.example.frontend.data.repository.ReviewRepository;
import com.example.frontend.data.remote.NetworkResult;
import java.util.List;

public class MyReviewsViewModel extends AndroidViewModel {
    private final ReviewRepository repository;
    private final MutableLiveData<NetworkResult<List<MyReviewDto>>> myReviews = new MutableLiveData<>();

    public MyReviewsViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ReviewRepository(application);
    }

    public LiveData<NetworkResult<List<MyReviewDto>>> getMyReviews() {
        return myReviews;
    }

    public void loadMyReviews() {
        repository.getMyReviews(myReviews);
    }
}
