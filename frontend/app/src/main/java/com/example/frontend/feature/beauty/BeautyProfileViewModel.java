package com.example.frontend.feature.beauty;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.beauty.BeautyReferenceDto;
import com.example.frontend.data.model.beauty.CustomerBeautyProfileDto;
import com.example.frontend.data.model.beauty.SavedRoutineDto;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.BeautyProfileRepository;
import java.util.ArrayList;
import java.util.List;

public class BeautyProfileViewModel extends AndroidViewModel {
    private final BeautyProfileRepository repository;
    private final MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> profileResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<BeautyReferenceDto>>> referencesResult = new MutableLiveData<>();
    
    // In-memory storage for saved routines
    private final MutableLiveData<List<SavedRoutineDto>> savedRoutines = new MutableLiveData<>(new ArrayList<>());

    public BeautyProfileViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BeautyProfileRepository(application);
        // Initialize with some default items if empty
        initDefaultRoutines();
    }

    private void initDefaultRoutines() {
        List<SavedRoutineDto> current = new ArrayList<>();
        current.add(new SavedRoutineDto("default_1", "Clean Girl Look", System.currentTimeMillis() - (2 * 60 * 60 * 1000), com.example.frontend.R.drawable.hinh_nen));
        current.add(new SavedRoutineDto("default_2", "Glass Skin Routine", System.currentTimeMillis() - (4L * 24 * 60 * 60 * 1000), com.example.frontend.R.drawable.bg_slide_1));
        savedRoutines.setValue(current);
    }

    public LiveData<NetworkResult<CustomerBeautyProfileDto>> getProfileResult() {
        return profileResult;
    }

    public LiveData<NetworkResult<List<BeautyReferenceDto>>> getReferencesResult() {
        return referencesResult;
    }

    public LiveData<List<SavedRoutineDto>> getSavedRoutines() {
        return savedRoutines;
    }

    public void saveRoutine(SavedRoutineDto routine) {
        List<SavedRoutineDto> current = savedRoutines.getValue();
        if (current == null) current = new ArrayList<>();
        
        // Remove existing if same ID or Name to avoid duplicates in this simple mock
        current.removeIf(item -> item.getName().equals(routine.getName()));
        
        current.add(0, routine); // Add to top
        savedRoutines.setValue(current);
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
