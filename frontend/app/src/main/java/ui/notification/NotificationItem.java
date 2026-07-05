package ui.notification;

/**
 * Model cho 1 dòng thông báo. refId để dành cho backend (id đơn hàng, voucher,
 * bài viết...) và có thể null.
 */
public class NotificationItem {

    private final String id;
    private final NotificationType type;
    private final String title;
    private final String content;
    private final String time;
    private final boolean read;
    private final String refId;

    public NotificationItem(String id,
                            NotificationType type,
                            String title,
                            String content,
                            String time,
                            boolean isRead,
                            String refId) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.time = time;
        this.read = isRead;
        this.refId = refId;
    }

    public String getId() {
        return id;
    }

    public NotificationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

    public boolean isRead() {
        return read;
    }

    public String getRefId() {
        return refId;
    }
}
