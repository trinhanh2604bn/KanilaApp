package ui.community;

public class CommunityNotification {
    private final String id;
    private final String type; // REWARD, CHALLENGE_EXPIRING, CHALLENGE_REMINDER, LIKE, COMMENT
    private final String title;
    private final String content;
    private final String createdAt;
    private final String targetType; // POST, CHALLENGE, BLOG
    private final String targetId;
    private boolean isRead;

    public CommunityNotification(String id, String type, String title, String content, String createdAt, String targetType, String targetId, boolean isRead) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.targetType = targetType;
        this.targetId = targetId;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCreatedAt() { return createdAt; }
    public String getTargetType() { return targetType; }
    public String getTargetId() { return targetId; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
