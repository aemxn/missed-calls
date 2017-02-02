package com.aimanbaharum.missedcalls.ui.main;

import com.aimanbaharum.missedcalls.base.RemoteView;
import com.aimanbaharum.missedcalls.data.Calls;

import java.util.List;

/**
 * Created by cliqers on 26/1/2017.
 */

public interface MainContract {

    interface ViewActions {
        void onListRequested();
        void onSyncRequested();
        void onStartLogCalls();
    }

    interface MainView extends RemoteView {
        void onShowMissedCalls(List<Calls> callsList);
        void onSyncSuccess(String message);
        void onSyncFailed(String message);
    }
}
