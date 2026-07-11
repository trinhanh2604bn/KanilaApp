package ui.loyalty.model;

import com.google.gson.annotations.SerializedName;

public class LoyaltyDto {
    @SerializedName("points_balance")
    private int pointsBalance;

    @SerializedName("tier_name")
    private String tierName;

    @SerializedName("order_count")
    private int orderCount;

    @SerializedName("spent_amount")
    private double spentAmount;

    public int getPointsBalance() {
        return pointsBalance;
    }

    public String getTierName() {
        return tierName;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public double getSpentAmount() {
        return spentAmount;
    }
}
