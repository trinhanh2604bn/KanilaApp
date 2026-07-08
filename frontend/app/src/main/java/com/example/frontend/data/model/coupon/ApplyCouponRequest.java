package com.example.frontend.data.model.coupon;

import com.google.gson.annotations.SerializedName;

public class ApplyCouponRequest {
    @SerializedName("couponCode")
    private String couponCode;

    @SerializedName("orderAmount")
    private double orderAmount;

    public ApplyCouponRequest(String couponCode, double orderAmount) {
        this.couponCode = couponCode;
        this.orderAmount = orderAmount;
    }

    public String getCouponCode() { return couponCode; }
    public double getOrderAmount() { return orderAmount; }
}
