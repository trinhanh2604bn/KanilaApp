package ui.loyalty;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.NetworkResult;
import java.util.List;
import ui.loyalty.model.LoyaltyCouponDto;
import ui.loyalty.model.LoyaltyDto;

public class LoyaltyViewModel extends AndroidViewModel {
    private final LoyaltyRepository repository;
    private final MutableLiveData<LoyaltyUiState> uiState = new MutableLiveData<>(LoyaltyUiState.initial());
    
    private final MutableLiveData<NetworkResult<LoyaltyDto>> loyaltyRes = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<LoyaltyCouponDto>>> couponsRes = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<Void>> saveRes = new MutableLiveData<>();

    public LoyaltyViewModel(@NonNull Application application) {
        super(application);
        this.repository = new LoyaltyRepository(application);

        loyaltyRes.observeForever(result -> updateUiState());
        couponsRes.observeForever(result -> updateUiState());
        saveRes.observeForever(result -> {
            if (result != null && result.status == NetworkResult.Status.SUCCESS) {
                // Refresh coupons after save
                loadAvailableCoupons();
            }
            updateUiState();
        });
    }

    public LiveData<LoyaltyUiState> getUiState() {
        return uiState;
    }

    public void loadAll() {
        loadLoyalty();
        loadAvailableCoupons();
    }

    public void loadLoyalty() {
        repository.getLoyaltyMe(loyaltyRes);
    }

    public void loadAvailableCoupons() {
        // repository.getAvailableCoupons(couponsRes);
        
        // Cung cấp dữ liệu mặc định để phần ưu đãi luôn hiển thị đẹp
        List<LoyaltyCouponDto> mockVouchers = new java.util.ArrayList<>();
        
        LoyaltyCouponDto v1 = new LoyaltyCouponDto();
        // Giả lập các trường dữ liệu
        try {
            java.lang.reflect.Field fId = v1.getClass().getDeclaredField("id");
            fId.setAccessible(true); fId.set(v1, "mock_1");
            
            java.lang.reflect.Field fName = v1.getClass().getDeclaredField("promotionName");
            fName.setAccessible(true); fName.set(v1, "Giảm đến 60k đơn 200k");
            
            java.lang.reflect.Field fMin = v1.getClass().getDeclaredField("minOrderAmount");
            fMin.setAccessible(true); fMin.set(v1, 200000.0);
            
            java.lang.reflect.Field fDate = v1.getClass().getDeclaredField("validTo");
            fDate.setAccessible(true); fDate.set(v1, "2026-07-10T00:00:00Z");
        } catch (Exception ignored) {}
        mockVouchers.add(v1);

        LoyaltyCouponDto v2 = new LoyaltyCouponDto();
        try {
            java.lang.reflect.Field fId = v2.getClass().getDeclaredField("id");
            fId.setAccessible(true); fId.set(v2, "mock_2");
            
            java.lang.reflect.Field fName = v2.getClass().getDeclaredField("promotionName");
            fName.setAccessible(true); fName.set(v2, "Giảm đến 100k đơn 600k");
            
            java.lang.reflect.Field fMin = v2.getClass().getDeclaredField("minOrderAmount");
            fMin.setAccessible(true); fMin.set(v2, 600000.0);
            
            java.lang.reflect.Field fDate = v2.getClass().getDeclaredField("validTo");
            fDate.setAccessible(true); fDate.set(v2, "2026-07-15T00:00:00Z");
        } catch (Exception ignored) {}
        mockVouchers.add(v2);

        couponsRes.setValue(NetworkResult.success(mockVouchers));
    }

    public void saveCoupon(String couponId) {
        repository.saveCoupon(couponId, saveRes);
    }

    private void updateUiState() {
        NetworkResult<LoyaltyDto> l = loyaltyRes.getValue();
        NetworkResult<List<LoyaltyCouponDto>> c = couponsRes.getValue();
        NetworkResult<Void> s = saveRes.getValue();

        boolean loading = (l != null && l.status == NetworkResult.Status.LOADING) ||
                          (c != null && c.status == NetworkResult.Status.LOADING);
        
        String error = null;
        if (l != null && l.status == NetworkResult.Status.ERROR && ! "Unauthorized".equals(l.message)) {
            error = l.message;
        } else if (c != null && c.status == NetworkResult.Status.ERROR) {
            error = c.message;
        }

        String saveResult = null;
        if (s != null) {
            if (s.status == NetworkResult.Status.SUCCESS) saveResult = "Success";
            else if (s.status == NetworkResult.Status.ERROR) error = s.message;
        }

        LoyaltyDto loyaltyData = l != null ? l.data : null;
        if (loyaltyData != null && "Member".equalsIgnoreCase(loyaltyData.getTierName())) {
            // Force change "Member" to "Đồng" for consistency with UI
            try {
                java.lang.reflect.Field field = loyaltyData.getClass().getDeclaredField("tierName");
                field.setAccessible(true);
                field.set(loyaltyData, "Đồng");
            } catch (Exception ignored) {}
        }

        uiState.setValue(new LoyaltyUiState(
                loading,
                loyaltyData,
                c != null ? c.data : null,
                error,
                saveResult
        ));
    }
}
