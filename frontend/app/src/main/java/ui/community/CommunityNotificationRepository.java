package ui.community;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.List;

public class CommunityNotificationRepository {
    private static CommunityNotificationRepository instance;
    private final MutableLiveData<List<CommunityNotification>> notifications = new MutableLiveData<>();
    private final MutableLiveData<Integer> unreadCount = new MutableLiveData<>();

    private CommunityNotificationRepository() {
        loadMockNotifications();
    }

    public static synchronized CommunityNotificationRepository getInstance() {
        if (instance == null) {
            instance = new CommunityNotificationRepository();
        }
        return instance;
    }

    private void loadMockNotifications() {
        List<CommunityNotification> list = new ArrayList<>();
        list.add(new CommunityNotification("1", "REWARD", "Chúc mừng!", "Bạn vừa nhận được 50 điểm thưởng từ bài viết Niacinamide.", "2 giờ trước", "POST", "1", false));
        list.add(new CommunityNotification("2", "CHALLENGE_EXPIRING", "Thử thách sắp kết thúc", "Thử thách 'Da sáng đón Tết' chỉ còn 24 giờ. Hãy nhanh tay tham gia!", "5 giờ trước", "CHALLENGE", "101", false));
        list.add(new CommunityNotification("3", "CHALLENGE_REMINDER", "Cập nhật tiến độ", "Đừng quên đăng ảnh cập nhật ngày thứ 7 của thử thách nhé.", "1 ngày trước", "CHALLENGE", "101", true));
        list.add(new CommunityNotification("4", "LIKE", "Yêu thích mới", "Linh Nguyễn đã thích bài viết của bạn.", "2 ngày trước", "POST", "1", true));
        
        notifications.setValue(list);
        updateUnreadCount(list);
    }

    public LiveData<List<CommunityNotification>> getNotifications() {
        return notifications;
    }

    public LiveData<Integer> getUnreadCount() {
        return unreadCount;
    }

    public void markAsRead(String id) {
        List<CommunityNotification> list = notifications.getValue();
        if (list != null) {
            for (CommunityNotification n : list) {
                if (n.getId().equals(id)) {
                    n.setRead(true);
                    break;
                }
            }
            notifications.setValue(list);
            updateUnreadCount(list);
        }
        // TODO: Call API PATCH /api/community/notifications/:id/read
    }

    public void markAllAsRead() {
        List<CommunityNotification> list = notifications.getValue();
        if (list != null) {
            for (CommunityNotification n : list) {
                n.setRead(true);
            }
            notifications.setValue(list);
            updateUnreadCount(list);
        }
        // TODO: Call API PATCH /api/community/notifications/read-all
    }

    private void updateUnreadCount(List<CommunityNotification> list) {
        int count = 0;
        for (CommunityNotification n : list) {
            if (!n.isRead()) {
                count++;
            }
        }
        unreadCount.setValue(count);
    }
}
