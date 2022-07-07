package com.sergey.zhuravlev.mobile.social.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDetailDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.data.ProfileRepository;
import com.sergey.zhuravlev.mobile.social.ui.common.LiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.NetworkLiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.UiResult;

import kotlinx.coroutines.CoroutineScope;

public class ProfileFragmentViewModel extends ViewModel {

    private final ProfileRepository profileRepository;

    private final MutableLiveData<UiResult<ProfileDetailDto>> currentProfileResult = new MutableLiveData<>();
    private final MutableLiveData<PageDto<Void>> currentFriendPage = new MutableLiveData<>();
    private final MutableLiveData<PageDto<Void>> currentFriendRequestPage = new MutableLiveData<>();

    public ProfileFragmentViewModel() {
        this.profileRepository = ProfileRepository.getInstance();
    }

    public LiveData<PagingData<ProfileDto>> fetchCurrentUserFriendLiveData() {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        return PagingLiveData.cachedIn(profileRepository.letCurrentUserFriendLiveData(new LiveDataFutureCallback<>(currentFriendPage)), viewModelScope);
    }

    public LiveData<PagingData<ProfileDto>> fetchCurrentUserFriendRequestLiveData() {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        return PagingLiveData.cachedIn(profileRepository.letCurrentUserFriendRequestLiveData(new LiveDataFutureCallback<>(currentFriendRequestPage)), viewModelScope);
    }

    public void getCurrentProfile() {
        profileRepository.getCurrentProfile(new NetworkLiveDataFutureCallback<>(currentProfileResult));
    }

    public LiveData<UiResult<ProfileDetailDto>> getCurrentProfileResult() {
        return currentProfileResult;
    }

    public LiveData<PageDto<Void>> getCurrentFriendPage() {
        return currentFriendPage;
    }

    public LiveData<PageDto<Void>> getCurrentFriendRequestPage() {
        return currentFriendRequestPage;
    }
}
