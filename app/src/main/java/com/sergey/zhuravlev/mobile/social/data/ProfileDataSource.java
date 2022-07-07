package com.sergey.zhuravlev.mobile.social.data;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.ProfileEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDetailDto;

import java.io.IOException;
import java.util.concurrent.Executor;

import retrofit2.HttpException;

public class ProfileDataSource {

    private final ProfileEndpoints profileEndpoints;
    private final Executor executor;

    public ProfileDataSource(ProfileEndpoints profileEndpoints, Executor executor) {
        this.profileEndpoints = profileEndpoints;
        this.executor = executor;
    }
    public ListenableFuture<Result<ProfileDetailDto, ErrorDto>> getCurrentProfile() {
        ListenableFuture<Result<ProfileDetailDto, ErrorDto>> loginFuture =
                Futures.transform(profileEndpoints.getCurrentUserProfile(),
                        Result.Success::new, executor);

        ListenableFuture<Result<ProfileDetailDto, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }

    public ListenableFuture<Result<ProfileDetailDto, ErrorDto>> getProfile(String username) {
        ListenableFuture<Result<ProfileDetailDto, ErrorDto>> loginFuture =
                Futures.transform(profileEndpoints.getProfile(username),
                        Result.Success::new, executor);

        ListenableFuture<Result<ProfileDetailDto, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }
}
