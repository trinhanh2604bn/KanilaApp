package com.example.frontend.data.model.account;

import com.google.gson.annotations.SerializedName;

public class ProfileHubDto {
    @SerializedName("account")
    private AccountInfo account;

    @SerializedName("loyalty")
    private LoyaltyInfo loyalty;

    @SerializedName("stats")
    private StatsInfo stats;

    public AccountInfo getAccount() { return account; }
    public LoyaltyInfo getLoyalty() { return loyalty; }
    public StatsInfo getStats() { return stats; }

    public static class AccountInfo {
        @SerializedName("fullName")
        private String fullName;
        @SerializedName("email")
        private String email;
        @SerializedName("avatarUrl")
        private String avatarUrl;

        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getAvatarUrl() { return avatarUrl; }
    }

    public static class LoyaltyInfo {
        @SerializedName("points")
        private int points;
        @SerializedName("tierName")
        private String tierName;

        public int getPoints() { return points; }
        public String getTierName() { return tierName; }
    }

    public static class StatsInfo {
        @SerializedName("orderCount")
        private int orderCount;
        @SerializedName("voucherCount")
        private int voucherCount;
        @SerializedName("wishlistCount")
        private int wishlistCount;
        @SerializedName("reviewCount")
        private int reviewCount;

        public int getOrderCount() { return orderCount; }
        public int getVoucherCount() { return voucherCount; }
        public int getWishlistCount() { return wishlistCount; }
        public int getReviewCount() { return reviewCount; }
    }
}
