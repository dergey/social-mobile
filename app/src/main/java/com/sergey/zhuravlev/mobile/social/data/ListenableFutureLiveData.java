package com.sergey.zhuravlev.mobile.social.data;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.Executor;

public class ListenableFutureLiveData<T> extends MutableLiveData<T> {

    private Executor executor;
    private ListenableFuture<T> listenableFuture;
    //private Map<Observer<? super T>, FutureCallback<T>> observerCallbacks;

    public ListenableFutureLiveData(ListenableFuture<T> listenableFuture, Executor executor) {
        super();
        this.executor = executor;
        this.listenableFuture = listenableFuture;
    }

    @Override
    public void observe(@NonNull @NotNull LifecycleOwner owner, @NonNull @NotNull Observer<? super T> observer) {
        super.observe(owner, observer);
        Futures.addCallback(listenableFuture, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                ListenableFutureLiveData.super.postValue(result);
            }

            @Override
            public void onFailure(Throwable t) {
                ListenableFutureLiveData.super.postValue(null);
            }
        }, executor);
    }

    @Override
    public void postValue(T value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setValue(T value) {
        throw new UnsupportedOperationException();
    }

}
