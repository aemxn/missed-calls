package com.aimanbaharum.missedcalls.model;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by aimanb on 15/12/2016.
 */

public class Calls extends RealmObject {

    @PrimaryKey
    private String dateCalled;
    private String callerName;
    private String callerNumber;
    private boolean isSynced;

    public Calls() {
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCallerNumber() {
        return callerNumber;
    }

    public void setCallerNumber(String callerNumber) {
        this.callerNumber = callerNumber;
    }

    public String getDateCalled() {
        return dateCalled;
    }

    public void setDateCalled(String dateCalled) {
        this.dateCalled = dateCalled;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public static List<Calls> getMissedCalledList() {
        return Realm.getDefaultInstance()
                .copyFromRealm(Realm.getDefaultInstance()
                        .where(Calls.class)
                        .findAll());
    }

    public static List<Calls> getUnsynced() {
        return Realm.getDefaultInstance()
                .copyFromRealm(Realm.getDefaultInstance()
                        .where(Calls.class)
                        .equalTo("isSynced", false)
                        .findAll());
    }

    public void save(Calls calls) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        if (realm.where(Calls.class).equalTo("dateCalled", calls.getDateCalled()).count() == 0) {
            realm.copyToRealmOrUpdate(calls);
        }
        realm.commitTransaction();
    }

    public static void setSynced(List<Calls> callsList) {
        Realm.getDefaultInstance().beginTransaction();
        for (Calls calls : callsList) {
            calls.setSynced(true);
            Realm.getDefaultInstance().copyToRealmOrUpdate(calls);
        }
        Realm.getDefaultInstance().commitTransaction();
    }
}
