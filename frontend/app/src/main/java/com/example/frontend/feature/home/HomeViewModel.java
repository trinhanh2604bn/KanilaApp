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
                state.recommendedError = null;
                checkLoadingState(state);
            } else if (result.status == NetworkResult.Status.EMPTY) {
                state.recommendedProducts = new java.util.ArrayList<>();
                state.recommendedError = null;
                checkLoadingState(state);
            } else if (result.status == NetworkResult.Status.ERROR) {
                state.recommendedError = result.message;
                // If it's a personalized recommendation failure, we don't necessarily want to fail the whole page
                // But we still need to stop loading state if this was the last thing
                checkLoadingState(state);
            }
        });

        // Load all products
        MutableLiveData<NetworkResult<List<Product>>> allProductsResult = new MutableLiveData<>();
        allProductsResult.observeForever(result -> {
            HomeUiState state = uiState.getValue();
            if (state == null) state = new HomeUiState();
            
            if (result.status == NetworkResult.Status.SUCCESS) {
                state.allProducts = result.data;
                state.allProductsError = null;
                checkLoadingState(state);
            } else if (result.status == NetworkResult.Status.EMPTY) {
                state.allProducts = new java.util.ArrayList<>();
                state.allProductsError = null;
                checkLoadingState(state);
            } else if (result.status == NetworkResult.Status.ERROR) {
                state.allProductsError = result.message;
                checkLoadingState(state);
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
        boolean recommendedDone = state.recommendedProducts != null || state.recommendedError != null;
        boolean allProductsDone = state.allProducts != null || state.allProductsError != null;
        
        if (recommendedDone && allProductsDone) {
            state.loading = false;
            // General error is only set if everything fails or if we want to show a page-level error
            if (state.allProductsError != null && state.recommendedError != null) {
                state.error = "Không thể tải dữ liệu trang chủ. Vui lòng thử lại.";
            } else {
                state.error = null;
            }
            uiState.setValue(state);
        }
    }
    
    public boolean isLoggedIn() {
        return tokenManager.isLoggedIn();
    }
}
