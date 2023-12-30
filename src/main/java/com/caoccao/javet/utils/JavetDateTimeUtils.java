/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * The type Date time utils.
 */
public final class JavetDateTimeUtils {

    /**
     * The constant ZONE_ID_UTC.
     */
    public static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    private JavetDateTimeUtils() {
    }

    /**
     * Gets utc now.
     *
     * @return the utc now
     */
    public static ZonedDateTime getUTCNow() {
        return ZonedDateTime.now(ZONE_ID_UTC);
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
}
