package com.example.frontend.feature.ar.domain;

public class LipRenderProfile {
    public float coverage;
    public float edgeFeather;
    public float textureRetention;
    public float saturation;
    public float roughness;
    public float specularStrength;

    public LipRenderProfile(float coverage, float edgeFeather, float textureRetention, float saturation, float roughness, float specularStrength) {
        this.coverage = coverage;
        this.edgeFeather = edgeFeather;
        this.textureRetention = textureRetention;
        this.saturation = saturation;
        this.roughness = roughness;
        this.specularStrength = specularStrength;
    }

    public static LipRenderProfile getDefaultProfile(String finishType) {
        if (finishType == null) finishType = "MATTE";
        switch (finishType.toUpperCase()) {
            case "GLOSS":
                return new LipRenderProfile(0.6f, 0.15f, 0.7f, 1.0f, 0.1f, 0.8f);
            case "TINT":
                return new LipRenderProfile(0.4f, 0.25f, 0.9f, 0.8f, 0.9f, 0.1f);
            case "SATIN":
                return new LipRenderProfile(0.7f, 0.12f, 0.6f, 1.0f, 0.5f, 0.3f);
            case "MATTE":
            default:
                return new LipRenderProfile(0.85f, 0.1f, 0.4f, 0.95f, 0.9f, 0.05f);
        }
    }
}
