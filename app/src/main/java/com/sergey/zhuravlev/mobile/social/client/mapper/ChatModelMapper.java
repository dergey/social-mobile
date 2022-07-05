package com.sergey.zhuravlev.mobile.social.client.mapper;

import com.sergey.zhuravlev.mobile.social.client.dto.chat.ChatPreviewDto;
import com.sergey.zhuravlev.mobile.social.database.model.ChatAndLastMessageModel;
import com.sergey.zhuravlev.mobile.social.database.model.ChatModel;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;

import java.util.Objects;

public class ChatModelMapper {

    public static ChatModel toModel(ChatPreviewDto dto, MessageModel messageModel) {
        if (Objects.isNull(dto)) {
            return null;
        }

        ChatModel model = new ChatModel();
        model.setId(dto.getId());
        model.setTargetProfile(ProfileModelMapper.toEmbeddable(dto.getTargetProfile()));
        model.setCreateAt(dto.getCreateAt());
        model.setUpdateAt(dto.getUpdateAt());
        model.setMessageAllow(dto.isMessageAllow());
        model.setLastMessageId(messageModel.getId());
        return model;
    }

    public static ChatModel updateModel(ChatModel model, ChatPreviewDto dto, MessageModel messageModel) {
        if (Objects.isNull(dto)) {
            return model;
        }

        model.setTargetProfile(ProfileModelMapper.toEmbeddable(dto.getTargetProfile()));
        model.setCreateAt(dto.getCreateAt());
        model.setUpdateAt(dto.getUpdateAt());
        model.setMessageAllow(dto.isMessageAllow());
        model.setLastMessageId(messageModel.getId());
        return model;
    }

}
