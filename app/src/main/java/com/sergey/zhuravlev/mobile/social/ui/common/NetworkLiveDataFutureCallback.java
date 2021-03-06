package com.sergey.zhuravlev.mobile.social.ui.common;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.common.util.concurrent.FutureCallback;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.data.Result;

public class NetworkLiveDataFutureCallback<T> implements FutureCallback<Result<T, ErrorDto>> {

    private final MutableLiveData<UiNetworkResult<T>> provideLiveData;

    public NetworkLiveDataFutureCallback(MutableLiveData<UiNetworkResult<T>> provideLiveData) {
        this.provideLiveData = provideLiveData;
    }

    @Override
    public void onSuccess(@Nullable Result<T, ErrorDto> result) {
        if (result == null) {
            return;
        }
        if (result.isSuccess()) {
            T data = ((Result.Success<T, ErrorDto>) result).getData();
            provideLiveData.postValue(UiNetworkResult.success(data));
        } else {
            String errorMessage = ((Result.Error<T, ErrorDto>) result).getMessage();
            ErrorDto errorData = ((Result.Error<T, ErrorDto>) result).getErrorObject(ErrorDto.class);
            if (errorData != null) {
                provideLiveData.postValue(UiNetworkResult.error(errorData));
            } else {
                provideLiveData.postValue(UiNetworkResult.error(errorMessage));
            }
        }
    }

    @Override
    public void onFailure(Throwable t) {

    }
}
