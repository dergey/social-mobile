package com.sergey.zhuravlev.mobile.social.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.MessageEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.database.AppDatabase;
import com.sergey.zhuravlev.mobile.social.database.dao.ChatPreviewModelDao;
import com.sergey.zhuravlev.mobile.social.database.dao.MessageModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.ChatPreviewModel;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@ExperimentalPagingApi
public class MessageRepository {

    private static volatile MessageRepository instance;

    public final static Integer DEFAULT_PAGE_INDEX = 0;
    public final static Integer DEFAULT_PAGE_SIZE = 20;

    private final Executor executor;
    private final AppDatabase database;
    private final MessageEndpoints messageEndpoints;
    private final MessageModelDao messageModelDao;

    private MessageRepository(Context context) {
        this.messageEndpoints = Client.getMessageEndpoints();
        this.executor = Executors.newSingleThreadExecutor();
        this.database = AppDatabase.getInstance(context);
        this.messageModelDao = database.getMessageModelDao();
    }

    public static MessageRepository getInstance(Context context) {
        if (instance == null) {
            instance = new MessageRepository(context);
        }
        return instance;
    }

    public LiveData<PagingData<MessageModel>> letChatMessageModelLiveData(Long chatId) {
        Pager<Integer, MessageModel> pager = new Pager<>(
                getDefaultPageConfig(),
                null,
                new MessageRemoteMediator(messageEndpoints, database, chatId, DEFAULT_PAGE_SIZE, executor),
                () -> messageModelDao.getAllMessageModel(chatId));
        return PagingLiveData.getLiveData(pager);
    }

    public PagingConfig getDefaultPageConfig() {
        return new PagingConfig(DEFAULT_PAGE_SIZE, 0, true);
    }

}
