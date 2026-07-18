package com.example.frontend.feature.ar.domain;

import androidx.camera.core.ImageProxy;

public class FakeFaceLandmarkProvider implements FaceLandmarkProvider {
    
    private FaceLandmarkResult fakeResult;
    private Exception fakeException;
    private boolean isClosed = false;

    public void setFakeResult(FaceLandmarkResult result) {
        this.fakeResult = result;
    }

    public void setFakeException(Exception e) {
        this.fakeException = e;
    }
    
    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void processImage(ImageProxy imageProxy, int rotationDegrees, Callback callback) {
        if (imageProxy != null) {
            imageProxy.close();
        }
        
        if (fakeException != null) {
            callback.onFailure(fakeException);
        } else {
            callback.onSuccess(fakeResult);
        }
    }

    @Override
    public void close() {
        isClosed = true;
    }
}
