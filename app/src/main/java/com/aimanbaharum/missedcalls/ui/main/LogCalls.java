package com.aimanbaharum.missedcalls.ui.main;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.aimanbaharum.missedcalls.model.Calls;

import java.text.DateFormat;

/**
 * Created by aimanb on 16/12/2016.
 */

public class LogCalls {

    private static final String TAG = LogCalls.class.getSimpleName();
    private Context mContext;

    public LogCalls(Context context) {
        this.mContext = context;
    }

    public void logCalls() {
        String[] strFields = {
                android.provider.CallLog.Calls.CACHED_NAME,
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.TYPE
        };
        String strOrder = CallLog.Calls.DATE + " DESC";

        Cursor mCallCursor = mContext.getContentResolver().query(
                android.provider.CallLog.Calls.CONTENT_URI, strFields, null, null, strOrder);

        if (mCallCursor.moveToFirst()) {
            do {
                boolean missed = mCallCursor.getInt(mCallCursor
                        .getColumnIndex(CallLog.Calls.TYPE)) == CallLog.Calls.MISSED_TYPE;
                if (missed) {
                    String mCallerName = mCallCursor.getString(mCallCursor
                            .getColumnIndex(CallLog.Calls.CACHED_NAME));
                    String mCallerNumber = mCallCursor.getString(mCallCursor
                            .getColumnIndex(CallLog.Calls.NUMBER));

                    long mCallerTimestamp = mCallCursor.getLong(mCallCursor
                            .getColumnIndex(CallLog.Calls.DATE));
                    String mCallerTime = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG)
                            .format(mCallerTimestamp);


                    Calls newCalls = new Calls();
                    newCalls.setCallerName(mCallerName);
                    newCalls.setCallerNumber(mCallerNumber);
                    newCalls.setDateCalled(mCallerTime);
                    newCalls.setCallerTimestamp(mCallerTimestamp);
                    newCalls.save(newCalls);
                }

            } while (mCallCursor.moveToNext());
        }
    }

}
