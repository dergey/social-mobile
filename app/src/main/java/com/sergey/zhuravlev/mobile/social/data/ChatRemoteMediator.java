package com.sergey.zhuravlev.mobile.social.data;

import androidx.paging.ExperimentalPagingApi;
import androidx.paging.ListenableFutureRemoteMediator;
import androidx.paging.LoadType;
import androidx.paging.PagingState;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.ChatEndpoints;
import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.chat.ChatPreviewDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.client.mapper.MessageModelMapper;
import com.sergey.zhuravlev.mobile.social.database.AppDatabase;
import com.sergey.zhuravlev.mobile.social.database.dao.ChatPreviewModelDao;
import com.sergey.zhuravlev.mobile.social.client.mapper.ChatModelMapper;
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
public class ChatRemoteMediator extends ListenableFutureRemoteMediator<Integer, ChatPreviewModel> {

    private final ChatEndpoints endpoints;
    private final AppDatabase database;
    private final ChatPreviewModelDao chatPreviewModelDao;
    private final Executor executor;
    private final Integer pageSize;

    public ChatRemoteMediator(ChatEndpoints chatEndpoints,
                              AppDatabase database,
                              Integer pageSize,
                              Executor executor) {
        this.endpoints = chatEndpoints;
        this.database = database;
        this.chatPreviewModelDao = database.getChatPreviewModelDao();
        this.pageSize = pageSize;
        this.executor = executor;
    }

    @NotNull
    @Override
    public ListenableFuture<MediatorResult> loadFuture(@NotNull LoadType loadType,
                                                       @NotNull PagingState<Integer, ChatPreviewModel> state) {
        // Метод загрузки сети принимает необязательный параметр after=<chat.id>. Для
        // каждой странице после первой, передавать последний идентификатор пользователя, чтобы продолжить с
        // на том месте, где она остановилась. Для REFRESH передайте null, чтобы загрузить первую страницу.
        Integer page = null;
        switch (loadType) {
            case REFRESH:
                break;
            case PREPEND:
                // В этом примере вам никогда не потребуется добавлять префикс, поскольку REFRESH будет всегда
                // загружать первую страницу в списке. Немедленно вернитесь, сообщив о завершении
                // пагинации.
                return Futures.immediateFuture(new MediatorResult.Success(true));
            case APPEND:
                ChatPreviewModel lastItem = state.lastItemOrNull();

                // You must explicitly check if the last item is null when appending,
                // since passing null to networkService is only valid for initial load.
                // If lastItem is null it means no items were loaded after the initial
                // REFRESH and there are no more items to load.
                if (lastItem == null) {
                    return Futures.immediateFuture(new MediatorResult.Success(true));
                }

                page = lastItem.getPageable().getPage();
                break;
        }

        ListenableFuture<MediatorResult> networkResult = Futures.transform(
                endpoints.getCurrentUserChats(page, pageSize),
                response -> {
                    database.runInTransaction(() -> {
                        if (loadType == LoadType.REFRESH) {
                            chatPreviewModelDao.clearAllChatModel();
                        }

                        // Insert new users into database, which invalidates the current
                        // PagingData, allowing Paging to present the updates in the DB.
                        chatPreviewModelDao.insertAll(toPageableModels(response));
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
    public static List<ChatPreviewModel> toPageableModels(PageDto<ChatPreviewDto> page) {
        List<ChatPreviewModel> models = new ArrayList<>();
        for (ChatPreviewDto dto : page.getContent()) {
            ChatPreviewModel model = ChatModelMapper.toModel(dto);
            model.setPageable(new Pageable(page.getNumber()));
            models.add(model);
        }

        return models;
    }

}
