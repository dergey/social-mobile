package com.sergey.zhuravlev.mobile.social.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.LoginDto;
import com.sergey.zhuravlev.mobile.social.client.dto.LoginResponseDto;
import com.sergey.zhuravlev.mobile.social.client.dto.user.UserDto;
import com.sergey.zhuravlev.mobile.social.data.LoginRepository;
import com.sergey.zhuravlev.mobile.social.data.ProfileRepository;
import com.sergey.zhuravlev.mobile.social.data.RegistrationRepository;
import com.sergey.zhuravlev.mobile.social.ui.common.NetworkLiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.UiResult;

import java.time.LocalDate;

public class ProfileSettingViewModel extends ViewModel {

    private final MutableLiveData<ProfileSettingFormState> profileSettingFormState = new MutableLiveData<>();
    private final MutableLiveData<UiResult<UserDto>> registerResult = new MutableLiveData<>();
    private final MutableLiveData<UiResult<LoginResponseDto>> loginResult = new MutableLiveData<>();

    private final LoginRepository loginRepository;
    private final ProfileRepository profileRepository;
    private final RegistrationRepository registrationRepository;

    public LiveData<ProfileSettingFormState> getProfileSettingFormState() {
        return profileSettingFormState;
    }

    public LiveData<UiResult<UserDto>> getRegisterResult() {
        return registerResult;
    }

    public LiveData<UiResult<LoginResponseDto>> getLoginResult() {
        return loginResult;
    }

    public ProfileSettingViewModel() {
        this.profileRepository = ProfileRepository.getInstance();
        this.registrationRepository = RegistrationRepository.getInstance();
        this.loginRepository = LoginRepository.getInstance();
    }

    public void register(String email, String password, String username, String firstName,
                         String middleName, String secondName, LocalDate birthDate) {
        registrationRepository.register(email, password, username, firstName, middleName, secondName,
                birthDate, new NetworkLiveDataFutureCallback<>(registerResult));
    }

    public void profileSettingDataChanged(String firstName, String lastName, String additionalName,
                                          String username, String birthDate) {
//  todo :
//        if (!isUserNameValid(username)) {
//            registrationFormState.setValue(new RegistrationFormState(R.string.invalid_username, null, null));
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
                case "email":
                    //todo!
                    break;
                case "password":
                    //todo!
                    break;
                case "username":
                    formState.setUsernameErrorString(field.getMessage());
                    break;
                case "firstName":
                    formState.setFirstNameErrorString(field.getMessage());
                    break;
                case "middleName":
                    formState.setAdditionalNameErrorString(field.getMessage());
                    break;
                case "secondName":
                    formState.setLastNameErrorString(field.getMessage());
                    break;
                case "birthDate":
                    formState.setBirthDateErrorString(field.getMessage());
                    break;
            }
        }
    }

    public void login(String email, String password) {
        loginRepository.login(email, password, new NetworkLiveDataFutureCallback<>(loginResult));
    }
}