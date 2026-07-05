package com.example.frontend.feature.auth;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.frontend.data.model.auth.LoginResponse;
import com.example.frontend.data.remote.NetworkResult;
import com.example.frontend.data.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private final AuthRepository repository;
    private final MutableLiveData<NetworkResult<LoginResponse>> loginResult = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.repository = new AuthRepository(application);
    }

    public LiveData<NetworkResult<LoginResponse>> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        repository.login(email, password, loginResult);
    }
}
