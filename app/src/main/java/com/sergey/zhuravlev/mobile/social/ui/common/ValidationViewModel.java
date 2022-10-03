package com.sergey.zhuravlev.mobile.social.ui.common;

import android.content.Context;
import android.util.Patterns;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.enums.ValidatedField;
import com.sergey.zhuravlev.mobile.social.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public abstract class ValidationViewModel extends ViewModel {

    private final MutableLiveData<FormState> formState = new MutableLiveData<>();

    private final Context context;
    private final Set<ValidatedField> validatedFields;

    public ValidationViewModel(Context context, Set<ValidatedField> validatedFields) {
        this.context = context;
        this.validatedFields = validatedFields;
    }

    public LiveData<FormState> getFormState() {
        return formState;
    }

    public void processFormDataChanged(Map<ValidatedField, String> fieldValues) {
        Map<ValidatedField, String> fieldErrors = new HashMap<>(fieldValues.size());
        for (ValidatedField validatedField : validatedFields) {
            switch (validatedField) {
                case EMAIL:
                    if (!isEmailValid(fieldValues.get(ValidatedField.EMAIL))) {
                        fieldErrors.put(ValidatedField.EMAIL, context.getString(R.string.invalid_email));
                    }
                    break;
                case PASSWORD:
                    if (!isPasswordValid(fieldValues.get(ValidatedField.PASSWORD))) {
                        fieldErrors.put(ValidatedField.PASSWORD, context.getString(R.string.invalid_password));
                    }
                    break;
                case PASSWORD_CONFIRMATION:
                    if (!isPasswordConfirmValid(fieldValues.get(ValidatedField.PASSWORD), fieldValues.get(ValidatedField.PASSWORD_CONFIRMATION))) {
                        fieldErrors.put(ValidatedField.PASSWORD_CONFIRMATION, context.getString(R.string.invalid_confirm_password));
                    }
                    break;
                case CONFIRMATION_CODE:
                    if (!isConfirmationCodeValid(fieldValues.get(ValidatedField.CONFIRMATION_CODE))) {
                        fieldErrors.put(ValidatedField.CONFIRMATION_CODE, context.getString(R.string.invalid_confirmation_code));
                    }
                    break;
            }
        }
        formState.setValue(new FormState(fieldErrors));
    }

    public void processServerDataFieldError(ErrorDto errorDto) {
        Map<ValidatedField, String> fieldErrors = new HashMap<>();
        for (ErrorDto.FieldError field : errorDto.getFields()) {
            switch (field.getField()) {
                case "phoneOrEmail":
                    fieldErrors.put(ValidatedField.EMAIL, StringUtils.getString(field.getCode(), context, "Email"));
                    break;
            }
            formState.postValue(new FormState(fieldErrors));
        }
    }

    public void processConnectionError(String error) {
        Toast.makeText(context, "Error connection to server. " + error, Toast.LENGTH_SHORT).show();
    }

    public void setFieldErrors(Map<ValidatedField, String> fieldErrors) {
        formState.setValue(new FormState(fieldErrors));
    }

    protected boolean isEmailValid(String email) {
        return !StringUtils.isBlank(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    protected boolean isPasswordValid(String password) {
        return !StringUtils.isBlank(password) && password.trim().length() > 5;
    }

    protected boolean isPasswordConfirmValid(String password, String confirmPassword) {
        return !StringUtils.isBlank(password) && !StringUtils.isBlank(confirmPassword) && Objects.equals(password, confirmPassword);
    }

    protected boolean isConfirmationCodeValid(String confirmationCode) {
        return !StringUtils.isBlank(confirmationCode) && confirmationCode.trim().length() == 5;
    }

}
