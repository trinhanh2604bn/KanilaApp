package com.example.frontend.feature.ar.data;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

public class ArShade {
    
    @SerializedName("variant_id")
    private String variantId;
    
    @SerializedName("sku")
    private String sku;
    
    @SerializedName("variant_name")
    private String variantName;
    
    @SerializedName("shade_hex")
    private String shadeHex;
    
    @SerializedName("finish_type")
    private String finishType;
    
    @SerializedName("opacity")
    private Float opacity;
    
    @SerializedName("price")
    private Long price;
    
    @SerializedName("currency_code")
    private String currencyCode;
    
    @SerializedName("in_stock")
    private Boolean inStock;
    
    @SerializedName("thumbnail_url")
    private String thumbnailUrl;
    
    @SerializedName("enabled")
    private Boolean enabled;

    public String getVariantId() { return variantId; }
    public void setVariantId(String variantId) { this.variantId = variantId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getShadeHex() { return shadeHex; }
    public void setShadeHex(String shadeHex) { this.shadeHex = shadeHex; }

    public String getFinishType() { return finishType; }
    public void setFinishType(String finishType) { this.finishType = finishType; }

    public Float getOpacity() { return opacity; }
    public void setOpacity(Float opacity) { this.opacity = opacity; }

    public Long getPrice() { return price; }
    public void setPrice(Long price) { this.price = price; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public Boolean getInStock() { return inStock != null ? inStock : false; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Boolean getEnabled() { return enabled != null ? enabled : true; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArShade arShade = (ArShade) o;
        return Objects.equals(variantId, arShade.variantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variantId);
    }
}
