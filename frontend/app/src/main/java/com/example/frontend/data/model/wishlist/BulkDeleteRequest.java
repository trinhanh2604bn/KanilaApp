package com.example.frontend.data.model.wishlist;

import java.util.List;

public class BulkDeleteRequest {
    private List<String> itemIds;

    public BulkDeleteRequest(List<String> itemIds) {
        this.itemIds = itemIds;
    }

    public List<String> getItemIds() { return itemIds; }
}
