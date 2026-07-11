package com.example.frontend.data.model.recommendation;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class RecommendationData {
    @SerializedName("recommendation_type")
    private String recommendationType;

    @SerializedName("profile_source")
    private String profileSource;

    @SerializedName("from_snapshot")
    private Boolean fromSnapshot;

    @SerializedName("algorithm_version")
    private String algorithmVersion;

    @SerializedName("snapshot_generated_at")
    private String snapshotGeneratedAt;

    @SerializedName("products")
    private List<RecommendedProduct> products;

    public String getRecommendationType() {
        return recommendationType;
    }

    public String getProfileSource() {
        return profileSource;
    }

    public Boolean getFromSnapshot() {
        return fromSnapshot;
    }

    public String getAlgorithmVersion() {
        return algorithmVersion;
    }

    public String getSnapshotGeneratedAt() {
        return snapshotGeneratedAt;
    }

    public List<RecommendedProduct> getProducts() {
        return products;
    }
}
