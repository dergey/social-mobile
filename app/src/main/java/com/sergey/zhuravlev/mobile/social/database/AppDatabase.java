package com.sergey.zhuravlev.mobile.social.database;

import android.content.Context;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.sergey.zhuravlev.mobile.social.database.converter.EnumStringConverter;
import com.sergey.zhuravlev.mobile.social.database.converter.LocalDateTimeConverter;
import com.sergey.zhuravlev.mobile.social.database.dao.ChatPreviewModelDao;
import com.sergey.zhuravlev.mobile.social.database.dao.MessageModelDao;
import com.sergey.zhuravlev.mobile.social.database.model.ChatPreviewModel;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;

@Database(version = 1, entities = {ChatPreviewModel.class, MessageModel.class}, exportSchema = false)
@TypeConverters({LocalDateTimeConverter.class, EnumStringConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String SOCIAL_DB = "social.db";
    private static volatile AppDatabase instance;

    public abstract ChatPreviewModelDao getChatPreviewModelDao();

    public abstract MessageModelDao getMessageModelDao();

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
