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
        if (current == null) current = new HomeUiState();
        current.loading = true;
        uiState.setValue(current);

        // Load recommended products
        MutableLiveData<NetworkResult<List<Product>>> recommendedResult = new MutableLiveData<>();
        recommendedResult.observeForever(result -> {
            HomeUiState state = uiState.getValue();
            if (state == null) state = new HomeUiState();
            
            if (result.status == NetworkResult.Status.SUCCESS) {
                state.recommendedProducts = result.data;
                checkLoadingState(state);
            } else if (result.status == NetworkResult.Status.EMPTY) {
                state.recommendedProducts = new java.util.ArrayList<>();
                checkLoadingState(state);
            } else if (result.status == NetworkResult.Status.ERROR) {
                state.error = result.message;
                state.loading = false;
                uiState.setValue(state);
            }
        });

        // Load all products
        MutableLiveData<NetworkResult<List<Product>>> allProductsResult = new MutableLiveData<>();
        allProductsResult.observeForever(result -> {
            HomeUiState state = uiState.getValue();
            if (state == null) state = new HomeUiState();
            
            if (result.status == NetworkResult.Status.SUCCESS) {
                state.allProducts = result.data;
                checkLoadingState(state);
            } else if (result.status == NetworkResult.Status.EMPTY) {
                state.allProducts = new java.util.ArrayList<>();
                checkLoadingState(state);
            } else if (result.status == NetworkResult.Status.ERROR) {
                state.error = result.message;
                state.loading = false;
                uiState.setValue(state);
            }
        });

        if (tokenManager.isLoggedIn()) {
            homeRepository.getHomepageRecommendations(recommendedResult);
        } else {
            // If not logged in, we can fallback to popular products for recommendations
            homeRepository.getProducts("popular", recommendedResult);
        }
        
        homeRepository.getProducts(null, allProductsResult);
    }

    private void checkLoadingState(HomeUiState state) {
        if (state.recommendedProducts != null && state.allProducts != null) {
            state.loading = false;
            state.error = null;
            uiState.setValue(state);
        }
    }
    
    public boolean isLoggedIn() {
        return tokenManager.isLoggedIn();
    }
}
