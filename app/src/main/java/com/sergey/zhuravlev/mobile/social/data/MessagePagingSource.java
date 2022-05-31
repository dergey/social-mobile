package com.sergey.zhuravlev.mobile.social.data;

import androidx.annotation.NonNull;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.client.api.MessageEndpoints;
import com.sergey.zhuravlev.mobile.social.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.dto.message.ImageMessageDto;
import com.sergey.zhuravlev.mobile.social.dto.message.MessageDto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import retrofit2.HttpException;

@ExperimentalPagingApi
public class MessagePagingSource extends ListenableFuturePagingSource<Integer, MessageDto> {

    @NonNull
    private final MessageEndpoints endpoints;

    @NonNull
    private Long chatId;

    @NonNull
    private final Executor executor;

    public MessagePagingSource(@NonNull MessageEndpoints endpoints, @NonNull Long chatId, @NonNull Executor executor) {
        this.endpoints = endpoints;
        this.chatId = chatId;
        this.executor = executor;
    }

    @NotNull
    @Override
    public ListenableFuture<LoadResult<Integer, MessageDto>> loadFuture(@NotNull LoadParams<Integer> params) {
        Integer nextPageNumber = params.getKey();
        if (nextPageNumber == null) {
            nextPageNumber = MessageRepository.DEFAULT_PAGE_INDEX;
        }

        ListenableFuture<LoadResult<Integer, MessageDto>> pageFuture =
                Futures.transform(endpoints.getChatMessages(chatId, nextPageNumber, MessageRepository.DEFAULT_PAGE_SIZE),
                        this::toLoadResult, executor);

        ListenableFuture<LoadResult<Integer, MessageDto>> partialLoadResultFuture =
                Futures.catching(pageFuture, HttpException.class,
                        LoadResult.Error::new, executor);

        return Futures.catching(partialLoadResultFuture,
                IOException.class, LoadResult.Error::new, executor);
    }

    private LoadResult<Integer, MessageDto> toLoadResult(@NonNull PageDto<MessageDto> response) {
        Integer prevKey = response.getNumber() - 1 <= 0 ? null : response.getNumber() - 1;
        Integer nextKey = response.getNumber() + 1 >= response.getTotalPages() ? null : response.getNumber() + 1;

        response.getContent().stream()
                .filter(m -> m instanceof ImageMessageDto)
                .forEach(m -> ((ImageMessageDto) m).setChatId(chatId));

        return new LoadResult.Page<>(response.getContent(),
                prevKey,
                nextKey,
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NotNull PagingState<Integer, MessageDto> state) {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        Integer anchorPosition = state.getAnchorPosition();
        if (anchorPosition == null) {
            return null;
        }

        LoadResult.Page<Integer, MessageDto> anchorPage = state.closestPageToPosition(anchorPosition);
        if (anchorPage == null) {
            return null;
        }

        Integer prevKey = anchorPage.getPrevKey();
        if (prevKey != null) {
            return prevKey + 1;
        }

        Integer nextKey = anchorPage.getNextKey();
        if (nextKey != null) {
            return nextKey - 1;
        }

        return null;
    }
}
