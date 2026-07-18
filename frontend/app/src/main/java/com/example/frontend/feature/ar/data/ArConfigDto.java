package com.example.frontend.feature.ar.data;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ArConfigDto {
    
    @SerializedName("product_id")
    private String productId;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("ar_type")
    private String arType;
    
    @SerializedName("renderer_version")
    private String rendererVersion;
    
    @SerializedName("variants")
    private List<ArShade> variants;

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getArType() { return arType; }
    public void setArType(String arType) { this.arType = arType; }

    public String getRendererVersion() { return rendererVersion; }
    public void setRendererVersion(String rendererVersion) { this.rendererVersion = rendererVersion; }

    public List<ArShade> getVariants() { return variants; }
    public void setVariants(List<ArShade> variants) { this.variants = variants; }
}
