package com.sergey.zhuravlev.mobile.social.data;

import androidx.lifecycle.LiveData;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.ProfileEndpoints;
import com.sergey.zhuravlev.mobile.social.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.dto.profile.ProfileDetailDto;
import com.sergey.zhuravlev.mobile.social.dto.profile.ProfileDto;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    public LiveData<PagingData<ProfileDto>> letProfileFriendLiveData(final String username) {
        Pager<Integer, ProfileDto> pager = new Pager<>(getDefaultPageConfig(), () -> new CommonPagingSource<>(
                l -> profileEndpoints.getProfileFriends(l.getPayloadString("username"), l.getNextPageNumber(), l.getPageSize()),
                ImmutableMap.of("username", username),
                DEFAULT_PAGE_SIZE,
                executor));
        return PagingLiveData.getLiveData(pager);
    }

    public LiveData<PagingData<ProfileDto>> letCurrentUserFriendLiveData() {
        Pager<Integer, ProfileDto> pager = new Pager<>(getDefaultPageConfig(), () -> new CommonPagingSource<>(
                l -> profileEndpoints.getCurrentUserFriends(l.getNextPageNumber(), l.getPageSize()),
                Collections.emptyMap(),
                DEFAULT_PAGE_SIZE,
                executor));
        return PagingLiveData.getLiveData(pager);
    }

    public void getCurrentProfile(FutureCallback<Result<ProfileDetailDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.getCurrentProfile(), callback, executor);
    }

    public PagingConfig getDefaultPageConfig() {
        return new PagingConfig(DEFAULT_PAGE_SIZE, 0, true);
    }

}
