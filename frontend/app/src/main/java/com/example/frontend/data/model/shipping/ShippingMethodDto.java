package com.example.frontend.data.model.shipping;

import com.google.gson.annotations.SerializedName;

public class ShippingMethodDto {
    @SerializedName("_id")
    private String id;

    @SerializedName("shipping_method_code")
    private String code;

    @SerializedName("shipping_method_name")
    private String name;

    @SerializedName("carrier_code")
    private String carrierCode;

    @SerializedName("service_level")
    private String serviceLevel;

    @SerializedName("description")
    private String description;

    @SerializedName("shipping_fee")
    private double shippingFee;

    @SerializedName("estimated_delivery")
    private String estimatedDelivery;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("is_default")
    private boolean isDefault;

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCarrierCode() { return carrierCode; }
    public String getServiceLevel() { return serviceLevel; }
    public String getDescription() { return description; }
    public double getShippingFee() { return shippingFee; }
    public String getEstimatedDelivery() { return estimatedDelivery; }
    public boolean isActive() { return isActive; }
    public boolean isDefault() { return isDefault; }
}
