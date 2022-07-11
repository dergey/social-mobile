package com.sergey.zhuravlev.mobile.social.ui.profile;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDetailDto;
import com.sergey.zhuravlev.mobile.social.data.repository.ProfileRepository;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.ui.common.LiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.NetworkLiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.UiNetworkResult;

import kotlinx.coroutines.CoroutineScope;

public class ProfileViewModel extends ViewModel {

    private final ProfileRepository profileRepository;

    private final MutableLiveData<UiNetworkResult<ProfileDetailDto>> profileResult = new MutableLiveData<>();
    private final MutableLiveData<PageDto<Void>> profileFriendPage = new MutableLiveData<>();

    public ProfileViewModel(Context context) {
        this.profileRepository = ProfileRepository.getInstance(context);
    }

    public LiveData<PagingData<ProfileDto>> fetchProfileFriendLiveData(final String username) {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        return PagingLiveData.cachedIn(profileRepository.letProfileFriendLiveData(username, new LiveDataFutureCallback<>(profileFriendPage)), viewModelScope);
    }

    public void getProfile(final String username) {
        profileRepository.getProfile(username, new NetworkLiveDataFutureCallback<>(profileResult));
    }

    public LiveData<UiNetworkResult<ProfileDetailDto>> getProfileResult() {
        return profileResult;
    }

    public LiveData<PageDto<Void>> getProfileFriendPage() {
        return profileFriendPage;
    }
}
