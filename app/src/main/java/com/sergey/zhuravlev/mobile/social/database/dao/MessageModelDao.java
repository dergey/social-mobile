package com.sergey.zhuravlev.mobile.social.database.dao;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.enums.MessageSenderType;

import java.util.List;

@Dao
public interface MessageModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MessageModel messageModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(List<MessageModel> messageModels);

    @Query("SELECT * FROM messages WHERE id = :id")
    MessageModel getOneById(long id);

    @Query("SELECT * FROM messages WHERE chat_id = :chatId AND sender = :messageSenderType")
    List<MessageModel> getAllByChatIdAndMessageSenderType(Long chatId, MessageSenderType messageSenderType);


    @Query("SELECT * FROM messages WHERE chat_id = :chatId ORDER BY create_at DESC")
    PagingSource<Integer, MessageModel> getAllMessageModel(long chatId);

    @Query("UPDATE messages SET pageable_page = NULL WHERE chat_id = :chatId AND pageable_page >= :page")
    void resetPageableAfterPageMessageModel(long chatId, int page);

    @Query("SELECT * FROM messages WHERE network_id in (:networkIds)")
    List<MessageModel> getAllByNetworkIds(List<Long> networkIds);

    @Query("DELETE FROM messages WHERE id in (:ids)")
    void clearAll(List<Long> ids);

    @Query("SELECT max(messages.pageable_page) FROM messages WHERE chat_id = :chatId")
    Integer getLastPage(Long chatId);


}
