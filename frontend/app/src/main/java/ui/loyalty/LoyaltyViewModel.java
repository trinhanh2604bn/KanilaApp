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
import com.example.frontend.data.model.account.ProfileHubDto;
import com.example.frontend.data.model.order.OrderSummaryDto;

public class LoyaltyViewModel extends AndroidViewModel {
    private final LoyaltyRepository repository;
    private final com.example.frontend.data.repository.AccountRepository accountRepository;
    private final com.example.frontend.data.repository.OrderRepository orderRepository;
    private final MutableLiveData<LoyaltyUiState> uiState = new MutableLiveData<>(LoyaltyUiState.initial());
    
    private final MutableLiveData<NetworkResult<LoyaltyDto>> loyaltyRes = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<LoyaltyCouponDto>>> couponsRes = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<Void>> saveRes = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<ProfileHubDto>> profileHubRes = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<com.example.frontend.data.model.order.OrderSummaryDto>>> orderListRes = new MutableLiveData<>();

    public LoyaltyViewModel(@NonNull Application application) {
        super(application);
        this.repository = new LoyaltyRepository(application);
        this.accountRepository = new com.example.frontend.data.repository.AccountRepository(application);
        this.orderRepository = new com.example.frontend.data.repository.OrderRepository(application);

        loyaltyRes.observeForever(result -> updateUiState());
        couponsRes.observeForever(result -> updateUiState());
        profileHubRes.observeForever(result -> updateUiState());
        orderListRes.observeForever(result -> updateUiState());
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
        loadStats();
    }

    private void loadStats() {
        accountRepository.getProfileHub(profileHubRes);
        // Fetch orders to calculate total spent
        orderRepository.getMyOrders(null, 1, orderListRes);
    }

    public void loadLoyalty() {
        repository.getLoyaltyMe(loyaltyRes);
    }

    public void loadAvailableCoupons() {
        repository.getAvailableCoupons(couponsRes);
    }

    public void saveCoupon(String couponId) {
        repository.saveCoupon(couponId, saveRes);
    }

    private void updateUiState() {
        NetworkResult<LoyaltyDto> l = loyaltyRes.getValue();
        NetworkResult<List<LoyaltyCouponDto>> c = couponsRes.getValue();
        NetworkResult<Void> s = saveRes.getValue();
        NetworkResult<ProfileHubDto> p = profileHubRes.getValue();
        NetworkResult<List<com.example.frontend.data.model.order.OrderSummaryDto>> o = orderListRes.getValue();

        boolean loading = (l != null && l.status == NetworkResult.Status.LOADING) ||
                          (c != null && c.status == NetworkResult.Status.LOADING);
        
        String error = null;
        if (l != null && l.status == NetworkResult.Status.ERROR && ! "Unauthorized".equals(l.message)) {
            error = l.message;
        }

        String saveResult = null;
        if (s != null) {
            if (s.status == NetworkResult.Status.SUCCESS) saveResult = "Success";
            else if (s.status == NetworkResult.Status.ERROR) error = s.message;
        }

        LoyaltyDto loyaltyData = l != null ? l.data : null;
        
        // Merge order count and spent amount into loyaltyData for UI
        if (loyaltyData != null) {
            // 1. Get Order Count from Profile Hub (most reliable)
            if (p != null && p.data != null && p.data.getStats() != null) {
                try {
                    java.lang.reflect.Field fOrder = loyaltyData.getClass().getDeclaredField("orderCount");
                    fOrder.setAccessible(true);
                    fOrder.set(loyaltyData, p.data.getStats().getOrderCount());
                } catch (Exception ignored) {}
            }
            
            // 2. Calculate Spent Amount from Order List
            if (o != null && o.data != null) {
                double totalSpent = 0;
                for (com.example.frontend.data.model.order.OrderSummaryDto order : o.data) {
                    // Only count orders that are not cancelled
                    if (!"cancelled".equalsIgnoreCase(order.getOrderStatus())) {
                        totalSpent += order.getGrandTotalAmount();
                    }
                }
                
                try {
                    java.lang.reflect.Field fSpent = loyaltyData.getClass().getDeclaredField("spentAmount");
                    fSpent.setAccessible(true);
                    fSpent.set(loyaltyData, totalSpent);
                } catch (Exception ignored) {}
            }

            if ("Member".equalsIgnoreCase(loyaltyData.getTierName())) {
                try {
                    java.lang.reflect.Field field = loyaltyData.getClass().getDeclaredField("tierName");
                    field.setAccessible(true);
                    field.set(loyaltyData, "Đồng");
                } catch (Exception ignored) {}
            }
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
