package com.sergey.zhuravlev.mobile.social.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.google.common.util.concurrent.FutureCallback;
import com.sergey.zhuravlev.mobile.social.data.LoginRepository;
import com.sergey.zhuravlev.mobile.social.data.Result;
import com.sergey.zhuravlev.mobile.social.R;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.LoginResponseDto;
import com.sergey.zhuravlev.mobile.social.enums.ErrorCode;

import org.jetbrains.annotations.NotNull;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final LoginRepository loginRepository;

    LoginViewModel() {
        this.loginRepository = LoginRepository.getInstance();
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        loginRepository.login(username, password, new FutureCallback<Result<LoginResponseDto, ErrorDto>>() {
            @Override
            public void onSuccess(Result<LoginResponseDto, ErrorDto> result) {
                if (result.isSuccess()) {
                    LoginResponseDto data = ((Result.Success<LoginResponseDto, ErrorDto>) result).getData();
                    Log.i("LoginViewModel/login", "Successful authorization [jwtToken = " + data.getJwtToken() + "]");
                    loginResult.postValue(LoginResult.success(data.getJwtToken()));
                } else {
                    String errorMessage = ((Result.Error<LoginResponseDto, ErrorDto>) result).getMessage();
                    ErrorDto errorData = ((Result.Error<LoginResponseDto, ErrorDto>) result).getErrorObject(ErrorDto.class);
                    if (errorData != null) {
                        Log.w("LoginViewModel/login", "Authorization error [code = " + errorData.getCode() + "; message = " + errorData.getMessage() + "]");
                        processServerFieldError(errorData);
                        loginResult.postValue(LoginResult.error(errorData));
                    } else {
                        Log.e("LoginViewModel/login", "Authorization error. " + errorMessage);
                        loginResult.postValue(LoginResult.error(errorMessage));
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private void processServerFieldError(ErrorDto errorDto) {
        if (!errorDto.getCode().equals(ErrorCode.NOT_VALID) && !errorDto.getCode().equals(ErrorCode.FIELD_ERROR)) {
            return;
        }

        String emailError = null;
        String passwordError = null;
        for (ErrorDto.FieldError field : errorDto.getFields()) {
            if (field.getField().equals("email")) {
                emailError = field.getMessage();
            } else if (field.getField().equals("password")) {
                passwordError = field.getMessage();
            }
        }

        loginFormState.postValue(new LoginFormState(emailError, passwordError));
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
}