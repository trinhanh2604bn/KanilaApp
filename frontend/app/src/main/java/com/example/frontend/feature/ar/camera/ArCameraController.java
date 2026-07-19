package com.example.frontend.feature.ar.camera;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.example.frontend.feature.ar.domain.FaceLandmarkProvider;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArCameraController {
    private static final String TAG = "ArCameraController";
    
    private final Context context;
    private final LifecycleOwner lifecycleOwner;
    private final PreviewView previewView;
    private final FaceLandmarkProvider landmarkProvider;
    private final FaceLandmarkProvider.Callback landmarkCallback;
    
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ExecutorService cameraExecutor;

    public ArCameraController(Context context, LifecycleOwner lifecycleOwner, PreviewView previewView,
                              FaceLandmarkProvider landmarkProvider, FaceLandmarkProvider.Callback landmarkCallback) {
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        this.previewView = previewView;
        this.landmarkProvider = landmarkProvider;
        this.landmarkCallback = landmarkCallback;
    }

    public void startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor();
        cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize camera", e);
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void bindCameraUseCases(@NonNull ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
            landmarkProvider.processImage(imageProxy, rotationDegrees, landmarkCallback);
        });

        cameraProvider.unbindAll();
        try {
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis);
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }

    public void stopCamera() {
        if (cameraProviderFuture != null) {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                cameraProvider.unbindAll();
            } catch (Exception e) {
                Log.e(TAG, "Failed to unbind camera", e);
            }
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (landmarkProvider != null) {
            landmarkProvider.close();
        }
    }
}
