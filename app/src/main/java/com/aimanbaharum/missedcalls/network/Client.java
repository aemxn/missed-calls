package com.aimanbaharum.missedcalls.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by aimanb on 16/12/2016.
 */

public class Client {

    private OkHttpClient okHttpClient;

    public Client() {

        okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
//                .addInterceptor(new NetworkInterceptor())
                .build();
    }

    public OkHttpClient getService() {
        return okHttpClient;
    }
}
