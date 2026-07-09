package com.example.frontend.feature.chatbot.data.response;

import com.google.gson.annotations.SerializedName;

public class ChatCartSummaryResponse {
    @SerializedName("items_count")
    private Integer itemsCount;

    @SerializedName("subtotal")
    private Long subtotal;

    @SerializedName("discount")
    private Long discount;

    @SerializedName("total")
    private Long total;

    public Integer getItemsCount() {
        return itemsCount;
    }

    public Long getSubtotal() {
        return subtotal;
    }

    public Long getDiscount() {
        return discount;
    }

    public Long getTotal() {
        return total;
    }
}
