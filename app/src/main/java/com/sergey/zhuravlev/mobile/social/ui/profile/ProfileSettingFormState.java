package com.sergey.zhuravlev.mobile.social.ui.profile;

import androidx.annotation.Nullable;

/**
 * Data validation state of the registration form.
 */
class ProfileSettingFormState {
    @Nullable
    private Integer firstNameError;
    @Nullable
    private String firstNameErrorString;
    @Nullable
    private Integer lastNameError;
    @Nullable
    private String lastNameErrorString;
    @Nullable
    private Integer additionalNameError;
    @Nullable
    private String additionalNameErrorString;
    @Nullable
    private Integer usernameError;
    @Nullable
    private String usernameErrorString;
    @Nullable
    private Integer birthDateError;
    @Nullable
    private String birthDateErrorString;

    private boolean isDataValid;

    ProfileSettingFormState(@Nullable Integer firstNameError, @Nullable Integer lastNameError,
                            @Nullable Integer additionalNameError, @Nullable Integer usernameError,
                            @Nullable Integer birthDateError) {
        this.firstNameError = firstNameError;
        this.lastNameError = lastNameError;
        this.additionalNameError = additionalNameError;
        this.usernameError = usernameError;
        this.birthDateError = birthDateError;
        this.isDataValid = false;
    }

    ProfileSettingFormState(@Nullable String firstNameError, @Nullable String lastNameError,
                            @Nullable String additionalNameError, @Nullable String usernameError,
                            @Nullable String birthDateError) {
        this.firstNameError = null;
        this.firstNameErrorString = firstNameError;
        this.lastNameError = null;
        this.lastNameErrorString = lastNameError;
        this.additionalNameError = null;
        this.additionalNameErrorString = additionalNameError;
        this.usernameError = null;
        this.usernameErrorString = usernameError;
        this.birthDateError = null;
        this.birthDateErrorString = birthDateError;
        this.isDataValid = false;
    }

    ProfileSettingFormState(boolean isDataValid) {
        this.firstNameError = null;
        this.firstNameErrorString = null;
        this.lastNameError = null;
        this.lastNameErrorString = null;
        this.additionalNameError = null;
        this.additionalNameErrorString = null;
        this.usernameError = null;
        this.usernameErrorString = null;
        this.birthDateError = null;
        this.birthDateErrorString = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getFirstNameError() {
        return firstNameError;
    }

    public void setFirstNameError(@Nullable Integer firstNameError) {
        this.firstNameError = firstNameError;
    }

    @Nullable
    public String getFirstNameErrorString() {
        return firstNameErrorString;
    }

    public void setFirstNameErrorString(@Nullable String firstNameErrorString) {
        this.firstNameErrorString = firstNameErrorString;
    }

    @Nullable
    public Integer getLastNameError() {
        return lastNameError;
    }

    public void setLastNameError(@Nullable Integer lastNameError) {
        this.lastNameError = lastNameError;
    }

    @Nullable
    public String getLastNameErrorString() {
        return lastNameErrorString;
    }

    public void setLastNameErrorString(@Nullable String lastNameErrorString) {
        this.lastNameErrorString = lastNameErrorString;
    }

    @Nullable
    public Integer getAdditionalNameError() {
        return additionalNameError;
    }

    public void setAdditionalNameError(@Nullable Integer additionalNameError) {
        this.additionalNameError = additionalNameError;
    }

    @Nullable
    public String getAdditionalNameErrorString() {
        return additionalNameErrorString;
    }

    public void setAdditionalNameErrorString(@Nullable String additionalNameErrorString) {
        this.additionalNameErrorString = additionalNameErrorString;
    }

    @Nullable
    public Integer getUsernameError() {
        return usernameError;
    }

    public void setUsernameError(@Nullable Integer usernameError) {
        this.usernameError = usernameError;
    }

    @Nullable
    public String getUsernameErrorString() {
        return usernameErrorString;
    }

    public void setUsernameErrorString(@Nullable String usernameErrorString) {
        this.usernameErrorString = usernameErrorString;
    }

    @Nullable
    public Integer getBirthDateError() {
        return birthDateError;
    }

    public void setBirthDateError(@Nullable Integer birthDateError) {
        this.birthDateError = birthDateError;
    }

    @Nullable
    public String getBirthDateErrorString() {
        return birthDateErrorString;
    }

    public void setBirthDateErrorString(@Nullable String birthDateErrorString) {
        this.birthDateErrorString = birthDateErrorString;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}