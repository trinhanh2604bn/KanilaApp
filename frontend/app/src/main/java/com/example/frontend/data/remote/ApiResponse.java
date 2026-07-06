package com.example.frontend.data.remote;

import com.google.gson.annotations.SerializedName;

public class ApiResponse<T> {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("count")
    private int count;

    @SerializedName("data")
    private T data;

    @SerializedName("error")
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return count;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
