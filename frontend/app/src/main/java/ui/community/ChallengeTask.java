package ui.community;

public class ChallengeTask {
    private String id;
    private String title;
    private String subtitle;
    private int iconResId;
    private boolean isCompleted;

    public ChallengeTask(String id, String title, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.isCompleted = isCompleted;
    }

    public ChallengeTask(String id, String title, String subtitle, int iconResId, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.iconResId = iconResId;
        this.isCompleted = isCompleted;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
