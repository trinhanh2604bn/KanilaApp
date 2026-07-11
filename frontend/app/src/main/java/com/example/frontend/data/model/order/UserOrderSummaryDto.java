package com.example.frontend.data.model.order;

import com.google.gson.annotations.SerializedName;

public class UserOrderSummaryDto {
    @SerializedName("total_orders")
    private int totalOrders;

    @SerializedName("total_spent_amount")
    private double totalSpentAmount;

    public int getTotalOrders() {
        return totalOrders;
    }

    public double getTotalSpentAmount() {
        return totalSpentAmount;
    }
}
