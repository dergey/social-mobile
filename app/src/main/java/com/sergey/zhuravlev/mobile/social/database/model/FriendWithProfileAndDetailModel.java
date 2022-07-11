package com.sergey.zhuravlev.mobile.social.database.model;

public class FriendWithProfileAndDetailModel {

    private FriendModel friend;

    private ProfileAndDetailModel profileAndDetail;


    public FriendModel getFriend() {
        return friend;
    }

    public void setFriend(FriendModel friend) {
        this.friend = friend;
    }

    public ProfileAndDetailModel getProfileAndDetail() {
        return profileAndDetail;
    }

    public void setProfileAndDetail(ProfileAndDetailModel profileAndDetail) {
        this.profileAndDetail = profileAndDetail;
    }
}
