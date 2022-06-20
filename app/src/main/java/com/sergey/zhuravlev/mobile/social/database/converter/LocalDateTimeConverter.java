package com.sergey.zhuravlev.mobile.social.database.converter;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class LocalDateTimeConverter {

    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("GMT");

    @TypeConverter
    public Long toTimestamp(LocalDateTime date) {
        if (date == null) {
            return null;
        } else {
            return ZonedDateTime.of(date, DEFAULT_ZONE_ID).toInstant().toEpochMilli();
        }
    }

    @TypeConverter
    public LocalDateTime fromTimestamp(Long timestamp) {
        if (timestamp == null) {
            return null;
        } else {
            return Instant.ofEpochMilli(timestamp).atZone(DEFAULT_ZONE_ID).toLocalDateTime();
        }
    }

}
