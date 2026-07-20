package com.example.frontend.data.remote;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkRetryInterceptor implements Interceptor {
    private final int maxRetries;

    public NetworkRetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        boolean responseOK = false;
        int tryCount = 0;

        while (!responseOK && tryCount < maxRetries) {
            try {
                if (response != null) {
                    response.close();
                }
                response = chain.proceed(request);
                
                // Retry for 5xx errors or null response
                responseOK = response.isSuccessful() || response.code() < 500;
            } catch (IOException e) {
                if (tryCount >= maxRetries - 1) {
                    throw e; // Rethrow on last attempt
                }
                // Sleep with exponential backoff could be added here, e.g., Thread.sleep(...)
                try {
                    Thread.sleep(1000 * (tryCount + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            } finally {
                tryCount++;
            }
        }

        // Return whatever the last response was, or throw an exception if none was obtained
        if (response == null) {
            throw new IOException("Failed to execute request after " + maxRetries + " retries");
        }
        return response;
    }
}
