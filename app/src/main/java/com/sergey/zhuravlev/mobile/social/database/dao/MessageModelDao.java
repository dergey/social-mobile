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

    @Query("SELECT * FROM messages WHERE chat_id = :chatId")
    PagingSource<Integer, MessageModel> getAllMessageModel(Long chatId);

    @Query("SELECT * FROM messages WHERE network_id in (:networkIds)")
    List<MessageModel> getAllByNetworkIds(List<Long> networkIds);

    @Query("SELECT count(*) FROM messages WHERE chat_id = :chatId AND pageable_page = :page ")
    Integer getCountOfStoredPageElement(Long chatId, Integer page);

    @Query("DELETE FROM messages")
    void clearAllMessageModel();

    @Query("DELETE FROM messages WHERE id = :id")
    void clearOne(Long id);

    @Query("DELETE FROM messages WHERE id in (:ids)")
    void clearAll(List<Long> ids);

    @Query("DELETE FROM messages WHERE network_id in (:networkIds)")
    void clearAllByNetworkId(List<Long> networkIds);

}
