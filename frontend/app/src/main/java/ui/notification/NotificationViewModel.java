package ui.notification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Giữ danh sách master + filter hiện tại, expose danh sách ĐÃ LỌC qua LiveData.
 * Theo cùng kiểu tách state với HomeViewModel/HomeUiState.
 */
public class NotificationViewModel extends ViewModel {

    private final List<NotificationItem> masterList = new ArrayList<>();
    private NotificationType currentFilter = null; // null = Tất cả

    private final MutableLiveData<NotificationUiState> uiState = new MutableLiveData<>();

    public NotificationViewModel() {
        loadMockNotifications();
        applyFilter();
    }

    public LiveData<NotificationUiState> getUiState() {
        return uiState;
    }

    /**
     * Đặt filter loại thông báo. filter == null nghĩa là "Tất cả".
     */
    public void setFilter(NotificationType filter) {
        this.currentFilter = filter;
        applyFilter();
    }

    private void applyFilter() {
        List<NotificationItem> filtered = new ArrayList<>();
        for (NotificationItem item : masterList) {
            if (currentFilter == null || item.getType() == currentFilter) {
                filtered.add(item);
            }
        }

        NotificationUiState state = new NotificationUiState();
        state.filter = currentFilter;
        state.items = filtered;
        state.empty = filtered.isEmpty();
        uiState.setValue(state);
    }

    private void loadMockNotifications() {
        // TODO: thay bằng API khi backend sẵn sàng
        masterList.add(new NotificationItem(
                "1", NotificationType.ORDER,
                "Đơn #KN20458 đang giao",
                "Đơn hàng của bạn đang trên đường giao đến bạn.",
                "10:30", false, "KN20458"));

        masterList.add(new NotificationItem(
                "2", NotificationType.OFFER,
                "Bạn nhận voucher 100.000đ",
                "Voucher độc quyền cho đơn từ 500.000đ, dùng ngay hôm nay.",
                "09:15", false, "KANILA100K"));

        masterList.add(new NotificationItem(
                "3", NotificationType.COMMUNITY,
                "Bài review của bạn đạt 125 lượt thích",
                "Cảm ơn bạn đã chia sẻ trải nghiệm tuyệt vời!",
                "Hôm qua", false, "post_881"));

        masterList.add(new NotificationItem(
                "4", NotificationType.PERSONAL,
                "Chúc mừng bạn lên hạng Gold Member",
                "Nhận ngay những đặc quyền ưu tiên từ Kanila.",
                "Hôm qua", false, "loyalty_gold"));

        masterList.add(new NotificationItem(
                "5", NotificationType.ORDER,
                "Đơn #KN20390 đã giao thành công",
                "Đừng quên đánh giá sản phẩm để nhận điểm thưởng nhé!",
                "20/05", true, "KN20390"));

        masterList.add(new NotificationItem(
                "6", NotificationType.OFFER,
                "Flash Sale cuối tuần đã kết thúc",
                "Hẹn gặp lại bạn ở đợt ưu đãi tiếp theo.",
                "18/05", true, "sale_weekend"));

        masterList.add(new NotificationItem(
                "7", NotificationType.PERSONAL,
                "Skin Journey tuần 2 cần cập nhật",
                "Đừng quên cập nhật để theo dõi tiến trình da nhé!",
                "17/05", true, "journey_w2"));

        masterList.add(new NotificationItem(
                "8", NotificationType.COMMUNITY,
                "An vừa bình luận về bài viết của bạn",
                "\"Serum này mình cũng đang dùng, hợp da lắm!\"",
                "16/05", true, "post_881"));
    }
}
