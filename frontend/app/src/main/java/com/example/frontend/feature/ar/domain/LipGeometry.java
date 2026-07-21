package com.example.frontend.feature.ar.domain;

import android.graphics.Path;
import android.graphics.PointF;
import java.util.List;

public class LipGeometry {
    // Top-to-bottom, clockwise or counter-clockwise, derived from ML Kit Face Mesh standard topology
    public static final int[] OUTER_UPPER_LIP = {61, 185, 40, 39, 37, 0, 267, 269, 270, 409, 291};
    public static final int[] OUTER_LOWER_LIP = {291, 375, 321, 405, 314, 17, 84, 181, 91, 146, 61};
    
    public static final int[] INNER_UPPER_LIP = {78, 191, 80, 81, 82, 13, 312, 311, 310, 415, 308};
    public static final int[] INNER_LOWER_LIP = {308, 324, 318, 402, 317, 14, 87, 178, 88, 95, 78};

    public static boolean isValidFaceMesh(List<LandmarkPoint> points) {
        return points != null && points.size() >= 468;
    }
}
