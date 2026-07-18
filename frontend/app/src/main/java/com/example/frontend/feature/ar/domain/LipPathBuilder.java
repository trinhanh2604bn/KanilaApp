package com.example.frontend.feature.ar.domain;

import android.graphics.Path;
import android.graphics.PointF;
import java.util.List;

public class LipPathBuilder {

    public Path buildLipPath(Path path, List<LandmarkPoint> mappedPoints) {
        if (!LipGeometry.isValidFaceMesh(mappedPoints)) {
            return path;
        }

        path.reset();
        path.setFillType(Path.FillType.EVEN_ODD); // Important: creates a hole for the inner mouth

        // Outer Lip Path (Clockwise)
        buildPathFromIndices(path, mappedPoints, LipGeometry.OUTER_UPPER_LIP, true);
        buildPathFromIndices(path, mappedPoints, LipGeometry.OUTER_LOWER_LIP, false);
        path.close();

        // Inner Lip Path (Counter-Clockwise to create hole with EVEN_ODD)
        buildPathFromIndices(path, mappedPoints, LipGeometry.INNER_UPPER_LIP, true);
        buildPathFromIndices(path, mappedPoints, LipGeometry.INNER_LOWER_LIP, false);
        path.close();

        return path;
    }

    private void buildPathFromIndices(Path path, List<LandmarkPoint> points, int[] indices, boolean isStart) {
        for (int i = 0; i < indices.length; i++) {
            LandmarkPoint p = points.get(indices[i]);
            if (i == 0 && isStart) {
                path.moveTo(p.x, p.y);
            } else {
                path.lineTo(p.x, p.y);
            }
        }
    }
}
