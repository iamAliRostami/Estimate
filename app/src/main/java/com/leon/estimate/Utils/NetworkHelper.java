package com.leon.estimate.Utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Enums.CompanyNames;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Leon on 12/9/2017.
 */

public final class NetworkHelper {
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final long READ_TIMEOUT = 120;
    private static final long WRITE_TIMEOUT = 60;
    private static final long CONNECT_TIMEOUT = 10;
    private static final boolean RETRY_ENABLED = true;

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
        String baseUrl = DifferentCompanyManager.getBaseUrl(
                DifferentCompanyManager.getActiveCompanyName());
        if (!b)
            baseUrl = DifferentCompanyManager.getLocalBaseUrl(CompanyNames.SEPANO);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(NetworkHelper.getHttpClient(token))
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit;
    }

    public static Retrofit getInstanceMap() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        String baseUrl = DifferentCompanyManager.getBaseUrl(
                DifferentCompanyManager.getActiveCompanyName());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(NetworkHelper.getHttpClient(""))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }

    public static Retrofit getInstanceWithCache(Context context) {
        int cacheSize = 50 * 1024 * 1024; // 50 MB
        File httpCacheDirectory = new File(context.getCacheDir(), "responses");
        Cache cache = new Cache(httpCacheDirectory, cacheSize);
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(READ_TIMEOUT, TIME_UNIT)
                .writeTimeout(WRITE_TIMEOUT, TIME_UNIT).connectTimeout(CONNECT_TIMEOUT, TIME_UNIT)
                .retryOnConnectionFailure(RETRY_ENABLED).addInterceptor(chain ->
                        chain.proceed(chain.request().newBuilder().build()))
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY)).cache(cache).build();
        return new Retrofit.Builder()
                .baseUrl(DifferentCompanyManager.getBaseUrl(
                        DifferentCompanyManager.getActiveCompanyName()))
                .client(client).addConverterFactory(GsonConverterFactory
                        .create(new GsonBuilder().setLenient().create())).build();
    }
}
