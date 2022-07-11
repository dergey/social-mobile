package com.sergey.zhuravlev.mobile.social.data.datasource;

import android.content.Context;
import android.util.Log;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.ChatEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.data.Result;
import com.sergey.zhuravlev.mobile.social.database.AppDatabase;
import com.sergey.zhuravlev.mobile.social.database.dao.ChatModelDao;
import com.sergey.zhuravlev.mobile.social.database.dao.MessageModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.ChatModel;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.enums.MessageSenderType;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.HttpException;

@SuppressWarnings("UnstableApiUsage")
public class ChatDataSource {

    private final ChatEndpoints chatEndpoints;
    private final AppDatabase database;
    private final ChatModelDao chatModelDao;
    private final MessageModelDao messageModelDao;
    private final Executor executor;

    public ChatDataSource(ChatEndpoints chatEndpoints, AppDatabase database, Executor executor) {
        this.chatEndpoints = chatEndpoints;
        this.database = database;
        this.chatModelDao = database.getChatModelDao();
        this.messageModelDao = database.getMessageModelDao();
        this.executor = executor;
    }

    public ListenableFuture<Result<Void, ErrorDto>> updateReadStatus(Long chatId) {
        ListenableFuture<Void> networkFuture = chatEndpoints.updateReadStatus(chatId);

        ListenableFuture<Result<Void, ErrorDto>> networkResultFuture =
                Futures.transform(networkFuture, Result.Success::new, executor);

        ListenableFuture<Result<Void, ErrorDto>> partialResultFuture =
                Futures.catching(networkResultFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        ListenableFuture<Result<Void, ErrorDto>> catchingResultFuture = Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);

        return Futures.transform(catchingResultFuture,
                result -> {
                    if (result.isSuccess()) {
                        Result.Success<Void, ErrorDto> successResult = (Result.Success<Void, ErrorDto>) result;
                        database.runInTransaction(() -> {
                            // Update chat unread messages count:
                            ChatModel chatModel = chatModelDao.getOneById(chatId);
                            chatModel.setUnreadMessages(0L);
                            chatModelDao.insert(chatModel);
                            // Update messages read flag:
                            List<MessageModel> messages = messageModelDao.getAllByChatIdAndMessageSenderType(chatId, MessageSenderType.TARGET);
                            for (MessageModel message : messages) {
                                message.setRead(true);
                            }
                            messageModelDao.insertAll(messages);
                        });
                    }
                    return result;
                },
                executor);
    }

}