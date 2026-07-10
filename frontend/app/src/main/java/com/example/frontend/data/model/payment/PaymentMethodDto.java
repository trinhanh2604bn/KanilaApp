package com.example.frontend.data.model.payment;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PaymentMethodDto implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("payment_method_code")
    private String code;

    @SerializedName("payment_method_name")
    private String name;

    @SerializedName("provider_code")
    private String providerCode;

    @SerializedName("method_type")
    private String methodType;

    @SerializedName("description")
    private String description;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("sort_order")
    private int sortOrder;

    public String getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getProviderCode() { return providerCode; }
    public String getMethodType() { return methodType; }
    public String getDescription() { return description; }
    public boolean isActive() { return isActive; }
    public int getSortOrder() { return sortOrder; }
}
