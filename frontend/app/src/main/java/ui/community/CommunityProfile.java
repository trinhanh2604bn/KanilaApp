package ui.community;

import java.util.List;

public class CommunityProfile {
    private String userId;
    private String name;
    private String username;
    private String avatarUrl;
    private List<String> skinTags;
    private int followerCount;
    private int followingCount;
    private int totalLikes;
    private int rewardPoints;

    public CommunityProfile(String userId, String name, String username, String avatarUrl, List<String> skinTags, int followerCount, int followingCount, int totalLikes, int rewardPoints) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.skinTags = skinTags;
        this.followerCount = followerCount;
        this.followingCount = followingCount;
        this.totalLikes = totalLikes;
        this.rewardPoints = rewardPoints;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public List<String> getSkinTags() { return skinTags; }
    public void setSkinTags(List<String> skinTags) { this.skinTags = skinTags; }
    public int getFollowerCount() { return followerCount; }
    public void setFollowerCount(int followerCount) { this.followerCount = followerCount; }
    public int getFollowingCount() { return followingCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }
    public int getTotalLikes() { return totalLikes; }
    public void setTotalLikes(int totalLikes) { this.totalLikes = totalLikes; }
    public int getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(int rewardPoints) { this.rewardPoints = rewardPoints; }
}
