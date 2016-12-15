package com.aimanbaharum.missedcalls.view;

import com.aimanbaharum.missedcalls.model.Calls;

import java.util.List;

/**
 * Created by aimanb on 16/12/2016.
 */

public interface CallsView {

    void onShowMissedCalls(List<Calls> callsList);
    void onEmptyMissedCalls();
}
