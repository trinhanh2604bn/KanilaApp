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
        
        Path outerPath = new Path();
        buildPathFromIndices(outerPath, mappedPoints, LipGeometry.OUTER_UPPER_LIP, true);
        buildPathFromIndices(outerPath, mappedPoints, LipGeometry.OUTER_LOWER_LIP, false);
        outerPath.close();

        Path innerPath = new Path();
        buildPathFromIndices(innerPath, mappedPoints, LipGeometry.INNER_UPPER_LIP, true);
        buildPathFromIndices(innerPath, mappedPoints, LipGeometry.INNER_LOWER_LIP, false);
        innerPath.close();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            path.op(outerPath, innerPath, Path.Op.DIFFERENCE);
        } else {
            // Fallback for very old devices
            path.addPath(outerPath);
            path.addPath(innerPath);
            path.setFillType(Path.FillType.EVEN_ODD);
        }

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
