package ui.community;

import java.util.List;

public class BlogPost {
    private String id;
    private String title;
    private String excerpt;
    private String content;
    private String thumbnailUrl;
    private String authorName;
    private boolean isAuthorVerified;
    private String createdAt;
    private String category;
    private int likeCount;
    private int commentCount;
    private int shareCount;
    private boolean isLiked;
    private boolean isSaved;
    private List<String> productIds;

    public BlogPost(String id, String title, String excerpt, String thumbnailUrl, String authorName, boolean isAuthorVerified, String createdAt, String category) {
        this.id = id;
        this.title = title;
        this.excerpt = excerpt;
        this.thumbnailUrl = thumbnailUrl;
        this.authorName = authorName;
        this.isAuthorVerified = isAuthorVerified;
        this.createdAt = createdAt;
        this.category = category;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getExcerpt() { return excerpt; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getAuthorName() { return authorName; }
    public boolean isAuthorVerified() { return isAuthorVerified; }
    public String getCreatedAt() { return createdAt; }
    public String getCategory() { return category; }
    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public int getShareCount() { return shareCount; }
    public void setShareCount(int shareCount) { this.shareCount = shareCount; }
    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }
    public List<String> getProductIds() { return productIds; }
    public void setProductIds(List<String> productIds) { this.productIds = productIds; }
}
