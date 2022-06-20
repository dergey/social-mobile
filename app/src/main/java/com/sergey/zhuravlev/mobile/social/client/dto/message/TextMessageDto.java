package com.sergey.zhuravlev.mobile.social.client.dto.message;

import com.sergey.zhuravlev.mobile.social.enums.MessageType;

public class TextMessageDto extends MessageDto {

    private String text;

    public TextMessageDto() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public MessageType getType() {
        return MessageType.TEXT;
    }
}
