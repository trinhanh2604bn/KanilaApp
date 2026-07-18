package com.example.frontend.feature.ar.domain;

import androidx.camera.core.ImageProxy;

public interface FaceLandmarkProvider {
    void processImage(ImageProxy imageProxy, int rotationDegrees, Callback callback);
    void close();

    interface Callback {
        void onSuccess(FaceLandmarkResult result);
        void onFailure(Exception e);
    }
}
