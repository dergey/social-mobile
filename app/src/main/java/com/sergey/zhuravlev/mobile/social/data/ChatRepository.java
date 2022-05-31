package com.sergey.zhuravlev.mobile.social.data;

import androidx.lifecycle.LiveData;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.ChatEndpoints;
import com.sergey.zhuravlev.mobile.social.dto.chat.ChatPreviewDto;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@ExperimentalPagingApi
public class ChatRepository {

    private static volatile ChatRepository instance;

    public final static Integer DEFAULT_PAGE_INDEX = 0;
    public final static Integer DEFAULT_PAGE_SIZE = 20;

    private final Executor executor;
    private final ChatEndpoints chatEndpoints;

    private ChatRepository() {
        this.chatEndpoints = Client.getChatEndpoints();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public static ChatRepository getInstance() {
        if (instance == null) {
            instance = new ChatRepository();
        }
        return instance;
    }

    public void updateReadStatus() {

    }

    public LiveData<PagingData<ChatPreviewDto>> letChatPreviewsLiveData() {
        Pager<Integer, ChatPreviewDto> pager = new Pager<>(getDefaultPageConfig(), () -> new ChatPagingSource(chatEndpoints, executor));
        return PagingLiveData.getLiveData(pager);
    }

    public PagingConfig getDefaultPageConfig() {
        return new PagingConfig(DEFAULT_PAGE_SIZE, 0, true);
    }

}
