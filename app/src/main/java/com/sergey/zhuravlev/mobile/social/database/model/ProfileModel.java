package com.sergey.zhuravlev.mobile.social.database.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "profiles")
public class ProfileModel {

    @PrimaryKey
    @NonNull
    private String username;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "middle_name")
    private String middleName;

    @ColumnInfo(name = "second_name")
    private String secondName;

    @ColumnInfo(name = "is_current")
    private boolean isCurrent;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }
}
