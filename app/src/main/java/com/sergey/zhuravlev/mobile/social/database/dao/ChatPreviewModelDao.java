package com.sergey.zhuravlev.mobile.social.database.dao;

import androidx.paging.PagingSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sergey.zhuravlev.mobile.social.database.model.ChatPreviewModel;

import java.util.List;

@Dao
public interface ChatPreviewModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ChatPreviewModel> chatPreviewModels);

    @Query("SELECT * FROM chat_previews")
    PagingSource<Integer, ChatPreviewModel> getAllChatModel();

    @Query("DELETE FROM chat_previews")
    void clearAllChatModel();

}
