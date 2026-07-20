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

    // ── Optional GPU calibration fields (all nullable, backward-compatible) ──
    // Validate 0-1 on use. Old variants without these fields use profile defaults.

    /** Override GPU coverage (0-1). Null = use finish profile default. */
    @SerializedName("coverage")
    private Float coverage;

    /** Override texture retention (0-1). Null = use finish profile default. */
    @SerializedName("texture_retention")
    private Float textureRetention;

    /** Override brightness bias (-0.1 to 0.1). Null = use finish profile default. */
    @SerializedName("brightness_bias")
    private Float brightnessBias;

    /** Override saturation multiplier (0-1). Null = use finish profile default. */
    @SerializedName("saturation")
    private Float saturation;

    /** Override roughness (0-1). Null = use finish profile default. */
    @SerializedName("roughness")
    private Float roughness;

    /** Override specular strength (0-1). Null = use finish profile default. */
    @SerializedName("specular_strength")
    private Float specularStrength;

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

    // ── Optional GPU calibration getters ──────────────────────────────────────
    /** Returns null if not set by backend (caller should use finish profile default). */
    public Float getCoverage()         { return coverage; }
    public Float getTextureRetention() { return textureRetention; }
    public Float getBrightnessBias()   { return brightnessBias; }
    public Float getSaturation()       { return saturation; }
    public Float getRoughness()        { return roughness; }
    public Float getSpecularStrength() { return specularStrength; }

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
