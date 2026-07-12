package ui.order;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.order.OrderSummaryDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.OrderRepository;
import java.util.List;

public class OrderListViewModel extends AndroidViewModel {
    private final OrderRepository repository;
    private final MutableLiveData<OrderListUiState> uiState = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<OrderSummaryDto>>> orderResult = new MutableLiveData<>();

    public OrderListViewModel(@NonNull Application application) {
        super(application);
        this.repository = new OrderRepository(application);
        
        orderResult.observeForever(result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    uiState.setValue(OrderListUiState.loading());
                    break;
                case SUCCESS:
                    uiState.setValue(OrderListUiState.success(result.data));
                    break;
                case ERROR:
                    uiState.setValue(OrderListUiState.error(result.message));
                    break;
                case EMPTY:
                    uiState.setValue(OrderListUiState.empty());
                    break;
            }
        });
    }

    public LiveData<OrderListUiState> getUiState() {
        return uiState;
    }

    public void loadOrders(String status) {
        repository.getMyOrders(status, 1, orderResult);
    }

}
