package com.sergey.zhuravlev.mobile.social.ui.chat;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.data.ChatRepository;
import com.sergey.zhuravlev.mobile.social.client.dto.chat.ChatPreviewDto;
import com.sergey.zhuravlev.mobile.social.database.model.ChatPreviewModel;

import kotlinx.coroutines.CoroutineScope;

@ExperimentalPagingApi
public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;

    public ChatViewModel(Context context) {
        this.chatRepository = ChatRepository.getInstance(context);
    }

    public LiveData<PagingData<ChatPreviewModel>> fetchChatPreviewModelLiveData() {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        return PagingLiveData.cachedIn(chatRepository.letChatPreviewModelLiveData(), viewModelScope);
    }

}