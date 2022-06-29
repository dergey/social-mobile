package com.sergey.zhuravlev.mobile.social.database.dao;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sergey.zhuravlev.mobile.social.database.model.ChatPreviewModel;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;

import java.util.List;

@Dao
public interface MessageModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MessageModel messageModel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MessageModel> messageModels);

    @Query("SELECT * FROM messages WHERE chat_id = :chatId")
    PagingSource<Integer, MessageModel> getAllMessageModel(Long chatId);

    @Query("SELECT count(*) FROM messages WHERE chat_id = :chatId AND pageable_page = :page ")
    Integer getCountOfStoredPageElement(Long chatId, Integer page);

    @Query("DELETE FROM messages")
    void clearAllMessageModel();

}
