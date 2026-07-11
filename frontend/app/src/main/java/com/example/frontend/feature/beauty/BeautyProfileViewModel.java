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
import com.example.frontend.data.repository.RecommendationRepository;
import com.example.frontend.data.model.recommendation.RecommendationData;
import java.util.ArrayList;
import java.util.List;

public class BeautyProfileViewModel extends AndroidViewModel {
    private final BeautyProfileRepository repository;
    private final RecommendationRepository recommendationRepository;
    private final MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> profileResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> saveResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<List<BeautyReferenceDto>>> referencesResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<RecommendationData>> recommendationsResult = new MutableLiveData<>();
    private BeautyReferenceResolver referenceResolver;
    
    private final MutableLiveData<List<SavedRoutineDto>> savedRoutines = new MutableLiveData<>(new ArrayList<>());

    public BeautyProfileViewModel(@NonNull Application application) {
        super(application);
        this.repository = new BeautyProfileRepository(application);
        this.recommendationRepository = new RecommendationRepository(application);
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

    public LiveData<NetworkResult<CustomerBeautyProfileDto>> getSaveResult() {
        return saveResult;
    }

    public LiveData<NetworkResult<List<BeautyReferenceDto>>> getReferencesResult() {
        return referencesResult;
    }

    public LiveData<NetworkResult<RecommendationData>> getRecommendationsResult() {
        return recommendationsResult;
    }

    public LiveData<List<SavedRoutineDto>> getSavedRoutines() {
        return savedRoutines;
    }

    public void loadProfile(String customerId) {
        if ("me".equals(customerId)) {
            repository.getMyBeautyProfile(profileResult);
        } else {
            repository.getBeautyProfile(customerId, profileResult);
        }
    }

    public void updateProfile(String customerId, UpdateBeautyProfileRequest request) {
        MutableLiveData<NetworkResult<CustomerBeautyProfileDto>> targetResult = new MutableLiveData<>();
        targetResult.observeForever(new androidx.lifecycle.Observer<NetworkResult<CustomerBeautyProfileDto>>() {
            @Override
            public void onChanged(NetworkResult<CustomerBeautyProfileDto> result) {
                if (result != null) {
                    saveResult.setValue(result);
                    if (result.status == NetworkResult.Status.SUCCESS) {
                        profileResult.setValue(result);
                        loadRecommendations();
                    }
                    if (result.status != NetworkResult.Status.LOADING) {
                        targetResult.removeObserver(this);
                    }
                }
            }
        });
        
        if ("me".equals(customerId)) {
            repository.updateMyBeautyProfile(request, targetResult);
        } else {
            repository.updateBeautyProfile(customerId, request, targetResult);
        }
    }
    
    public void resetSaveResult() {
        saveResult.setValue(null);
    }

    public void updateProfileLocally(CustomerBeautyProfileDto profile) {
        profileResult.setValue(NetworkResult.success(profile));
    }

    public void loadReferences() {
        referenceResolver = null;
        repository.getBeautyReferences(referencesResult);
    }

    public BeautyReferenceResolver getReferenceResolver() {
        if (referenceResolver == null) {
            NetworkResult<List<BeautyReferenceDto>> result = referencesResult.getValue();
            if (result != null && result.status == NetworkResult.Status.SUCCESS && result.data != null) {
                referenceResolver = new BeautyReferenceResolver(result.data);
            }
        }
        return referenceResolver;
    }

    public void loadRecommendations() {
        recommendationRepository.getMyRecommendations(recommendationsResult);
    }

    public void saveRoutine(com.example.frontend.data.model.beauty.SavedRoutineDto routine) {
        List<com.example.frontend.data.model.beauty.SavedRoutineDto> current = savedRoutines.getValue();
        if (current == null) current = new ArrayList<>();
        
        // Remove existing if same ID to avoid duplicates (if ID exists)
        if (routine.getId() != null) {
            current.removeIf(r -> routine.getId().equals(r.getId()));
        }
        
        current.add(0, routine); // Add to top
        savedRoutines.setValue(current);
    }
}
