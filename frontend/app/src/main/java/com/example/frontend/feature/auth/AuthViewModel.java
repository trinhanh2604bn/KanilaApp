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
    private final MutableLiveData<NetworkResult<AuthResponse>> loginResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<AuthResponse>> registerResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<AuthResponse>> forgotPasswordResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<AuthResponse>> verifyResult = new MutableLiveData<>();
    private final MutableLiveData<NetworkResult<Void>> resetPasswordResult = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.repository = new AuthRepository(application);
    }

    public LiveData<NetworkResult<AuthResponse>> getLoginResult() {
        return loginResult;
    }

    public LiveData<NetworkResult<AuthResponse>> getRegisterResult() {
        return registerResult;
    }

    public LiveData<NetworkResult<AuthResponse>> getForgotPasswordResult() {
        return forgotPasswordResult;
    }

    public LiveData<NetworkResult<AuthResponse>> getVerifyResult() {
        return verifyResult;
    }

    public LiveData<NetworkResult<Void>> getResetPasswordResult() {
        return resetPasswordResult;
    }

    public void resetStates() {
        loginResult.setValue(null);
        registerResult.setValue(null);
        forgotPasswordResult.setValue(null);
        verifyResult.setValue(null);
        resetPasswordResult.setValue(null);
    }

    public void login(String channel, String identifier, String password) {
        repository.login(channel, identifier, password, loginResult);
    }

    public void login(String channel, String identifier) {
        repository.login(channel, identifier, loginResult);
    }

    public void forgotPassword(String channel, String identifier) {
        repository.forgotPassword(channel, identifier, forgotPasswordResult);
    }

    public void register(String channel, String fullName, String email, String phone, String password, boolean terms, boolean marketing) {
        repository.register(channel, fullName, email, phone, password, terms, marketing, registerResult);
    }

    public void verifyOtp(String type, String value, String otp, String purpose) {
        repository.verifyOtp(type, value, otp, purpose, verifyResult);
    }

    public void resetPassword(String resetToken, String newPassword, String confirmPassword) {
        repository.resetPassword(resetToken, newPassword, confirmPassword, resetPasswordResult);
    }
}
