package com.example.frontend.data.model.account;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProfileHubDto {
    @SerializedName("profile")
    private AccountInfo profile;

    @SerializedName("loyalty")
    private LoyaltyInfo loyalty;

    @SerializedName("stats")
    private StatsInfo stats;

    @SerializedName("defaultAddress")
    private DefaultAddressInfo defaultAddress;

    @SerializedName("skinProfile")
    private SkinProfileInfo skinProfile;

    @SerializedName("security")
    private SecurityInfo security;

    public AccountInfo getProfile() { return profile; }
    public LoyaltyInfo getLoyalty() { return loyalty; }
    public StatsInfo getStats() { return stats; }
    public DefaultAddressInfo getDefaultAddress() { return defaultAddress; }
    public SkinProfileInfo getSkinProfile() { return skinProfile; }
    public SecurityInfo getSecurity() { return security; }

    public static class AccountInfo {
        @SerializedName("customerId")
        private String customerId;
        
        @SerializedName(value = "fullName", alternate = {"full_name", "name"})
        private String fullName;
        
        @SerializedName("email")
        private String email;
        
        @SerializedName(value = "phone", alternate = {"phoneNumber", "mobile"})
        private String phone;
        
        @SerializedName(value = "gender", alternate = {"sex"})
        private String gender;
        
        @SerializedName(value = "birthday", alternate = {"date_of_birth", "dob"})
        private String birthday;
        
        @SerializedName(value = "avatarUrl", alternate = {"avatar", "profile_image", "avatar_url"})
        private String avatarUrl;

        public String getCustomerId() { return customerId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getAvatarUrl() { return avatarUrl; }
        public String getPhone() { return phone; }
        public String getGender() { return gender; }
        public String getBirthday() { return birthday; }
    }

    public static class LoyaltyInfo {
        @SerializedName("pointsBalance")
        private int pointsBalance;
        @SerializedName("tierName")
        private String tierName;
        @SerializedName("nextTierName")
        private String nextTierName;
        @SerializedName("pointsToNextTier")
        private int pointsToNextTier;

        public int getPointsBalance() { return pointsBalance; }
        public String getTierName() { return tierName; }
        public String getNextTierName() { return nextTierName; }
        public int getPointsToNextTier() { return pointsToNextTier; }
    }

    public static class StatsInfo {
        @SerializedName("orderCount")
        private int orderCount;
        @SerializedName("processingOrderCount")
        private int processingOrderCount;
        @SerializedName("wishlistCount")
        private int wishlistCount;
        @SerializedName("couponCount")
        private int couponCount;
        @SerializedName("expiringCouponCount")
        private int expiringCouponCount;
        @SerializedName("addressCount")
        private int addressCount;

        public int getOrderCount() { return orderCount; }
        public int getProcessingOrderCount() { return processingOrderCount; }
        public int getWishlistCount() { return wishlistCount; }
        public int getCouponCount() { return couponCount; }
        public int getVoucherCount() { return couponCount; } // Alias for UI
        public int getExpiringCouponCount() { return expiringCouponCount; }
        public int getAddressCount() { return addressCount; }
    }

    public static class DefaultAddressInfo {
        @SerializedName("addressId")
        private String addressId;
        @SerializedName("recipientName")
        private String recipientName;
        @SerializedName("phone")
        private String phone;
        @SerializedName("fullAddress")
        private String fullAddress;
        @SerializedName("isDefault")
        private boolean isDefault;

        public String getFullAddress() { return fullAddress; }
        public String getRecipientName() { return recipientName; }
        public String getPhone() { return phone; }
    }

    public static class SkinProfileInfo {
        @SerializedName("skinType")
        private List<String> skinType;
        @SerializedName("skinTone")
        private String skinTone;
        @SerializedName("eyeColor")
        private String eyeColor;
        @SerializedName("concerns")
        private List<String> concerns;
        @SerializedName("ingredientPreferences")
        private List<String> ingredientPreferences;
        @SerializedName("favoriteBrands")
        private List<String> favoriteBrands;
        @SerializedName("goals")
        private List<String> goals;
        @SerializedName("sensitivityLevel")
        private String sensitivityLevel;

        public List<String> getSkinType() { return skinType; }
        public List<String> getConcerns() { return concerns; }
        public List<String> getIngredientPreferences() { return ingredientPreferences; }
        public List<String> getFavoriteBrands() { return favoriteBrands; }
        public List<String> getGoals() { return goals; }
        public String getSkinTone() { return skinTone; }
        public String getSensitivityLevel() { return sensitivityLevel; }
    }

    public static class SecurityInfo {
        @SerializedName("emailVerified")
        private boolean emailVerified;
        @SerializedName("linkedProviders")
        private List<LinkedProvider> linkedProviders;

        public boolean isEmailVerified() { return emailVerified; }
        public List<LinkedProvider> getLinkedProviders() { return linkedProviders; }
    }

    public static class LinkedProvider {
        @SerializedName("provider")
        private String provider;
        @SerializedName("email")
        private String email;
        @SerializedName("linkedAt")
        private String linkedAt;
    }
}
