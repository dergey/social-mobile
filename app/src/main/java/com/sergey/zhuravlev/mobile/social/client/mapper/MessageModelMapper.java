package com.sergey.zhuravlev.mobile.social.client.mapper;

import com.sergey.zhuravlev.mobile.social.client.dto.PageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.chat.ChatPreviewDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.ImageMessageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.MessageDto;
import com.sergey.zhuravlev.mobile.social.client.dto.message.TextMessageDto;
import com.sergey.zhuravlev.mobile.social.database.model.ChatPreviewModel;
import com.sergey.zhuravlev.mobile.social.database.model.MessageEmbeddable;
import com.sergey.zhuravlev.mobile.social.database.model.MessageModel;
import com.sergey.zhuravlev.mobile.social.database.paggeble.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageModelMapper {

    public static MessageEmbeddable toEmbeddable(MessageDto dto) {
        if (Objects.isNull(dto)) {
            return null;
        }

        MessageEmbeddable model = new MessageEmbeddable();
        model.setNetworkId(dto.getId());
        model.setType(dto.getType());
        model.setSender(dto.getSender());
        model.setCreateAt(dto.getCreateAt());
        model.setUpdateAt(dto.getUpdateAt());
        model.setRead(dto.isRead());
        if (dto instanceof TextMessageDto) {
            model.setText(((TextMessageDto) dto).getText());
        }
        //todo add type specific setter/getter
        return model;
    }

    public static MessageModel toModel(MessageDto dto) {
        if (Objects.isNull(dto)) {
            return null;
        }

        MessageModel model = new MessageModel();
        model.setNetworkId(dto.getId());
        model.setType(dto.getType());
        model.setSender(dto.getSender());
        model.setCreateAt(dto.getCreateAt());
        model.setUpdateAt(dto.getUpdateAt());
        model.setRead(dto.isRead());
        if (dto instanceof TextMessageDto) {
            model.setText(((TextMessageDto) dto).getText());
        }
        //todo add type specific setter/getter
        return model;
    }

    public static MessageModel updateModel(MessageModel model, MessageDto dto) {
        if (Objects.isNull(dto)) {
            return model;
        }

        model.setNetworkId(dto.getId());
        model.setType(dto.getType());
        model.setSender(dto.getSender());
        model.setCreateAt(dto.getCreateAt());
        model.setUpdateAt(dto.getUpdateAt());
        model.setRead(dto.isRead());
        model.setPrepend(false);
        model.setPrependError(false);
        if (dto instanceof TextMessageDto) {
            model.setText(((TextMessageDto) dto).getText());
        }
        if (dto instanceof ImageMessageDto) {
            model.setGlideSignature(null);
        }
        return model;
    }

}
