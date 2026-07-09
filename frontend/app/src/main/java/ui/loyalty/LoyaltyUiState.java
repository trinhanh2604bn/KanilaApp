package ui.loyalty;

import java.util.List;
import ui.loyalty.model.LoyaltyCouponDto;
import ui.loyalty.model.LoyaltyDto;

public class LoyaltyUiState {
    public final boolean loading;
    public final LoyaltyDto loyalty;
    public final List<LoyaltyCouponDto> coupons;
    public final String error;
    public final String saveCouponResult; // Success message or null

    public LoyaltyUiState(boolean loading, LoyaltyDto loyalty, List<LoyaltyCouponDto> coupons, String error, String saveCouponResult) {
        this.loading = loading;
        this.loyalty = loyalty;
        this.coupons = coupons;
        this.error = error;
        this.saveCouponResult = saveCouponResult;
    }

    public static LoyaltyUiState initial() {
        return new LoyaltyUiState(false, null, null, null, null);
    }
}
