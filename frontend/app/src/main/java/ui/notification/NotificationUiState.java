package ui.notification;

import java.util.ArrayList;
import java.util.List;

/**
 * UI state cho màn Notification Center. Theo cùng kiểu với HomeUiState:
 * là 1 POJO phẳng do ViewModel expose qua LiveData.
 */
public class NotificationUiState {

    /** Danh sách đã lọc theo filter hiện tại. */
    public List<NotificationItem> items = new ArrayList<>();

    /** true khi danh sách lọc ra rỗng -> hiển thị empty state. */
    public boolean empty = false;

    /** Filter đang áp dụng; null nghĩa là "Tất cả". */
    public NotificationType filter = null;

    public NotificationUiState() {
    }
}
