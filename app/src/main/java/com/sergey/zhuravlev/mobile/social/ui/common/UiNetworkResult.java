package com.sergey.zhuravlev.mobile.social.ui.common;

import androidx.annotation.Nullable;

import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;

public class UiNetworkResult<T> extends UiResult<T> {

    @Nullable
    ErrorDto errorDto;

    private UiNetworkResult() {
        super();
    }

    public static <T> UiNetworkResult<T> error(String errorMessage) {
        UiNetworkResult<T> result = new UiNetworkResult<>();
        result.errorMessage = errorMessage;
        result.hasErrors = true;
        return result;
    }

    public static <T> UiNetworkResult<T> success(T data) {
        UiNetworkResult<T> result = new UiNetworkResult<>();
        result.data = data;
        return result;
    }

    public static <T> UiNetworkResult<T> error(ErrorDto dto) {
        UiNetworkResult<T> result = new UiNetworkResult<>();
        result.errorMessage = dto.getMessage();
        result.errorDto = dto;
        result.hasErrors = true;
        return result;
    }

    @Nullable
    public ErrorDto getErrorDto() {
        return errorDto;
    }

}
