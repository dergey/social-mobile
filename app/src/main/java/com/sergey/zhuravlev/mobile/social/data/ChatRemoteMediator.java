package com.sergey.zhuravlev.mobile.social.data;

import android.util.Log;

import androidx.paging.ExperimentalPagingApi;
import androidx.paging.ListenableFutureRemoteMediator;
import androidx.paging.LoadType;
import androidx.paging.PagingState;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.ChatEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.chat.ChatPreviewDto;
import com.sergey.zhuravlev.mobile.social.client.mapper.ChatModelMapper;
import com.sergey.zhuravlev.mobile.social.client.mapper.MessageModelMapper;
import com.sergey.zhuravlev.mobile.social.client.utils.Direction;
import com.sergey.zhuravlev.mobile.social.client.utils.Sort;
import com.sergey.zhuravlev.mobile.social.database.AppDatabase;
import com.sergey.zhuravlev.mobile.social.database.dao.ChatModelDao;
import com.sergey.zhuravlev.mobile.social.database.dao.MessageModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.ChatAndLastMessageModel;
import com.sergey.zhuravlev.mobile.social.database.model.ChatModel;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.database.paggeble.Pageable;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

import retrofit2.HttpException;


@ExperimentalPagingApi
@SuppressWarnings("UnstableApiUsage")
public class ChatRemoteMediator extends ListenableFutureRemoteMediator<Integer, ChatAndLastMessageModel> {

    private final ChatEndpoints endpoints;
    private final AppDatabase database;
    private final ChatModelDao chatModelDao;
    private final MessageModelDao messageModelDao;
    private final Executor executor;
    private final Integer pageSize;

    public ChatRemoteMediator(ChatEndpoints chatEndpoints,
                              AppDatabase database,
                              Integer pageSize,
                              Executor executor) {
        this.endpoints = chatEndpoints;
        this.database = database;
        this.chatModelDao = database.getChatModelDao();
        this.messageModelDao = database.getMessageModelDao();
        this.pageSize = pageSize;
        this.executor = executor;
    }

    @NotNull
    @Override
    public ListenableFuture<MediatorResult> loadFuture(@NotNull LoadType loadType,
                                                       @NotNull PagingState<Integer, ChatAndLastMessageModel> state) {
        switch (loadType) {
            case REFRESH:
                Log.i("ChatRemoteMediator/loadFuture", "Calling refresh");
                return refreshFuture();
            case PREPEND:
                // Skip the prepend step, because the refresh is called on start up.
                return Futures.immediateFuture(new MediatorResult.Success(true));
            case APPEND:
                Log.i("ChatRemoteMediator/loadFuture", "Calling append");
                return appendFuture();
            default:
                throw new IllegalArgumentException("LoadType: " + loadType);
        }
    }

    private ListenableFuture<MediatorResult> refreshFuture() {
        // Updating can come fully, because chats are constantly moving due to sorting
        Integer page = 0;
        Log.i("ChatRemoteMediator/refreshFuture", String.format("Refreshing %s page", page));
        ListenableFuture<MediatorResult> mediatorResult = Futures.transform(
                endpoints.getCurrentUserChats(page, pageSize, new Sort("updateAt", Direction.DESC)),
                response -> {
                    database.runInTransaction(() -> {
                        // Reset all saved page instance after refreshing page:
                        chatModelDao.resetPageableAfterPageMessageModel(page);
                        // Update last messages in database:
                        List<MessageModel> updatedMessageModels = updateMessageModels(response);
                        // Create map where Key - Message.chatId, Value - Message
                        Map<Long, MessageModel> updatedChatLastMessageMap = updatedMessageModels.stream()
                                .collect(Collectors.toMap(MessageModel::getChatId, Function.identity()));
                        // Updating chat page in database with new network data:
                        updateChatModels(response, updatedChatLastMessageMap);
                    });
                    return new MediatorResult.Success(!response.getHasNext());
                }, executor);

        return catchFutureNetworkException(mediatorResult);
    }

    private ListenableFuture<MediatorResult> appendFuture() {
        ListenableFuture<Integer> databaseLastPageResult = Futures.submit(() -> {
            Integer lastPage = chatModelDao.getLastPage();
            Log.i("ChatRemoteMediator/appendFuture", String.format("Gets %s page", lastPage + 1));
            return lastPage;
        }, executor);
        ListenableFuture<PageDto<ChatPreviewDto>> networkResult = Futures.transformAsync(databaseLastPageResult,
                lastPage -> endpoints.getCurrentUserChats(lastPage + 1, pageSize, new Sort("updateAt", Direction.DESC)),
                executor);
        ListenableFuture<MediatorResult> mediatorResult = Futures.transform(
                networkResult,
                response -> {
                    database.runInTransaction(() -> {
                        // Update last messages in database:
                        List<MessageModel> updatedMessageModels = updateMessageModels(response);
                        // Create map where Key - Message.chatId, Value - Message
                        Map<Long, MessageModel> updatedChatLastMessageMap = updatedMessageModels.stream()
                                .collect(Collectors.toMap(MessageModel::getChatId, Function.identity()));
                        // Updating chat page in database with new network data:
                        updateChatModels(response, updatedChatLastMessageMap);
                    });
                    return new MediatorResult.Success(!response.getHasNext());
                }, executor);

        return catchFutureNetworkException(mediatorResult);
    }

    // Note: This method is transactional
    @SuppressWarnings("ConstantConditions")
    private void updateChatModels(PageDto<ChatPreviewDto> pageDto, Map<Long, MessageModel> updatedChatLastMessageMap) {
        List<Long> newChatIds = pageDto.getContent().stream()
                .map(ChatPreviewDto::getId)
                .collect(Collectors.toList());
        Map<Long, ChatModel> currentChatModels = chatModelDao.getAllByIds(newChatIds).stream()
                .collect(Collectors.toMap(ChatModel::getId, Function.identity()));
        List<ChatModel> updatedChatModels = new ArrayList<>();
        for (ChatPreviewDto chatPreviewDto : pageDto.getContent()) {
            ChatModel model;
            if (currentChatModels.containsKey(chatPreviewDto.getId())) {
                model = ChatModelMapper.updateModel(currentChatModels.get(chatPreviewDto.getId()),
                        chatPreviewDto,
                        updatedChatLastMessageMap.get(chatPreviewDto.getId()));
            } else {
                model = ChatModelMapper.toModel(chatPreviewDto, updatedChatLastMessageMap.get(chatPreviewDto.getId()));
            }
            model.setPageable(new Pageable(pageDto.getNumber()));
            updatedChatModels.add(model);
        }
        chatModelDao.insertAll(updatedChatModels);
    }

    // Note: This method is transactional
    @SuppressWarnings("ConstantConditions")
    private List<MessageModel> updateMessageModels(PageDto<ChatPreviewDto> pageDto) {
        List<Long> newLastMessageNetworkIds = pageDto.getContent().stream()
                .map(cp -> cp.getLastMessage().getId())
                .collect(Collectors.toList());
        Map<Long, MessageModel> currentMessageModels = messageModelDao.getAllByNetworkIds(newLastMessageNetworkIds).stream()
                .collect(Collectors.toMap(MessageModel::getNetworkId, Function.identity()));
        List<MessageModel> updatedMessageModels = new ArrayList<>();
        for (ChatPreviewDto chatPreviewDto : pageDto.getContent()) {
            MessageModel lastMessageModel;
            if (currentMessageModels.containsKey(chatPreviewDto.getLastMessage().getId())) {
                lastMessageModel = MessageModelMapper.updateModel(currentMessageModels.get(chatPreviewDto.getLastMessage().getId()), chatPreviewDto.getLastMessage());
            } else {
                lastMessageModel = MessageModelMapper.toModel(chatPreviewDto.getLastMessage());
            }
            lastMessageModel.setChatId(chatPreviewDto.getId());
            updatedMessageModels.add(lastMessageModel);
        }
        long[] ids = messageModelDao.insertAll(updatedMessageModels);
        for (int i = 0; i < updatedMessageModels.size(); i++) {
            updatedMessageModels.get(i).setId(ids[i]);
        }
        return updatedMessageModels;
    }

    private ListenableFuture<MediatorResult> catchFutureNetworkException(ListenableFuture<MediatorResult> mediatorResult) {
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

}
