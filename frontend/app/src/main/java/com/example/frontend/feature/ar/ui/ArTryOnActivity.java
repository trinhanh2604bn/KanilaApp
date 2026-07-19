package com.example.frontend.feature.ar.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.feature.ar.camera.ArCameraController;
import com.example.frontend.feature.ar.data.ArShade;
import com.example.frontend.feature.ar.domain.FaceLandmarkProvider;
import com.example.frontend.feature.ar.domain.FaceLandmarkResult;
import com.example.frontend.feature.ar.domain.LandmarkCoordinateMapper;
import com.example.frontend.feature.ar.domain.LandmarkPoint;
import com.example.frontend.feature.ar.domain.LipLightingEstimator;
import com.example.frontend.feature.ar.domain.LipPathBuilder;
import com.example.frontend.feature.ar.domain.LipRenderProfile;
import com.example.frontend.feature.ar.domain.MlKitFaceMeshProvider;
import com.example.frontend.feature.ar.domain.OneEuroLipSmoother;
import com.example.frontend.feature.ar.gpu.ArRendererFactory;
import com.example.frontend.feature.ar.gpu.LipGlSurfaceView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArTryOnActivity extends AppCompatActivity implements FaceLandmarkProvider.Callback {

    private static final String TAG = "ArTryOnActivity";
    private static final int PERMISSION_REQUEST_CODE = 1001;

    // ── Views ──────────────────────────────────────────────────────────────────
    /** PreviewView — used in Canvas mode only. Hidden in GPU mode. */
    private PreviewView previewView;
    /** LipOverlayView — used in Canvas mode only. Hidden in GPU mode. */
    private LipOverlayView lipOverlayView;
    /** LipGlSurfaceView — used in GPU mode only. Hidden in Canvas mode. */
    private LipGlSurfaceView lipGlSurfaceView;

    private SwitchMaterial debugSwitch;
    private TextView tvVariantName;
    private TextView tvPrice;
    private TextView tvFinishAndStock;
    private RecyclerView rvShades;
    private MaterialButton btnAddToCart;

    // ── AR pipeline ────────────────────────────────────────────────────────────
    private ArCameraController cameraController;
    private FaceLandmarkProvider landmarkProvider;
    private OneEuroLipSmoother smoother;
    private LipPathBuilder lipPathBuilder;
    private LipColorRenderer colorRenderer;      // Canvas path
    private LandmarkCoordinateMapper mapper;
    private LipLightingEstimator lightingEstimator;

    // ── Current renderer mode ──────────────────────────────────────────────────
    /** True when GPU renderer is active (may be toggled in debug builds). */
    private boolean useGpuRenderer;

    // ── Canvas state ───────────────────────────────────────────────────────────
    private Paint currentLipPaint;

    // ── ViewModel / Adapter ───────────────────────────────────────────────────
    private ArTryOnViewModel viewModel;
    private ArShadeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_try_on);

        previewView     = findViewById(R.id.previewView);
        lipOverlayView  = findViewById(R.id.lipOverlayView);
        lipGlSurfaceView = findViewById(R.id.lipGlSurfaceView);
        debugSwitch     = findViewById(R.id.debugSwitch);

        tvVariantName    = findViewById(R.id.tvVariantName);
        tvPrice          = findViewById(R.id.tvPrice);
        tvFinishAndStock = findViewById(R.id.tvFinishAndStock);
        rvShades         = findViewById(R.id.rvShades);
        btnAddToCart     = findViewById(R.id.btnAddToCart);

        // Determine renderer mode
        useGpuRenderer = ArRendererFactory.isGpuEnabled() && lipGlSurfaceView != null;
        applyRendererVisibility();

        // Init shared pipeline
        smoother           = new OneEuroLipSmoother(1.0f, 0.007f, 1.0f);
        lipPathBuilder     = new LipPathBuilder();
        colorRenderer      = new LipColorRenderer();
        lightingEstimator  = new LipLightingEstimator();
        currentLipPaint    = new Paint();

        // Debug switch: in debug builds allow toggling Canvas ↔ GPU
        debugSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (useGpuRenderer) {
                // GPU mode: debug switch shows landmark dots overlay
                // (landmark overlay on GPU is not yet implemented — PENDING)
            } else {
                lipOverlayView.setDebugMode(isChecked);
            }
        });

        setupViewModel();
        setupRecyclerView();

        String productId       = getIntent().getStringExtra("product_id");
        String initialVariantId = getIntent().getStringExtra("variant_id");
        if (productId != null) {
            viewModel.loadArConfig(productId, initialVariantId);
        }

        btnAddToCart.setOnClickListener(v -> {
            btnAddToCart.setEnabled(false);
            viewModel.addToCart();
        });

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
    }

    // ── Renderer visibility ────────────────────────────────────────────────────

    private void applyRendererVisibility() {
        if (useGpuRenderer) {
            // GPU mode: GLSurfaceView fills screen, PreviewView and Canvas overlay hidden
            if (previewView    != null) previewView.setVisibility(View.GONE);
            if (lipOverlayView != null) lipOverlayView.setVisibility(View.GONE);
            if (lipGlSurfaceView != null) lipGlSurfaceView.setVisibility(View.VISIBLE);
        } else {
            // Canvas mode: PreviewView + LipOverlayView, GLSurfaceView hidden
            if (previewView    != null) previewView.setVisibility(View.VISIBLE);
            if (lipOverlayView != null) lipOverlayView.setVisibility(View.VISIBLE);
            if (lipGlSurfaceView != null) lipGlSurfaceView.setVisibility(View.GONE);
        }
    }

    // ── ViewModel ─────────────────────────────────────────────────────────────

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ArTryOnViewModel.class);

        viewModel.getShades().observe(this, shades -> adapter.submitList(shades));

        viewModel.getSelectedShade().observe(this, shade -> {
            if (shade == null) return;
            adapter.setSelectedShade(shade);

            LipColorRenderer.FinishType finishType =
                    LipColorRenderer.FinishType.fromString(shade.getFinishType());
            float opacity = shade.getOpacity() != null ? shade.getOpacity() : 0.6f;
            LipRenderProfile canvasProfile = LipRenderProfile.getDefaultProfile(shade.getFinishType());

            // Lighting adjustment (shared by both paths)
            LipLightingEstimator.LipLightingState lightingState = lightingEstimator.getCurrentState();
            float adjustedOpacity = opacity * lightingState.exposureFactor;

            // Apply shade to active renderer
            Paint paint = ArRendererFactory.applyShade(
                    shade,
                    lipGlSurfaceView,
                    lipOverlayView,
                    colorRenderer,
                    finishType,
                    adjustedOpacity,
                    canvasProfile);

            if (!useGpuRenderer && paint != null) {
                currentLipPaint = paint;
                if (lipOverlayView != null) lipOverlayView.invalidate();
            }

            // Update UI
            tvVariantName.setText(shade.getVariantName());

            if (shade.getPrice() != null) {
                tvPrice.setText(String.format(Locale.US, "%,dđ", shade.getPrice()).replace(",", "."));
            } else {
                tvPrice.setText("");
            }

            String finishText = shade.getFinishType() != null ? shade.getFinishType() : "MATTE";
            String stockText  = shade.getInStock() ? "Còn hàng" : "Hết hàng";
            tvFinishAndStock.setText(finishText + " • " + stockText);

            btnAddToCart.setEnabled(shade.getInStock());
            btnAddToCart.setText(shade.getInStock() ? "Thêm vào giỏ hàng" : "Hết hàng");
        });

        viewModel.getAddToCartResult().observe(this, result -> {
            ArShade selected = viewModel.getSelectedShade().getValue();
            btnAddToCart.setEnabled(selected != null && selected.getInStock());
            if (result == null) return;
            if (result.status == NetworkResult.Status.SUCCESS) {
                Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            } else if (result.status == NetworkResult.Status.ERROR) {
                Toast.makeText(this, "Lỗi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new ArShadeAdapter(shade -> viewModel.selectShade(shade));
        rvShades.setAdapter(adapter);
    }

    // ── Camera start ──────────────────────────────────────────────────────────

    private void startCamera() {
        landmarkProvider = new MlKitFaceMeshProvider();
        cameraController = new ArCameraController(
                this, this, previewView, landmarkProvider, this);

        if (useGpuRenderer && lipGlSurfaceView != null) {
            // Route camera preview to GL surface
            cameraController.setCustomSurfaceProvider(lipGlSurfaceView.getSurfaceProvider());
            // Mapper uses GL surface dimensions
            lipGlSurfaceView.post(() -> {
                int w = lipGlSurfaceView.getWidth();
                int h = lipGlSurfaceView.getHeight();
                mapper = new LandmarkCoordinateMapper(480, 640, w > 0 ? w : 480, h > 0 ? h : 640, true);
                cameraController.startCamera();
            });
        } else {
            previewView.post(() -> {
                mapper = new LandmarkCoordinateMapper(
                        480, 640, previewView.getWidth(), previewView.getHeight(), true);
                cameraController.startCamera();
            });
        }
    }

    // ── FaceLandmarkProvider.Callback ─────────────────────────────────────────

    @Override
    public void onSuccess(FaceLandmarkResult result) {
        if (result == null) {
            runOnUiThread(() -> {
                if (!useGpuRenderer && lipOverlayView != null) lipOverlayView.clear();
                if (useGpuRenderer && lipGlSurfaceView != null) {
                    lipGlSurfaceView.updateLandmarks(null, System.currentTimeMillis());
                }
            });
            return;
        }

        List<LandmarkPoint> rawPoints     = result.getFaceMeshPoints();
        List<LandmarkPoint> smoothedPoints = smoother.smooth(rawPoints, result.getTrackingId());
        if (mapper == null) return;

        List<LandmarkPoint> mappedPoints = new ArrayList<>(smoothedPoints.size());
        for (LandmarkPoint point : smoothedPoints) {
            mappedPoints.add(mapper.map(point));
        }

        long timestampMs = System.currentTimeMillis();

        if (useGpuRenderer && lipGlSurfaceView != null) {
            // GPU path: pass landmarks directly to GL surface, no Canvas Path needed
            lipGlSurfaceView.updateLandmarks(mappedPoints, timestampMs);

            // Still run lighting estimator once per second for adaptive opacity
            runOnUiThread(() -> {
                // Lighting from previewView not available in GPU mode.
                // TODO: sample luminance from SurfaceTexture pixels via PBO (future improvement).
                // For now: lighting state stays at default (1.0 exposure factor).
            });
        } else {
            // Canvas path: build Path and draw overlay
            Path path = new Path();
            lipPathBuilder.buildLipPath(path, mappedPoints);

            runOnUiThread(() -> {
                if (lightingEstimator.shouldEstimate()) {
                    android.graphics.Bitmap bitmap = previewView.getBitmap();
                    if (bitmap != null) {
                        lightingEstimator.estimate(bitmap, mappedPoints);
                    }
                }
                if (lipOverlayView != null) {
                    lipOverlayView.setLipPath(path, currentLipPaint, mappedPoints);
                }
            });
        }
    }

    @Override
    public void onFailure(Exception e) {
        Log.e(TAG, "Face tracking error", e);
        runOnUiThread(() -> Toast.makeText(this, "Face tracking error", Toast.LENGTH_SHORT).show());
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    protected void onResume() {
        super.onResume();
        if (useGpuRenderer && lipGlSurfaceView != null) {
            lipGlSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (useGpuRenderer && lipGlSurfaceView != null) {
            lipGlSurfaceView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraController != null) cameraController.stopCamera();
        if (useGpuRenderer && lipGlSurfaceView != null) lipGlSurfaceView.release();
    }

    // ── Permissions ───────────────────────────────────────────────────────────

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults) {
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
