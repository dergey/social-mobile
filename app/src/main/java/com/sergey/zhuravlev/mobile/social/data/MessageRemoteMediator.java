package com.sergey.zhuravlev.mobile.social.data;

import androidx.paging.ExperimentalPagingApi;
import androidx.paging.ListenableFutureRemoteMediator;
import androidx.paging.LoadType;
import androidx.paging.PagingState;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.ChatEndpoints;
import com.sergey.zhuravlev.mobile.social.client.api.MessageEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.client.mapper.ChatModelMapper;
import com.sergey.zhuravlev.mobile.social.client.mapper.MessageModelMapper;
import com.sergey.zhuravlev.mobile.social.database.AppDatabase;
import com.sergey.zhuravlev.mobile.social.database.dao.ChatPreviewModelDao;
import com.sergey.zhuravlev.mobile.social.database.dao.MessageModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.ChatPreviewModel;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.database.paggeble.Pageable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.HttpException;


@ExperimentalPagingApi
@SuppressWarnings("UnstableApiUsage")
public class MessageRemoteMediator extends ListenableFutureRemoteMediator<Integer, MessageModel> {

    private final MessageEndpoints endpoints;
    private final AppDatabase database;
    private final MessageModelDao messageModelDao;
    private final Executor executor;

    private final Integer pageSize;
    private final Long chatId;

    public MessageRemoteMediator(MessageEndpoints messageEndpoints,
                                 AppDatabase database,
                                 Long chatId,
                                 Integer pageSize,
                                 Executor executor) {
        this.endpoints = messageEndpoints;
        this.database = database;
        this.messageModelDao = database.getMessageModelDao();
        this.chatId = chatId;
        this.pageSize = pageSize;
        this.executor = executor;
    }

    @NotNull
    @Override
    public ListenableFuture<MediatorResult> loadFuture(@NotNull LoadType loadType,
                                                       @NotNull PagingState<Integer, MessageModel> state) {
        Integer page = null;
        switch (loadType) {
            case REFRESH:
                break;
            case PREPEND:
                return Futures.immediateFuture(new MediatorResult.Success(true));
            case APPEND:
                MessageModel lastItem = state.lastItemOrNull();
                if (lastItem == null) {
                    return Futures.immediateFuture(new MediatorResult.Success(true));
                }

                page = lastItem.getPageable().getPage();
                break;
        }

        ListenableFuture<MediatorResult> networkResult = Futures.transform(
                endpoints.getChatMessages(chatId, page, pageSize),
                response -> {
                    database.runInTransaction(() -> {
                        if (loadType == LoadType.REFRESH) {
                            messageModelDao.clearAllMessageModel();
                        }

                        // Insert new users into database, which invalidates the current
                        // PagingData, allowing Paging to present the updates in the DB.
                        messageModelDao.insertAll(toPageableModels(response, chatId));
                    });

                    return new MediatorResult.Success(response.getHasNext());
                }, executor);

        ListenableFuture<MediatorResult> ioCatchingNetworkResult =
                Futures.catching(
                        networkResult,
                        IOException.class,
                        MediatorResult.Error::new,
                        executor
                );

        return Futures.catching(
                ioCatchingNetworkResult,
                HttpException.class,
                MediatorResult.Error::new,
                executor
        );
    }

    @SuppressWarnings("ConstantConditions")
    public static List<MessageModel> toPageableModels(PageDto<MessageDto> page, Long chatId) {
        List<MessageModel> models = new ArrayList<>();
        for (MessageDto dto : page.getContent()) {
            MessageModel model = MessageModelMapper.toModel(dto);
            model.setChatId(chatId);
            model.setPageable(new Pageable(page.getNumber()));
            models.add(model);
        }
        return models;
    }

}
