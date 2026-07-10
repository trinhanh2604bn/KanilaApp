package com.example.frontend.feature.checkout;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.payment.PaymentMethodDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.PaymentRepository;
import java.util.List;

public class PaymentViewModel extends AndroidViewModel {
    private final PaymentRepository paymentRepository;
    private final MutableLiveData<NetworkResult<List<PaymentMethodDto>>> paymentMethodsResult = new MutableLiveData<>();

    public PaymentViewModel(@NonNull Application application) {
        super(application);
        this.paymentRepository = new PaymentRepository(application);
    }

    public LiveData<NetworkResult<List<PaymentMethodDto>>> getPaymentMethodsResult() {
        return paymentMethodsResult;
    }

    public void loadPaymentMethods() {
        paymentRepository.getPaymentMethods(paymentMethodsResult);
    }
}
