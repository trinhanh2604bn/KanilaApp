package com.example.frontend.feature.checkout;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.shipping.ShippingMethodDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.ShippingRepository;
import java.util.List;

public class ShippingViewModel extends AndroidViewModel {
    private final ShippingRepository shippingRepository;
    private final MutableLiveData<NetworkResult<List<ShippingMethodDto>>> shippingMethods = new MutableLiveData<>();

    public ShippingViewModel(@NonNull Application application) {
        super(application);
        this.shippingRepository = new ShippingRepository(application);
    }

    public LiveData<NetworkResult<List<ShippingMethodDto>>> getShippingMethodsResult() {
        return shippingMethods;
    }

    public void loadShippingMethods() {
        shippingRepository.getShippingMethods(shippingMethods);
    }
}
