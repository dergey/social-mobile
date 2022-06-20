package com.sergey.zhuravlev.mobile.social.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.data.ProfileRepository;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;

import kotlinx.coroutines.CoroutineScope;

public class ProfileViewModel extends ViewModel {

    private final ProfileRepository profileRepository;


    public ProfileViewModel() {
        this.profileRepository = ProfileRepository.getInstance();
    }

    public LiveData<PagingData<ProfileDto>> fetchProfileFriendLiveData(String username) {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        return PagingLiveData.cachedIn(profileRepository.letProfileFriendLiveData(username), viewModelScope);
    }

}
