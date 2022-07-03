package com.sergey.zhuravlev.mobile.social.ui.registration;

import androidx.annotation.Nullable;

/**
 * Data validation state of the registration form.
 */
class RegistrationFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private String emailErrorString;
    @Nullable
    private Integer passwordError;
    @Nullable
    private String passwordErrorString;
    @Nullable
    private Integer confirmPasswordError;
    @Nullable
    private String confirmPasswordErrorString;

    private boolean isDataValid;

    RegistrationFormState(@Nullable Integer emailError, @Nullable Integer passwordError,  @Nullable Integer confirmPasswordError) {
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.confirmPasswordError = confirmPasswordError;
        this.isDataValid = false;
    }

    RegistrationFormState(@Nullable String emailError, @Nullable String passwordError, @Nullable String confirmPasswordErrorString) {
        this.emailError = null;
        this.emailErrorString = emailError;
        this.passwordError = null;
        this.passwordErrorString = passwordError;
        this.confirmPasswordError = null;
        this.confirmPasswordErrorString = confirmPasswordErrorString;
        this.isDataValid = false;
    }

    RegistrationFormState(boolean isDataValid) {
        this.emailError = null;
        this.emailErrorString = null;
        this.passwordError = null;
        this.passwordErrorString = null;
        this.confirmPasswordError = null;
        this.confirmPasswordErrorString = null;
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

    @Nullable
    public Integer getConfirmPasswordError() {
        return confirmPasswordError;
    }

    @Nullable
    public String getConfirmPasswordErrorString() {
        return confirmPasswordErrorString;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}