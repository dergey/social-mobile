package com.sergey.zhuravlev.mobile.social.dto.chat;

public class CreateChatDto {

    private String targetUsername;

    public CreateChatDto() {
    }

    public CreateChatDto(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }
}
