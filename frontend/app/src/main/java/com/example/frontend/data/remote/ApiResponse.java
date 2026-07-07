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

    @SerializedName("pagination")
    private PaginationInfo pagination;

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

    public PaginationInfo getPagination() {
        return pagination;
    }

    public String getError() {
        return error;
    }

    public static class PaginationInfo {
        @SerializedName("page")
        private int page;
        @SerializedName("limit")
        private int limit;
        @SerializedName("total")
        private int total;
        @SerializedName("totalPages")
        private int totalPages;

        public int getPage() { return page; }
        public int getLimit() { return limit; }
        public int getTotal() { return total; }
        public int getTotalPages() { return totalPages; }
    }
}
