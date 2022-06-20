package com.sergey.zhuravlev.mobile.social.client.dto.message;

import com.sergey.zhuravlev.mobile.social.enums.MessageType;

public class StickerMessageDto extends MessageDto {

    public StickerMessageDto() {
    }

    @Override
    public MessageType getType() {
        return MessageType.STICKER;
    }

}
