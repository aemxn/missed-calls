package com.aimanbaharum.missedcalls.ui;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.aimanbaharum.missedcalls.view.CallsView;
import com.aimanbaharum.missedcalls.view.SyncView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements CallsView, SyncView {

    @BindView(R.id.tv_empty)
    TextView tvEmpty;

    @BindView(R.id.rv_calls_list)
    RecyclerView rvCallsList;

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSyncSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        callsPresenter.showMissedCalledList(this);
    }

    @Override
    public void onSyncFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        callsPresenter.showMissedCalledList(this);
    }
}
