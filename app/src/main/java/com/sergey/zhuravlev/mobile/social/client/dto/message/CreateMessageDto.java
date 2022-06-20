package com.sergey.zhuravlev.mobile.social.client.dto.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.sergey.zhuravlev.mobile.social.enums.MessageType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateTextMessageDto.class, name = "TEXT"),
        @JsonSubTypes.Type(value = CreateStickerMessageDto.class, name = "STICKER")
})
public abstract class CreateMessageDto {

    private MessageType type;

    public CreateMessageDto() {
    }

    public CreateMessageDto(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}
