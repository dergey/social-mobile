package com.sergey.zhuravlev.mobile.social.database.converter;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class LocalDateTimeConverter {

    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("GMT");

    @TypeConverter
    public Long toEpochMilli(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        } else {
            return ZonedDateTime.of(localDateTime, DEFAULT_ZONE_ID).toInstant().toEpochMilli();
        }
    }

    @TypeConverter
    public LocalDateTime fromEpochMilli(Long epochMilli) {
        if (epochMilli == null) {
            return null;
        } else {
            return Instant.ofEpochMilli(epochMilli).atZone(DEFAULT_ZONE_ID).toLocalDateTime();
        }
    }

    @TypeConverter
    public Long toEpochDay(LocalDate localDate) {
        if (localDate == null) {
            return null;
        } else {
            return localDate.toEpochDay();
        }
    }

    @TypeConverter
    public LocalDate fromEpochDay(Long epochDay) {
        if (epochDay == null) {
            return null;
        } else {
            return LocalDate.ofEpochDay(epochDay);
        }
    }

}
