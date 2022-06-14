package com.sergey.zhuravlev.mobile.social.ui.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.google.common.util.concurrent.FutureCallback;
import com.sergey.zhuravlev.mobile.social.data.ProfileRepository;
import com.sergey.zhuravlev.mobile.social.data.Result;
import com.sergey.zhuravlev.mobile.social.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.dto.profile.ProfileDetailDto;
import com.sergey.zhuravlev.mobile.social.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.ui.profile.result.GetCurrentProfileResult;

import org.jetbrains.annotations.NotNull;

import kotlinx.coroutines.CoroutineScope;

public class ProfileFragmentViewModel extends ViewModel {

    private final ProfileRepository profileRepository;

    private final MutableLiveData<GetCurrentProfileResult> currentProfileResult = new MutableLiveData<>();

    public ProfileFragmentViewModel() {
        this.profileRepository = ProfileRepository.getInstance();
    }

    public LiveData<PagingData<ProfileDto>> fetchCurrentUserFriendLiveData() {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        return PagingLiveData.cachedIn(profileRepository.letCurrentUserFriendLiveData(), viewModelScope);
    }

    public void getCurrentProfile() {
        profileRepository.getCurrentProfile(new FutureCallback<Result<ProfileDetailDto, ErrorDto>>() {
            @Override
            public void onSuccess(Result<ProfileDetailDto, ErrorDto> result) {
                if (result.isSuccess()) {
                    ProfileDetailDto data = ((Result.Success<ProfileDetailDto, ErrorDto>) result).getData();
                    Log.i("GET_CURRENT_PROFILE", "Successful get current profile [profile = " + data + "]");
                    currentProfileResult.postValue(GetCurrentProfileResult.success(data));
                } else {
                    String errorMessage = ((Result.Error<ProfileDetailDto, ErrorDto>) result).getMessage();
                    ErrorDto errorData = ((Result.Error<ProfileDetailDto, ErrorDto>) result).getErrorObject(ErrorDto.class);
                    if (errorData != null) {
                        Log.w("GET_CURRENT_PROFILE", "Error [code = " + errorData.getCode() + "; message = " + errorData.getMessage() + "]");
                        currentProfileResult.postValue(GetCurrentProfileResult.error(errorData));
                    } else {
                        Log.e("GET_CURRENT_PROFILE", "Error. " + errorMessage);
                        currentProfileResult.postValue(GetCurrentProfileResult.error(errorMessage));
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Throwable t) {
            }
        });
    }

    public MutableLiveData<GetCurrentProfileResult> getCurrentProfileResult() {
        return currentProfileResult;
    }
}
