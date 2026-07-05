package com.example.frontend.feature.home;

import com.example.frontend.model.Product;
import java.util.List;

public class HomeUiState {
    public boolean loading = false;
    public String error = null;
    public List<Product> products = null;
    public int cartCount = 0;
    public int wishlistCount = 0;
    public int couponCount = 0;
    public boolean isLoggedIn = false;

    public HomeUiState() {}

    public static HomeUiState loading() {
        HomeUiState state = new HomeUiState();
        state.loading = true;
        return state;
    }

    public static HomeUiState success(List<Product> products) {
        HomeUiState state = new HomeUiState();
        state.products = products;
        return state;
    }

    public static HomeUiState error(String message) {
        HomeUiState state = new HomeUiState();
        state.error = message;
        return state;
    }
}
