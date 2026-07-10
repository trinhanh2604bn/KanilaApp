package ui.community;

import com.example.frontend.model.Product;
import java.util.List;

public class Post {
    private String id;
    private String userId;
    private String userName;
    private String userAvatar;
    private String time;
    private String title;
    private String content;
    private List<String> images;
    private int likeCount;
    private int commentCount;
    private int shareCount;
    private boolean isLiked;
    private boolean isSaved;
    private boolean isShared;
    private boolean isVerified;
    private boolean isPurchased;
    private String postType;
    private String skinType;
    private List<Product> products;

    public Post(String id, String userName, String userAvatar, String time, String title, String content, List<String> images, int likeCount, int commentCount, int shareCount, boolean isVerified, boolean isPurchased) {
        this.id = id;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.time = time;
        this.title = title;
        this.content = content;
        this.images = images;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.shareCount = shareCount;
        this.isVerified = isVerified;
        this.isPurchased = isPurchased;
    }

    public String getId() { return id; }
    public String getUserName() { return userName; }
    public String getUserAvatar() { return userAvatar; }
    public String getTime() { return time; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
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
    public boolean isShared() { return isShared; }
    public void setShared(boolean shared) { isShared = shared; }
    public boolean isVerified() { return isVerified; }
    public boolean isPurchased() { return isPurchased; }

    public String getPostType() { return postType; }
    public void setPostType(String postType) { this.postType = postType; }

    public String getSkinType() { return skinType; }
    public void setSkinType(String skinType) { this.skinType = skinType; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}
