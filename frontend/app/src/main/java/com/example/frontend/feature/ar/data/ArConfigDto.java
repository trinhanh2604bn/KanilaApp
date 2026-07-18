package com.example.frontend.feature.ar.data;

import java.util.List;

public class ArConfigDto {
    private String product_id;
    private List<VariantArConfig> configs;

    public String getProductId() { return product_id; }
    public void setProductId(String product_id) { this.product_id = product_id; }

    public List<VariantArConfig> getConfigs() { return configs; }
    public void setConfigs(List<VariantArConfig> configs) { this.configs = configs; }

    public static class VariantArConfig {
        private String variant_id;
        private String variant_name;
        private ArConfigDetail ar_config;

        public String getVariantId() { return variant_id; }
        public void setVariantId(String variant_id) { this.variant_id = variant_id; }

        public String getVariantName() { return variant_name; }
        public void setVariantName(String variant_name) { this.variant_name = variant_name; }

        public ArConfigDetail getArConfig() { return ar_config; }
        public void setArConfig(ArConfigDetail ar_config) { this.ar_config = ar_config; }
    }

    public static class ArConfigDetail {
        private String model_type;
        private String hex_color;
        private String material;
        private Float opacity;

        public String getModelType() { return model_type; }
        public void setModelType(String model_type) { this.model_type = model_type; }

        public String getHexColor() { return hex_color; }
        public void setHexColor(String hex_color) { this.hex_color = hex_color; }

        public String getMaterial() { return material; }
        public void setMaterial(String material) { this.material = material; }

        public Float getOpacity() { return opacity; }
        public void setOpacity(Float opacity) { this.opacity = opacity; }
    }
}
