package com.aimanbaharum.missedcalls.base;

/**
 * Created by cliqers on 26/1/2017.
 */

public interface RemoteView {

    void showProgress();

    void hideProgress();

    void showUnauthorizedError();

    void showEmpty();

    void showError(String errorMessage);

    void showMessageLayout(boolean show);
}
