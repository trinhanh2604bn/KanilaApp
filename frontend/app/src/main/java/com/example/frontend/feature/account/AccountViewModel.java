package com.example.frontend.feature.account;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.account.ProfileHubDto;
import com.example.frontend.data.model.address.AddressDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.AccountRepository;
import java.util.List;

public class AccountViewModel extends AndroidViewModel {
    private final AccountRepository repository;
    private final MutableLiveData<NetworkResult<ProfileHubDto>> profileHubResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<AddressDto>>> addressesResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<ProfileHubDto.AccountInfo>> updateProfileResult = new MutableLiveData<>();

    private final MutableLiveData<NetworkResult<List<AddressDto>>> accountAddressesResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<AddressDto>> addAccountAddressResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<Object>> deleteAccountAddressResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<AddressDto>> setDefaultAccountAddressResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<AddressDto>> updateAccountAddressResult = new MutableLiveData<>();

    public AccountViewModel(@NonNull Application application) {
        super(application);
        this.repository = new AccountRepository(application);
    }

    public LiveData<NetworkResult<ProfileHubDto>> getProfileHubResult() {
        return profileHubResult;
    }

    public LiveData<NetworkResult<List<AddressDto>>> getAddressesResult() {
        return addressesResult;
    }

    public LiveData<NetworkResult<ProfileHubDto.AccountInfo>> getUpdateProfileResult() {
        return updateProfileResult;
    }

    public void loadProfileHub() {
        repository.getProfileHub(profileHubResult);
    }

    public void loadAddresses() {
        repository.getAddresses(addressesResult);
    }

    public void updateProfile(java.util.Map<String, Object> data) {
        repository.updateProfile(data, updateProfileResult);
    }

    public void resetUpdateProfileResult() {
        updateProfileResult.setValue(null);
    }

    public void resetAddAccountAddressResult() {
        addAccountAddressResult.setValue(null);
    }

    public void resetUpdateAccountAddressResult() {
        updateAccountAddressResult.setValue(null);
    }

    public void resetDeleteAccountAddressResult() {
        deleteAccountAddressResult.setValue(null);
    }

    public void resetSetDefaultAccountAddressResult() {
        setDefaultAccountAddressResult.setValue(null);
    }

    public LiveData<NetworkResult<List<AddressDto>>> getAccountAddressesResult() {
        return accountAddressesResult;
    }

    public LiveData<NetworkResult<AddressDto>> getAddAccountAddressResult() {
        return addAccountAddressResult;
    }

    public LiveData<NetworkResult<Object>> getDeleteAccountAddressResult() {
        return deleteAccountAddressResult;
    }

    public LiveData<NetworkResult<AddressDto>> getSetDefaultAccountAddressResult() {
        return setDefaultAccountAddressResult;
    }

    public LiveData<NetworkResult<AddressDto>> getUpdateAccountAddressResult() {
        return updateAccountAddressResult;
    }

    public void loadAccountAddresses() {
        repository.getAccountAddresses(accountAddressesResult);
    }

    public void addAccountAddress(java.util.Map<String, Object> body) {
        repository.addAccountAddress(body, addAccountAddressResult);
    }

    public void updateAccountAddress(String id, java.util.Map<String, Object> body) {
        repository.updateAccountAddress(id, body, updateAccountAddressResult);
    }

    public void deleteAccountAddress(String id) {
        repository.deleteAccountAddress(id, deleteAccountAddressResult);
    }

    public void setDefaultAccountAddress(String id) {
        repository.setDefaultAccountAddress(id, setDefaultAccountAddressResult);
    }
}
