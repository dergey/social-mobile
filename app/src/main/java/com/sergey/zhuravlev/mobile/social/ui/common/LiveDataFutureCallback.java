package com.sergey.zhuravlev.mobile.social.ui.common;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.common.util.concurrent.FutureCallback;

public class LiveDataFutureCallback<T> implements FutureCallback<T> {

    private final MutableLiveData<T> provideLiveData;

    public LiveDataFutureCallback(MutableLiveData<T> provideLiveData) {
        this.provideLiveData = provideLiveData;
    }

    @Override
    public void onSuccess(@Nullable T result) {
        if (result == null) {
            return;
        }
        provideLiveData.postValue(result);
    }

    @Override
    public void onFailure(Throwable t) {

    }
}
