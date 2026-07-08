package ui.community;

public class RewardItem {
    private String id;
    private String title;
    private String description;
    private int pointCost;
    private String imageUrl;
    private String type; // VOUCHER, PRODUCT, etc.

    public RewardItem(String id, String title, String description, int pointCost, String imageUrl, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pointCost = pointCost;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPointCost() { return pointCost; }
    public String getImageUrl() { return imageUrl; }
    public String getType() { return type; }
}
