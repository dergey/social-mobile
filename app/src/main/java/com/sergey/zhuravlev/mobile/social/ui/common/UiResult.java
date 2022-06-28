package com.sergey.zhuravlev.mobile.social.ui.common;

import androidx.annotation.Nullable;

import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;

public class UiResult<T> {

    @Nullable
    private T data;
    @Nullable
    private ErrorDto errorDto;
    @Nullable
    private String errorMessage;

    private boolean hasErrors;

    private UiResult() {
    }

    public static <T> UiResult<T> error(String errorMessage) {
        UiResult<T> result = new UiResult<>();
        result.errorMessage = errorMessage;
        result.hasErrors = true;
        return result;
    }

    public static <T> UiResult<T> error(ErrorDto dto) {
        UiResult<T> result = new UiResult<>();
        result.errorMessage = dto.getMessage();
        result.errorDto = dto;
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
    public ErrorDto getErrorDto() {
        return errorDto;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

}
