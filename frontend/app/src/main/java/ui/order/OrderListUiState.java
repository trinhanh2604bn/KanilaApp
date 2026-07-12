package ui.order;

import com.example.frontend.data.model.order.OrderSummaryDto;
import java.util.List;

public class OrderListUiState {
    public final boolean loading;
    public final List<OrderSummaryDto> orders;
    public final String error;
    public final boolean empty;
    public final boolean reorderSuccess;

    public OrderListUiState(boolean loading, List<OrderSummaryDto> orders, String error, boolean empty, boolean reorderSuccess) {
        this.loading = loading;
        this.orders = orders;
        this.error = error;
        this.empty = empty;
        this.reorderSuccess = reorderSuccess;
    }

    public static OrderListUiState loading() {
        return new OrderListUiState(true, null, null, false, false);
    }

    public static OrderListUiState success(List<OrderSummaryDto> orders) {
        return new OrderListUiState(false, orders, null, false, false);
    }

    public static OrderListUiState error(String message) {
        return new OrderListUiState(false, null, message, false, false);
    }

    public static OrderListUiState empty() {
        return new OrderListUiState(false, null, null, true, false);
    }

    public static OrderListUiState reorderSuccess() {
        return new OrderListUiState(false, null, null, false, true);
    }
}
