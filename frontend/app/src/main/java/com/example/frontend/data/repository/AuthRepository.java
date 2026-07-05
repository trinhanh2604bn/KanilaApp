package com.example.frontend.data.repository;

import android.content.Context;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final ApiService apiService;
    private final TokenManager tokenManager;

    public AuthRepository(Context context) {
        this.apiService = ApiClient.getClient(context).create(ApiService.class);
        this.tokenManager = TokenManager.getInstance(context);
    }

    // Add login, register methods here
}
