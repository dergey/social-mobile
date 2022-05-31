package com.sergey.zhuravlev.mobile.social.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private String emailErrorString;
    @Nullable
    private Integer passwordError;
    @Nullable
    private String passwordErrorString;

    private boolean isDataValid;

    LoginFormState(@Nullable Integer emailError, @Nullable Integer passwordError) {
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    LoginFormState(@Nullable String emailError, @Nullable String passwordError) {
        this.emailError = null;
        this.emailErrorString = emailError;
        this.passwordError = null;
        this.passwordErrorString = passwordError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.emailError = null;
        this.emailErrorString = null;
        this.passwordError = null;
        this.passwordErrorString = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public String getEmailErrorString() {
        return emailErrorString;
    }

    @Nullable
    public String getPasswordErrorString() {
        return passwordErrorString;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}