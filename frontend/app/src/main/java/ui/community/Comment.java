package ui.community;

public class Comment {
    private String id;
    private String userName;
    private String userAvatar;
    private String content;
    private String time;
    private int likeCount;
    private boolean isLiked;
    private boolean isAuthor;
    private String parentId;

    public Comment(String id, String userName, String userAvatar, String content, String time, int likeCount, boolean isAuthor) {
        this(id, userName, userAvatar, content, time, likeCount, isAuthor, null);
    }

    public Comment(String id, String userName, String userAvatar, String content, String time, int likeCount, boolean isAuthor, String parentId) {
        this.id = id;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.content = content;
        this.time = time;
        this.likeCount = likeCount;
        this.isAuthor = isAuthor;
        this.parentId = parentId;
    }

    public String getId() { return id; }
    public String getUserName() { return userName; }
    public String getUserAvatar() { return userAvatar; }
    public String getContent() { return content; }
    public String getTime() { return time; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
    public boolean isAuthor() { return isAuthor; }
    public String getParentId() { return parentId; }
    
    public boolean isReply() {
        return parentId != null && !parentId.trim().isEmpty();
    }
}
