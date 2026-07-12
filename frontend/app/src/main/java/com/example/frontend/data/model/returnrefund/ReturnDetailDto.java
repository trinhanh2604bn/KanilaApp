package com.example.frontend.data.model.returnrefund;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ReturnDetailDto {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("order_id")
    private Object orderId; // Can be String or Object with order_number
    
    @SerializedName("returnNumber")
    private String returnNumber;
    
    @SerializedName("returnReason")
    private String returnReason;
    
    @SerializedName("returnStatus")
    private String returnStatus;
    
    @SerializedName("return_shipping_method")
    private String shippingMethod;
    
    @SerializedName("requestedAt")
    private String requestedAt;
    
    @SerializedName("note")
    private String note;

    private List<ReturnMediaDto> media;

    public String getId() { return id; }
    public String getReturnNumber() { return returnNumber; }
    public String getReturnReason() { return returnReason; }
    public String getReturnStatus() { return returnStatus; }
    public String getShippingMethod() { return shippingMethod; }
    public String getRequestedAt() { return requestedAt; }
    public String getNote() { return note; }
    public List<ReturnMediaDto> getMedia() { return media; }

    public static class ReturnMediaDto {
        @SerializedName("media_url")
        private String mediaUrl;
        @SerializedName("media_type")
        private String mediaType;

        public String getMediaUrl() { return mediaUrl; }
        public String getMediaType() { return mediaType; }
    }
}
