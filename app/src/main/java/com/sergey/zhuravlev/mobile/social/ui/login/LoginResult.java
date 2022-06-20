package com.sergey.zhuravlev.mobile.social.ui.login;

import androidx.annotation.Nullable;

import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private String jwtToken;
    @Nullable
    private ErrorDto errorDto;
    @Nullable
    private String errorMessage;

    private boolean hasErrors;

    private LoginResult() {
    }

    static LoginResult error(String errorMessage) {
        LoginResult result = new LoginResult();
        result.errorMessage = errorMessage;
        result.hasErrors = true;
        return result;
    }

    static LoginResult error(ErrorDto dto) {
        LoginResult result = new LoginResult();
        result.errorMessage = dto.getMessage();
        result.errorDto = dto;
        result.hasErrors = true;
        return result;
    }

    static LoginResult success(String jwtToken) {
        LoginResult result = new LoginResult();
        result.jwtToken = jwtToken;
        return result;
    }

    @Nullable
    public String getJwtToken() {
        return jwtToken;
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