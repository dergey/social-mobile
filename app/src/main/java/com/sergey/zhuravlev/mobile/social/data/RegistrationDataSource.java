package com.sergey.zhuravlev.mobile.social.data;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.LoginEndpoints;
import com.sergey.zhuravlev.mobile.social.client.api.RegistrationEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.LoginDto;
import com.sergey.zhuravlev.mobile.social.client.dto.LoginResponseDto;
import com.sergey.zhuravlev.mobile.social.client.dto.RegistrationDto;
import com.sergey.zhuravlev.mobile.social.client.dto.user.UserDto;

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

    public ListenableFuture<Result<UserDto, ErrorDto>> register(String email, String password,
                                                                String username, String firstName,
                                                                String middleName, String secondName,
                                                                String city, LocalDate birthDate) {
        RegistrationDto request = new RegistrationDto(email, password, username, firstName,
                middleName, secondName, city, birthDate);

        ListenableFuture<Result<UserDto, ErrorDto>> loginFuture =
                Futures.transform(registrationEndpoints.register(request),
                        Result.Success::new, executor);

        ListenableFuture<Result<UserDto, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }

}