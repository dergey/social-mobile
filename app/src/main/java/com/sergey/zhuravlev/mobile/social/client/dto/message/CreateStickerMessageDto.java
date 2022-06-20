package com.sergey.zhuravlev.mobile.social.client.dto.message;

public class CreateStickerMessageDto extends CreateMessageDto {

    private Long stickerId;

    public CreateStickerMessageDto() {
    }

    public Long getStickerId() {
        return stickerId;
    }

    public void setStickerId(Long stickerId) {
        this.stickerId = stickerId;
    }
}
