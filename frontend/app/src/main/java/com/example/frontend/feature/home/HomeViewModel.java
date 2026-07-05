package com.example.frontend.feature.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.data.repository.HomeRepository;
import com.example.frontend.model.Product;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final HomeRepository homeRepository;
    private final MutableLiveData<HomeUiState> uiState = new MutableLiveData<>(new HomeUiState());
    private final TokenManager tokenManager;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.homeRepository = new HomeRepository(application);
        this.tokenManager = TokenManager.getInstance(application);
    }

    public LiveData<HomeUiState> getUiState() {
        return uiState;
    }

    public void loadHomeData() {
        HomeUiState current = uiState.getValue();
        if (current != null) {
            current.loading = true;
            uiState.setValue(current);
        }

        MutableLiveData<NetworkResult<List<Product>>> productResult = new MutableLiveData<>();
        productResult.observeForever(result -> {
            HomeUiState state = uiState.getValue();
            if (state == null) state = new HomeUiState();
            
            state.loading = (result.status == NetworkResult.Status.LOADING);
            
            if (result.status == NetworkResult.Status.SUCCESS) {
                state.products = result.data;
                state.error = null;
            } else if (result.status == NetworkResult.Status.ERROR) {
                state.error = result.message;
            }
            
            uiState.setValue(state);
        });

        if (tokenManager.isLoggedIn()) {
            homeRepository.getHomepageRecommendations(productResult);
        } else {
            homeRepository.getProducts(null, productResult);
        }
        
        // Load other counts if needed
    }
    
    public boolean isLoggedIn() {
        return tokenManager.isLoggedIn();
    }
}
