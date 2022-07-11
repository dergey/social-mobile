package com.sergey.zhuravlev.mobile.social.database.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "my_friends")
public class FriendModel {

    @PrimaryKey
    @NonNull
    private String username;

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }
}
