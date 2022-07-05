package com.sergey.zhuravlev.mobile.social.data;

import android.util.Log;

import androidx.paging.ExperimentalPagingApi;
import androidx.paging.ListenableFutureRemoteMediator;
import androidx.paging.LoadType;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.MessageEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.client.mapper.MessageModelMapper;
import com.sergey.zhuravlev.mobile.social.client.utils.Direction;
import com.sergey.zhuravlev.mobile.social.client.utils.Sort;
import com.sergey.zhuravlev.mobile.social.database.AppDatabase;
import com.sergey.zhuravlev.mobile.social.database.dao.MessageModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.database.paggeble.Pageable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import retrofit2.HttpException;


@ExperimentalPagingApi
@SuppressWarnings("UnstableApiUsage")
public class MessageRemoteMediator extends ListenableFutureRemoteMediator<Integer, MessageModel> {

    private final MessageEndpoints endpoints;
    private final AppDatabase database;
    private final MessageModelDao messageModelDao;
    private final Executor executor;

    private final Integer pageSize;
    private final Integer pageStart;
    private final Long chatId;

    public MessageRemoteMediator(MessageEndpoints messageEndpoints,
                                 AppDatabase database,
                                 Long chatId,
                                 Integer pageStart,
                                 Integer pageSize,
                                 Executor executor) {
        this.endpoints = messageEndpoints;
        this.database = database;
        this.messageModelDao = database.getMessageModelDao();
        this.chatId = chatId;
        this.pageStart = pageStart;
        this.pageSize = pageSize;
        this.executor = executor;
    }

    @NotNull
    @Override
    public ListenableFuture<MediatorResult> loadFuture(@NotNull LoadType loadType,
                                                       @NotNull PagingState<Integer, MessageModel> state) {
        switch (loadType) {
            case REFRESH:
                Log.i("MessageRemoteMediator/loadFuture", "Calling refresh");
                return refreshFuture(state);
            case PREPEND:
                Log.i("MessageRemoteMediator/loadFuture", "Calling prepend");
                return Futures.immediateFuture(new MediatorResult.Success(true));
            case APPEND:
                Log.i("MessageRemoteMediator/loadFuture", "Calling append");
                return appendFuture(state);
            default:
                throw new IllegalArgumentException("LoadType: " + loadType);
        }
    }

    private ListenableFuture<MediatorResult> refreshFuture(@NotNull PagingState<Integer, MessageModel> state) {
        Integer page = getClosestRemoteKey(state);
        Log.i("MessageRemoteMediator/refreshFuture", String.format("Refreshing %s page", page));
        ListenableFuture<MediatorResult> mediatorResult = Futures.transform(
                endpoints.getChatMessages(chatId, page, pageSize, new Sort("createAt", Direction.DESC)),
                response -> {
                    database.runInTransaction(() -> {
                        // Reset all saved page instance after refreshing page:
                        messageModelDao.resetPageableAfterPageMessageModel(chatId, page);
                        // Updating page cache with new network data:
                        List<Long> newNetworkIds = response.getContent().stream()
                                .map(MessageDto::getId)
                                .collect(Collectors.toList());
                        Map<Long, MessageModel> currentMessageModels = messageModelDao.getAllByNetworkIds(newNetworkIds).stream()
                                .collect(Collectors.toMap(MessageModel::getNetworkId, Function.identity()));
                        List<MessageModel> updatedPageableModels = updatePageableModels(currentMessageModels, response, chatId);
                        messageModelDao.insertAll(updatedPageableModels);
                    });
                    return new MediatorResult.Success(!response.getHasNext());
                }, executor);

        ListenableFuture<MediatorResult> ioCatchingNetworkResult =
                Futures.catching(
                        mediatorResult,
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

    private ListenableFuture<MediatorResult> appendFuture(@NotNull PagingState<Integer, MessageModel> state) {
        ListenableFuture<Integer> databaseLastPageResult = Futures.submit(() -> {
            Integer lastPage = messageModelDao.getLastPage(chatId);
            Log.i("MessageRemoteMediator/appendFuture", String.format("Gets %s page", lastPage + 1));
            return lastPage;
        }, executor);
        ListenableFuture<PageDto<MessageDto>> networkResult = Futures.transformAsync(databaseLastPageResult,
                lastPage -> endpoints.getChatMessages(chatId, lastPage + 1, pageSize, new Sort("createAt", Direction.DESC)),
                executor);
        ListenableFuture<MediatorResult> mediatorResult = Futures.transform(
                networkResult,
                response -> {
                    database.runInTransaction(() -> {
                        List<Long> newNetworkIds = response.getContent().stream()
                                .map(MessageDto::getId)
                                .collect(Collectors.toList());
                        Map<Long, MessageModel> currentMessageModels = messageModelDao.getAllByNetworkIds(newNetworkIds).stream()
                                .collect(Collectors.toMap(MessageModel::getNetworkId, Function.identity()));
                        List<MessageModel> updatedPageableModels = updatePageableModels(currentMessageModels, response, chatId);
                        messageModelDao.insertAll(updatedPageableModels);
                    });
                    return new MediatorResult.Success(!response.getHasNext());
                }, executor);


        ListenableFuture<MediatorResult> ioCatchingNetworkResult =
                Futures.catching(
                        mediatorResult,
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

    /**
     * get the closest remote key inserted which had the data
     */
    private Integer getClosestRemoteKey(PagingState<Integer, MessageModel> state) {
        return Optional.ofNullable(state.getAnchorPosition())
                .flatMap(position -> Optional.ofNullable(state.closestItemToPosition(position)))
                .map(model -> model.getPageable().getPage())
                .orElse(pageStart);
    }

    @SuppressWarnings("ConstantConditions")
    public static List<MessageModel> updatePageableModels(Map<Long, MessageModel> oldModels, PageDto<MessageDto> page, Long chatId) {
        List<MessageModel> models = new ArrayList<>();
        for (MessageDto dto : page.getContent()) {
            MessageModel model;
            if (oldModels.containsKey(dto.getId())) {
                model = MessageModelMapper.updateModel(oldModels.get(dto.getId()), dto);
            } else {
                model = MessageModelMapper.toModel(dto);
            }
            model.setChatId(chatId);
            model.setPageable(new Pageable(page.getNumber()));
            models.add(model);
        }
        return models;
    }
}
