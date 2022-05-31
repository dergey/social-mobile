package com.sergey.zhuravlev.mobile.social.dto.message;

import com.sergey.zhuravlev.mobile.social.dto.enums.MessageType;

public class StickerMessageDto extends MessageDto {

    public StickerMessageDto() {
    }

    @Override
    public MessageType getType() {
        return MessageType.STICKER;
    }

}
