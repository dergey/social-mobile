package com.sergey.zhuravlev.mobile.social.ui.message;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.ExperimentalPagingApi;

import com.sergey.zhuravlev.mobile.social.ui.chat.ChatViewModel;

@ExperimentalPagingApi
public class MessageViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public MessageViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MessageViewModel.class)) {
            return (T) new MessageViewModel(context);
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }

}
