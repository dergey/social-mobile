package com.sergey.zhuravlev.mobile.social.data;

import androidx.lifecycle.LiveData;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.MessageEndpoints;
import com.sergey.zhuravlev.mobile.social.client.api.ProfileEndpoints;
import com.sergey.zhuravlev.mobile.social.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.dto.profile.ProfileDto;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProfileRepository {

    private static volatile ProfileRepository instance;

    public final static Integer DEFAULT_PAGE_INDEX = 0;
    public final static Integer DEFAULT_PAGE_SIZE = 20;

    private final Executor executor;
    private final ProfileEndpoints profileEndpoints;

    private ProfileRepository() {
        this.profileEndpoints = Client.getProfileEndpoints();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public static ProfileRepository getInstance() {
        if (instance == null) {
            instance = new ProfileRepository();
        }
        return instance;
    }

    public LiveData<PagingData<ProfileDto>> letProfileFriendLiveData(String username) {
        Pager<Integer, ProfileDto> pager = new Pager<>(getDefaultPageConfig(), () -> new ProfilePagingSource(profileEndpoints, username, executor));
        return PagingLiveData.getLiveData(pager);
    }

    public PagingConfig getDefaultPageConfig() {
        return new PagingConfig(DEFAULT_PAGE_SIZE, 0, true);
    }

}
