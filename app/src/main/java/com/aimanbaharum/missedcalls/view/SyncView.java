package com.aimanbaharum.missedcalls.view;

/**
 * Created by aimanb on 16/12/2016.
 */

public interface SyncView {
    void onSyncSuccess(String message);
    void onSyncFailed(String message);
}
