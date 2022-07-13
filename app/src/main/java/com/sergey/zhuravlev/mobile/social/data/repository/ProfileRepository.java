package com.sergey.zhuravlev.mobile.social.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;
import androidx.room.Database;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.ChatEndpoints;
import com.sergey.zhuravlev.mobile.social.client.api.ProfileEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDetailDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.data.Result;
import com.sergey.zhuravlev.mobile.social.data.datasource.ChatDataSource;
import com.sergey.zhuravlev.mobile.social.data.datasource.ProfileDataSource;
import com.sergey.zhuravlev.mobile.social.data.paging.CommonPagingSource;
import com.sergey.zhuravlev.mobile.social.database.AppDatabase;
import com.sergey.zhuravlev.mobile.social.database.dao.ProfileDetailModelDao;
import com.sergey.zhuravlev.mobile.social.database.dao.ProfileModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.ChatModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileAndDetailModel;
import com.sergey.zhuravlev.mobile.social.ui.common.LiveDataFutureCallback;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

@ExperimentalPagingApi
public class ProfileRepository {

    private static volatile ProfileRepository instance;

    public final static Integer DEFAULT_PAGE_SIZE = 20;

    private final ProfileEndpoints profileEndpoints;
    private final ChatEndpoints chatEndpoints;

    private final Executor executor;
    private final AppDatabase database;
    private final ProfileDataSource profileDataSource;
    private final ChatDataSource chatDataSource;

    private ProfileRepository(Context context) {
        this.profileEndpoints = Client.getProfileEndpoints();
        this.chatEndpoints = Client.getChatEndpoints();
        this.database = AppDatabase.getInstance(context);
        this.executor = Executors.newSingleThreadExecutor();
        this.profileDataSource = new ProfileDataSource(profileEndpoints, database, executor);
        this.chatDataSource = new ChatDataSource(chatEndpoints, database, executor);
    }

    public static ProfileRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ProfileRepository(context);
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

    public void getOrCreateChat(String username,
                                FutureCallback<Result<ChatModel, Void>> cacheCallback,
                                FutureCallback<Result<ChatModel, ErrorDto>> networkCallback) {
        Futures.addCallback(chatDataSource.getCacheChat(username), cacheCallback, executor);
        Futures.addCallback(chatDataSource.getOrCreateNetworkChat(username), networkCallback, executor);
    }

    public void getCurrentProfile(FutureCallback<Result<ProfileAndDetailModel, Void>> cacheCallback,
                                  FutureCallback<Result<ProfileAndDetailModel, ErrorDto>> networkCallback) {
        Futures.addCallback(profileDataSource.getCacheCurrentProfile(), cacheCallback, executor);
        Futures.addCallback(profileDataSource.getNetworkCurrentProfile(), networkCallback, executor);
    }

    public void getProfile(final String username, FutureCallback<Result<ProfileDetailDto, ErrorDto>> callback) {
        Futures.addCallback(profileDataSource.getProfile(username), callback, executor);
    }

    public void acceptFriendRequest(final String username, FutureCallback<Result<Void, ErrorDto>> callback) {
        Futures.addCallback(profileDataSource.acceptFriendRequest(username), callback, executor);
    }

    public void declineFriendRequest(final String username, FutureCallback<Result<Void, ErrorDto>> callback) {
        Futures.addCallback(profileDataSource.declineFriendRequest(username), callback, executor);
    }

    public PagingConfig getDefaultPageConfig() {
        return new PagingConfig(DEFAULT_PAGE_SIZE, 0, true);
    }

}
