package com.aimanbaharum.missedcalls.network;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by aimanb on 16/12/2016.
 */

public abstract class BaseHttpCall {

    public Request post(String endpoint, RequestBody requestBody) {
        return new Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
    }
}
