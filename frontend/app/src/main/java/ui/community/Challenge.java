package ui.community;

import java.util.List;

public class Challenge {
    private String id;
    private String title;
    private String description;
    private String bannerUrl;
    private int imageResId;
    private int participantCount;
    private int rewardPoints;
    private int durationDays;
    private boolean isHot;
    private boolean isNew;
    private boolean isJoined;
    private int currentProgress; // current day/step
    private String remainingTime;
    private List<ChallengeTask> tasks;
    private List<String> productIds;
    private List<ChallengeParticipant> participants;
    private String rules;

    public Challenge(String id, String title, String bannerUrl, int participantCount, int rewardPoints, int durationDays, boolean isHot, boolean isNew) {
        this.id = id;
        this.title = title;
        this.bannerUrl = bannerUrl;
        this.participantCount = participantCount;
        this.rewardPoints = rewardPoints;
        this.durationDays = durationDays;
        this.isHot = isHot;
        this.isNew = isNew;
    }

    public Challenge(String id, String title, int imageResId, int participantCount, int rewardPoints, int durationDays, boolean isHot, boolean isNew) {
        this.id = id;
        this.title = title;
        this.imageResId = imageResId;
        this.participantCount = participantCount;
        this.rewardPoints = rewardPoints;
        this.durationDays = durationDays;
        this.isHot = isHot;
        this.isNew = isNew;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBannerUrl() { return bannerUrl; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public int getParticipantCount() { return participantCount; }
    public int getRewardPoints() { return rewardPoints; }
    public int getDurationDays() { return durationDays; }
    public boolean isHot() { return isHot; }
    public boolean isNew() { return isNew; }
    public boolean isJoined() { return isJoined; }
    public void setJoined(boolean joined) { isJoined = joined; }
    public int getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(int currentProgress) { this.currentProgress = currentProgress; }
    public String getRemainingTime() { return remainingTime; }
    public void setRemainingTime(String remainingTime) { this.remainingTime = remainingTime; }
    public List<ChallengeTask> getTasks() { return tasks; }
    public void setTasks(List<ChallengeTask> tasks) { this.tasks = tasks; }
    public List<String> getProductIds() { return productIds; }
    public void setProductIds(List<String> productIds) { this.productIds = productIds; }
    public List<ChallengeParticipant> getParticipants() { return participants; }
    public void setParticipants(List<ChallengeParticipant> participants) { this.participants = participants; }
    public void addParticipant(ChallengeParticipant participant) {
        if (participants == null) participants = new java.util.ArrayList<>();
        // Check if already in list
        for (ChallengeParticipant p : participants) {
            if (p.getUserId().equals(participant.getUserId())) return;
        }
        participants.add(0, participant); // Add to top
    }
    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }
}
