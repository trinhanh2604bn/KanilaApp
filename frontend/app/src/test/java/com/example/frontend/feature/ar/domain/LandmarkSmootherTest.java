package com.example.frontend.feature.ar.domain;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LandmarkSmootherTest {

    @Test
    public void testSmoothingFirstFrame() {
        LandmarkSmoother smoother = new LandmarkSmoother(0.5f);
        List<LandmarkPoint> points = new ArrayList<>();
        points.add(new LandmarkPoint(10f, 10f));
        
        List<LandmarkPoint> result = smoother.smooth(points, 1);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(10f, result.get(0).x, 0.001f);
        assertEquals(10f, result.get(0).y, 0.001f);
    }

    @Test
    public void testSmoothingSecondFrame() {
        LandmarkSmoother smoother = new LandmarkSmoother(0.5f);
        List<LandmarkPoint> frame1 = new ArrayList<>();
        frame1.add(new LandmarkPoint(10f, 10f));
        smoother.smooth(frame1, 1);

        List<LandmarkPoint> frame2 = new ArrayList<>();
        frame2.add(new LandmarkPoint(20f, 20f));
        List<LandmarkPoint> result = smoother.smooth(frame2, 1);

        // Alpha = 0.5, so it should be exactly in the middle
        assertEquals(15f, result.get(0).x, 0.001f);
        assertEquals(15f, result.get(0).y, 0.001f);
    }

    @Test
    public void testSmoothingTrackingIdChanged() {
        LandmarkSmoother smoother = new LandmarkSmoother(0.5f);
        List<LandmarkPoint> frame1 = new ArrayList<>();
        frame1.add(new LandmarkPoint(10f, 10f));
        smoother.smooth(frame1, 1);

        List<LandmarkPoint> frame2 = new ArrayList<>();
        frame2.add(new LandmarkPoint(20f, 20f));
        // Tracking ID changed, smoothing should reset
        List<LandmarkPoint> result = smoother.smooth(frame2, 2);

        assertEquals(20f, result.get(0).x, 0.001f);
        assertEquals(20f, result.get(0).y, 0.001f);
    }
}
