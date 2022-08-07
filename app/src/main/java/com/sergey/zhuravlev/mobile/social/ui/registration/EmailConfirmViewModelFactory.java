package com.sergey.zhuravlev.mobile.social.ui.registration;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class EmailConfirmViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public EmailConfirmViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(EmailConfirmViewModel.class)) {
            return (T) new EmailConfirmViewModel(context);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}