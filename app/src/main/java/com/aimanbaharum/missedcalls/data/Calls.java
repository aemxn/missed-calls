package com.aimanbaharum.missedcalls.data;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by aimanb on 15/12/2016.
 */

public class Calls extends RealmObject {

    @PrimaryKey
    private String dateCalled;
    private long callerTimestamp;
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

    public long getCallerTimestamp() {
        return callerTimestamp;
    }

    public void setCallerTimestamp(long callerTimestamp) {
        this.callerTimestamp = callerTimestamp;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }
}
