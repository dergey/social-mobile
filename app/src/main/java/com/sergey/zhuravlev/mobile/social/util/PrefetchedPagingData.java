package com.sergey.zhuravlev.mobile.social.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.paging.AsyncPagingDataDiffer;
import androidx.paging.PagingData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;

@SuppressWarnings("rawtypes")
public class PrefetchedPagingData<V> {

    private final AsyncPagingDataDiffer<V> differ;


    static DiffUtil.ItemCallback<Object> EMPTY_ITEM_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
            return false;
        }
    };

    static ListUpdateCallback EMPTY_LIST_UPDATE_CALLBACK = new ListUpdateCallback() {
        @Override
        public void onInserted(int position, int count) {
        }

        @Override
        public void onRemoved(int position, int count) {
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
        }

        @Override
        public void onChanged(int position, int count, @Nullable Object payload) {
        }
    };

    public PrefetchedPagingData() {
        this.differ = new AsyncPagingDataDiffer(EMPTY_ITEM_CALLBACK, EMPTY_LIST_UPDATE_CALLBACK);
    }


    public void submitData(Lifecycle lifecycle, PagingData<V> pagingData) {
        differ.submitData(lifecycle, pagingData);
    }
}
