package com.sergey.zhuravlev.mobile.social.client.dto.message;

import com.sergey.zhuravlev.mobile.social.enums.MessageType;

public class CreateTextMessageDto extends CreateMessageDto {

    private String text;

    public CreateTextMessageDto() {
        super(MessageType.TEXT);
    }

    public CreateTextMessageDto(String text) {
        super(MessageType.TEXT);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
