package com.sergey.zhuravlev.mobile.social.data.datasource;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.ProfileEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDetailDto;
import com.sergey.zhuravlev.mobile.social.client.mapper.ProfileModelMapper;
import com.sergey.zhuravlev.mobile.social.data.Result;
import com.sergey.zhuravlev.mobile.social.database.dao.ProfileDetailModelDao;
import com.sergey.zhuravlev.mobile.social.database.dao.ProfileModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileAndDetailModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileModel;
import com.sergey.zhuravlev.mobile.social.exception.CacheNotFoundException;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import retrofit2.HttpException;

@SuppressWarnings("UnstableApiUsage")
public class ProfileDataSource {

    private final ProfileEndpoints profileEndpoints;
    private final ProfileModelDao profileModelDao;
    private final ProfileDetailModelDao profileDetailModelDao;
    private final Executor executor;

    public ProfileDataSource(ProfileEndpoints profileEndpoints, ProfileModelDao profileModelDao,
                             ProfileDetailModelDao profileDetailModelDao, Executor executor) {
        this.profileEndpoints = profileEndpoints;
        this.profileModelDao = profileModelDao;
        this.profileDetailModelDao = profileDetailModelDao;
        this.executor = executor;
    }

    public ListenableFuture<Result<ProfileAndDetailModel, Void>> getCacheCurrentProfile() {
        ListenableFuture<ProfileAndDetailModel> databaseFuture = Futures.submit(
                () -> Optional.ofNullable(profileModelDao.getCurrentWithDetail())
                        .orElseThrow(() -> new CacheNotFoundException(ProfileAndDetailModel.class)),
                executor);

        ListenableFuture<Result<ProfileAndDetailModel, Void>> databaseResultFuture =
                Futures.transform(databaseFuture, Result.Success::new, executor);

        return Futures.catching(databaseResultFuture, CacheNotFoundException.class,
                Result.Error::fromCacheException, executor);
    }

    public ListenableFuture<Result<ProfileAndDetailModel, ErrorDto>> getNetworkCurrentProfile() {
        ListenableFuture<Result<ProfileDetailDto, ErrorDto>> loginFuture =
                Futures.transform(profileEndpoints.getCurrentUserProfile(),
                        Result.Success::new, executor);

        ListenableFuture<Result<ProfileDetailDto, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        ListenableFuture<Result<ProfileDetailDto, ErrorDto>> catchingResultFuture = Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);

        ListenableFuture<Result<ProfileAndDetailModel, ErrorDto>> modelResultFuture = Futures.transform(catchingResultFuture,
                networkResult -> networkResult.map(ProfileModelMapper::toModel), executor);

        // Save network result to cache:
        return Futures.transform(modelResultFuture, result -> {
            if (result.isSuccess() && !result.isCache()) {
                ProfileAndDetailModel model = ((Result.Success<ProfileAndDetailModel, ErrorDto>) result).getData();
                model.getProfile().setCurrent(true);
                profileModelDao.insert(model.getProfile());
                profileDetailModelDao.insert(model.getDetail());
            }
            return result;
        }, executor);
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

    public ListenableFuture<Result<Void, ErrorDto>> acceptFriendRequest(String username) {
        ListenableFuture<Result<Void, ErrorDto>> loginFuture =
                Futures.transform(profileEndpoints.acceptFriendRequest(username),
                        r -> new Result.Success<>(null), executor);

        ListenableFuture<Result<Void, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }

    public ListenableFuture<Result<Void, ErrorDto>> declineFriendRequest(String username) {
        ListenableFuture<Result<Void, ErrorDto>> loginFuture =
                Futures.transform(profileEndpoints.declineFriendRequest(username),
                        r -> new Result.Success<>(null), executor);

        ListenableFuture<Result<Void, ErrorDto>> partialResultFuture =
                Futures.catching(loginFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        return Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);
    }
}
