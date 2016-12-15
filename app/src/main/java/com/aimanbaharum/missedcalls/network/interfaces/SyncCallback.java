package com.aimanbaharum.missedcalls.network.interfaces;

/**
 * Created by aimanb on 16/12/2016.
 */

public interface SyncCallback {

    interface SyncApiCallback {
        void onSyncApiSuccess(int code, String response);
        void onSyncApiFailure(String message);
    }

}
