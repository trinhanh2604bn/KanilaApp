package com.example.frontend.feature.chatbot.model;

public class ChatCartSummaryUiModel {
    private final int itemsCount;
    private final String subtotal;
    private final String discount;
    private final String total;
    private final boolean hasDiscount;

    public ChatCartSummaryUiModel(int itemsCount, String subtotal, String discount, String total, boolean hasDiscount) {
        this.itemsCount = itemsCount;
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
        this.hasDiscount = hasDiscount;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public String getDiscount() {
        return discount;
    }

    public String getTotal() {
        return total;
    }

    public boolean isHasDiscount() {
        return hasDiscount;
    }
}
