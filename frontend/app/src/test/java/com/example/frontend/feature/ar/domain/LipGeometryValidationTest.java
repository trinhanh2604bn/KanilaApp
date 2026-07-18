package com.example.frontend.feature.ar.domain;

import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertTrue;

public class LipGeometryValidationTest {

    @Test
    public void testValidFaceMesh() {
        List<LandmarkPoint> points = new ArrayList<>();
        for (int i = 0; i < 468; i++) {
            points.add(new LandmarkPoint(0, 0));
        }
        assertTrue(LipGeometry.isValidFaceMesh(points));
    }

    @Test
    public void testInvalidFaceMesh() {
        List<LandmarkPoint> points = new ArrayList<>();
        // Less than 468
        for (int i = 0; i < 100; i++) {
            points.add(new LandmarkPoint(0, 0));
        }
        assertTrue(!LipGeometry.isValidFaceMesh(points));
        assertTrue(!LipGeometry.isValidFaceMesh(null));
    }
}
