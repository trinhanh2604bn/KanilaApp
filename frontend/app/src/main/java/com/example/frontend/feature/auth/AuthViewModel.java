package com.example.frontend.feature.auth;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.auth.AuthResponse;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repository;
    private final MutableLiveData<NetworkResult<AuthResponse>> authResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<AuthResponse>> verifyResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<Void>> resetPasswordResult = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.repository = new AuthRepository(application);
    }

    public LiveData<NetworkResult<AuthResponse>> getAuthResult() {
        return authResult;
    }

    public LiveData<NetworkResult<AuthResponse>> getVerifyResult() {
        return verifyResult;
    }

    public LiveData<NetworkResult<Void>> getResetPasswordResult() {
        return resetPasswordResult;
    }

    public void login(String channel, String identifier) {
        repository.login(channel, identifier, authResult);
    }

    public void forgotPassword(String channel, String identifier) {
        repository.forgotPassword(channel, identifier, authResult);
    }

    public void register(String channel, String fullName, String email, String phone, boolean terms, boolean marketing) {
        repository.register(channel, fullName, email, phone, terms, marketing, authResult);
    }

    public void verifyOtp(String type, String value, String otp, String purpose) {
        repository.verifyOtp(type, value, otp, purpose, verifyResult);
    }

    public void resetPassword(String resetToken, String newPassword, String confirmPassword) {
        repository.resetPassword(resetToken, newPassword, confirmPassword, resetPasswordResult);
    }
}
