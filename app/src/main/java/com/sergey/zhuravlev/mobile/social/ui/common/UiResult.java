package com.sergey.zhuravlev.mobile.social.ui.common;

import androidx.annotation.Nullable;

public class UiResult<T> {

    @Nullable
    T data;
    @Nullable
    String errorMessage;

    boolean hasErrors;

    UiResult() {
    }

    public static <T> UiResult<T> error(String errorMessage) {
        UiResult<T> result = new UiResult<>();
        result.errorMessage = errorMessage;
        result.hasErrors = true;
        return result;
    }

    public static <T> UiResult<T> success(T data) {
        UiResult<T> result = new UiResult<>();
        result.data = data;
        return result;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

}
