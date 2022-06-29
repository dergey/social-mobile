package com.sergey.zhuravlev.mobile.social.data;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.PagingLiveData;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.sergey.zhuravlev.mobile.social.client.Client;
import com.sergey.zhuravlev.mobile.social.client.api.MessageEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.LoginResponseDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.client.mapper.MessageModelMapper;
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
    private final MessageDataSource dataSource;

    private MessageRepository(Context context) {
        this.messageEndpoints = Client.getMessageEndpoints();
        this.executor = Executors.newSingleThreadExecutor();
        this.database = AppDatabase.getInstance(context);
        this.messageModelDao = database.getMessageModelDao();
        this.dataSource = new MessageDataSource(messageEndpoints, executor, context);
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

    public void createTextMessage(Long chatId, String text, FutureCallback<Result<MessageDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.createTextMessage(chatId, text), new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Result<MessageDto, ErrorDto> result) {
                if (result.isSuccess()) {
                    messageModelDao.insert(MessageModelMapper.toModel(((Result.Success<MessageDto, ErrorDto>) result).getData()));
                }
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        }, executor);


    }

    public void createImageMessage(Long chatId, final Uri filePath, FutureCallback<Result<MessageDto, ErrorDto>> callback) {
        Futures.addCallback(dataSource.createImageMessage(chatId, filePath), callback, executor);
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
