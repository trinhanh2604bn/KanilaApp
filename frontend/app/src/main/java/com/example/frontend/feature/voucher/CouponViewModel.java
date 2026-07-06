package com.example.frontend.feature.voucher;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.coupon.CouponDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.CouponRepository;
import java.util.List;

public class CouponViewModel extends AndroidViewModel {
    private final CouponRepository repository;
    private final MutableLiveData<NetworkResult<List<CouponDto>>> myCouponsResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<CouponDto>>> availableCouponsResult = new MutableLiveData<>();

    public CouponViewModel(@NonNull Application application) {
        super(application);
        this.repository = new CouponRepository(application);
    }

    public LiveData<NetworkResult<List<CouponDto>>> getMyCouponsResult() {
        return myCouponsResult;
    }

    public LiveData<NetworkResult<List<CouponDto>>> getAvailableCouponsResult() {
        return availableCouponsResult;
    }

    public void loadMyCoupons() {
        repository.getMyCoupons(myCouponsResult);
    }

    public void loadAvailableCoupons() {
        repository.getAvailableCoupons(availableCouponsResult);
    }
}
