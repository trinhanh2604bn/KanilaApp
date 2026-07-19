package com.example.frontend.feature.ar.domain;

import android.graphics.PointF;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.facemesh.FaceMesh;
import com.google.mlkit.vision.facemesh.FaceMeshDetection;
import com.google.mlkit.vision.facemesh.FaceMeshDetector;
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions;
import com.google.mlkit.vision.facemesh.FaceMeshPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MlKitFaceMeshProvider implements FaceLandmarkProvider {

    private final FaceMeshDetector detector;
    private final ExecutorService executor;
    private boolean isProcessing = false;

    public MlKitFaceMeshProvider() {
        FaceMeshDetectorOptions options = new FaceMeshDetectorOptions.Builder()
                .setUseCase(FaceMeshDetectorOptions.FACE_MESH)
                .build();
        detector = FaceMeshDetection.getClient(options);
        executor = Executors.newSingleThreadExecutor();
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    @Override
    public void processImage(ImageProxy imageProxy, int rotationDegrees, Callback callback) {
        if (isProcessing || imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        isProcessing = true;
        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), rotationDegrees);

        detector.process(image)
                .addOnSuccessListener(executor, faceMeshes -> {
                    if (faceMeshes != null && !faceMeshes.isEmpty()) {
                        // For MVP, just use the first/largest face
                        FaceMesh faceMesh = faceMeshes.get(0);
                        List<LandmarkPoint> points = new ArrayList<>(468);
                        for (FaceMeshPoint point : faceMesh.getAllPoints()) {
                            points.add(new LandmarkPoint(point.getPosition().getX(), point.getPosition().getY()));
                        }
                        callback.onSuccess(new FaceLandmarkResult(points, faceMesh.hashCode()));
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(executor, callback::onFailure)
                .addOnCompleteListener(executor, task -> {
                    isProcessing = false;
                    imageProxy.close();
                });
    }

    @Override
    public void close() {
        detector.close();
        executor.shutdown();
    }
}
