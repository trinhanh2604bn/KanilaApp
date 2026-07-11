package ui.order;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.order.OrderDetailDto;
import com.example.frontend.data.model.order.OrderSummaryDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.OrderRepository;
import com.example.frontend.data.repository.ProductRepository;
import com.example.frontend.model.Product;
import java.util.List;

public class OrderDetailViewModel extends AndroidViewModel {
    private final OrderRepository repository;
    private final ProductRepository productRepository;
    private final MutableLiveData<OrderDetailUiState> uiState = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<OrderDetailDto>> orderResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<OrderSummaryDto>> cancelResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<Product>>> recResult = new MutableLiveData<>();

    private OrderDetailDto currentOrder;
    private List<Product> currentRecs;

    public OrderDetailViewModel(@NonNull Application application) {
        super(application);
        this.repository = new OrderRepository(application);
        this.productRepository = new ProductRepository(application);

        orderResult.observeForever(result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    uiState.setValue(OrderDetailUiState.loading());
                    break;
                case SUCCESS:
                    currentOrder = result.data;
                    updateUiState();
                    break;
                case ERROR:
                    uiState.setValue(OrderDetailUiState.error(result.message));
                    break;
            }
        });

        recResult.observeForever(result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                currentRecs = result.data;
                updateUiState();
            }
        });

        cancelResult.observeForever(result -> {
            if (result == null) return;
            switch (result.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    uiState.setValue(OrderDetailUiState.cancelSuccess());
                    break;
                case ERROR:
                    uiState.setValue(OrderDetailUiState.error("Hủy đơn thất bại: " + result.message));
                    break;
            }
        });
    }

    private void updateUiState() {
        if (currentOrder != null) {
            uiState.setValue(OrderDetailUiState.success(currentOrder, currentRecs));
        }
    }

    public LiveData<OrderDetailUiState> getUiState() {
        return uiState;
    }

    public void loadOrderDetail(String orderId) {
        if (orderId == null || orderId.isEmpty()) return;

        // If it's a mock checkout placeholder, we might still be loading it or it might be a code
        if (orderId.startsWith("KNL")) {
            repository.getOrderByCode(orderId, orderResult);
        } else if (orderId.equals("mock_order_id")) {
             // Skip or handle as error if we don't have a real ID yet
             uiState.setValue(OrderDetailUiState.error("Đang khởi tạo đơn hàng..."));
        } else {
            repository.getOrderDetail(orderId, orderResult);
        }
        
        productRepository.getProducts("popular", null, null).observeForever(result -> {
            recResult.setValue(result);
        });
    }

    public void cancelOrder(String orderId, String reason) {
        repository.cancelOrder(orderId, reason, cancelResult);
    }

    public void cancelReturnRequest(String orderId) {
        repository.cancelReturnRequest(orderId, new MutableLiveData<com.example.frontend.data.remote.NetworkResult<Object>>() {
            @Override
            public void setValue(com.example.frontend.data.remote.NetworkResult<Object> value) {
                super.setValue(value);
                if (value.status == com.example.frontend.data.remote.NetworkResult.Status.SUCCESS) {
                    uiState.setValue(OrderDetailUiState.cancelSuccess());
                } else if (value.status == com.example.frontend.data.remote.NetworkResult.Status.ERROR) {
                    uiState.setValue(OrderDetailUiState.error(value.message));
                }
            }
        });
    }
}
