package com.sergey.zhuravlev.mobile.social.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;
import androidx.paging.PagingSource;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.MessageEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.database.AppDatabase;
import com.sergey.zhuravlev.mobile.social.database.dao.MessageModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.ui.common.LiveDataFutureCallback;
import com.sergey.zhuravlev.mobile.social.ui.common.NetworkLiveDataFutureCallback;

import java.util.LinkedHashMap;
import java.util.Map;
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
    private final MessageDataSource dataSource;

    private MessageRepository(Context context) {
        this.messageEndpoints = Client.getMessageEndpoints();
        this.executor = Executors.newSingleThreadExecutor();
        this.database = AppDatabase.getInstance(context);
        this.messageModelDao = database.getMessageModelDao();
        this.dataSource = new MessageDataSource(messageEndpoints, database, executor, context);
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
                () -> {
                    PagingSource<Integer, MessageModel> pagingSource = messageModelDao.getAllMessageModel(chatId);
                    return pagingSource;
                });
        return PagingLiveData.getLiveData(pager);
    }

    public void createTextMessage(Long chatId, String text,
                                  FutureCallback<MessageModel> partialCallback,
                                  FutureCallback<Result<MessageDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.createTextMessage(chatId, text, partialCallback), callback, executor);
    }

    public void createImageMessage(Long chatId, final Uri filePath,
                                   FutureCallback<MessageModel> partialCallback,
                                   FutureCallback<Result<MessageDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.createImageMessage(chatId, filePath, partialCallback), callback, executor);
    }

    public void createStickerMessage(Long chatId, Long stickerId, FutureCallback<Result<MessageDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.createStickerMessage(chatId, stickerId), callback, executor);
    }

    public void deleteMessage(Long chatId, Long messageId, FutureCallback<Result<Void, ErrorDto>> callback) {
        Futures.addCallback(dataSource.deleteMessage(chatId, messageId), callback, executor);
    }

    public PagingConfig getDefaultPageConfig() {
        return new PagingConfig(DEFAULT_PAGE_SIZE, 1, false);
    }

}
