package com.sergey.zhuravlev.mobile.social.ui.message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.data.MessageRepository;
import com.sergey.zhuravlev.mobile.social.dto.message.MessageDto;

import kotlinx.coroutines.CoroutineScope;

public class MessageViewModel extends ViewModel {

    private final MessageRepository messageRepository;

    public MessageViewModel() {
        this.messageRepository = MessageRepository.getInstance();
    }

    public LiveData<PagingData<MessageDto>> fetchChatMessageLiveData(Long chatId) {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        return PagingLiveData.cachedIn(messageRepository.letChatMessageLiveData(chatId), viewModelScope);
    }

}
