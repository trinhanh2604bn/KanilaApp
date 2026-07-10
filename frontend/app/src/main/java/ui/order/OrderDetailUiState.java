package ui.order;

import com.example.frontend.data.model.order.OrderDetailDto;
import com.example.frontend.model.Product;
import java.util.List;

public class OrderDetailUiState {
    public final boolean loading;
    public final OrderDetailDto order;
    public final List<Product> recommendations;
    public final String error;
    public final boolean cancelSuccess;

    public OrderDetailUiState(boolean loading, OrderDetailDto order, List<Product> recommendations, String error, boolean cancelSuccess) {
        this.loading = loading;
        this.order = order;
        this.recommendations = recommendations;
        this.error = error;
        this.cancelSuccess = cancelSuccess;
    }

    public static OrderDetailUiState loading() {
        return new OrderDetailUiState(true, null, null, null, false);
    }

    public static OrderDetailUiState success(OrderDetailDto order, List<Product> recommendations) {
        return new OrderDetailUiState(false, order, recommendations, null, false);
    }

    public static OrderDetailUiState error(String message) {
        return new OrderDetailUiState(false, null, null, message, false);
    }

    public static OrderDetailUiState cancelSuccess() {
        return new OrderDetailUiState(false, null, null, null, true);
    }
}
