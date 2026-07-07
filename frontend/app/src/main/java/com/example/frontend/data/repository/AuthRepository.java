package com.example.frontend.data.repository;

import android.content.Context;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.remote.ApiClient;
import com.example.frontend.data.remote.ApiResponse;
import com.example.frontend.data.remote.ApiService;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.remote.TokenManager;
import com.example.frontend.data.model.auth.LoginRequest;
import com.example.frontend.data.model.auth.RegisterRequest;
import com.example.frontend.data.model.auth.VerifyOtpRequest;
import com.example.frontend.data.model.auth.AuthResponse;
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

    public void login(String channel, String identifier, MutableLiveData<NetworkResult<AuthResponse>> result) {
        result.setValue(NetworkResult.loading());
        String guestSessionId = tokenManager.getGuestSession();
        apiService.login(new LoginRequest(channel, identifier, guestSessionId)).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                handleAuthResponse(response, result);
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void forgotPassword(String channel, String identifier, MutableLiveData<NetworkResult<AuthResponse>> result) {
        result.setValue(NetworkResult.loading());
        String guestSessionId = tokenManager.getGuestSession();
        apiService.forgotPassword(new LoginRequest(channel, identifier, guestSessionId)).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                handleAuthResponse(response, result);
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void register(String channel, String fullName, String email, String phone, 
                         boolean termsAccepted, boolean marketingOptIn, MutableLiveData<NetworkResult<AuthResponse>> result) {
        result.setValue(NetworkResult.loading());
        String guestSessionId = tokenManager.getGuestSession();
        apiService.register(new RegisterRequest(channel, fullName, email, phone, termsAccepted, marketingOptIn, guestSessionId))
                .enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                handleAuthResponse(response, result);
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void verifyOtp(String type, String value, String otp, String purpose, MutableLiveData<NetworkResult<AuthResponse>> result) {
        result.setValue(NetworkResult.loading());
        String guestSessionId = tokenManager.getGuestSession();
        apiService.verifyOtp(new VerifyOtpRequest(type, value, otp, purpose, guestSessionId)).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        AuthResponse data = apiResponse.getData();
                        // Only save tokens if they exist (login/register success)
                        // If purpose is reset_password, it returns reset_token instead of access_token
                        if (data.getAccessToken() != null) {
                            tokenManager.saveTokens(data.getAccessToken(), data.getRefreshToken());
                        }
                        result.setValue(NetworkResult.success(data));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Verification failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    public void resetPassword(String resetToken, String newPassword, String confirmPassword, MutableLiveData<NetworkResult<Void>> result) {
        result.setValue(NetworkResult.loading());
        apiService.resetPassword(new com.example.frontend.data.model.auth.ResetPasswordRequest(resetToken, newPassword, confirmPassword))
                .enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        result.setValue(NetworkResult.success(null));
                    } else {
                        result.setValue(NetworkResult.error(apiResponse.getMessage()));
                    }
                } else {
                    result.setValue(NetworkResult.error("Reset password failed"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                result.setValue(NetworkResult.error(t.getMessage()));
            }
        });
    }

    private void handleAuthResponse(Response<ApiResponse<AuthResponse>> response, MutableLiveData<NetworkResult<AuthResponse>> result) {
        if (response.isSuccessful() && response.body() != null) {
            ApiResponse<AuthResponse> apiResponse = response.body();
            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                AuthResponse data = apiResponse.getData();
                if (data.getAccessToken() != null) {
                    tokenManager.saveTokens(data.getAccessToken(), data.getRefreshToken());
                }
                result.setValue(NetworkResult.success(data));
            } else {
                result.setValue(NetworkResult.error(apiResponse.getMessage()));
            }
        } else {
            result.setValue(NetworkResult.error("Request failed"));
        }
    }
}
