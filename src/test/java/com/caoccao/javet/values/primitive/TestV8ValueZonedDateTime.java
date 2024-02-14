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

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetDateTimeUtils;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueZonedDateTime extends BaseTestJavetRuntime {

    protected static final int DELTA = 2000;

    @Test
    public void testEquals() throws JavetException {
        V8ValueZonedDateTime v8ValueZonedDateTime = v8Runtime.getExecutor("new Date(123)").execute();
        assertTrue(v8ValueZonedDateTime.equals(
                v8Runtime.createV8ValueZonedDateTime(JavetDateTimeUtils.toZonedDateTime(123L))));
        assertFalse(v8ValueZonedDateTime.equals(null));
        assertFalse(v8ValueZonedDateTime.equals(
                v8Runtime.createV8ValueZonedDateTime(JavetDateTimeUtils.toZonedDateTime(234L))));
        assertFalse(v8ValueZonedDateTime.equals(v8Runtime.createV8ValueLong(1)));
    }

    @Test
    public void testMaxAndMin() throws JavetException {
        ZonedDateTime maxZonedDateTime = ZonedDateTime.of(
                9999, 12, 31, 23, 59, 59, 0, JavetDateTimeUtils.ZONE_ID_UTC);
        ZonedDateTime minZonedDateTime = ZonedDateTime.of(
                1900, 1, 1, 0, 0, 0, 0, JavetDateTimeUtils.ZONE_ID_UTC);
        assertEquals(253402300799L, maxZonedDateTime.toInstant().getEpochSecond());
        assertEquals(-2208988800L, minZonedDateTime.toInstant().getEpochSecond());
        assertEquals(maxZonedDateTime, v8Runtime.getExecutor("new Date(253402300799000)")
                .executeZonedDateTime().withZoneSameInstant(JavetDateTimeUtils.ZONE_ID_UTC));
        assertEquals(minZonedDateTime, v8Runtime.getExecutor("new Date(-2208988800000)")
                .executeZonedDateTime().withZoneSameInstant(JavetDateTimeUtils.ZONE_ID_UTC));
    }

    @Test
    public void testZonedDateTime() throws JavetException {
        ZonedDateTime now = ZonedDateTime.now();
        try (V8ValueZonedDateTime v8ValueZonedDateTime = v8Runtime.getExecutor("new Date()").execute()) {
            assertNotNull(v8ValueZonedDateTime);
            assertEquals(v8Runtime, v8ValueZonedDateTime.getV8Runtime());
            long deltaEpochSecond = v8ValueZonedDateTime.getValue().toEpochSecond() - now.toEpochSecond();
            assertTrue(deltaEpochSecond >= 0 && deltaEpochSecond <= DELTA);
        }
        ZonedDateTime zonedDateTime = v8Runtime.getExecutor("new Date(1611710223719)").executeZonedDateTime();
        zonedDateTime = zonedDateTime.withZoneSameInstant(JavetDateTimeUtils.ZONE_ID_UTC);
        assertEquals(2021, zonedDateTime.getYear());
        assertEquals(1, zonedDateTime.getMonthValue());
        assertEquals(27, zonedDateTime.getDayOfMonth());
        assertEquals(1, zonedDateTime.getHour());
        assertEquals(17, zonedDateTime.getMinute());
        assertEquals(3, zonedDateTime.getSecond());
        assertEquals("2021-01-27T01:17:03.719Z[UTC]", zonedDateTime.toString());
    }
}
