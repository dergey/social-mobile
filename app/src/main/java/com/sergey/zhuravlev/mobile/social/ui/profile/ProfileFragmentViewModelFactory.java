package com.sergey.zhuravlev.mobile.social.ui.profile;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProfileFragmentViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public ProfileFragmentViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileFragmentViewModel.class)) {
            return (T) new ProfileFragmentViewModel(context);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}