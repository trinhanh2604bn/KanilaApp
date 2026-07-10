package com.example.frontend.data.model.address;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AddressDto implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("customer_id")
    private String customerId;

    @SerializedName("address_label")
    private String addressLabel;

    @SerializedName("recipient_name")
    private String recipientName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("address_line_1")
    private String addressLine1;

    @SerializedName("address_line_2")
    private String addressLine2;

    @SerializedName("ward")
    private String ward;

    @SerializedName("district")
    private String district;

    @SerializedName("city")
    private String city;

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("postal_code")
    private String postalCode;

    @SerializedName("address_type")
    private String addressType;

    @SerializedName("address_note")
    private String addressNote;

    @SerializedName("is_default_shipping")
    private boolean defaultShipping;

    @SerializedName("is_default_billing")
    private boolean defaultBilling;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    // Local UI-only field
    private transient boolean selected;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getAddressLabel() { return addressLabel; }
    public void setAddressLabel(String addressLabel) { this.addressLabel = addressLabel; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

    public String getWard() { return ward; }
    public void setWard(String ward) { this.ward = ward; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }

    public String getAddressNote() { return addressNote; }
    public void setAddressNote(String addressNote) { this.addressNote = addressNote; }

    public boolean isDefaultShipping() { return defaultShipping; }
    public void setDefaultShipping(boolean defaultShipping) { this.defaultShipping = defaultShipping; }

    public boolean isDefaultBilling() { return defaultBilling; }
    public void setDefaultBilling(boolean defaultBilling) { this.defaultBilling = defaultBilling; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
    
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        appendIfNotEmpty(sb, addressLine1);
        appendIfNotEmpty(sb, addressLine2);
        appendIfNotEmpty(sb, ward);
        appendIfNotEmpty(sb, district);
        appendIfNotEmpty(sb, city);
        return sb.toString();
    }

    private void appendIfNotEmpty(StringBuilder sb, String text) {
        if (text != null && !text.trim().isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(text.trim());
        }
    }

    @Override
    public String toString() {
        return getFullAddress();
    }
    
    // Compatibility getters
    public String getFullName() { return recipientName != null ? recipientName : ""; }
    public String getAddressLine() { return getFullAddress(); }
}
