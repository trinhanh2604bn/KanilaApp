package com.example.frontend.feature.recommendation;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.recommendation.RecommendationData;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.RecommendationRepository;
import com.example.frontend.data.repository.AuthRepository;

public class RecommendationViewModel extends AndroidViewModel {
    private final RecommendationRepository recommendationRepository;
    private final AuthRepository authRepository;
    private final MutableLiveData<NetworkResult<RecommendationData>> homepageRecommendations = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<RecommendationData>> myRecommendations = new MutableLiveData<>();

    public RecommendationViewModel(@NonNull Application application) {
        super(application);
        this.recommendationRepository = new RecommendationRepository(application);
        this.authRepository = new AuthRepository(application);
    }

    public LiveData<NetworkResult<RecommendationData>> getHomepageRecommendations() {
        return homepageRecommendations;
    }

    public LiveData<NetworkResult<RecommendationData>> getMyRecommendations() {
        return myRecommendations;
    }

    public void fetchHomepageRecommendations() {
        if (authRepository.isLoggedIn()) {
            recommendationRepository.getHomepageRecommendations(homepageRecommendations);
        } else {
            homepageRecommendations.setValue(NetworkResult.unauthorized());
        }
    }

    public void fetchMyRecommendations() {
        if (authRepository.isLoggedIn()) {
            recommendationRepository.getMyRecommendations(myRecommendations);
        } else {
            myRecommendations.setValue(NetworkResult.unauthorized());
        }
    }
}
