package com.sergey.zhuravlev.mobile.social.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.LoginResponseDto;
import com.sergey.zhuravlev.mobile.social.client.dto.user.UserDto;
import com.sergey.zhuravlev.mobile.social.data.repository.LoginRepository;
import com.sergey.zhuravlev.mobile.social.data.repository.RegistrationRepository;
import com.sergey.zhuravlev.mobile.social.ui.common.NetworkLiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.UiNetworkResult;

import java.time.LocalDate;

public class ProfileSettingViewModel extends ViewModel {

    private final MutableLiveData<ProfileSettingFormState> profileSettingFormState = new MutableLiveData<>();
    private final MutableLiveData<UiNetworkResult<UserDto>> completeRegistrationResult = new MutableLiveData<>();
    private final MutableLiveData<UiNetworkResult<LoginResponseDto>> loginResult = new MutableLiveData<>();

    private final LoginRepository loginRepository;
    private final RegistrationRepository registrationRepository;

    public LiveData<ProfileSettingFormState> getProfileSettingFormState() {
        return profileSettingFormState;
    }

    public LiveData<UiNetworkResult<UserDto>> getCompleteRegistrationResult() {
        return completeRegistrationResult;
    }

    public LiveData<UiNetworkResult<LoginResponseDto>> getLoginResult() {
        return loginResult;
    }

    public ProfileSettingViewModel() {
        //this.profileRepository = ProfileRepository.getInstance(context);
        this.registrationRepository = RegistrationRepository.getInstance();
        this.loginRepository = LoginRepository.getInstance();
    }

    public void completeRegistration(String continuationCode, String password, String username,
                                     String firstName, String middleName, String secondName,
                                     String city, LocalDate birthDate) {
        registrationRepository.completeRegistration(continuationCode, password, username, firstName,
                middleName, secondName, city, birthDate, new NetworkLiveDataFutureCallback<>(completeRegistrationResult));
    }

    public void profileSettingDataChanged(String firstName, String lastName, String additionalName,
                                          String username, String birthDate) {
//  todo :
//        if (!isUserNameValid(username)) {
//            registrationFormState.setValue(new RegistrationFormState(R.string.invalid_email, null, null));
//        } else if (!isPasswordValid(password)) {
//            registrationFormState.setValue(new RegistrationFormState(null, R.string.invalid_password, null));
//        } else if (!isPasswordConfirmValid(password, confirmPassword)) {
//            registrationFormState.setValue(new RegistrationFormState(null, null, R.string.invalid_confirm_password));
//        } else {
//            registrationFormState.setValue(new RegistrationFormState(true));
//        }
        profileSettingFormState.setValue(new ProfileSettingFormState(true));
    }

    public void processServerFieldError(ErrorDto errorDto) {
        ProfileSettingFormState formState = new ProfileSettingFormState(false);
        for (ErrorDto.FieldError field : errorDto.getFields()) {
            switch (field.getField()) {
                case "password":
                    formState.setPasswordErrorString(field.getCode());
                    break;
                case "username":
                    formState.setUsernameErrorString(field.getCode());
                    break;
                case "firstName":
                    formState.setFirstNameErrorString(field.getCode());
                    break;
                case "middleName":
                    formState.setAdditionalNameErrorString(field.getCode());
                    break;
                case "secondName":
                    formState.setLastNameErrorString(field.getCode());
                    break;
                case "birthDate":
                    formState.setBirthDateErrorString(field.getCode());
                    break;
            }
            profileSettingFormState.postValue(formState);
        }
    }

    public void login(String email, String password) {
        loginRepository.login(email, password, new NetworkLiveDataFutureCallback<>(loginResult));
    }
}
