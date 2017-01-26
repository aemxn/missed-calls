package com.aimanbaharum.missedcalls.ui.main;

import android.content.Context;

import com.aimanbaharum.missedcalls.base.BasePresenter;
import com.aimanbaharum.missedcalls.model.Calls;
import com.aimanbaharum.missedcalls.network.HttpStatus;
import com.aimanbaharum.missedcalls.network.SyncService;
import com.aimanbaharum.missedcalls.network.interfaces.SyncCallback;
import com.aimanbaharum.missedcalls.utils.PrefKey;
import com.iamhabib.easy_preference.EasyPreference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aimanb on 16/12/2016.
 */

public class MainPresenter extends BasePresenter<MainContract.MainView> implements MainContract.ViewActions {

    private static final String TAG = MainPresenter.class.getSimpleName();
    private Context mContext;


    public MainPresenter(Context context) {
        this.mContext = context;
    }

    @Override
    public void onListRequested() {
        showMissedCalledList();
    }

    @Override
    public void onSyncRequested() {
        syncNumbers();
    }

    private void showMissedCalledList() {
        if (!isViewAttached()) return;
        mView.showMessageLayout(false);

        List<Calls> callsList = Calls.getMissedCalledList();
        if (callsList.size() > 0) {
            mView.onShowMissedCalls(callsList);
        } else {
            mView.showEmpty();
        }
    }

    private void syncNumbers() {

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
                                mView.onSyncSuccess(response);
                                break;
                            case HttpStatus.FAILED:
                                mView.onSyncFailed(response);
                                break;
                        }
                    }

                    @Override
                    public void onSyncApiFailure(String message) {
                        mView.showError(message);
                    }
                });
            } else {
                mView.showError("No unsynced numbers");
            }
        } else {
            mView.showError("Please set endpoint in the Settings");
        }
    }
}
