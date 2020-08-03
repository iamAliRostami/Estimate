package com.leon.estimate.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Enums.CompanyNames;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Leon on 12/9/2017.
 */

public final class NetworkHelper {
    private static TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static long READ_TIMEOUT = 120;
    private static long WRITE_TIMEOUT = 60;
    private static long CONNECT_TIMEOUT = 10;
    private static boolean RETRY_ENABLED = true;

    private NetworkHelper() {

    }

    public static OkHttpClient getHttpClient(final String token) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient
                .Builder()
                .readTimeout(READ_TIMEOUT, TIME_UNIT)
                .writeTimeout(WRITE_TIMEOUT, TIME_UNIT)
                .connectTimeout(CONNECT_TIMEOUT, TIME_UNIT)
                .retryOnConnectionFailure(RETRY_ENABLED)
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer " + token).build();
                    return chain.proceed(request);
                })
                .addInterceptor(interceptor).build();

        return client;
    }

    public static Retrofit getInstance(boolean b, String token) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        String baseUrl = DifferentCompanyManager.getBaseUrl(DifferentCompanyManager.getActiveCompanyName());
        if (!b)
            baseUrl = DifferentCompanyManager.getLocalBaseUrl(CompanyNames.SEPANO);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(NetworkHelper.getHttpClient(token))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }
}
