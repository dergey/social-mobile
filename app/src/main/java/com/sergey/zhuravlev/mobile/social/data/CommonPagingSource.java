package com.sergey.zhuravlev.mobile.social.data;

import androidx.annotation.NonNull;
import androidx.paging.ExperimentalPagingApi;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingState;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sergey.zhuravlev.mobile.social.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.dto.profile.ProfileDto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Function;

import retrofit2.HttpException;

@ExperimentalPagingApi
@SuppressWarnings("UnstableApiUsage")
public class CommonPagingSource<V> extends ListenableFuturePagingSource<Integer, V> {

    public static class PageableLoad {

        private Integer pageSize;
        private Integer nextPageNumber;
        private Map<String, ?> staticPayload;

        PageableLoad(Integer pageSize, Integer nextPageNumber, Map<String, ?> staticPayload) {
            this.pageSize = pageSize;
            this.nextPageNumber = nextPageNumber;
            this.staticPayload = staticPayload;
        }

        public Integer getPageSize() {
            return pageSize;
        }

        public Integer getNextPageNumber() {
            return nextPageNumber;
        }

        public String getPayloadString(String name) {
            if (!staticPayload.containsKey(name) && staticPayload.get(name) instanceof String) {
                throw new IllegalArgumentException(name);
            }
            return (String) staticPayload.get(name);
        }

    }

    @NonNull
    private Function<PageableLoad, ListenableFuture<PageDto<V>>> endpointCall;

    @NonNull
    private final Map<String, ?> staticPayload;

    @NonNull
    private final Integer pageSize;

    @NonNull
    private final Executor executor;

    public CommonPagingSource(@NonNull Function<PageableLoad, ListenableFuture<PageDto<V>>> endpointCall,
                              @NonNull Map<String, ?> staticPayload, @NonNull Integer pageSize,
                              @NonNull  Executor executor) {
        this.endpointCall = endpointCall;
        this.staticPayload = staticPayload;
        this.executor = executor;
        this.pageSize = pageSize;
    }

    @NotNull
    @Override
    public ListenableFuture<LoadResult<Integer, V>> loadFuture(@NotNull LoadParams<Integer> params) {
        Integer nextPageNumber = params.getKey();
        if (nextPageNumber == null) {
            nextPageNumber = MessageRepository.DEFAULT_PAGE_INDEX;
        }

        ListenableFuture<LoadResult<Integer, V>> pageFuture =
                Futures.transform(endpointCall.apply(new PageableLoad(pageSize, nextPageNumber, staticPayload)),
                        this::toLoadResult, executor);

        ListenableFuture<LoadResult<Integer, V>> partialLoadResultFuture =
                Futures.catching(pageFuture, HttpException.class,
                        LoadResult.Error::new, executor);

        return Futures.catching(partialLoadResultFuture,
                IOException.class, LoadResult.Error::new, executor);
    }

    private LoadResult<Integer, V> toLoadResult(@NonNull PageDto<V> response) {
        Integer prevKey = response.getNumber() - 1 <= 0 ? null : response.getNumber() - 1;
        Integer nextKey = response.getNumber() + 1 >= response.getTotalPages() ? null : response.getNumber() + 1;

        return new LoadResult.Page<>(response.getContent(),
                prevKey,
                nextKey,
                LoadResult.Page.COUNT_UNDEFINED,
                LoadResult.Page.COUNT_UNDEFINED);
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NotNull PagingState<Integer, V> state) {
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

        LoadResult.Page<Integer, V> anchorPage = state.closestPageToPosition(anchorPosition);
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