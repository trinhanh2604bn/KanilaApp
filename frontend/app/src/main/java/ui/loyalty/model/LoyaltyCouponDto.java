package ui.loyalty.model;

import com.google.gson.annotations.SerializedName;

public class LoyaltyCouponDto {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("couponCode")
    private String couponCode;
    
    @SerializedName("promotionName")
    private String promotionName;
    
    @SerializedName("discountType")
    private String discountType;
    
    @SerializedName("discountValue")
    private double discountValue;
    
    @SerializedName("minOrderAmount")
    private double minOrderAmount;
    
    @SerializedName("validTo")
    private String validTo;
    
    @SerializedName("isSaved")
    private boolean isSaved;

    public String getId() { return id; }
    public String getCouponCode() { return couponCode; }
    public String getPromotionName() { return promotionName; }
    public String getDiscountType() { return discountType; }
    public double getDiscountValue() { return discountValue; }
    public double getMinOrderAmount() { return minOrderAmount; }
    public String getValidTo() { return validTo; }
    public boolean isSaved() { return isSaved; }
    public void setSaved(boolean saved) { isSaved = saved; }
}
