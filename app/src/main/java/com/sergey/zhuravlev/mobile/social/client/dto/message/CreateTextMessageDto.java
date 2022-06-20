package com.sergey.zhuravlev.mobile.social.client.dto.message;

public class CreateTextMessageDto extends CreateMessageDto {

    private String text;

    public CreateTextMessageDto() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
