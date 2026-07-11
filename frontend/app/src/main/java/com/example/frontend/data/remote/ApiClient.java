package com.example.frontend.data.remote;

import android.content.Context;
import com.example.frontend.BuildConfig;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Emulator: http://10.0.2.2:5000/
     //Physical Device: http://192.168.171.141:5000/ (e.g., http://192.168.1.5:5000/) (GIA NGAN HELUS) http://192.168.171.111:5000 (ANH: http://10.160.98.213:5000/)
    //192.168.171.109 (chos phú)
    private static final String BASE_URL = "http://10.0.2.2:5000/";
    // Important: must end with / for Retrofit
    // Physical Device: http://10.160.98.213:5000/ (e.g., http://192.168.1.5:5000/) (GIA NGAN HELUS) http://10.160.98.85:5000/ (ANH: http://10.160.98.213:5000/)
//    TT: 192.168.110.214  ;   192.168.171.212
//    private static final String BASE_URL = "http://10.141.160.213:5000/"; // Important: must end with / for Retrofit


    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            // Only log in debug builds
            logging.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

            TokenManager tokenManager = TokenManager.getInstance(context);
            AuthInterceptor authInterceptor = new AuthInterceptor(tokenManager);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .addInterceptor(authInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
