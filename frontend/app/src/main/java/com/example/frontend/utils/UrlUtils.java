package com.example.frontend.utils;

import com.example.frontend.data.remote.ApiClient;

public class UrlUtils {
    public static String getFullUrl(String relativeUrl) {
        if (relativeUrl == null || relativeUrl.isEmpty()) {
            return null;
        }

        if (relativeUrl.startsWith("http://") || relativeUrl.startsWith("https://") || relativeUrl.startsWith("content://") || relativeUrl.startsWith("file://")) {
            return relativeUrl;
        }

        String baseUrl = ApiClient.BASE_URL;
        if (baseUrl.endsWith("/") && relativeUrl.startsWith("/")) {
            return baseUrl + relativeUrl.substring(1);
        } else if (!baseUrl.endsWith("/") && !relativeUrl.startsWith("/")) {
            return baseUrl + "/" + relativeUrl;
        } else {
            return baseUrl + relativeUrl;
        }
    }
}
