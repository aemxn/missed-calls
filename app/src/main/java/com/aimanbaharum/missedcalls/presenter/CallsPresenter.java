package com.aimanbaharum.missedcalls.presenter;

import android.content.Context;

import com.aimanbaharum.missedcalls.model.Calls;
import com.aimanbaharum.missedcalls.network.HttpStatus;
import com.aimanbaharum.missedcalls.network.SyncService;
import com.aimanbaharum.missedcalls.network.interfaces.SyncCallback;
import com.aimanbaharum.missedcalls.utils.PrefKey;
import com.aimanbaharum.missedcalls.view.CallsView;
import com.aimanbaharum.missedcalls.view.SyncView;
import com.iamhabib.easy_preference.EasyPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aimanb on 16/12/2016.
 */

public class CallsPresenter {

    private static final String TAG = CallsPresenter.class.getSimpleName();
    private Context mContext;


    public CallsPresenter(Context context) {
        this.mContext = context;
    }

    public void showMissedCalledList(CallsView callsView) {

        List<Calls> callsList = Calls.getMissedCalledList();

        if (callsList.size() > 0) {
            callsView.onShowMissedCalls(callsList);
        } else {
            callsView.onEmptyMissedCalls();
        }
    }

    public void syncNumbers(final SyncView syncView) {

        String strEndpoint = EasyPreference.with(mContext)
                .getString(PrefKey.KEY_ENDPOINT.name(), "");

        if (!strEndpoint.isEmpty()) {
//            final List<Calls> unsyncedNumbers = Calls.getUnsynced();
            final List<Calls> allNumbers = Calls.getMissedCalledList();

            // Numbers to be uploaded is limited
            int syncLimit = EasyPreference.with(mContext)
                    .getInt(PrefKey.KEY_SYNC_LIMIT.name(), allNumbers.size());
            List<Calls> limNumbers = new ArrayList<>();

            for (int i = 0; i < (syncLimit > allNumbers.size() ? allNumbers.size() : syncLimit); i++) {
                limNumbers.add(allNumbers.get(i));
            }

            if (limNumbers.size() > 0) {

                String[] numbers = new String[limNumbers.size()];
                for (int i = 0; i < numbers.length; i++) {
                    numbers[i] = limNumbers.get(i).getCallerNumber();
                }

                SyncService syncService = new SyncService();
                syncService.syncNumbers(strEndpoint, numbers, new SyncCallback.SyncApiCallback() {
                    @Override
                    public void onSyncApiSuccess(int code, String response) {
                        switch (code) {
                            case HttpStatus.SUCCESS:
//                                Calls.setSynced(allNumbers);
                                syncView.onSyncSuccess(response);
                                break;
                            case HttpStatus.FAILED:
                                syncView.onSyncFailed(response);
                                break;
                        }
                    }

                    @Override
                    public void onSyncApiFailure(String message) {
                        syncView.onSyncFailed(message);
                    }
                });
            } else {
                syncView.onSyncFailed("No unsynced numbers");
            }
        } else {
            syncView.onSyncFailed("Please set endpoint in the Settings");
        }
    }
}
