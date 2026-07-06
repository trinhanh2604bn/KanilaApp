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

    public void loadProfileHub() {
        repository.getProfileHub(profileHubResult);
    }

    public void loadAddresses() {
        repository.getAddresses(addressesResult);
    }
}
