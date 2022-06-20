package com.sergey.zhuravlev.mobile.social.ui.profile.result;

import androidx.annotation.Nullable;

import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDetailDto;

public class GetCurrentProfileResult {

    @Nullable
    private ProfileDetailDto data;
    @Nullable
    private ErrorDto errorDto;
    @Nullable
    private String errorMessage;

    private boolean hasErrors;


    private GetCurrentProfileResult() {
    }

    public static GetCurrentProfileResult error(String errorMessage) {
        GetCurrentProfileResult result = new GetCurrentProfileResult();
        result.errorMessage = errorMessage;
        result.hasErrors = true;
        return result;
    }

    public static GetCurrentProfileResult error(ErrorDto dto) {
        GetCurrentProfileResult result = new GetCurrentProfileResult();
        result.errorMessage = dto.getMessage();
        result.errorDto = dto;
        result.hasErrors = true;
        return result;
    }

    public static GetCurrentProfileResult success(ProfileDetailDto data) {
        GetCurrentProfileResult result = new GetCurrentProfileResult();
        result.data = data;
        result.hasErrors = false;
        return result;
    }

    @Nullable
    public ProfileDetailDto getData() {
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
