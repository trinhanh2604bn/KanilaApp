package com.example.frontend.feature.ar.domain;

import android.graphics.PointF;
import java.util.ArrayList;
import java.util.List;

public class LandmarkSmoother {
    private final float alpha;
    private List<LandmarkPoint> previousPoints;
    private int trackingId = -1;
    private long lastSeenTime = 0;
    private static final long MAX_LOST_TIME_MS = 500;

    public LandmarkSmoother(float alpha) {
        this.alpha = alpha;
    }

    public List<LandmarkPoint> smooth(List<LandmarkPoint> newPoints, int currentTrackingId) {
        if (newPoints == null || newPoints.isEmpty()) {
            return newPoints;
        }

        long currentTime = System.currentTimeMillis();
        
        // Reset smoothing if lost for too long or tracking ID changed
        if (trackingId != currentTrackingId || previousPoints == null || 
            (currentTime - lastSeenTime) > MAX_LOST_TIME_MS ||
            previousPoints.size() != newPoints.size()) {
            
            previousPoints = new ArrayList<>();
            for (LandmarkPoint p : newPoints) {
                previousPoints.add(new LandmarkPoint(p.x, p.y));
            }
            trackingId = currentTrackingId;
            lastSeenTime = currentTime;
            return previousPoints;
        }

        List<LandmarkPoint> smoothed = new ArrayList<>(newPoints.size());
        for (int i = 0; i < newPoints.size(); i++) {
            LandmarkPoint prev = previousPoints.get(i);
            LandmarkPoint curr = newPoints.get(i);
            
            float smoothedX = prev.x + alpha * (curr.x - prev.x);
            float smoothedY = prev.y + alpha * (curr.y - prev.y);
            
            LandmarkPoint s = new LandmarkPoint(smoothedX, smoothedY);
            smoothed.add(s);
            previousPoints.set(i, s);
        }
        
        lastSeenTime = currentTime;
        return smoothed;
    }

    public void reset() {
        previousPoints = null;
        trackingId = -1;
    }
}
