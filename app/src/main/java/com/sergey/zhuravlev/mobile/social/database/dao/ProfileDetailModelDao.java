package com.sergey.zhuravlev.mobile.social.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.sergey.zhuravlev.mobile.social.database.model.ProfileAndDetailModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileDetailModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileModel;

@Dao
public interface ProfileDetailModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProfileDetailModel profileDetailModel);

}
