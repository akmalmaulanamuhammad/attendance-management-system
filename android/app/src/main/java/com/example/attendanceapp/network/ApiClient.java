package com.example.attendanceapp.network;

import com.example.attendanceapp.utils.Constants;
import com.example.attendanceapp.utils.PreferenceManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final int TIMEOUT = 60; // seconds
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    public static ApiService getClient() {
        if (apiService == null) {
            apiService = getRetrofitInstance().create(ApiService.class);
        }
        return apiService;
    }

    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            // Create OkHttpClient with logging and timeout
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS);

            // Add logging interceptor for debug builds
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                httpClient.addInterceptor(logging);
            }

            // Add authorization interceptor
            httpClient.addInterceptor(chain -> {
                okhttp3.Request original = chain.request();
                PreferenceManager prefs = new PreferenceManager(AttendanceApp.getAppContext());
                String token = prefs.getAuthToken();

                // If token exists, add it to the header
                if (token != null && !original.url().toString().contains(Constants.ENDPOINT_LOGIN)) {
                    okhttp3.Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .method(original.method(), original.body());
                    
                    return chain.proceed(requestBuilder.build());
                }

                return chain.proceed(original);
            });

            // Build Retrofit instance
            retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        }
        return retrofit;
    }

    public static void resetClient() {
        retrofit = null;
        apiService = null;
    }

    // Helper method to create RequestBody from String
    public static okhttp3.RequestBody createRequestBody(String value) {
        return okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), value);
    }

    // Helper method to create MultipartBody.Part from file
    public static MultipartBody.Part createMultipartBody(File file, String paramName) {
        RequestBody requestFile = RequestBody.create(
            MediaType.parse("image/*"),
            file
        );
        return MultipartBody.Part.createFormData(paramName, file.getName(), requestFile);
    }
}
