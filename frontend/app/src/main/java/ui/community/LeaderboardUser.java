package ui.community;

public class LeaderboardUser {
    private int rank;
    private String name;
    private String avatarUrl;
    private int points;
    private boolean isCurrentUser;
    private int targetPoints;
    private int pointsToNextTarget;

    public LeaderboardUser(int rank, String name, String avatarUrl, int points) {
        this(rank, name, avatarUrl, points, false, 0, 0);
    }

    public LeaderboardUser(int rank, String name, String avatarUrl, int points, boolean isCurrentUser, int targetPoints, int pointsToNextTarget) {
        this.rank = rank;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.points = points;
        this.isCurrentUser = isCurrentUser;
        this.targetPoints = targetPoints;
        this.pointsToNextTarget = pointsToNextTarget;
    }

    public int getRank() { return rank; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
    public int getPoints() { return points; }
    public boolean isCurrentUser() { return isCurrentUser; }
    public int getTargetPoints() { return targetPoints; }
    public int getPointsToNextTarget() { return pointsToNextTarget; }
}
