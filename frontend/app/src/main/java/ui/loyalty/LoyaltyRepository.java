package ui.loyalty;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.google.gson.Gson;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ui.loyalty.model.LoyaltyCouponDto;
import ui.loyalty.model.LoyaltyDto;

public class LoyaltyRepository {
    private final ApiService apiService;
    private final Gson gson = new Gson();

    public LoyaltyRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
    }

    public void getLoyaltyMe(MutableLiveData<NetworkResult<LoyaltyDto>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getMyLoyaltyAccount().enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiRes = response.body();
                    if (apiRes.isSuccess()) {
                        String json = gson.toJson(apiRes.getData());
                        LoyaltyDto dto = gson.fromJson(json, LoyaltyDto.class);
                        result.setValue(NetworkResult.success(dto));
                    } else {
                        result.setValue(NetworkResult.error(apiRes.getMessage()));
                    }
                } else if (response.code() == 401) {
                    result.setValue(NetworkResult.error("Unauthorized"));
                } else {
                    result.setValue(NetworkResult.error("Failed to load loyalty info"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void getAvailableCoupons(MutableLiveData<NetworkResult<List<LoyaltyCouponDto>>> result) {
        result.setValue(NetworkResult.loading());
        apiService.getAvailableCoupons().enqueue(new Callback<ApiResponse<List<com.example.frontend.data.model.coupon.CouponDto>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<com.example.frontend.data.model.coupon.CouponDto>>> call, Response<ApiResponse<List<com.example.frontend.data.model.coupon.CouponDto>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<com.example.frontend.data.model.coupon.CouponDto>> apiRes = response.body();
                    if (apiRes.isSuccess()) {
                        // Mapper to LoyaltyCouponDto to ensure we use the same structure
                        String json = gson.toJson(apiRes.getData());
                        List<LoyaltyCouponDto> items = gson.fromJson(json, new com.google.gson.reflect.TypeToken<List<LoyaltyCouponDto>>(){}.getType());
                        result.setValue(NetworkResult.success(items));
                    } else {
                        result.setValue(NetworkResult.error(apiRes.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to load coupons"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<com.example.frontend.data.model.coupon.CouponDto>>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void saveCoupon(String couponId, MutableLiveData<NetworkResult<Void>> result) {
        result.setValue(NetworkResult.loading());
        apiService.saveCoupon(couponId).enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Object> apiRes = response.body();
                    if (apiRes.isSuccess()) {
                        result.setValue(NetworkResult.success(null));
                    } else {
                        result.setValue(NetworkResult.error(apiRes.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Failed to save coupon"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }
}
