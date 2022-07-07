package com.sergey.zhuravlev.mobile.social.data;

import androidx.lifecycle.LiveData;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.ProfileEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDetailDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.ui.common.LiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.NetworkLiveDataFutureCallback;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class ProfileRepository {

    private static volatile ProfileRepository instance;

    public final static Integer DEFAULT_PAGE_SIZE = 20;

    private final ProfileEndpoints profileEndpoints;

    private final Executor executor;
    private final ProfileDataSource dataSource;

    private ProfileRepository() {
        this.profileEndpoints = Client.getProfileEndpoints();
        this.executor = Executors.newSingleThreadExecutor();
        this.dataSource = new ProfileDataSource(profileEndpoints, executor);
    }

    public static ProfileRepository getInstance() {
        if (instance == null) {
            instance = new ProfileRepository();
        }
        return instance;
    }

    public LiveData<PagingData<ProfileDto>> letProfileFriendLiveData(final String username, FutureCallback<PageDto<Void>> onePageCallback) {
        Function<CommonPagingSource.PageableLoad, ListenableFuture<PageDto<ProfileDto>>> endpointCall =
                l -> Futures.transform(profileEndpoints.getProfileFriends(l.getPayloadString("username"), l.getNextPageNumber(), l.getPageSize()),
                        response -> {
                            PageDto<Void> onePage = new PageDto<>();
                            onePage.setSize(response.getSize());
                            onePage.setNumber(response.getNumber());
                            onePage.setTotalPages(response.getTotalPages());
                            onePage.setTotalElements(response.getTotalElements());
                            onePage.setHasNext(response.getHasNext());
                            onePageCallback.onSuccess(onePage);
                            return response;
                        },
                        executor);
        Pager<Integer, ProfileDto> pager = new Pager<>(getDefaultPageConfig(), () -> new CommonPagingSource<>(
                endpointCall,
                ImmutableMap.of("username", username),
                DEFAULT_PAGE_SIZE,
                executor));
        return PagingLiveData.getLiveData(pager);
    }

    public LiveData<PagingData<ProfileDto>> letCurrentUserFriendLiveData(FutureCallback<PageDto<Void>> onePageCallback) {
        Function<CommonPagingSource.PageableLoad, ListenableFuture<PageDto<ProfileDto>>> endpointCall =
                l -> Futures.transform(profileEndpoints.getCurrentUserFriends(l.getNextPageNumber(), l.getPageSize()),
                        response -> {
                            PageDto<Void> onePage = new PageDto<>();
                            onePage.setSize(response.getSize());
                            onePage.setNumber(response.getNumber());
                            onePage.setTotalPages(response.getTotalPages());
                            onePage.setTotalElements(response.getTotalElements());
                            onePage.setHasNext(response.getHasNext());
                            onePageCallback.onSuccess(onePage);
                            return response;
                        },
                        executor);
        Pager<Integer, ProfileDto> pager = new Pager<>(getDefaultPageConfig(), () -> new CommonPagingSource<>(
                endpointCall,
                Collections.emptyMap(),
                DEFAULT_PAGE_SIZE,
                executor));
        return PagingLiveData.getLiveData(pager);
    }

    public LiveData<PagingData<ProfileDto>> letCurrentUserFriendRequestLiveData(LiveDataFutureCallback<PageDto<Void>> onePageCallback) {
        Function<CommonPagingSource.PageableLoad, ListenableFuture<PageDto<ProfileDto>>> endpointCall =
                l -> Futures.transform(profileEndpoints.getCurrentUserIncomingFriendRequests(l.getNextPageNumber(), l.getPageSize()),
                        response -> {
                            PageDto<Void> onePage = new PageDto<>();
                            onePage.setSize(response.getSize());
                            onePage.setNumber(response.getNumber());
                            onePage.setTotalPages(response.getTotalPages());
                            onePage.setTotalElements(response.getTotalElements());
                            onePage.setHasNext(response.getHasNext());
                            onePageCallback.onSuccess(onePage);
                            return response;
                        },
                        executor);
        Pager<Integer, ProfileDto> pager = new Pager<>(getDefaultPageConfig(), () -> new CommonPagingSource<>(
                endpointCall,
                Collections.emptyMap(),
                DEFAULT_PAGE_SIZE,
                executor));
        return PagingLiveData.getLiveData(pager);
    }

    public void getCurrentProfile(FutureCallback<Result<ProfileDetailDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.getCurrentProfile(), callback, executor);
    }

    public void getProfile(final String username, FutureCallback<Result<ProfileDetailDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.getProfile(username), callback, executor);
    }

    public PagingConfig getDefaultPageConfig() {
        return new PagingConfig(DEFAULT_PAGE_SIZE, 0, true);
    }

}
