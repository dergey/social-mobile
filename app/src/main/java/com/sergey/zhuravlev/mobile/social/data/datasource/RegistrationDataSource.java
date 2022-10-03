package com.sergey.zhuravlev.mobile.social.data.datasource;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.RegistrationEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.registration.CompleteRegistrationDto;
import com.sergey.zhuravlev.mobile.social.client.dto.registration.ContinuationDto;
import com.sergey.zhuravlev.mobile.social.client.dto.registration.ManualCodeConfirmationDto;
import com.sergey.zhuravlev.mobile.social.client.dto.registration.RegistrationStatusDto;
import com.sergey.zhuravlev.mobile.social.client.dto.registration.StartRegistrationDto;
import com.sergey.zhuravlev.mobile.social.client.dto.user.UserDto;
import com.sergey.zhuravlev.mobile.social.data.Result;
import com.sergey.zhuravlev.mobile.social.enums.Gender;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.Executor;

import retrofit2.HttpException;

public class RegistrationDataSource {

    private final RegistrationEndpoints registrationEndpoints;
    private final Executor executor;

    public RegistrationDataSource(RegistrationEndpoints registrationEndpoints, Executor executor) {
        this.registrationEndpoints = registrationEndpoints;
        this.executor = executor;
    }

    public ListenableFuture<Result<RegistrationStatusDto, ErrorDto>> startRegistration(String email) {
        StartRegistrationDto request = new StartRegistrationDto();
        request.setPhoneOrEmail(email);

        ListenableFuture<Result<RegistrationStatusDto, ErrorDto>> loginFuture =
                Futures.transform(registrationEndpoints.startRegistration(request),
                        Result.Success::new, executor);

        ListenableFuture<Result<RegistrationStatusDto, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }

    public ListenableFuture<Result<RegistrationStatusDto, ErrorDto>> confirmByCode(String code, String continuationCode) {
        ManualCodeConfirmationDto request = new ManualCodeConfirmationDto();
        request.setManualCode(code);
        request.setContinuationCode(continuationCode);

        ListenableFuture<Result<RegistrationStatusDto, ErrorDto>> loginFuture =
                Futures.transform(registrationEndpoints.confirmByCode(request),
                        Result.Success::new, executor);

        ListenableFuture<Result<RegistrationStatusDto, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }

    public ListenableFuture<Result<RegistrationStatusDto, ErrorDto>> resend(String continuationCode) {
        ContinuationDto request = new ContinuationDto();
        request.setContinuationCode(continuationCode);

        ListenableFuture<Result<RegistrationStatusDto, ErrorDto>> loginFuture =
                Futures.transform(registrationEndpoints.resendConfirmation(request),
                        Result.Success::new, executor);

        ListenableFuture<Result<RegistrationStatusDto, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }

    public ListenableFuture<Result<UserDto, ErrorDto>> completeRegistration(String continuationCode, String password,
                                                                            String username, String firstName,
                                                                            String middleName, String secondName,
                                                                            Gender gender, LocalDate birthDate) {
        CompleteRegistrationDto request = new CompleteRegistrationDto();
        request.setContinuationCode(continuationCode);
        request.setPassword(password);
        request.setUsername(username);
        request.setFirstName(firstName);
        request.setMiddleName(middleName);
        request.setSecondName(secondName);
        request.setGender(gender);
        request.setBirthDate(birthDate);

        ListenableFuture<Result<UserDto, ErrorDto>> loginFuture =
                Futures.transform(registrationEndpoints.completeRegistration(request),
                        Result.Success::new, executor);

        ListenableFuture<Result<UserDto, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }

}