package com.sergey.zhuravlev.mobile.social.client.mapper;

import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDetailDto;
import com.sergey.zhuravlev.mobile.social.client.dto.profile.ProfileDto;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileAndDetailModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileDetailModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfileModel;
import com.sergey.zhuravlev.mobile.social.database.model.ProfilePreviewEmbeddable;

import java.util.Objects;

public class ProfileModelMapper {

    public static ProfileAndDetailModel toModel(ProfileDetailDto dto) {
        if (Objects.isNull(dto)) {
            return null;
        }

        ProfileModel profileModel = new ProfileModel();
        profileModel.setUsername(dto.getUsername());
        profileModel.setFirstName(dto.getFirstName());
        profileModel.setMiddleName(dto.getMiddleName());
        profileModel.setSecondName(dto.getSecondName());

        ProfileDetailModel detailModel = new ProfileDetailModel();
        detailModel.setUsername(dto.getUsername());
        detailModel.setLastSeen(dto.getLastSeen());
        detailModel.setOverview(dto.getOverview());
        detailModel.setRelationshipStatus(dto.getRelationshipStatus());
        detailModel.setCity(dto.getCity());
        detailModel.setWorkplace(dto.getWorkplace());
        detailModel.setEducation(dto.getEducation());
        detailModel.setBirthDate(dto.getBirthDate());
        detailModel.setCreateAt(dto.getCreateAt());
        detailModel.setUpdateAt(dto.getUpdateAt());

        ProfileAndDetailModel model = new ProfileAndDetailModel();
        model.setProfile(profileModel);
        model.setDetail(detailModel);
        return model;
    }

    public static ProfileModel toModel(ProfileDto dto) {
        if (Objects.isNull(dto)) {
            return null;
        }

        ProfileModel model = new ProfileModel();
        model.setUsername(dto.getUsername());
        model.setFirstName(dto.getFirstName());
        model.setMiddleName(dto.getMiddleName());
        model.setSecondName(dto.getSecondName());
        return model;
    }


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
