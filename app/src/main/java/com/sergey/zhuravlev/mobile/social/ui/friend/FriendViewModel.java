package com.sergey.zhuravlev.mobile.social.ui.friend;

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

public class FriendViewModel extends ViewModel {

    private final ProfileRepository profileRepository;

    private final MutableLiveData<PageDto<Void>> currentFriendPage = new MutableLiveData<>();
    private final MutableLiveData<PageDto<Void>> currentFriendRequestPage = new MutableLiveData<>();
    private final MutableLiveData<UiResult<Void>> acceptResult = new MutableLiveData<>();
    private final MutableLiveData<UiResult<Void>> rejectResult = new MutableLiveData<>();

    public FriendViewModel() {
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

    public void acceptFriendRequest(final String username) {
        profileRepository.acceptFriendRequest(username, new NetworkLiveDataFutureCallback<>(acceptResult));
    }

    public void declineFriendRequest(final String username) {
        profileRepository.declineFriendRequest(username, new NetworkLiveDataFutureCallback<>(rejectResult));
    }

    public LiveData<PageDto<Void>> getCurrentFriendPage() {
        return currentFriendPage;
    }

    public LiveData<PageDto<Void>> getCurrentFriendRequestPage() {
        return currentFriendRequestPage;
    }

    public LiveData<UiResult<Void>> getAcceptResult() {
        return acceptResult;
    }

    public LiveData<UiResult<Void>> getRejectResult() {
        return rejectResult;
    }
}
