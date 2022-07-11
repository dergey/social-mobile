package com.sergey.zhuravlev.mobile.social.client.mapper;

import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.ui.friend.FriendRequestItem;

import java.util.Objects;

public class FriendRequestItemMapper {

    public static FriendRequestItem toItem(ProfileDto dto) {
        if (Objects.isNull(dto)) {
            return null;
        }

        FriendRequestItem item = new FriendRequestItem();
        item.setUsername(dto.getUsername());
        item.setFirstName(dto.getFirstName());
        item.setMiddleName(dto.getMiddleName());
        item.setSecondName(dto.getSecondName());
        item.setAccepted(false);
        return item;
    }

}
