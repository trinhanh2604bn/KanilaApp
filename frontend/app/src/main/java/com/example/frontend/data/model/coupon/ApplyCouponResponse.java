package com.example.frontend.data.model.coupon;

import com.google.gson.annotations.SerializedName;

public class ApplyCouponResponse {
    @SerializedName("couponId")
    private String couponId;

    @SerializedName("couponCode")
    private String couponCode;

    @SerializedName("discountAmount")
    private double discountAmount;

    @SerializedName("orderAmount")
    private double orderAmount;

    @SerializedName("finalAmount")
    private double finalAmount;

    public String getCouponId() { return couponId; }
    public String getCouponCode() { return couponCode; }
    public double getDiscountAmount() { return discountAmount; }
    public double getOrderAmount() { return orderAmount; }
    public double getFinalAmount() { return finalAmount; }
}
