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
        
        // Reset state for new load
        current.loading = true;
        current.error = null;
        current.recommendedError = null;
        current.allProductsError = null;
        uiState.setValue(current);

        // Load recommended products
        homeRepository.getHomepageRecommendations(new MutableLiveData<NetworkResult<List<Product>>>() {
            @Override
            protected void onActive() {}

            @Override
            public void setValue(NetworkResult<List<Product>> result) {
                if (result == null) return;
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
                    checkLoadingState(state);
                } else if (result.status == NetworkResult.Status.UNAUTHORIZED) {
                    state.recommendedError = "Unauthorized";
                    checkLoadingState(state);
                }
            }
        });

        // Load all products
        homeRepository.getProducts(null, new MutableLiveData<NetworkResult<List<Product>>>() {
            @Override
            protected void onActive() {}

            @Override
            public void setValue(NetworkResult<List<Product>> result) {
                if (result == null) return;
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
                } else if (result.status == NetworkResult.Status.UNAUTHORIZED) {
                    state.allProductsError = "Unauthorized";
                    checkLoadingState(state);
                }
            }
        });
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
