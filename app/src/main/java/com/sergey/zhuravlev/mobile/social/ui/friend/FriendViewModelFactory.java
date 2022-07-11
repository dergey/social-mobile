package com.sergey.zhuravlev.mobile.social.ui.friend;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.sergey.zhuravlev.mobile.social.ui.profile.ProfileFragmentViewModel;

public class FriendViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public FriendViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FriendViewModel.class)) {
            return (T) new FriendViewModel(context);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}