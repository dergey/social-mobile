package com.sergey.zhuravlev.mobile.social.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;

import com.sergey.zhuravlev.mobile.social.database.model.ChatModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileAndDetailModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileDetailModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileModel;

@Dao
public interface ProfileModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProfileModel profileModel);

    @Query("SELECT * FROM profiles WHERE username = :username")
    ProfileModel getOneByUsername(String username);

    @Transaction
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM profiles INNER JOIN profile_details ON profiles.username = profile_details.username WHERE is_current = 1")
    ProfileAndDetailModel getCurrentWithDetail();

}
