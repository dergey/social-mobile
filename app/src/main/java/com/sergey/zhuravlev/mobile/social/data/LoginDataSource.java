package com.sergey.zhuravlev.mobile.social.data;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.LoginEndpoints;
import com.sergey.zhuravlev.mobile.social.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.dto.LoginDto;
import com.sergey.zhuravlev.mobile.social.dto.LoginResponseDto;

import java.io.IOException;
import java.util.concurrent.Executor;

import retrofit2.HttpException;

public class LoginDataSource {

    private final LoginEndpoints loginEndpoints;
    private final Executor executor;

    public LoginDataSource(LoginEndpoints loginEndpoints, Executor executor) {
        this.loginEndpoints = loginEndpoints;
        this.executor = executor;
    }
    public ListenableFuture<Result<LoginResponseDto, ErrorDto>> login(String email, String password) {
        LoginDto request = new LoginDto(email, password, true);

            ListenableFuture<Result<LoginResponseDto, ErrorDto>> loginFuture =
                    Futures.transform(loginEndpoints.authenticate(request),
                            Result.Success::new, executor);

            ListenableFuture<Result<LoginResponseDto, ErrorDto>> partialResultFuture =
                    Futures.catching(loginFuture, HttpException.class,
                            Result.Error::fromHttpException, executor);

            return Futures.catching(partialResultFuture,
                    IOException.class, Result.Error::fromIOException, executor);
        }

}