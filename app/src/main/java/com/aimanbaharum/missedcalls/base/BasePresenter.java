package com.aimanbaharum.missedcalls.base;

import android.support.annotation.NonNull;

/**
 * Created by cliqers on 26/1/2017.
 */

public abstract class BasePresenter<V> {

    protected V mView;

    public final void attachView(@NonNull V view) {
        mView = view;
    }

    public final void detachView() {
        mView = null;
    }

    protected final boolean isViewAttached() {
        return mView != null;
    }
}
