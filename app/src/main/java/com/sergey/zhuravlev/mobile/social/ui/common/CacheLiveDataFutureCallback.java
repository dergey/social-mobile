package com.sergey.zhuravlev.mobile.social.ui.common;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.common.util.concurrent.FutureCallback;
import com.sergey.zhuravlev.mobile.social.data.Result;

public class CacheLiveDataFutureCallback<T> implements FutureCallback<Result<T, Void>> {

    private final MutableLiveData<UiResult<T>> provideLiveData;

    public CacheLiveDataFutureCallback(MutableLiveData<UiResult<T>> provideLiveData) {
        this.provideLiveData = provideLiveData;
    }

    @Override
    public void onSuccess(@Nullable Result<T, Void> result) {
        if (result == null) {
            return;
        }
        if (result.isSuccess()) {
            T data = ((Result.Success<T, Void>) result).getData();
            provideLiveData.postValue(UiResult.success(data));
        } else {
            String errorMessage = ((Result.Error<T, Void>) result).getMessage();
            provideLiveData.postValue(UiResult.error(errorMessage));
        }
    }

    @Override
    public void onFailure(Throwable t) {

    }
}
