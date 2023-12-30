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

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestJavetDateTimeUtils {
    @Test
    public void testConversion() {
        long jsTimestamp = 1611653084680L;
        ZonedDateTime zonedDateTime = JavetDateTimeUtils.toZonedDateTime(jsTimestamp);
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
