package com.sergey.zhuravlev.mobile.social.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sergey.zhuravlev.mobile.social.dto.enums.MessageType;

public class ImageMessageDto extends MessageDto {

    @JsonIgnore
    private Long chatId;

    public ImageMessageDto() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public MessageType getType() {
        return MessageType.IMAGE;
    }

}
