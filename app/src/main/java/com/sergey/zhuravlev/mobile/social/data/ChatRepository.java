package com.sergey.zhuravlev.mobile.social.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.ChatEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.chat.ChatPreviewDto;
import com.sergey.zhuravlev.mobile.social.database.AppDatabase;
import com.sergey.zhuravlev.mobile.social.database.dao.ChatPreviewModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.ChatPreviewModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@ExperimentalPagingApi
public class ChatRepository {

    private static volatile ChatRepository instance;

    public final static Integer DEFAULT_PAGE_INDEX = 0;
    public final static Integer DEFAULT_PAGE_SIZE = 20;

    private final Executor executor;
    private final AppDatabase database;
    private final ChatEndpoints chatEndpoints;
    private final ChatPreviewModelDao chatPreviewModelDao;

    private ChatRepository(Context context) {
        this.chatEndpoints = Client.getChatEndpoints();
        this.executor = Executors.newSingleThreadExecutor();
        this.database = AppDatabase.getInstance(context);
        this.chatPreviewModelDao = database.getChatPreviewModelDao();
    }

    public static ChatRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ChatRepository(context);
        }
        return instance;
    }

    public void updateReadStatus() {

    }

    public LiveData<PagingData<ChatPreviewModel>> letChatPreviewModelLiveData() {
        Pager<Integer, ChatPreviewModel> pager = new Pager<>(
                getDefaultPageConfig(),
                null,
                new ChatRemoteMediator(chatEndpoints, database, DEFAULT_PAGE_SIZE, executor),
                chatPreviewModelDao::getAllChatModel);
        return PagingLiveData.getLiveData(pager);
    }

    public PagingConfig getDefaultPageConfig() {
        return new PagingConfig(DEFAULT_PAGE_SIZE, 0, true);
    }

}
