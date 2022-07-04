package com.sergey.zhuravlev.mobile.social.database.dao;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;

import java.util.List;

@Dao
public interface MessageModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MessageModel messageModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<MessageModel> messageModels);

    @Query("SELECT * FROM messages WHERE id = :id")
    MessageModel getOne(long id);

    @Query("SELECT * FROM messages WHERE chat_id = :chatId ORDER BY create_at DESC")
    PagingSource<Integer, MessageModel> getAllMessageModel(Long chatId);

    @Query("SELECT * FROM messages WHERE network_id in (:networkIds)")
    List<MessageModel> getAllByNetworkIds(List<Long> networkIds);

    @Query("DELETE FROM messages WHERE id in (:ids)")
    void clearAll(List<Long> ids);

}
