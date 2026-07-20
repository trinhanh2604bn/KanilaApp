package com.example.frontend.feature.ar.domain;

import java.util.List;

public class FaceLandmarkResult {
    private final List<LandmarkPoint> faceMeshPoints;
    private final int trackingId;

    public FaceLandmarkResult(List<LandmarkPoint> faceMeshPoints, int trackingId) {
        this.faceMeshPoints = faceMeshPoints;
        this.trackingId = trackingId;
    }

    public List<LandmarkPoint> getFaceMeshPoints() {
        return faceMeshPoints;
    }

    public int getTrackingId() {
        return trackingId;
    }
}
