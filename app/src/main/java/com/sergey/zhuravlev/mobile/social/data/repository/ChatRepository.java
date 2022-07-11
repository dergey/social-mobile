package com.sergey.zhuravlev.mobile.social.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.ChatEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.data.datasource.ChatDataSource;
import com.sergey.zhuravlev.mobile.social.data.Result;
import com.sergey.zhuravlev.mobile.social.data.mediator.ChatRemoteMediator;
import com.sergey.zhuravlev.mobile.social.database.AppDatabase;
import com.sergey.zhuravlev.mobile.social.database.dao.ChatModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.ChatAndLastMessageModel;

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
    private final ChatModelDao chatModelDao;
    private final ChatDataSource dataSource;

    private ChatRepository(Context context) {
        this.chatEndpoints = Client.getChatEndpoints();
        this.executor = Executors.newSingleThreadExecutor();
        this.database = AppDatabase.getInstance(context);
        this.chatModelDao = database.getChatModelDao();
        this.dataSource = new ChatDataSource(chatEndpoints, database, executor);
    }

    public static ChatRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ChatRepository(context);
        }
        return instance;
    }

    public void updateReadStatus(Long chatId, FutureCallback<Result<Void, ErrorDto>> callback) {
        Futures.addCallback(dataSource.updateReadStatus(chatId), callback, executor);
    }

    public LiveData<PagingData<ChatAndLastMessageModel>> letChatPreviewModelLiveData() {
        Pager<Integer, ChatAndLastMessageModel> pager = new Pager<>(
                getDefaultPageConfig(),
                null,
                new ChatRemoteMediator(chatEndpoints, database, DEFAULT_PAGE_SIZE, executor),
                chatModelDao::getAllChatAndLastMessageModel);
        return PagingLiveData.getLiveData(pager);
    }

    public PagingConfig getDefaultPageConfig() {
        return new PagingConfig(DEFAULT_PAGE_SIZE, 0, true);
    }

}
