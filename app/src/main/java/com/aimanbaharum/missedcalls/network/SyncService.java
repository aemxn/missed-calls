package com.aimanbaharum.missedcalls.network;

import com.aimanbaharum.missedcalls.network.interfaces.SyncCallback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by aimanb on 16/12/2016.
 */

public class SyncService extends BaseHttpCall {

    public SyncService() {
    }

    public void syncNumbers(String endpoint, String[] numbers,
                            final SyncCallback.SyncApiCallback callback) {

        StringBuilder sb = new StringBuilder();
        for (String n : numbers) {
            if (sb.length() > 0) sb.append(',');
            sb.append(n);
        }

        String fEndpoint = endpoint + "?numbers=" + sb.toString();

        try {
//            MediaType mediaType = MediaType.parse("application/json");
//            RequestBody requestBody = RequestBody.create(mediaType, postJson.toString());
            Request request = post(fEndpoint, null);
            new Client().getService().newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    callback.onSyncApiFailure(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonResponse = response.body().string();
                    if (response.isSuccessful()) {
                        callback.onSyncApiSuccess(HttpStatus.SUCCESS, jsonResponse);
                    } else {
                        callback.onSyncApiSuccess(HttpStatus.FAILED, jsonResponse);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            callback.onSyncApiFailure(e.getMessage());
        }
    }
}
