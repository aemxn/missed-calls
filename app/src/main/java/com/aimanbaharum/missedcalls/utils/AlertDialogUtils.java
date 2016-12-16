package com.aimanbaharum.missedcalls.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import com.aimanbaharum.missedcalls.R;
import com.iamhabib.easy_preference.EasyPreference;

/**
 * Created by aimanb on 16/12/2016.
 */

public class AlertDialogUtils {

    public static void showSettings(final Context context) {
        final EditText mEtEndpoint = new EditText(context);
        mEtEndpoint.setHint(context.getResources().getString(R.string.hint_endpoint));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true)
                .setTitle("Set Endpoint")
                .setView(mEtEndpoint)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String strEndpoint = mEtEndpoint.getText().toString();
                        EasyPreference.with(context)
                                .addString(PrefKey.KEY_ENDPOINT.name(), strEndpoint)
                                .save();
                        Toast.makeText(context, "Endpoint saved", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showEndpoint(final Context context) {

        String strEndpoint = EasyPreference.with(context)
                .getString(PrefKey.KEY_ENDPOINT.name(), "Endpoint not set");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true)
                .setTitle("Current Endpoint")
                .setMessage(strEndpoint)
                .setNegativeButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void setInterval(final Context context, final IntervalSelect callback) {
        int intervalIdx = EasyPreference.with(context)
                .getInt(PrefKey.KEY_INTERVAL_INDEX.name(), 2);

        AlertDialog.Builder b = new AlertDialog.Builder(context);
        b.setTitle("Set Interval");
        String[] indexes = {"1 min", "5 mins", "10 mins", "15 mins", "30 mins", "1 hour"};
        b.setSingleChoiceItems(indexes, intervalIdx, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        callback.onIntervalSelect(which, 60 * 1000);
                        break;
                    case 1:
                        callback.onIntervalSelect(which, 5 * 60 * 1000);
                        break;
                    case 2:
                        callback.onIntervalSelect(which, 10 * 60 * 1000);
                        break;
                    case 3:
                        callback.onIntervalSelect(which, 15 * 60 * 1000);
                        break;
                    case 4:
                        callback.onIntervalSelect(which, 30 * 60 * 1000);
                        break;
                    case 5:
                        callback.onIntervalSelect(which, 60 * 60 * 1000);
                        break;
                }
                dialog.dismiss();
            }
        });
        b.show();
    }

    public interface IntervalSelect {
        void onIntervalSelect(int index, long millis);
    }

}
