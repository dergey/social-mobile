package com.sergey.zhuravlev.mobile.social.ui.profile;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.data.repository.ProfileRepository;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileAndDetailModel;
import com.sergey.zhuravlev.mobile.social.ui.common.CacheLiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.LiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.NetworkLiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.UiResult;
import com.sergey.zhuravlev.mobile.social.ui.common.UiNetworkResult;

import kotlinx.coroutines.CoroutineScope;

@SuppressLint("UnsafeOptInUsageError")
public class ProfileFragmentViewModel extends ViewModel {

    private final ProfileRepository profileRepository;

    private final MutableLiveData<UiResult<ProfileAndDetailModel>> cacheCurrentProfileResult = new MutableLiveData<>();
    private final MutableLiveData<UiNetworkResult<ProfileAndDetailModel>> networkCurrentProfileResult = new MutableLiveData<>();
    private final MutableLiveData<PageDto<Void>> currentFriendPage = new MutableLiveData<>();
    private final MutableLiveData<PageDto<Void>> currentFriendRequestPage = new MutableLiveData<>();

    public ProfileFragmentViewModel(Context context) {
        this.profileRepository = ProfileRepository.getInstance(context);
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
        profileRepository.getCurrentProfile(
                new CacheLiveDataFutureCallback<>(cacheCurrentProfileResult),
                new NetworkLiveDataFutureCallback<>(networkCurrentProfileResult));
    }

    public MutableLiveData<UiResult<ProfileAndDetailModel>> getCacheCurrentProfileResult() {
        return cacheCurrentProfileResult;
    }

    public MutableLiveData<UiNetworkResult<ProfileAndDetailModel>> getNetworkCurrentProfileResult() {
        return networkCurrentProfileResult;
    }

    public LiveData<PageDto<Void>> getCurrentFriendPage() {
        return currentFriendPage;
    }

    public LiveData<PageDto<Void>> getCurrentFriendRequestPage() {
        return currentFriendRequestPage;
    }
}
