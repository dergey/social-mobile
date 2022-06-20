package com.sergey.zhuravlev.mobile.social.ui.message;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.data.MessageRepository;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;

import kotlinx.coroutines.CoroutineScope;

@ExperimentalPagingApi
public class MessageViewModel extends ViewModel {

    private final MessageRepository messageRepository;

    public MessageViewModel(Context context) {
        this.messageRepository = MessageRepository.getInstance(context);
    }

    public LiveData<PagingData<MessageModel>> fetchChatMessageModelLiveData(Long chatId) {
        CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
        return PagingLiveData.cachedIn(messageRepository.letChatMessageModelLiveData(chatId), viewModelScope);
    }

}
