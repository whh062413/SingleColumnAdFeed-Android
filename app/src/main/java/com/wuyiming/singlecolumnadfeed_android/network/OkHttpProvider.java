package com.wuyiming.singlecolumnadfeed_android.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpProvider {
    private static volatile OkHttpClient instance;

    public static OkHttpClient getInstance() {
        if (instance == null) {
            synchronized (OkHttpProvider.class) {
                if (instance == null) {
                    instance = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return instance;
    }
}
