package com.sergey.zhuravlev.mobile.social.database.dao;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.sergey.zhuravlev.mobile.social.database.model.ChatAndLastMessageModel;
import com.sergey.zhuravlev.mobile.social.database.model.ChatModel;

import java.util.Arrays;
import java.util.List;

@Dao
public interface ChatModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChatModel chatModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ChatModel> chatModels);

    @Query("SELECT * FROM chats WHERE id = :chatId")
    ChatModel getOneById(Long chatId);

    @Query("SELECT * FROM chats WHERE id IN (:newChatIds)")
    List<ChatModel> getAllByIds(List<Long> newChatIds);

    @Transaction
    @Query("SELECT * FROM chats INNER JOIN messages ON chats.last_message_id = messages.id")
    PagingSource<Integer, ChatAndLastMessageModel> getAllChatAndLastMessageModel();

    @Query("SELECT max(chats.pageable_page) FROM chats")
    Integer getLastPage();

    @Query("UPDATE chats SET pageable_page = NULL WHERE pageable_page >= :page")
    void resetPageableAfterPageMessageModel(Integer page);

}
