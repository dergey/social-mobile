package com.sergey.zhuravlev.mobile.social.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.sergey.zhuravlev.mobile.social.database.converter.EnumStringConverter;
import com.sergey.zhuravlev.mobile.social.database.converter.LocalDateTimeConverter;
import com.sergey.zhuravlev.mobile.social.database.dao.ChatModelDao;
import com.sergey.zhuravlev.mobile.social.database.dao.MessageModelDao;
import com.sergey.zhuravlev.mobile.social.database.dao.ProfileDetailModelDao;
import com.sergey.zhuravlev.mobile.social.database.dao.ProfileModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.ChatModel;
import com.sergey.zhuravlev.mobile.social.database.model.FriendModel;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileDetailModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileModel;

@Database(version = 7,
        entities = {
                ChatModel.class,
                MessageModel.class,
                ProfileModel.class,
                ProfileDetailModel.class,
                FriendModel.class
        },
        exportSchema = false)
@TypeConverters({LocalDateTimeConverter.class, EnumStringConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String SOCIAL_DB = "social.db";
    private static volatile AppDatabase instance;

    public abstract ChatModelDao getChatModelDao();

    public abstract MessageModelDao getMessageModelDao();

    public abstract ProfileModelDao getProfileModelDao();

    public abstract ProfileDetailModelDao getProfileDetailModelDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = buildDatabase(context);
        }
        return instance;
    }

    private static AppDatabase buildDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, SOCIAL_DB)
                .fallbackToDestructiveMigration()
                .build();
    }

}
