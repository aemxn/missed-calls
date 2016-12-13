package com.aimanbaharum.missedcalls;

import android.Manifest;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivityPermissionsDispatcher.startLoggingWithCheck(this);
        startLogging();
    }

    @NeedsPermission({Manifest.permission.READ_CALL_LOG})
    public void startLogging() {
//        final String[] projection = null;
//        final String selection = null;
//        final String[] selectionArgs = null;
//        final String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";
//        Cursor cursor = null;
//        final int MISSED_CALL_TYPE = android.provider.CallLog.Calls.MISSED_TYPE;
//        try {
//            cursor = getContentResolver().query(
//                    Uri.parse("content://call_log/calls"),
//                    projection,
//                    selection,
//                    selectionArgs,
//                    sortOrder);
//            while (cursor.moveToNext()) {
//                String callLogID = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls._ID));
//                String callNumber = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER));
//                String callDate = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DATE));
//                String callType = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE));
//                String isCallNew = cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NEW));
//                if (Integer.parseInt(callType) == MISSED_CALL_TYPE && Integer.parseInt(isCallNew) > 0) {
//                    Log.v("Missed Call Found: ", callNumber);
//                }
//            }
//        } catch (Exception ex) {
//            Log.e("ERROR: ", ex.toString());
//        } finally {
//            cursor.close();
//        }

        String[] strFields = {
                android.provider.CallLog.Calls.CACHED_NAME,
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.TYPE
        };
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";

        Cursor mCallCursor = getContentResolver().query(
                android.provider.CallLog.Calls.CONTENT_URI, strFields, null, null, strOrder);

        if (mCallCursor.moveToFirst()) {
            do {
                boolean missed = mCallCursor.getInt(mCallCursor.getColumnIndex(CallLog.Calls.TYPE)) == CallLog.Calls.MISSED_TYPE;
                if (missed) {
                    String name = mCallCursor.getString(mCallCursor
                            .getColumnIndex(CallLog.Calls.CACHED_NAME));
                    String number = mCallCursor.getString(mCallCursor
                            .getColumnIndex(CallLog.Calls.NUMBER));

//                  String time = DateFormat.getDateTimeInstance(DateFormat., DateFormat.LONG)
//                          .format(mCallCursor.getLong(mCallCursor
//                                  .getColumnIndex(CallLog.Calls.DATE)));
//
                  Log.d("PhoneLog", "You have a missed call from " + name + " on " + number);
                }

            } while (mCallCursor.moveToNext());

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
