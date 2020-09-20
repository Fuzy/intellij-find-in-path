package com.fuzy.find.converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.intellij.util.xmlb.Converter;

public class LocalDateTimeConverter extends Converter<LocalDateTime> {

    public LocalDateTime fromString(String value) {
        if (value == null) {
            return null;
        }
        final long epochMilli = Long.parseLong(value);
        final ZoneId zoneId = ZoneId.systemDefault();
        return Instant.ofEpochMilli(epochMilli).atZone(zoneId).toLocalDateTime();
    }

    public String toString(LocalDateTime value) {
        final ZoneId zoneId = ZoneId.systemDefault();
        final long toEpochMilli = value.atZone(zoneId).toInstant().toEpochMilli();
        return Long.toString(toEpochMilli);
    }
}
