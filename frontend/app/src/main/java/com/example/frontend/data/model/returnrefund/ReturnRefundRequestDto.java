package com.example.frontend.data.model.returnrefund;

import java.util.List;

public class ReturnRefundRequestDto {
    private String orderId;
    private String orderItemId;
    private String reason;
    private String description;
    private List<EvidenceMedia> evidenceMedia;
    private String returnShippingMethod;
    private String refundMethod;
    private String refundAccountId;

    public ReturnRefundRequestDto() {}

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getOrderItemId() { return orderItemId; }
    public void setOrderItemId(String orderItemId) { this.orderItemId = orderItemId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<EvidenceMedia> getEvidenceMedia() { return evidenceMedia; }
    public void setEvidenceMedia(List<EvidenceMedia> evidenceMedia) { this.evidenceMedia = evidenceMedia; }

    public String getReturnShippingMethod() { return returnShippingMethod; }
    public void setReturnShippingMethod(String returnShippingMethod) { this.returnShippingMethod = returnShippingMethod; }

    public String getRefundMethod() { return refundMethod; }
    public void setRefundMethod(String refundMethod) { this.refundMethod = refundMethod; }

    public String getRefundAccountId() { return refundAccountId; }
    public void setRefundAccountId(String refundAccountId) { this.refundAccountId = refundAccountId; }

    public static class EvidenceMedia {
        private String mediaType;
        private String mediaUrl;

        public EvidenceMedia(String mediaType, String mediaUrl) {
            this.mediaType = mediaType;
            this.mediaUrl = mediaUrl;
        }

        public String getMediaType() { return mediaType; }
        public void setMediaType(String mediaType) { this.mediaType = mediaType; }

        public String getMediaUrl() { return mediaUrl; }
        public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    }
}
