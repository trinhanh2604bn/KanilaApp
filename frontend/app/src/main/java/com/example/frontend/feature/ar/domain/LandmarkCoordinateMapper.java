package com.example.frontend.feature.ar.domain;

import android.graphics.PointF;

public class LandmarkCoordinateMapper {
    private final int imageWidth;
    private final int imageHeight;
    private final int viewWidth;
    private final int viewHeight;
    private final boolean isFrontCamera;
    
    private final float scaleX;
    private final float scaleY;
    private final float offsetX;
    private final float offsetY;

    public LandmarkCoordinateMapper(int imageWidth, int imageHeight, int viewWidth, int viewHeight, boolean isFrontCamera) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.isFrontCamera = isFrontCamera;

        float imageRatio = (float) imageWidth / imageHeight;
        float viewRatio = (float) viewWidth / viewHeight;

        // Center crop strategy (similar to preview view)
        if (viewRatio > imageRatio) {
            scaleX = (float) viewWidth / imageWidth;
            scaleY = scaleX;
        } else {
            scaleY = (float) viewHeight / imageHeight;
            scaleX = scaleY;
        }

        float scaledImageWidth = imageWidth * scaleX;
        float scaledImageHeight = imageHeight * scaleY;
        offsetX = (viewWidth - scaledImageWidth) / 2f;
        offsetY = (viewHeight - scaledImageHeight) / 2f;
    }

    public LandmarkPoint map(LandmarkPoint point) {
        float x = point.x;
        float y = point.y;

        if (isFrontCamera) {
            x = imageWidth - x; // Mirror horizontally
        }

        float mappedX = x * scaleX + offsetX;
        float mappedY = y * scaleY + offsetY;

        // Clamp to view bounds
        mappedX = Math.max(0, Math.min(mappedX, viewWidth));
        mappedY = Math.max(0, Math.min(mappedY, viewHeight));

        return new LandmarkPoint(mappedX, mappedY);
    }
}
