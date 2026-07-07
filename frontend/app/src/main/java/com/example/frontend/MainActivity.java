package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.frontend.feature.home.HomeFragment;
import com.example.frontend.feature.home.HomeViewModel;
import com.example.frontend.utils.ToastHelper;

import ui.commerce.CartFragment;

public class MainActivity extends AppCompatActivity {

    private HomeViewModel viewModel;
    private com.example.frontend.feature.wishlist.WishlistViewModel wishlistViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        wishlistViewModel = new ViewModelProvider(this).get(com.example.frontend.feature.wishlist.WishlistViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main, new HomeFragment())
                    .commit();
        }

        checkAuthStatus();
    }

    private void checkAuthStatus() {
        com.example.frontend.data.remote.TokenManager tm = com.example.frontend.data.remote.TokenManager.getInstance(this);
        if (tm.isLoggedIn()) {
            // Validate token by calling /me
            com.example.frontend.data.remote.ApiClient.getClient(this)
                    .create(com.example.frontend.data.remote.ApiService.class)
                    .getMe()
                    .enqueue(new retrofit2.Callback<com.example.frontend.data.remote.ApiResponse<Object>>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.example.frontend.data.remote.ApiResponse<Object>> call, retrofit2.Response<com.example.frontend.data.remote.ApiResponse<Object>> response) {
                            if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                                tm.clearToken();
                                ToastHelper.showShort(MainActivity.this, "Phien dang nhap het han");
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<com.example.frontend.data.remote.ApiResponse<Object>> call, Throwable t) {
                            // Network error, maybe don't clear token yet
                        }
                    });
        }
    }

    public void navigateToCart() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main, new CartFragment())
                .addToBackStack(null)
                .commit();
    }
}
