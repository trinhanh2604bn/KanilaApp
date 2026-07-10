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
        // repository.getOrderDetail(orderId, orderResult);
        
        // Temporarily using Mock Data for development
        loadMockOrderDetail(orderId);
        
        productRepository.getProducts("popular", null, null).observeForever(result -> {
            recResult.setValue(result);
        });
    }

    private void loadMockOrderDetail(String orderId) {
        uiState.setValue(OrderDetailUiState.loading());
        
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            OrderDetailDto mock = createMockData(orderId);
            uiState.setValue(OrderDetailUiState.success(mock, currentRecs));
        }, 600);
    }

    private OrderDetailDto createMockData(String id) {
        // Khớp với dữ liệu từ OrderListViewModel
        String status = "confirmed";
        if ("1".equals(id)) status = "pending";
        else if ("2".equals(id)) status = "confirmed";
        else if ("3".equals(id)) status = "processing";
        else if ("4".equals(id)) status = "completed";
        else if ("5".equals(id)) status = "returned";
        else if ("6".equals(id)) status = "cancelled";

        OrderDetailDto mock = new OrderDetailDto(
            id,
            "KNL250" + (id != null ? id : "7002"),
            status,
            "Thanh toán khi nhận hàng",
            "10-07-2025 14:30"
        );
        
        mock.addItem("Son kem lì Merzy The First Velvet Tint", "V6 Fire Rose", 1, 250000);
        
        // Thêm thông tin đặc thù
        if ("cancelled".equals(status)) {
            mock.setPaymentStatus("Chưa thanh toán");
        } else if ("returned".equals(status)) {
            mock.setPaymentStatus("Đã hoàn tiền vào ví");
        }

        mock.setShippingAddress("Nguyễn Hoàng Gia Bảo", "(+84) 377 211 334", "Cổng Sau Ktx Khu B, Phường Đông Hòa", "Thành phố Dĩ An", "Tỉnh Bình Dương");
        mock.setTotal(250000);
        
        return mock;
    }

    public void cancelOrder(String orderId, String reason) {
        repository.cancelOrder(orderId, reason, cancelResult);
    }
}
