package com.caoccao.javet.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * The type Date time utils.
 */
public final class DateTimeUtils {
    private DateTimeUtils() {
    }

    /**
     * From JS timestamp to zoned date time.
     * <p>
     * Note: the ZoneId needs to be system default because that's what V8 sees.
     *
     * @param jsTimestamp the JS timestamp
     * @return the zoned date time
     */
    public static ZonedDateTime toZonedDateTime(long jsTimestamp) {
        return toZonedDateTime(jsTimestamp, ZoneId.systemDefault());
    }

    /**
     * From JS timestamp to zoned date time.
     *
     * @param jsTimestamp the JS timestamp
     * @param zoneId      the zone id
     * @return the zoned date time
     */
    public static ZonedDateTime toZonedDateTime(long jsTimestamp, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(jsTimestamp), zoneId);
    }
}
