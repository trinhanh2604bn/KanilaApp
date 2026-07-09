package ui.community;

public class LeaderboardUser {
    private int rank;
    private String name;
    private String avatarUrl;
    private int points;

    public LeaderboardUser(int rank, String name, String avatarUrl, int points) {
        this.rank = rank;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.points = points;
    }

    public int getRank() { return rank; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
    public int getPoints() { return points; }
}
