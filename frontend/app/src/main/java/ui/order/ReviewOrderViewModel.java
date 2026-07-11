package ui.order;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.order.ReviewOrderItemsDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.OrderRepository;

public class ReviewOrderViewModel extends AndroidViewModel {
    private final OrderRepository repository;
    private final MutableLiveData<NetworkResult<ReviewOrderItemsDto>> reviewItems = new MutableLiveData<>();

    public ReviewOrderViewModel(@NonNull Application application) {
        super(application);
        this.repository = new OrderRepository(application);
    }

    public LiveData<NetworkResult<ReviewOrderItemsDto>> getReviewItems() {
        return reviewItems;
    }

    public void loadReviewItems(String orderId) {
        repository.getOrderReviewItems(orderId, reviewItems);
    }
}
