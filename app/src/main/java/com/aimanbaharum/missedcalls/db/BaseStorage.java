package com.aimanbaharum.missedcalls.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.aimanbaharum.missedcalls.utils.L;

import io.realm.Realm;

/**
 * Created by cliqers on 2/2/2017.
 */

public abstract class BaseStorage {

    protected static void executeTransaction(@NonNull Realm.Transaction transaction) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(transaction);
        } catch (Throwable e) {
            L.e("executeTransaction", e);
        } finally {
            close(realm);
        }
    }

    protected static void close(@Nullable Realm realm) {
        if (realm != null) {
            realm.close();
        }
    }

}
