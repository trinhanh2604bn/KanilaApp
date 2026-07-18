package com.example.frontend.feature.ar.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Path;
import com.example.frontend.feature.ar.domain.LandmarkPoint;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.frontend.R;
import com.example.frontend.feature.ar.camera.ArCameraController;
import com.example.frontend.feature.ar.domain.FaceLandmarkProvider;
import com.example.frontend.feature.ar.domain.FaceLandmarkResult;
import com.example.frontend.feature.ar.domain.LandmarkCoordinateMapper;
import com.example.frontend.feature.ar.domain.LandmarkSmoother;
import com.example.frontend.feature.ar.domain.LipPathBuilder;
import com.example.frontend.feature.ar.domain.MlKitFaceMeshProvider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class ArTryOnActivity extends AppCompatActivity implements FaceLandmarkProvider.Callback {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private PreviewView previewView;
    private LipOverlayView lipOverlayView;
    private SwitchMaterial debugSwitch;

    private ArCameraController cameraController;
    private FaceLandmarkProvider landmarkProvider;
    private LandmarkSmoother smoother;
    private LipPathBuilder lipPathBuilder;
    private LipColorRenderer colorRenderer;
    private LandmarkCoordinateMapper mapper;

    // Fixed color for MVP POC
    private static final String POC_HEX_COLOR = "#D13444"; 
    private Paint currentLipPaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_try_on);

        previewView = findViewById(R.id.previewView);
        lipOverlayView = findViewById(R.id.lipOverlayView);
        debugSwitch = findViewById(R.id.debugSwitch);

        smoother = new LandmarkSmoother(0.5f);
        lipPathBuilder = new LipPathBuilder();
        colorRenderer = new LipColorRenderer();
        currentLipPaint = colorRenderer.getLipPaint(POC_HEX_COLOR, LipColorRenderer.FinishType.MATTE, 0.6f);

        String productId = getIntent().getStringExtra("product_id");
        if (productId != null) {
            fetchArConfig(productId);
        }

        debugSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            lipOverlayView.setDebugMode(isChecked);
        });

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
    }

    private void fetchArConfig(String productId) {
        com.example.frontend.data.remote.ApiClient.getClient(this)
            .create(com.example.frontend.data.remote.ApiService.class)
            .getProductArConfig(productId)
            .enqueue(new retrofit2.Callback<com.example.frontend.data.remote.ApiResponse<com.example.frontend.feature.ar.data.ArConfigDto>>() {
                @Override
                public void onResponse(retrofit2.Call<com.example.frontend.data.remote.ApiResponse<com.example.frontend.feature.ar.data.ArConfigDto>> call, retrofit2.Response<com.example.frontend.data.remote.ApiResponse<com.example.frontend.feature.ar.data.ArConfigDto>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        com.example.frontend.feature.ar.data.ArConfigDto config = response.body().getData();
                        if (config.getConfigs() != null && !config.getConfigs().isEmpty()) {
                            com.example.frontend.feature.ar.data.ArConfigDto.ArConfigDetail firstConfig = config.getConfigs().get(0).getArConfig();
                            if (firstConfig != null && firstConfig.getHexColor() != null) {
                                currentLipPaint = colorRenderer.getLipPaint(firstConfig.getHexColor(), LipColorRenderer.FinishType.MATTE, firstConfig.getOpacity() != null ? firstConfig.getOpacity() : 0.6f);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.example.frontend.data.remote.ApiResponse<com.example.frontend.feature.ar.data.ArConfigDto>> call, Throwable t) {
                    Toast.makeText(ArTryOnActivity.this, "Failed to load AR Config", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void startCamera() {
        landmarkProvider = new MlKitFaceMeshProvider();
        cameraController = new ArCameraController(this, this, previewView, landmarkProvider, this);
        
        previewView.post(() -> {
            // Assume 480x640 default analysis resolution for mapping scale
            mapper = new LandmarkCoordinateMapper(480, 640, previewView.getWidth(), previewView.getHeight(), true);
            cameraController.startCamera();
        });
    }

    @Override
    public void onSuccess(FaceLandmarkResult result) {
        if (result == null) {
            runOnUiThread(() -> lipOverlayView.clear());
            return;
        }

        List<LandmarkPoint> rawPoints = result.getFaceMeshPoints();
        List<LandmarkPoint> smoothedPoints = smoother.smooth(rawPoints, result.getTrackingId());
        
        if (mapper == null) return;

        List<LandmarkPoint> mappedPoints = new ArrayList<>(smoothedPoints.size());
        for (LandmarkPoint point : smoothedPoints) {
            mappedPoints.add(mapper.map(point));
        }

        Path path = new Path();
        lipPathBuilder.buildLipPath(path, mappedPoints);

        runOnUiThread(() -> lipOverlayView.setLipPath(path, currentLipPaint, mappedPoints));
    }

    @Override
    public void onFailure(Exception e) {
        runOnUiThread(() -> Toast.makeText(this, "Face tracking error", Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraController != null) {
            cameraController.stopCamera();
        }
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission required.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}
