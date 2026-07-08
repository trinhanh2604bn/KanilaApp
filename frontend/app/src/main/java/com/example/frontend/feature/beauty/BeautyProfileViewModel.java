package com.example.frontend.feature.beauty;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.beauty.BeautyReferenceDto;
import com.example.frontend.data.model.beauty.CustomerBeautyProfileDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.BeautyProfileRepository;
import java.util.List;

public class BeautyProfileViewModel extends AndroidViewModel {
    private final BeautyProfileRepository repository;
    private final MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> profileResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<BeautyReferenceDto>>> referencesResult = new MutableLiveData<>();

    public BeautyProfileViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BeautyProfileRepository(application);
    }

    public LiveData<NetworkResult<CustomerBeautyProfileDto>> getProfileResult() {
        return profileResult;
    }

    public LiveData<NetworkResult<List<BeautyReferenceDto>>> getReferencesResult() {
        return referencesResult;
    }

    public void loadProfile(String customerId) {
        repository.getBeautyProfile(customerId, profileResult);
    }

    public void updateProfileLocally(CustomerBeautyProfileDto profile) {
        profileResult.setValue(NetworkResult.success(profile));
    }

    public void loadReferences() {
        repository.getBeautyReferences(referencesResult);
    }
}
