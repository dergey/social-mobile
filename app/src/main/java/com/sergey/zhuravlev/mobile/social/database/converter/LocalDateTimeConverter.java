package com.sergey.zhuravlev.mobile.social.database.converter;

import androidx.room.TypeConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class LocalDateTimeConverter {

    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("GMT");

    public static Long toEpochMilli(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        } else {
            return ZonedDateTime.of(localDateTime, DEFAULT_ZONE_ID).toInstant().toEpochMilli();
        }
    }

    public static LocalDateTime fromEpochMilli(Long epochMilli) {
        if (epochMilli == null) {
            return null;
        } else {
            return Instant.ofEpochMilli(epochMilli).atZone(DEFAULT_ZONE_ID).toLocalDateTime();
        }
    }

    public static Long toEpochDay(LocalDate localDate) {
        if (localDate == null) {
            return null;
        } else {
            return localDate.toEpochDay();
        }
    }

    public static LocalDate fromEpochDay(Long epochDay) {
        if (epochDay == null) {
            return null;
        } else {
            return LocalDate.ofEpochDay(epochDay);
        }
    }

    @TypeConverter
    public Long toEpochMilliConvert(LocalDateTime localDateTime) {
        return toEpochMilli(localDateTime);
    }

    @TypeConverter
    public LocalDateTime fromEpochMilliConvert(Long epochMilli) {
        return fromEpochMilli(epochMilli);
    }

    @TypeConverter
    public Long toEpochDayConvert(LocalDate localDate) {
        return toEpochDay(localDate);
    }

    @TypeConverter
    public LocalDate fromEpochDayConvert(Long epochDay) {
        return fromEpochDay(epochDay);
    }

}
