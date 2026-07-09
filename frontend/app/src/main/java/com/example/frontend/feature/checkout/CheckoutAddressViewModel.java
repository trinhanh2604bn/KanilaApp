package com.example.frontend.feature.checkout;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.address.AddressDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.AddressRepository;
import java.util.List;

public class CheckoutAddressViewModel extends AndroidViewModel {
    private final AddressRepository repository;
    private final MutableLiveData<NetworkResult<List<AddressDto>>> addressResult = new MutableLiveData<>();
    private final MutableLiveData<AddressDto> selectedAddress = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<AddressDto>> saveResult = new MutableLiveData<>();

    public CheckoutAddressViewModel(@NonNull Application application) {
        super(application);
        this.repository = new AddressRepository(application);
    }

    public LiveData<NetworkResult<List<AddressDto>>> getAddressResult() {
        return addressResult;
    }

    public LiveData<AddressDto> getSelectedAddress() {
        return selectedAddress;
    }

    public LiveData<NetworkResult<AddressDto>> getSaveResult() {
        return saveResult;
    }

    public void loadCustomerAddresses() {
        repository.getCustomerAddresses(addressResult);
    }

    public void selectAddress(AddressDto address) {
        selectedAddress.setValue(address);
    }

    public void updateAddress(String id, Object addressData) {
        repository.updateAddress(id, addressData, saveResult);
    }

    public void addAddress(Object addressData) {
        repository.addAddress(addressData, saveResult);
    }

    public void setDefaultAddress(String id) {
        repository.setDefaultAddress(id, saveResult);
    }

    public void clearSaveResult() {
        saveResult.setValue(null);
    }
}
