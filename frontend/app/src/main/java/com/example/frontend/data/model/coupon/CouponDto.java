package com.example.frontend.data.model.coupon;

import com.google.gson.annotations.SerializedName;

public class CouponDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("coupon_code")
    private String couponCode;

    @SerializedName("display_name")
    private String displayName;

    @SerializedName("description")
    private String description;

    @SerializedName("discount_type")
    private String discountType; // "percentage", "fixed"

    @SerializedName("discount_value")
    private double discountValue;

    @SerializedName("min_spend_amount")
    private double minSpendAmount;

    @SerializedName("max_discount_amount")
    private double maxDiscountAmount;

    @SerializedName("start_date")
    private String startDate;

    @SerializedName("end_date")
    private String endDate;

    @SerializedName("is_active")
    private boolean isActive;

    public String getId() { return id; }
    public String getCouponCode() { return couponCode; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public String getDiscountType() { return discountType; }
    public double getDiscountValue() { return discountValue; }
    public double getMinSpendAmount() { return minSpendAmount; }
    public double getMaxDiscountAmount() { return maxDiscountAmount; }
    public String getEndDate() { return endDate; }
    public boolean isActive() { return isActive; }
}
