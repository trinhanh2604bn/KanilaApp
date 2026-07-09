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
        // repository.getMyOrders(status, 1, orderResult);
        
        // Temporarily using Mock Data for development as requested
        loadMockOrders(status);
    }

    private void loadMockOrders(String status) {
        uiState.setValue(OrderListUiState.loading());
        
        // Simulation delay
        new android.os.Handler().postDelayed(() -> {
            List<OrderSummaryDto> mockOrders = new java.util.ArrayList<>();
            
            if (status == null || status.equals("pending")) {
                mockOrders.add(new OrderSummaryDto("1", "KNL2507001", "pending", 250000, 1, "Son kem lì Merzy The First Velvet Tint", "V6 Fire Rose"));
            }
            if (status == null || status.equals("confirmed")) {
                mockOrders.add(new OrderSummaryDto("2", "KNL2507002", "confirmed", 480000, 2, "Kem nền Maybelline Fit Me Matte", "120 Classic Ivory"));
            }
            if (status == null || status.equals("processing")) {
                mockOrders.add(new OrderSummaryDto("3", "KNL2507003", "processing", 155000, 1, "Bông tẩy trang Silcot Nhật Bản", "82 miếng"));
            }
            if (status == null || status.equals("completed")) {
                mockOrders.add(new OrderSummaryDto("4", "KNL2507004", "completed", 890000, 1, "Nước hoa hồng Lancôme Tonique Confort", "200ml"));
            }
            if (status == null || status.equals("returned")) {
                mockOrders.add(new OrderSummaryDto("5", "KNL2507005", "returned", 320000, 1, "Chì kẻ mày Innisfree Auto Eyebrow Pencil", "02 Dark Brown"));
            }
            if (status == null || status.equals("cancelled")) {
                mockOrders.add(new OrderSummaryDto("6", "KNL2507006", "cancelled", 1200000, 3, "Bộ dưỡng da trà xanh Innisfree Green Tea", "Set 3 món"));
            }

            if (mockOrders.isEmpty()) {
                uiState.setValue(OrderListUiState.empty());
            } else {
                uiState.setValue(OrderListUiState.success(mockOrders));
            }
        }, 800);
    }
}
