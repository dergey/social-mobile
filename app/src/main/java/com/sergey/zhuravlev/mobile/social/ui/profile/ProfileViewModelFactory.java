package com.sergey.zhuravlev.mobile.social.ui.profile;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public ProfileViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(context);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}