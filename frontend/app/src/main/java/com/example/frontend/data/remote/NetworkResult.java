package com.example.frontend.data.remote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NetworkResult<T> {
    public enum Status {
        LOADING,
        SUCCESS,
        EMPTY,
        ERROR,
        NO_INTERNET,
        UNAUTHORIZED,
        GUEST
    }

    @NonNull
    public final Status status;
    @Nullable
    public final T data;
    @Nullable
    public final String message;

    private NetworkResult(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> NetworkResult<T> loading() {
        return new NetworkResult<>(Status.LOADING, null, null);
    }

    public static <T> NetworkResult<T> success(@NonNull T data) {
        return new NetworkResult<>(Status.SUCCESS, data, null);
    }

    public static <T> NetworkResult<T> empty() {
        return new NetworkResult<>(Status.EMPTY, null, null);
    }

    public static <T> NetworkResult<T> error(@NonNull String message) {
        return new NetworkResult<>(Status.ERROR, null, message);
    }

    public static <T> NetworkResult<T> error(@NonNull String message, @Nullable T data) {
        return new NetworkResult<>(Status.ERROR, data, message);
    }

    public static <T> NetworkResult<T> noInternet() {
        return new NetworkResult<>(Status.NO_INTERNET, null, "No internet connection");
    }

    public static <T> NetworkResult<T> unauthorized() {
        return new NetworkResult<>(Status.UNAUTHORIZED, null, "Unauthorized access");
    }

    public static <T> NetworkResult<T> guest() {
        return new NetworkResult<>(Status.GUEST, null, "Guest user");
    }
}
