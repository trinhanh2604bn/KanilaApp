package com.example.frontend.data.model.coupon;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class CouponDto implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("couponCode")
    private String couponCode;

    @SerializedName("promotionName")
    private String promotionName;

    @SerializedName("discountType")
    private String discountType; // "percentage", "fixed"

    @SerializedName("discountValue")
    private double discountValue;

    @SerializedName("minOrderAmount")
    private double minOrderAmount;

    @SerializedName("maxDiscountAmount")
    private double maxDiscountAmount;

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
    public double getMaxDiscountAmount() { return maxDiscountAmount; }
    public String getValidTo() { return validTo; }
    public boolean isSaved() { return isSaved; }
}
