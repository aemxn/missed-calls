package com.aimanbaharum.missedcalls.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aimanbaharum.missedcalls.R;
import com.aimanbaharum.missedcalls.model.Calls;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aimanb on 16/12/2016.
 */

public class CallsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Calls> mCallsData = new ArrayList<>();

    public CallsAdapter() {
    }

    static class CallsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_counter)
        TextView tvCounter;

        @BindView(R.id.tv_caller_number)
        TextView tvCallerNumber;

        @BindView(R.id.tv_caller_name)
        TextView tvCallerName;

        @BindView(R.id.tv_caller_timestamp)
        TextView tvCallerTimestamp;

//        @BindView(R.id.tv_synced)
//        TextView tvSynced;

        public CallsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View mRowView = inflater.inflate(R.layout.row_calls, parent, false);
        return new CallsViewHolder(mRowView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CallsViewHolder vh = (CallsViewHolder) holder;
        Calls calls = mCallsData.get(position);

        vh.tvCallerName.setText(calls.getCallerName());
        vh.tvCallerNumber.setText(calls.getCallerNumber());
        vh.tvCallerTimestamp.setText(calls.getDateCalled());
        vh.tvCounter.setText(String.format("%s.", String.valueOf(position + 1)));

//        if (!calls.isSynced()) {
//            vh.tvSynced.setText("Not synced");
//            vh.tvSynced.setTextColor(ContextCompat.getColor(
//                    vh.tvSynced.getContext(), android.R.color.holo_red_dark));
//        } else {
//            vh.tvSynced.setText("Synced");
//            vh.tvSynced.setTextColor(ContextCompat.getColor(
//                    vh.tvSynced.getContext(), android.R.color.holo_green_dark));
//        }
    }

    @Override
    public int getItemCount() {
        return mCallsData.size();
    }

    public void add(List<Calls> callsData) {
        this.mCallsData = callsData;
        notifyDataSetChanged();
    }

    public void clear() {
        if (mCallsData != null && mCallsData.size() > 0) {
            mCallsData.clear();
            notifyDataSetChanged();
        }
    }
}
