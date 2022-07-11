package com.sergey.zhuravlev.mobile.social.database.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ProfileAndDetailModel {

    @Embedded
    private ProfileModel profile;

    @Relation(
            parentColumn = "username",
            entityColumn = "username"
    )
    private ProfileDetailModel detail;

    public ProfileModel getProfile() {
        return profile;
    }

    public void setProfile(ProfileModel profile) {
        this.profile = profile;
    }

    public ProfileDetailModel getDetail() {
        return detail;
    }

    public void setDetail(ProfileDetailModel detail) {
        this.detail = detail;
    }

}
