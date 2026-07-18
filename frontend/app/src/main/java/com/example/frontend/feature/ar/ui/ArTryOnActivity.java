package com.example.frontend.feature.ar.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
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
import com.example.frontend.feature.ar.domain.LandmarkSmoother;
import com.example.frontend.feature.ar.domain.LipPathBuilder;
import com.example.frontend.feature.ar.domain.MlKitFaceMeshProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ArTryOnActivity extends AppCompatActivity implements FaceLandmarkProvider.Callback {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private PreviewView previewView;
    private LipOverlayView lipOverlayView;
    private SwitchMaterial debugSwitch;

    private TextView tvVariantName;
    private TextView tvPrice;
    private TextView tvFinishAndStock;
    private RecyclerView rvShades;
    private MaterialButton btnAddToCart;

    private ArCameraController cameraController;
    private FaceLandmarkProvider landmarkProvider;
    private LandmarkSmoother smoother;
    private LipPathBuilder lipPathBuilder;
    private LipColorRenderer colorRenderer;
    private LandmarkCoordinateMapper mapper;

    private Paint currentLipPaint;
    private ArTryOnViewModel viewModel;
    private ArShadeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_try_on);

        previewView = findViewById(R.id.previewView);
        lipOverlayView = findViewById(R.id.lipOverlayView);
        debugSwitch = findViewById(R.id.debugSwitch);

        tvVariantName = findViewById(R.id.tvVariantName);
        tvPrice = findViewById(R.id.tvPrice);
        tvFinishAndStock = findViewById(R.id.tvFinishAndStock);
        rvShades = findViewById(R.id.rvShades);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        smoother = new LandmarkSmoother(0.5f);
        lipPathBuilder = new LipPathBuilder();
        colorRenderer = new LipColorRenderer();

        // Default empty paint so it doesn't crash before loading
        currentLipPaint = new Paint();

        debugSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            lipOverlayView.setDebugMode(isChecked);
        });

        setupViewModel();
        setupRecyclerView();

        String productId = getIntent().getStringExtra("product_id");
        String initialVariantId = getIntent().getStringExtra("variant_id");
        if (productId != null) {
            viewModel.loadArConfig(productId, initialVariantId);
        }

        btnAddToCart.setOnClickListener(v -> {
            btnAddToCart.setEnabled(false); // disable temporarily
            viewModel.addToCart();
        });

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        }
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ArTryOnViewModel.class);

        viewModel.getShades().observe(this, shades -> {
            adapter.submitList(shades);
        });

        viewModel.getSelectedShade().observe(this, shade -> {
            if (shade == null) return;
            
            adapter.setSelectedShade(shade);
            
            // Update paint
            LipColorRenderer.FinishType finishType = LipColorRenderer.FinishType.fromString(shade.getFinishType());
            float opacity = shade.getOpacity() != null ? shade.getOpacity() : 0.6f;
            currentLipPaint = colorRenderer.getLipPaint(shade.getShadeHex(), finishType, opacity);
            lipOverlayView.invalidate(); // Force redraw without restarting camera
            
            // Update UI
            tvVariantName.setText(shade.getVariantName());
            
            if (shade.getPrice() != null) {
                tvPrice.setText(String.format(Locale.US, "%,dđ", shade.getPrice()).replace(",", "."));
            } else {
                tvPrice.setText("");
            }
            
            String finishText = shade.getFinishType() != null ? shade.getFinishType() : "MATTE";
            String stockText = shade.getInStock() ? "Còn hàng" : "Hết hàng";
            tvFinishAndStock.setText(finishText + " • " + stockText);
            
            btnAddToCart.setEnabled(shade.getInStock());
            if (!shade.getInStock()) {
                btnAddToCart.setText("Hết hàng");
            } else {
                btnAddToCart.setText("Thêm vào giỏ hàng");
            }
        });

        viewModel.getAddToCartResult().observe(this, result -> {
            btnAddToCart.setEnabled(viewModel.getSelectedShade().getValue() != null && viewModel.getSelectedShade().getValue().getInStock());
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
