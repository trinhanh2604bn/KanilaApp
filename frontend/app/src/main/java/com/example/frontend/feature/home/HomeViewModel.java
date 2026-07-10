package com.example.frontend.feature.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.data.repository.HomeRepository;
import com.example.frontend.model.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private final HomeRepository homeRepository;
    private final MutableLiveData<HomeUiState> uiState = new MutableLiveData<>(new HomeUiState());
    private final TokenManager tokenManager;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        this.homeRepository = new HomeRepository(application);
        this.tokenManager = TokenManager.getInstance(application);
        
        // Khởi tạo trạng thái ban đầu với dữ liệu Mock để tránh màn hình trắng
        HomeUiState initialState = new HomeUiState();
        initialState.recommendedProducts = getMockProducts("Gợi ý", "s1");
        initialState.allProducts = getMockProducts("Tất cả", "p1");
        uiState.setValue(initialState);
    }

    public LiveData<HomeUiState> getUiState() {
        return uiState;
    }

    public void loadHomeData() {
        HomeUiState current = uiState.getValue();
        if (current == null) current = new HomeUiState();
        current.loading = true;
        current.error = null;
        uiState.setValue(current);

        final boolean[] recommendedDone = {false};
        final boolean[] allProductsDone = {false};

        // Load recommended products
        MutableLiveData<NetworkResult<List<Product>>> recommendedResult = new MutableLiveData<>();
        recommendedResult.observeForever(new Observer<NetworkResult<List<Product>>>() {
            @Override
            public void onChanged(NetworkResult<List<Product>> result) {
                if (result == null) return;
                if (result.status != NetworkResult.Status.LOADING) {
                    handleRecommendedResult(result, recommendedDone, allProductsDone);
                    recommendedResult.removeObserver(this);
                }
            }
        });

        // Load all products
        MutableLiveData<NetworkResult<List<Product>>> allProductsResult = new MutableLiveData<>();
        allProductsResult.observeForever(new Observer<NetworkResult<List<Product>>>() {
            @Override
            public void onChanged(NetworkResult<List<Product>> result) {
                if (result == null) return;
                if (result.status != NetworkResult.Status.LOADING) {
                    handleAllProductsResult(result, recommendedDone, allProductsDone);
                    allProductsResult.removeObserver(this);
                }
            }
        });

        try {
            if (tokenManager.isLoggedIn()) {
                homeRepository.getHomepageRecommendations(recommendedResult);
            } else {
                homeRepository.getProducts("popular", recommendedResult);
            }
            homeRepository.getProducts(null, allProductsResult);
        } catch (Exception e) {
            // Nếu có lỗi hệ thống (như GSON crash), vẫn giữ dữ liệu Mock
            HomeUiState state = uiState.getValue();
            if (state != null) {
                state.loading = false;
                uiState.setValue(state);
            }
        }
    }

    private void handleRecommendedResult(NetworkResult<List<Product>> result, boolean[] recommendedDone, boolean[] allProductsDone) {
        HomeUiState state = uiState.getValue();
        if (state == null) state = new HomeUiState();

        if (result.status == NetworkResult.Status.SUCCESS || result.status == NetworkResult.Status.EMPTY) {
            if (result.data != null && !result.data.isEmpty()) {
                state.recommendedProducts = result.data;
            }
            recommendedDone[0] = true;
            checkLoadingState(state, recommendedDone[0], allProductsDone[0]);
        } else if (result.status == NetworkResult.Status.ERROR) {
            // Lỗi API thì vẫn giữ dữ liệu mock cũ, chỉ tắt loading
            recommendedDone[0] = true;
            checkLoadingState(state, recommendedDone[0], allProductsDone[0]);
        } else if (result.status == NetworkResult.Status.UNAUTHORIZED) {
            recommendedDone[0] = true;
            checkLoadingState(state, recommendedDone[0], allProductsDone[0]);
        }
    }

    private void handleAllProductsResult(NetworkResult<List<Product>> result, boolean[] recommendedDone, boolean[] allProductsDone) {
        HomeUiState state = uiState.getValue();
        if (state == null) state = new HomeUiState();

        if (result.status == NetworkResult.Status.SUCCESS || result.status == NetworkResult.Status.EMPTY) {
            if (result.data != null && !result.data.isEmpty()) {
                state.allProducts = result.data;
            }
            allProductsDone[0] = true;
            checkLoadingState(state, recommendedDone[0], allProductsDone[0]);
        } else if (result.status == NetworkResult.Status.ERROR) {
            allProductsDone[0] = true;
            checkLoadingState(state, recommendedDone[0], allProductsDone[0]);
        }
    }

    private void checkLoadingState(HomeUiState state, boolean recommendedDone, boolean allProductsDone) {
        if (recommendedDone && allProductsDone) {
            state.loading = false;
            state.error = null;
            uiState.setValue(state);
        }
    }

    private List<Product> getMockProducts(String prefix, String idPrefix) {
        List<Product> list = new ArrayList<>();
        list.add(new Product(idPrefix + "1", "Kanila", prefix + " Sản phẩm 1", "250000", "4.5", "100", com.example.frontend.R.drawable.ic_product, "New", "Skincare"));
        list.add(new Product(idPrefix + "2", "Kanila", prefix + " Sản phẩm 2", "350000", "4.8", "200", com.example.frontend.R.drawable.ic_product, "Hot", "Makeup"));
        list.add(new Product(idPrefix + "3", "Kanila", prefix + " Sản phẩm 3", "150000", "4.2", "50", com.example.frontend.R.drawable.ic_product, "", "Lipstick"));
        list.add(new Product(idPrefix + "4", "Kanila", prefix + " Sản phẩm 4", "450000", "4.9", "500", com.example.frontend.R.drawable.ic_product, "Sale", "Cushion"));
        return list;
    }
    
    public boolean isLoggedIn() {
        return tokenManager.isLoggedIn();
    }
}
