package com.sergey.zhuravlev.mobile.social.ui.registration;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.client.dto.user.UserDto;
import com.sergey.zhuravlev.mobile.social.data.RegistrationRepository;
import com.sergey.zhuravlev.mobile.social.ui.common.NetworkLiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.UiResult;

import java.time.LocalDate;
import java.util.Objects;

public class RegistrationViewModel extends ViewModel {

    private final MutableLiveData<UiResult<UserDto>> registerResult = new MutableLiveData<>();
    private final MutableLiveData<RegistrationFormState> registrationFormState = new MutableLiveData<>();

    private final RegistrationRepository registrationRepository;

    public LiveData<UiResult<UserDto>> getRegisterResult() {
        return registerResult;
    }

    public MutableLiveData<RegistrationFormState> getRegistrationFormState() {
        return registrationFormState;
    }

    public RegistrationViewModel() {
        registrationRepository = RegistrationRepository.getInstance();
    }

    public void register(String email, String password, String username, String firstName,
                                  String middleName, String secondName, LocalDate birthDate) {
        registrationRepository.register(email, password, username, firstName, middleName, secondName,
                birthDate, new NetworkLiveDataFutureCallback<>(registerResult));
    }

    public void registrationDataChanged(String username, String password, String confirmPassword) {
        if (!isUserNameValid(username)) {
            registrationFormState.setValue(new RegistrationFormState(R.string.invalid_username, null, null));
        } else if (!isPasswordValid(password)) {
            registrationFormState.setValue(new RegistrationFormState(null, R.string.invalid_password, null));
        } else if (!isPasswordConfirmValid(password, confirmPassword)) {
            registrationFormState.setValue(new RegistrationFormState(null, null, R.string.invalid_confirm_password));
        } else {
            registrationFormState.setValue(new RegistrationFormState(true));
        }
    }

    public void processServerFieldError(String emailError, String passwordError) {
        registrationFormState.postValue(new RegistrationFormState(emailError, passwordError, null));
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    // A placeholder confirm password validation check
    private boolean isPasswordConfirmValid(String password, String confirmPassword) {
        return Objects.equals(password, confirmPassword);
    }

}
