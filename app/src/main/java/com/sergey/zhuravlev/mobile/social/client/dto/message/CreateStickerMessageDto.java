package com.sergey.zhuravlev.mobile.social.client.dto.message;

import com.sergey.zhuravlev.mobile.social.enums.MessageType;

public class CreateStickerMessageDto extends CreateMessageDto {

    private Long stickerId;

    public CreateStickerMessageDto() {
        super(MessageType.STICKER);
    }

    public CreateStickerMessageDto(Long stickerId) {
        super(MessageType.STICKER);
        this.stickerId = stickerId;
    }

    public Long getStickerId() {
        return stickerId;
    }

    public void setStickerId(Long stickerId) {
        this.stickerId = stickerId;
    }
}
