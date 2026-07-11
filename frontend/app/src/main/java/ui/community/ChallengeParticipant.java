package ui.community;

import java.util.List;

public class ChallengeParticipant {
    private String userId;
    private String userName;
    private String userAvatar;
    private List<Post> progressPosts;

    public ChallengeParticipant(String userId, String userName, String userAvatar, List<Post> progressPosts) {
        this.userId = userId;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.progressPosts = progressPosts;
    }

    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserAvatar() { return userAvatar; }
    public List<Post> getProgressPosts() { return progressPosts; }
    public int getProgressCount() { return progressPosts != null ? progressPosts.size() : 0; }
}
