package com.sergey.zhuravlev.mobile.social.client.mapper;

import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.database.model.ProfilePreviewEmbeddable;

import java.util.Objects;

public class ProfileModelMapper {

    public static ProfilePreviewEmbeddable toEmbeddable(ProfileDto dto) {
        if (Objects.isNull(dto)) {
            return null;
        }

        ProfilePreviewEmbeddable model = new ProfilePreviewEmbeddable();
        model.setUsername(dto.getUsername());
        model.setFirstName(dto.getFirstName());
        model.setMiddleName(dto.getMiddleName());
        model.setSecondName(dto.getSecondName());
        return model;
    }

}
