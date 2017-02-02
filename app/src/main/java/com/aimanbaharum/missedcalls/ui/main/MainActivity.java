package com.aimanbaharum.missedcalls.ui.main;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.aimanbaharum.missedcalls.base.BaseActivity;
import com.aimanbaharum.missedcalls.model.Calls;
import com.aimanbaharum.missedcalls.utils.AlertDialogUtils;
import com.aimanbaharum.missedcalls.utils.Constants;
import com.aimanbaharum.missedcalls.utils.PrefKey;
import com.iamhabib.easy_preference.EasyPreference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.hypertrack.smart_scheduler.Job;
import io.hypertrack.smart_scheduler.SmartScheduler;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends BaseActivity implements MainContract.MainView,
        SmartScheduler.JobScheduledCallback, AlertDialogUtils.IntervalSelect {

    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    @BindView(R.id.rv_calls_list)
    RecyclerView rvCallsList;

    private static final int JOB_ID = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String JOB_PERIODIC_TASK_TAG = "com.aimanbaharum.missedcalls.JobPeriodicTask";

    private MainPresenter mainPresenter;

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
                    mainPresenter.onSyncRequested();
                }
            });
        }
    }

    @NeedsPermission({Manifest.permission.READ_CALL_LOG})
    public void startLogging() {
        mainPresenter = new MainPresenter(this);
        mainPresenter.attachView(this);
        mainPresenter.onStartLogCalls();
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
        CallsAdapter mCallsAdapter = new CallsAdapter();
        rvCallsList.setAdapter(mCallsAdapter);
        mCallsAdapter.add(callsList);
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
                AlertDialogUtils.setEndpoint(this);
                return true;
            case R.id.action_endpoint:
                AlertDialogUtils.showEndpoint(this);
                return true;
            case R.id.action_sync:
                mainPresenter.onSyncRequested();
                return true;
            case R.id.action_interval:
                AlertDialogUtils.setInterval(this, this);
                return true;
            case R.id.action_limit:
                AlertDialogUtils.setSyncLimit(this);
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
                startLogging();
//                callsPresenter.showMissedCalledList(MainActivity.this);
            }
        });
    }

    @Override
    public void onSyncFailed(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                startLogging();
//                callsPresenter.showMissedCalledList(MainActivity.this);
            }
        });
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showUnauthorizedError() {

    }

    @Override
    public void showEmpty() {
        tvEmpty.setVisibility(View.VISIBLE);
        rvCallsList.setVisibility(View.GONE);
    }

    @Override
    public void showError(String errorMessage) {
        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessageLayout(boolean show) {

    }
}
