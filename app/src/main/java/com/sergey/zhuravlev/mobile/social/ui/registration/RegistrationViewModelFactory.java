package com.sergey.zhuravlev.mobile.social.ui.registration;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RegistrationViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public RegistrationViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegistrationViewModel.class)) {
            return (T) new RegistrationViewModel(context);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}