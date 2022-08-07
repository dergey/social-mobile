package com.sergey.zhuravlev.mobile.social.data.datasource;

import android.content.Context;
import android.util.Log;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.ChatEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.ErrorDto;
import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.chat.ChatDto;
import com.sergey.zhuravlev.mobile.social.client.dto.chat.ChatPreviewDto;
import com.sergey.zhuravlev.mobile.social.client.dto.chat.CreateChatDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.client.mapper.ChatModelMapper;
import com.sergey.zhuravlev.mobile.social.client.mapper.MessageModelMapper;
import com.sergey.zhuravlev.mobile.social.data.Result;
import com.sergey.zhuravlev.mobile.social.database.AppDatabase;
import com.sergey.zhuravlev.mobile.social.database.dao.ChatModelDao;
import com.sergey.zhuravlev.mobile.social.database.dao.MessageModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.ChatAndLastMessageModel;
import com.sergey.zhuravlev.mobile.social.database.model.ChatModel;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileAndDetailModel;
import com.sergey.zhuravlev.mobile.social.database.paggeble.Pageable;
import com.sergey.zhuravlev.mobile.social.enums.MessageSenderType;
import com.sergey.zhuravlev.mobile.social.exception.CacheNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public ListenableFuture<Result<ChatModel, Void>> getCacheChat(String username) {
        ListenableFuture<ChatModel> databaseFuture = Futures.submit(
                () -> Optional.ofNullable(chatModelDao.getOneByUsername(username))
                        .orElseThrow(() -> new CacheNotFoundException(ChatModel.class)),
                executor);

        ListenableFuture<Result<ChatModel, Void>> databaseResultFuture =
                Futures.transform(databaseFuture, Result.Success::new, executor);

        return Futures.catching(databaseResultFuture, CacheNotFoundException.class,
                Result.Error::fromCacheException, executor);
    }

    public ListenableFuture<Result<ChatModel, ErrorDto>> getOrCreateNetworkChat(String username) {
        CreateChatDto request = new CreateChatDto(username);
        ListenableFuture<ChatDto> networkFuture = chatEndpoints.getOrCreateChat(request);

        ListenableFuture<Result<ChatDto, ErrorDto>> networkResultFuture =
                Futures.transform(networkFuture, Result.Success::new, executor);

        ListenableFuture<Result<ChatDto, ErrorDto>> partialResultFuture =
                Futures.catching(networkResultFuture, HttpException.class,
                        Result.Error::fromHttpException, executor);

        ListenableFuture<Result<ChatDto, ErrorDto>> catchingResultFuture = Futures.catching(partialResultFuture,
                IOException.class, Result.Error::fromIOException, executor);

        return Futures.transform(catchingResultFuture,
                result -> {
                    if (result.isSuccess()) {
                        Result.Success<ChatDto, ErrorDto> successResult = (Result.Success<ChatDto, ErrorDto>) result;
                        ChatDto data = successResult.getData();
                        database.runInTransaction(() -> {
                            // Update last messages in database:
                            List<MessageModel> updatedMessageModels = updateMessageModels(data);
                            // Find last message for getted chat:
                            MessageModel lastMessage = Iterables.getLast(updatedMessageModels);
                            // Update chat in database:
                            updateChatModel(data, lastMessage);
                        });
                    }
                    return result.map(ChatModelMapper::toModel);
                },
                executor);
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

    // Note: This method is transactional
    @SuppressWarnings("ConstantConditions")
    private void updateChatModel(ChatDto chatDto, MessageModel updatedChatLastMessage) {
        Long chatId = chatDto.getId();
        ChatAndLastMessageModel currentChatModel = chatModelDao.getOneChatAndLastMessageModelById(chatId);

        ChatModel model;
        if (currentChatModel != null) {
            MessageModel newChatLastMessage = updatedChatLastMessage;
            MessageModel currentLastMessageModel = currentChatModel.getLastMessage();
            if (!Objects.equals(currentLastMessageModel, updatedChatLastMessage) && currentLastMessageModel.getCreateAt()
                    .isAfter(updatedChatLastMessage.getCreateAt())) {
                newChatLastMessage = currentLastMessageModel;
            }
            model = ChatModelMapper.updateModel(currentChatModel.getChat(),
                    chatDto,
                    newChatLastMessage);
        } else {
            model = ChatModelMapper.toModel(chatDto, updatedChatLastMessage);
        }

        chatModelDao.insert(model);
    }

    // Note: This method is transactional
    @SuppressWarnings("ConstantConditions")
    private List<MessageModel> updateMessageModels(ChatDto chatDto) {
        Long chatId = chatDto.getId();
        Map<Long, MessageModel> currentMessageModels = messageModelDao.getAllByNetworkId(chatId).stream()
                .collect(Collectors.toMap(MessageModel::getNetworkId, Function.identity()));
        List<MessageModel> updatedMessageModels = new ArrayList<>();
        for (MessageDto networkMessage : chatDto.getLastMessages()) {
            MessageModel messageModel;
            if (currentMessageModels.containsKey(networkMessage.getId())) {
                messageModel = MessageModelMapper.updateModel(currentMessageModels.get(networkMessage.getId()), networkMessage);
            } else {
                messageModel = MessageModelMapper.toModel(networkMessage);
            }
            messageModel.setChatId(chatId);
            updatedMessageModels.add(messageModel);
        }

        long[] ids = messageModelDao.insertAll(updatedMessageModels);
        for (int i = 0; i < updatedMessageModels.size(); i++) {
            updatedMessageModels.get(i).setId(ids[i]);
        }
        return updatedMessageModels;
    }

}