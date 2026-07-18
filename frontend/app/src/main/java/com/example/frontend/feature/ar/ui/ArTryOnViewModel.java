package com.example.frontend.feature.ar.ui;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.frontend.data.model.cart.AddToCartRequest;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.CartRepository;
import com.example.frontend.feature.ar.data.ArConfigDto;
import com.example.frontend.feature.ar.data.ArEventBatchRequest;
import com.example.frontend.feature.ar.data.ArShade;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArTryOnViewModel extends AndroidViewModel {

    private static final String TAG = "ArTryOnViewModel";
    private final ApiService apiService;
    private final CartRepository cartRepository;

    private final MutableLiveData<List<ArShade>> shades = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<ArShade> selectedShade = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<com.example.frontend.data.model.cart.CartDto>> addToCartResult = new MutableLiveData<>();

    private String sessionId;
    private String productId;

    public ArTryOnViewModel(@NonNull Application application) {
        super(application);
        this.apiService = ApiClient.getClient(application).create(ApiService.class);
        this.cartRepository = new CartRepository(application);
        this.sessionId = UUID.randomUUID().toString();
    }

    public LiveData<List<ArShade>> getShades() {
        return shades;
    }

    public LiveData<ArShade> getSelectedShade() {
        return selectedShade;
    }

    public LiveData<NetworkResult<com.example.frontend.data.model.cart.CartDto>> getAddToCartResult() {
        return addToCartResult;
    }

    public void loadArConfig(String productId, String initialVariantId) {
        this.productId = productId;
        
        // --- HARDCODED FOR AR TRY ON MVP ---
        List<ArShade> hardcodedShades = new ArrayList<>();
        
        ArShade shade1 = new ArShade();
        shade1.setVariantId("AR-TEST-01");
        shade1.setVariantName("Màu 1");
        shade1.setShadeHex("#FF99CC");
        shade1.setFinishType("MATTE");
        shade1.setOpacity(0.62f);
        shade1.setPrice(239000L);
        shade1.setInStock(true);
        shade1.setEnabled(true);
        hardcodedShades.add(shade1);
        
        ArShade shade2 = new ArShade();
        shade2.setVariantId("AR-TEST-02");
        shade2.setVariantName("Màu 2");
        shade2.setShadeHex("#FFF0F5");
        shade2.setFinishType("SATIN");
        shade2.setOpacity(0.60f);
        shade2.setPrice(249000L);
        shade2.setInStock(true);
        shade2.setEnabled(true);
        hardcodedShades.add(shade2);
        
        ArShade shade3 = new ArShade();
        shade3.setVariantId("AR-TEST-03");
        shade3.setVariantName("Màu 3");
        shade3.setShadeHex("#CD9B9B");
        shade3.setFinishType("MATTE");
        shade3.setOpacity(0.58f);
        shade3.setPrice(259000L);
        shade3.setInStock(true);
        shade3.setEnabled(true);
        hardcodedShades.add(shade3);
        
        ArShade shade4 = new ArShade();
        shade4.setVariantId("AR-TEST-04");
        shade4.setVariantName("Màu 4");
        shade4.setShadeHex("#CD5C5C");
        shade4.setFinishType("TINT");
        shade4.setOpacity(0.50f);
        shade4.setPrice(239000L);
        shade4.setInStock(true);
        shade4.setEnabled(true);
        hardcodedShades.add(shade4);
        
        ArShade shade5 = new ArShade();
        shade5.setVariantId("AR-TEST-05");
        shade5.setVariantName("Màu 5");
        shade5.setShadeHex("#EE6363");
        shade5.setFinishType("SATIN");
        shade5.setOpacity(0.55f);
        shade5.setPrice(249000L);
        shade5.setInStock(true);
        shade5.setEnabled(true);
        hardcodedShades.add(shade5);

        shades.setValue(hardcodedShades);
        selectInitialShade(hardcodedShades, initialVariantId);
        
        // Still call API to keep any backend tracking/logging, but don't override the hardcoded shades
        apiService.getProductArConfig(productId).enqueue(new Callback<ApiResponse<ArConfigDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<ArConfigDto>> call, Response<ApiResponse<ArConfigDto>> response) {
                Log.d(TAG, "AR Config API call completed.");
            }

            @Override
            public void onFailure(Call<ApiResponse<ArConfigDto>> call, Throwable t) {
                Log.e(TAG, "API failure loading AR Config", t);
            }
        });
    }

    private void selectInitialShade(List<ArShade> validShades, String initialVariantId) {
        ArShade match = null;
        if (initialVariantId != null) {
            for (ArShade s : validShades) {
                if (initialVariantId.equals(s.getVariantId())) {
                    match = s;
                    break;
                }
            }
        }
        
        if (match == null) {
            for (ArShade s : validShades) {
                if (s.getInStock()) {
                    match = s;
                    break;
                }
            }
        }
        
        if (match == null) {
            match = validShades.get(0);
        }
        
        selectShade(match);
    }

    public void selectShade(ArShade shade) {
        ArShade current = selectedShade.getValue();
        if (current != null && current.getVariantId().equals(shade.getVariantId())) {
            return; // Already selected
        }
        
        String prevVariantId = current != null ? current.getVariantId() : null;
        selectedShade.setValue(shade);
        trackShadeSelected(shade, prevVariantId, shades.getValue() != null ? shades.getValue().size() : 0);
    }

    public void addToCart() {
        ArShade current = selectedShade.getValue();
        if (current == null || !current.getInStock()) return;
        
        if (productId != null) {
            AddToCartRequest request = new AddToCartRequest(productId, current.getVariantId(), 1);
            cartRepository.addToCart(request, addToCartResult);
        }
    }

    private void trackShadeSelected(ArShade shade, String previousVariantId, int shadeCount) {
        if (productId == null) return;
        
        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(new Date());
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("previous_variant_id", previousVariantId);
        metadata.put("selected_variant_id", shade.getVariantId());
        metadata.put("selected_finish_type", shade.getFinishType());
        metadata.put("shade_count", shadeCount);
        metadata.put("source_screen", "ar_try_on");

        ArEventBatchRequest.ArEvent event = new ArEventBatchRequest.ArEvent(
                "SHADE_SELECTED",
                shade.getVariantId(),
                timestamp,
                metadata
        );

        List<ArEventBatchRequest.ArEvent> events = new ArrayList<>();
        events.add(event);

        ArEventBatchRequest request = new ArEventBatchRequest(sessionId, productId, events);
        
        apiService.postArEvents(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                // Ignore response for analytics
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "Failed to track analytics event", t);
            }
        });
    }
}
