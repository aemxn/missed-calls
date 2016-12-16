package com.aimanbaharum.missedcalls.ui;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.aimanbaharum.missedcalls.R;
import com.aimanbaharum.missedcalls.adapters.CallsAdapter;
import com.aimanbaharum.missedcalls.model.Calls;
import com.aimanbaharum.missedcalls.presenter.CallsPresenter;
import com.aimanbaharum.missedcalls.presenter.LogCalls;
import com.aimanbaharum.missedcalls.utils.AlertDialogUtils;
import com.aimanbaharum.missedcalls.utils.Constants;
import com.aimanbaharum.missedcalls.utils.PrefKey;
import com.aimanbaharum.missedcalls.view.CallsView;
import com.aimanbaharum.missedcalls.view.SyncView;
import com.iamhabib.easy_preference.EasyPreference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements CallsView, SyncView,
        SmartScheduler.JobScheduledCallback, AlertDialogUtils.IntervalSelect {

    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    @BindView(R.id.rv_calls_list)
    RecyclerView rvCallsList;

    private static final int JOB_ID = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String JOB_PERIODIC_TASK_TAG = "com.aimanbaharum.missedcalls.JobPeriodicTask";

    private CallsPresenter callsPresenter;
    private CallsAdapter mCallsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        MainActivityPermissionsDispatcher.startLoggingWithCheck(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rvCallsList.addItemDecoration(itemDecoration);
        rvCallsList.setLayoutManager(manager);
        rvCallsList.setHasFixedSize(true);

        startScheduler();
    }

    private void startScheduler() {
        SmartScheduler jobScheduler = SmartScheduler.getInstance(this);

        Job job = createJob();
        if (jobScheduler.addJob(job)) {
            Log.d(TAG, "scheduler started");
//            Toast.makeText(MainActivity.this, "Job scheduled at 5000ms", Toast.LENGTH_SHORT).show();
        }
    }

    private Job createJob() {
        long interval = EasyPreference.with(this)
                .getLong(PrefKey.KEY_INTERVAL_MILLIS.name(), Constants.DEFAULT_INTERVAL);

        return new Job.Builder(JOB_ID, this, Job.Type.JOB_TYPE_PERIODIC_TASK, JOB_PERIODIC_TASK_TAG)
                .setPeriodic(interval)
                .build();
    }

    @Override
    public void onJobScheduled(Context context, final Job job) {
        if (job != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callsPresenter.syncNumbers(MainActivity.this);
//                    Toast.makeText(MainActivity.this, "Job: " + job.getJobId() + " scheduled!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @NeedsPermission({Manifest.permission.READ_CALL_LOG})
    public void startLogging() {
        LogCalls logCalls = new LogCalls(this);
        logCalls.logCalls();

        // todo combine logcalls with presenter

        callsPresenter = new CallsPresenter(this);
        callsPresenter.showMissedCalledList(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onShowMissedCalls(List<Calls> callsList) {
        tvEmpty.setVisibility(View.GONE);
        rvCallsList.setVisibility(View.VISIBLE);
        mCallsAdapter = new CallsAdapter();
        rvCallsList.setAdapter(mCallsAdapter);
        mCallsAdapter.add(callsList);
    }

    @Override
    public void onEmptyMissedCalls() {
        tvEmpty.setVisibility(View.VISIBLE);
        rvCallsList.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // todo add interval updates
                AlertDialogUtils.showSettings(this);
                return true;
            case R.id.action_endpoint:
                AlertDialogUtils.showEndpoint(this);
                return true;
            case R.id.action_sync:
                callsPresenter.syncNumbers(this);
                return true;
            case R.id.action_interval:
                AlertDialogUtils.setInterval(this, this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onIntervalSelect(int index, long millis) {
        EasyPreference.with(MainActivity.this)
                .addInt(PrefKey.KEY_INTERVAL_INDEX.name(), index)
                .addLong(PrefKey.KEY_INTERVAL_MILLIS.name(), millis)
                .save();

        SmartScheduler jobScheduler = SmartScheduler.getInstance(this);
        if (!jobScheduler.contains(JOB_ID)) {
            Toast.makeText(MainActivity.this, "No job exists with JobID: " + JOB_ID, Toast.LENGTH_SHORT).show();
            return;
        }

        if (jobScheduler.removeJob(JOB_ID)) {
            Log.d(TAG, "job stopped");
            startScheduler();
        }
    }

    @Override
    public void onSyncSuccess(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                callsPresenter.showMissedCalledList(MainActivity.this);
            }
        });
    }

    @Override
    public void onSyncFailed(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                callsPresenter.showMissedCalledList(MainActivity.this);
            }
        });
    }
}
