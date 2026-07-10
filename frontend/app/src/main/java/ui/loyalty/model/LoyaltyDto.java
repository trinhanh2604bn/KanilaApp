package ui.loyalty.model;

import com.google.gson.annotations.SerializedName;

public class LoyaltyDto {
    @SerializedName("points_balance")
    private int pointsBalance;

    @SerializedName("tier_name")
    private String tierName;

    public int getPointsBalance() {
        return pointsBalance;
    }

    public String getTierName() {
        return tierName;
    }
}
