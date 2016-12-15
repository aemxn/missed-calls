package com.aimanbaharum.missedcalls.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;

import com.iamhabib.easy_preference.EasyPreference;

/**
 * Created by aimanb on 16/12/2016.
 */

public class AlertDialogUtils {

    public static void showSettings(final Context context) {
        final EditText mEtEndpoint = new EditText(context);

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

}
