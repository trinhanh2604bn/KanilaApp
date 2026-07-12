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

        // Load all products
        MutableLiveData<NetworkResult<List<Product>>> allProductsResult = new MutableLiveData<>();
        allProductsResult.observeForever(new Observer<NetworkResult<List<Product>>>() {
            @Override
            public void onChanged(NetworkResult<List<Product>> result) {
                if (result == null) return;
                if (result.status != NetworkResult.Status.LOADING) {
                    handleAllProductsResult(result);
                    allProductsResult.removeObserver(this);
                }
            }
        });

        try {
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

    private void handleAllProductsResult(NetworkResult<List<Product>> result) {
        HomeUiState state = uiState.getValue();
        if (state == null) state = new HomeUiState();

        if (result.status == NetworkResult.Status.SUCCESS || result.status == NetworkResult.Status.EMPTY) {
            if (result.data != null && !result.data.isEmpty()) {
                state.allProducts = result.data;
            }
            state.loading = false;
            state.error = null;
            uiState.setValue(state);
        } else if (result.status == NetworkResult.Status.ERROR) {
            state.loading = false;
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
