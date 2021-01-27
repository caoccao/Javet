package com.caoccao.javet.utils;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestDateTimeUtils {
    @Test
    public void testConversion() {
        long jsTimestamp = 1611653084680L;
        ZonedDateTime zonedDateTime = DateTimeUtils.toZonedDateTime(jsTimestamp);
        if (zonedDateTime.getZone().getId().equals("Asia/Shanghai")) {
            assertEquals(2021, zonedDateTime.getYear());
            assertEquals(1, zonedDateTime.getMonthValue());
            assertEquals(26, zonedDateTime.getDayOfMonth());
            assertEquals(17, zonedDateTime.getHour());
            assertEquals(24, zonedDateTime.getMinute());
            assertEquals(44, zonedDateTime.getSecond());
        } else {
            zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(jsTimestamp), ZoneId.of("UTC"));
            assertEquals(2021, zonedDateTime.getYear());
            assertEquals(1, zonedDateTime.getMonthValue());
            assertEquals(26, zonedDateTime.getDayOfMonth());
            assertEquals(9, zonedDateTime.getHour());
            assertEquals(24, zonedDateTime.getMinute());
            assertEquals(44, zonedDateTime.getSecond());
        }
    }
}
