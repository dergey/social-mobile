package com.sergey.zhuravlev.mobile.social.database.converter;

import androidx.room.TypeConverter;

import com.sergey.zhuravlev.mobile.social.enums.MessageSenderType;
import com.sergey.zhuravlev.mobile.social.enums.MessageType;

public class EnumStringConverter {

    @TypeConverter
    public MessageType toMessageType(String value) {
        if (value == null) {
            return null;
        } else {
            return MessageType.valueOf(value);
        }
    }

    @TypeConverter
    public String fromMessageType(MessageType value) {
        if (value == null) {
            return null;
        } else {
            return value.name();
        }
    }

    @TypeConverter
    public MessageSenderType toMessageSenderType(String value) {
        if (value == null) {
            return null;
        } else {
            return MessageSenderType.valueOf(value);
        }
    }

    @TypeConverter
    public String fromMessageSenderType(MessageSenderType value) {
        if (value == null) {
            return null;
        } else {
            return value.name();
        }
    }

}
