package com.aimanbaharum.missedcalls.db;

import android.support.annotation.NonNull;

import com.aimanbaharum.missedcalls.data.Calls;

import java.util.List;

import io.realm.Realm;
import io.realm.Sort;

/**
 * Created by cliqers on 2/2/2017.
 */

public class CallsStorage extends BaseStorage {

    public static List<Calls> getMissedCalledList(@NonNull Realm realm) {
        return realm.where(Calls.class)
                .findAll().sort("callerTimestamp", Sort.DESCENDING);
    }

    public static List<Calls> getUnsynced(@NonNull Realm realm) {
        return realm.where(Calls.class)
                .equalTo("isSynced", false)
                .findAll();
    }

    public static void save(@NonNull final Calls calls) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (realm.where(Calls.class).equalTo("dateCalled", calls.getDateCalled()).count() == 0) {
                    realm.copyToRealmOrUpdate(calls);
                }
            }
        });
    }

    public static void setSynced(final List<Calls> callsList) {
        executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (Calls calls : callsList) {
                    calls.setSynced(true);
                    realm.copyToRealmOrUpdate(calls);
                }
            }
        });
    }
}
