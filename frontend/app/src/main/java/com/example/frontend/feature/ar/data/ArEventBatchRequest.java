package com.example.frontend.feature.ar.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class ArEventBatchRequest {
    
    @SerializedName("session_uuid")
    private String sessionUuid;
    
    @SerializedName("product_id")
    private String productId;
    
    @SerializedName("events")
    private List<ArEvent> events;

    public ArEventBatchRequest(String sessionUuid, String productId, List<ArEvent> events) {
        this.sessionUuid = sessionUuid;
        this.productId = productId;
        this.events = events;
    }

    public static class ArEvent {
        @SerializedName("event_type")
        private String eventType;
        
        @SerializedName("variant_id")
        private String variantId;
        
        @SerializedName("occurred_at")
        private String occurredAt;
        
        @SerializedName("metadata")
        private Map<String, Object> metadata;

        public ArEvent(String eventType, String variantId, String occurredAt, Map<String, Object> metadata) {
            this.eventType = eventType;
            this.variantId = variantId;
            this.occurredAt = occurredAt;
            this.metadata = metadata;
        }
    }
}
