package com.sergey.zhuravlev.mobile.social.data;

import androidx.lifecycle.LiveData;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.MessageEndpoints;
import com.sergey.zhuravlev.mobile.social.dto.message.MessageDto;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MessageRepository {

    private static volatile MessageRepository instance;

    public final static Integer DEFAULT_PAGE_INDEX = 0;
    public final static Integer DEFAULT_PAGE_SIZE = 20;

    private final Executor executor;
    private final MessageEndpoints messageEndpoints;

    private MessageRepository() {
        this.messageEndpoints = Client.getMessageEndpoints();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public static MessageRepository getInstance() {
        if (instance == null) {
            instance = new MessageRepository();
        }
        return instance;
    }

    public LiveData<PagingData<MessageDto>> letChatMessageLiveData(Long chatId) {
        Pager<Integer, MessageDto> pager = new Pager<>(getDefaultPageConfig(), () -> new MessagePagingSource(messageEndpoints, chatId, executor));
        return PagingLiveData.getLiveData(pager);
    }

    public PagingConfig getDefaultPageConfig() {
        return new PagingConfig(DEFAULT_PAGE_SIZE, 0, true);
    }

}
