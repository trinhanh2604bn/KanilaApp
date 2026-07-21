package com.example.frontend.feature.ar.domain;

import java.util.ArrayList;
import java.util.List;

public class OneEuroLipSmoother {
    private final float minCutoff;
    private final float beta;
    private final float dCutoff;
    
    private List<LandmarkPoint> xPrev;
    private List<LandmarkPoint> dxPrev;
    private long lastTime = -1;
    private int trackingId = -1;
    private static final long MAX_LOST_TIME_MS = 1000;

    public OneEuroLipSmoother(float minCutoff, float beta, float dCutoff) {
        this.minCutoff = minCutoff;
        this.beta = beta;
        this.dCutoff = dCutoff;
    }

    private float alpha(float cutoff, float dt) {
        float tau = (float) (1.0 / (2.0 * Math.PI * cutoff));
        return 1.0f / (1.0f + tau / dt);
    }

    public List<LandmarkPoint> smooth(List<LandmarkPoint> newPoints, int currentTrackingId) {
        if (newPoints == null || newPoints.isEmpty()) {
            return newPoints;
        }

        long currentTime = System.currentTimeMillis();
        
        if (trackingId != currentTrackingId || xPrev == null || 
            (lastTime > 0 && (currentTime - lastTime) > MAX_LOST_TIME_MS) ||
            xPrev.size() != newPoints.size()) {
            
            xPrev = new ArrayList<>();
            dxPrev = new ArrayList<>();
            for (LandmarkPoint p : newPoints) {
                xPrev.add(new LandmarkPoint(p.x, p.y));
                dxPrev.add(new LandmarkPoint(0, 0));
            }
            trackingId = currentTrackingId;
            lastTime = currentTime;
            return xPrev;
        }

        float dt = (currentTime - lastTime) / 1000.0f;
        if (dt <= 0) dt = 0.016f; // default to ~60fps

        List<LandmarkPoint> smoothed = new ArrayList<>(newPoints.size());
        
        float alphaD = alpha(dCutoff, dt);

        for (int i = 0; i < newPoints.size(); i++) {
            LandmarkPoint curr = newPoints.get(i);
            LandmarkPoint prevX = xPrev.get(i);
            LandmarkPoint prevDx = dxPrev.get(i);
            
            float dxX = (curr.x - prevX.x) / dt;
            float dxY = (curr.y - prevX.y) / dt;
            
            float filteredDxX = prevDx.x + alphaD * (dxX - prevDx.x);
            float filteredDxY = prevDx.y + alphaD * (dxY - prevDx.y);
            
            float cutoffX = minCutoff + beta * Math.abs(filteredDxX);
            float cutoffY = minCutoff + beta * Math.abs(filteredDxY);
            
            float alphaX = alpha(cutoffX, dt);
            float alphaY = alpha(cutoffY, dt);
            
            float smoothedX = prevX.x + alphaX * (curr.x - prevX.x);
            float smoothedY = prevX.y + alphaY * (curr.y - prevX.y);
            
            LandmarkPoint s = new LandmarkPoint(smoothedX, smoothedY);
            smoothed.add(s);
            
            xPrev.set(i, s);
            dxPrev.set(i, new LandmarkPoint(filteredDxX, filteredDxY));
        }
        
        lastTime = currentTime;
        return smoothed;
    }

    public void reset() {
        xPrev = null;
        dxPrev = null;
        trackingId = -1;
        lastTime = -1;
    }
}
