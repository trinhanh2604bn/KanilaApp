package com.example.frontend.feature.ar.domain;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class LipRealismTests {

    @Test
    public void testLipRenderProfileDefaults() {
        LipRenderProfile matte = LipRenderProfile.getDefaultProfile("MATTE");
        assertEquals(0.85f, matte.coverage, 0.01f);
        assertEquals(0.1f, matte.edgeFeather, 0.01f);
        
        LipRenderProfile gloss = LipRenderProfile.getDefaultProfile("GLOSS");
        assertEquals(0.6f, gloss.coverage, 0.01f);
        
        LipRenderProfile tint = LipRenderProfile.getDefaultProfile("TINT");
        assertEquals(0.4f, tint.coverage, 0.01f);
        
        LipRenderProfile invalid = LipRenderProfile.getDefaultProfile("UNKNOWN_FINISH");
        assertEquals(0.85f, invalid.coverage, 0.01f); // Should fallback to MATTE
    }

    @Test
    public void testOneEuroLipSmoother_StableInput() {
        OneEuroLipSmoother smoother = new OneEuroLipSmoother(1.0f, 0.007f, 1.0f);
        List<LandmarkPoint> points = new ArrayList<>();
        points.add(new LandmarkPoint(10f, 10f));
        
        List<LandmarkPoint> result1 = smoother.smooth(points, 1);
        assertEquals(10f, result1.get(0).x, 0.01f);
        
        // Wait a bit to simulate time passing (in tests, time might not pass between calls, but smoothing handles it)
        try { Thread.sleep(20); } catch (InterruptedException e) {}
        
        List<LandmarkPoint> points2 = new ArrayList<>();
        points2.add(new LandmarkPoint(10.5f, 10.5f));
        List<LandmarkPoint> result2 = smoother.smooth(points2, 1);
        
        // Output should move towards 10.5 but smoothly
        assertTrue(result2.get(0).x > 10f && result2.get(0).x <= 10.5f);
    }
    
    @Test
    public void testOneEuroLipSmoother_TrackingLost() {
        OneEuroLipSmoother smoother = new OneEuroLipSmoother(1.0f, 0.007f, 1.0f);
        List<LandmarkPoint> points = new ArrayList<>();
        points.add(new LandmarkPoint(10f, 10f));
        
        smoother.smooth(points, 1);
        
        // Simulate new tracking ID
        List<LandmarkPoint> newPoints = new ArrayList<>();
        newPoints.add(new LandmarkPoint(50f, 50f));
        
        List<LandmarkPoint> result = smoother.smooth(newPoints, 2); // Different ID
        
        // Should immediately jump to 50 without smoothing from 10
        assertEquals(50f, result.get(0).x, 0.01f);
    }
}
