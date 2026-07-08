package ui.community;

public class ChallengeTask {
    private String id;
    private String title;
    private boolean isCompleted;

    public ChallengeTask(String id, String title, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.isCompleted = isCompleted;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}
