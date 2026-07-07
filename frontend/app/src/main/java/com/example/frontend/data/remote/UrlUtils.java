package com.example.frontend.data.remote;

public class UrlUtils {
    public static String getFullUrl(String relativeOrFullUrl) {
        if (relativeOrFullUrl == null || relativeOrFullUrl.isEmpty()) {
            return null;
        }

        // If it's already an absolute URL starting with http, check if it contains localhost or example.com
        if (relativeOrFullUrl.startsWith("http")) {
            String url = relativeOrFullUrl.replace("localhost", "10.0.2.2");
            if (url.contains("example.com")) {
                String baseWithoutSlash = ApiClient.BASE_URL;
                if (baseWithoutSlash.endsWith("/")) {
                    baseWithoutSlash = baseWithoutSlash.substring(0, baseWithoutSlash.length() - 1);
                }
                return url.replace("https://example.com", baseWithoutSlash)
                          .replace("http://example.com", baseWithoutSlash);
            }
            return url;
        }

        // Prepend BASE_URL if it's a relative path
        String baseUrl = ApiClient.BASE_URL;
        if (relativeOrFullUrl.startsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1) + relativeOrFullUrl;
        } else {
            return baseUrl + relativeOrFullUrl;
        }
    }
}
