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
    private final MutableLiveData<android.net.Uri> tempAvatarUri = new MutableLiveData<>();

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

    public LiveData<android.net.Uri> getTempAvatarUri() {
        return tempAvatarUri;
    }

    public void setTempAvatarUri(android.net.Uri uri) {
        tempAvatarUri.setValue(uri);
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
}
