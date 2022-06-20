package com.sergey.zhuravlev.mobile.social.client.mapper;

import com.sergey.zhuravlev.mobile.social.client.dto.chat.ChatPreviewDto;
import com.sergey.zhuravlev.mobile.social.database.model.ChatPreviewModel;

import java.util.Objects;

public class ChatModelMapper {

    public static ChatPreviewModel toModel(ChatPreviewDto dto) {
        if (Objects.isNull(dto)) {
            return null;
        }

        ChatPreviewModel model = new ChatPreviewModel();
        model.setId(dto.getId());
        model.setTargetProfile(ProfileModelMapper.toEmbeddable(dto.getTargetProfile()));
        model.setCreateAt(dto.getCreateAt());
        model.setUpdateAt(dto.getUpdateAt());
        model.setMessageAllow(dto.isMessageAllow());
        model.setLastMessage(MessageModelMapper.toEmbeddable(dto.getLastMessage()));
        return model;
    }

}
