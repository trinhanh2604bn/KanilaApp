package ui.community;

public class SavedContent {
    public static final String TYPE_FEED = "FEED";
    public static final String TYPE_BLOG = "BLOG";
    public static final String TYPE_CHALLENGE = "CHALLENGE";

    private String id;
    private String type;
    private String title;
    private String thumbnailUrl;
    private String authorName;
    private String createdAt;

    public SavedContent(String id, String type, String title, String thumbnailUrl, String authorName, String createdAt) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.authorName = authorName;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getAuthorName() { return authorName; }
    public String getCreatedAt() { return createdAt; }
}
