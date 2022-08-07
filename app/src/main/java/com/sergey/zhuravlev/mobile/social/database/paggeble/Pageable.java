package com.sergey.zhuravlev.mobile.social.database.paggeble;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Ignore;

public class Pageable implements Parcelable {

    private Integer page;

    public Pageable() {
    }

    @Ignore
    public Pageable(Integer page) {
        this.page = page;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
    }

    public static final Parcelable.Creator<Pageable> CREATOR = new Parcelable.Creator<>() {

        public Pageable createFromParcel(Parcel in) {
            Integer page = in.readInt();
            return new Pageable(page);
        }

        public Pageable[] newArray(int size) {
            return new Pageable[size];
        }

    };

}
