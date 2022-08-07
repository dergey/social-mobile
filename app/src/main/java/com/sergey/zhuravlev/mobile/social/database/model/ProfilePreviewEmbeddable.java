package com.sergey.zhuravlev.mobile.social.database.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;

public class ProfilePreviewEmbeddable implements Parcelable {

    private String username;

    @ColumnInfo(name = "first_name")
    private String firstName;

    @ColumnInfo(name = "middle_name")
    private String middleName;

    @ColumnInfo(name = "second_name")
    private String secondName;

    public ProfilePreviewEmbeddable() {
    }

    @Ignore
    public ProfilePreviewEmbeddable(String username, String firstName, String middleName, String secondName) {
        this.username = username;
        this.firstName = firstName;
        this.middleName = middleName;
        this.secondName = secondName;
    }

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

    @Ignore
    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(firstName);
        dest.writeString(middleName);
        dest.writeString(secondName);
    }

    public static final Parcelable.Creator<ProfilePreviewEmbeddable> CREATOR = new Parcelable.Creator<>() {

        public ProfilePreviewEmbeddable createFromParcel(Parcel in) {
            String username = in.readString();
            String firstName = in.readString();
            String middleName = in.readString();
            String secondName = in.readString();
            return new ProfilePreviewEmbeddable(username, firstName, middleName, secondName);
        }

        public ProfilePreviewEmbeddable[] newArray(int size) {
            return new ProfilePreviewEmbeddable[size];
        }

    };

}
