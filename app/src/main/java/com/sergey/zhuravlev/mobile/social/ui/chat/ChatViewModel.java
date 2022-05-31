package com.sergey.zhuravlev.mobile.social.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.data.ChatRepository;
import com.sergey.zhuravlev.mobile.social.dto.chat.ChatPreviewDto;

import kotlinx.coroutines.CoroutineScope;

@ExperimentalPagingApi
public class ChatViewModel extends ViewModel {

    private final ChatRepository chatRepository;

    public ChatViewModel() {
        this.chatRepository = ChatRepository.getInstance();
    }

    public LiveData<PagingData<ChatPreviewDto>> fetchChatPreviewsLiveData() {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        return PagingLiveData.cachedIn(chatRepository.letChatPreviewsLiveData(), viewModelScope);
    }

}