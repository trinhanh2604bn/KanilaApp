package com.example.frontend.feature.checkout;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.checkout.CheckoutSessionDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.CheckoutRepository;

public class CheckoutViewModel extends AndroidViewModel {
    private final CheckoutRepository checkoutRepository;
    private final MutableLiveData<NetworkResult<CheckoutSessionDto>> checkoutSession = new MutableLiveData<>();

    public CheckoutViewModel(@NonNull Application application) {
        super(application);
        this.checkoutRepository = new CheckoutRepository(application);
    }

    public LiveData<NetworkResult<CheckoutSessionDto>> getCheckoutSession() {
        return checkoutSession;
    }

    public void prepareCheckout() {
        checkoutRepository.prepareCheckout(checkoutSession);
    }
}
