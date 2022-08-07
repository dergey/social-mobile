package com.sergey.zhuravlev.mobile.social.ui.common;

import com.sergey.zhuravlev.mobile.social.enums.ValidatedField;

import java.util.Map;

public class FormState {

    private Map<ValidatedField, String> fieldState;
    private boolean isDataValid;

    public FormState() {
        this.isDataValid = true;
    }

    public FormState(Map<ValidatedField, String> fieldState) {
        this.fieldState = fieldState;
        this.isDataValid = fieldState.isEmpty();
    }

    public Map<ValidatedField, String> getFieldState() {
        return fieldState;
    }

    public boolean isFieldContainError(ValidatedField field) {
        return fieldState.containsKey(field);
    }

    public String getFieldErrorString(ValidatedField field) {
        return fieldState.get(field);
    }

    public boolean isDataValid() {
        return isDataValid;
    }

}
