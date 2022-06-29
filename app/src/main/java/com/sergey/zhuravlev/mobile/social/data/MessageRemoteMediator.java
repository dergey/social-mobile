package com.sergey.zhuravlev.mobile.social.data;

import static androidx.paging.LoadType.*;

import android.util.Log;

import androidx.paging.ExperimentalPagingApi;
import androidx.paging.ListenableFutureRemoteMediator;
import androidx.paging.LoadType;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
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
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;

import okhttp3.MultipartBody;
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
        switch (loadType) {
            case REFRESH:
                Log.i("message_mediator", "REFRESH CALL");
                return refreshFuture(state);
            case PREPEND:
                Log.i("message_mediator", "PREPEND CALL");
                return Futures.immediateFuture(new MediatorResult.Success(true));
            case APPEND:
                Log.i("message_mediator", "APPEND CALL");
                return appendFuture(state);
            default:
                throw new IllegalArgumentException("LoadType: " + loadType);
        }
    }

    private ListenableFuture<MediatorResult> refreshFuture(@NotNull PagingState<Integer, MessageModel> state) {
        ListenableFuture<MediatorResult> networkResult = Futures.transform(
                endpoints.getChatMessages(chatId, getClosestRemoteKey(state), pageSize),
                response -> {
                    Log.i("message_mediator", "--- get response: " + response);
                    database.runInTransaction(() -> {
                        messageModelDao.insertAll(toPageableModels(response, chatId));
                    });
                    return new MediatorResult.Success(!response.getHasNext());
                }, executor);

        ListenableFuture<MediatorResult> ioCatchingNetworkResult =
                Futures.catching(
                        networkResult,
                        IOException.class,
                        e -> {
                            Log.i("message_mediator", "Error when api called: ", e);
                            return new MediatorResult.Error(e);
                        },
                        executor
                );

        return Futures.catching(
                ioCatchingNetworkResult,
                HttpException.class,
                e -> {
                    Log.i("message_mediator", "Error when api called: ", e);
                    return new MediatorResult.Error(e);
                },
                executor
        );
    }

    private ListenableFuture<MediatorResult> appendFuture(@NotNull PagingState<Integer, MessageModel> state) {
        PagingSource.LoadResult.Page<Integer, MessageModel> lastPage = Iterables.getLast(state.getPages(), null);

        int lastPageElements;
        int lastPageNumber;

        if (lastPage == null) {
            lastPageElements = 0;
            lastPageNumber = 0;
        } else {
            MessageModel lastItem = Iterables.getLast(lastPage.getData(), null);
            lastPageElements = lastPage.getData().size();
            if (lastItem == null) {
                lastPageNumber = 0;
            } else {
                lastPageNumber = lastItem.getPageable().getPage();
            }
        }

        ListenableFuture<MediatorResult> networkResult;
        if (lastPageElements % pageSize == 0) {
            // Get next page if current full
            networkResult = Futures.transform(
                    endpoints.getChatMessages(chatId, lastPageNumber + 1, pageSize),
                    response -> {
                        messageModelDao.insertAll(toPageableModels(response, chatId));
                        return new MediatorResult.Success(!response.getHasNext());
                    }, executor);
        } else {
            // Update current page
            Log.i("message_mediator", "--- update page: " + lastPageNumber);
            networkResult = Futures.transform(
                    endpoints.getChatMessages(chatId, lastPageNumber, pageSize),
                    response -> {
                        database.runInTransaction(() -> {
                            messageModelDao.insertAll(toPageableModels(response, chatId));
                        });

                        return new MediatorResult.Success(!response.getHasNext());
                    }, executor);
        }

        ListenableFuture<MediatorResult> ioCatchingNetworkResult =
                Futures.catching(
                        networkResult,
                        IOException.class,
                        e -> {
                            Log.i("message_mediator", "Error when api called: ", e);
                            return new MediatorResult.Error(e);
                        },
                        executor
                );

        return Futures.catching(
                ioCatchingNetworkResult,
                HttpException.class,
                e -> {
                    Log.i("message_mediator", "Error when api called: ", e);
                    return new MediatorResult.Error(e);
                },
                executor
        );
    }

    /**
     * get the first page number inserted which had the data
     */
    private Integer getFirstPageNumber(PagingState<Integer, MessageModel> state) {
        return Optional.ofNullable(Iterables.getFirst(state.getPages(), null))
                .filter(p -> !Iterables.isEmpty(p.getData()))
                .map(PagingSource.LoadResult.Page::getData)
                .flatMap(data -> Optional.ofNullable(Iterables.getFirst(data, null)))
                .map(model -> model.getPageable().getPage())
                .orElse(null);
    }

    /**
     * get the last page number inserted which had the data
     */
    private Integer getLastPageNumber(PagingState<Integer, MessageModel> state) {
        return Optional.ofNullable(Iterables.getLast(state.getPages(), null))
                .filter(p -> !Iterables.isEmpty(p.getData()))
                .map(PagingSource.LoadResult.Page::getData)
                .flatMap(data -> Optional.ofNullable(Iterables.getLast(data, null)))
                .map(model -> model.getPageable().getPage())
                .orElse(null);
    }

    /**
     * get the closest remote key inserted which had the data
     */
    private Integer getClosestRemoteKey(PagingState<Integer, MessageModel> state) {
        return Optional.ofNullable(state.getAnchorPosition())
                .flatMap(position -> Optional.ofNullable(state.closestItemToPosition(position)))
                .map(model -> model.getPageable().getPage())
                .orElse(null);
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
