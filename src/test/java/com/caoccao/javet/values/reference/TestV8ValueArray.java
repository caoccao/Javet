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

package com.caoccao.javet.values.reference;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8ValueArray extends BaseTestJavetRuntime {
    @Test
    public void testAsInt() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("[]").execute()) {
            assertEquals(0, v8ValueArray.asInt());
        }
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("[0,]").execute()) {
            assertEquals(0, v8ValueArray.asInt());
        }
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("[1,]").execute()) {
            assertEquals(1, v8ValueArray.asInt());
        }
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("[[1,],]").execute()) {
            assertEquals(1, v8ValueArray.asInt());
        }
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("['a']").execute()) {
            assertEquals(0, v8ValueArray.asInt());
        }
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("[' 1 ',]").execute()) {
            assertEquals(1, v8ValueArray.asInt());
        }
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("[' 1 ', '2']").execute()) {
            assertEquals(0, v8ValueArray.asInt());
        }
    }

    @Test
    public void testForEach() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("const a = new Array(0,1,2); a;").execute()) {
            AtomicInteger count = new AtomicInteger(0);
            v8ValueArray.forEach((V8ValueInteger value) -> {
                assertEquals(count.getAndIncrement(), value.getValue());
            });
            v8ValueArray.forEach((int index, V8ValueInteger value) -> {
                assertEquals(index, value.getValue());
            });
        }
    }

    @Test
    public void testGet() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor(
                "[1,'2',3n, true, 1.23, [4, 5, null, new Date(1611710223719)]]").execute()) {
            assertNotNull(v8ValueArray);
            assertEquals(v8Runtime, v8ValueArray.getV8Runtime());
            assertEquals(6, v8ValueArray.getLength());
            assertEquals(1, ((V8ValueInteger) v8ValueArray.get(0)).getValue());
            assertEquals(1, v8ValueArray.getInteger(0));
            assertEquals("2", ((V8ValueString) v8ValueArray.get(1)).getValue());
            assertEquals("2", v8ValueArray.getString(1));
            assertEquals(3L, ((V8ValueLong) v8ValueArray.get(2)).getValue());
            assertEquals(3L, v8ValueArray.getLong(2));
            assertTrue(((V8ValueBoolean) v8ValueArray.get(3)).getValue());
            assertTrue(v8ValueArray.getBoolean(3));
            assertEquals(1.23, ((V8ValueDouble) v8ValueArray.get(4)).getValue(), 0.001);
            assertEquals(1.23, v8ValueArray.getDouble(4), 0.001);
            assertTrue(v8ValueArray.get(-1).isUndefined());
            assertTrue(v8ValueArray.get(100).isUndefined());
            assertEquals(1, v8Runtime.getReferenceCount());
            try (V8ValueArray childV8ValueArray = v8ValueArray.get(5)) {
                assertNotNull(childV8ValueArray);
                assertEquals(v8Runtime, childV8ValueArray.getV8Runtime());
                assertEquals(4, childV8ValueArray.getLength());
                assertEquals(4, childV8ValueArray.getInteger(0));
                assertEquals(5, childV8ValueArray.getInteger(1));
                assertTrue(childV8ValueArray.get(2).isNull());
                assertEquals(
                        "2021-01-27T01:17:03.719Z[UTC]",
                        childV8ValueArray.getZonedDateTime(3).withZoneSameInstant(ZoneId.of("UTC")).toString());
                assertEquals(2, v8Runtime.getReferenceCount());
            }
            assertEquals(1, v8Runtime.getReferenceCount());
            V8Value[] v8Values = v8ValueArray.batchGet();
            assertEquals(6, v8Values.length);
            assertEquals(1, ((V8ValueInteger) v8Values[0]).getValue());
            assertEquals("2", ((V8ValueString) v8Values[1]).getValue());
            assertEquals(3L, ((V8ValueLong) v8Values[2]).getValue());
            assertTrue(((V8ValueBoolean) v8Values[3]).getValue());
            assertEquals(1.23, ((V8ValueDouble) v8Values[4]).getValue(), 0.001D);
            assertInstanceOf(V8ValueArray.class, v8Values[5]);
            assertEquals(2, v8Runtime.getReferenceCount());
            JavetResourceUtils.safeClose(v8Values);
            assertEquals(1, v8Runtime.getReferenceCount());
            Arrays.fill(v8Values, null);
            assertEquals(0, v8ValueArray.batchGet(v8Values, 2, 1), "The actual length should be 0.");
            assertEquals(2, v8ValueArray.batchGet(v8Values, 1, 3));
            assertEquals("2", ((V8ValueString) v8Values[0]).getValue());
            assertEquals(3L, ((V8ValueLong) v8Values[1]).getValue());
            JavetResourceUtils.safeClose(v8Values);
        }
    }

    @Test
    public void testGetAndSet() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("const a = new Array(); a;").execute()) {
            v8ValueArray.set(0, "x");
            v8ValueArray.set(1, "y");
            v8ValueArray.set(2, "z");
            v8ValueArray.set("a", 1);
            v8ValueArray.set("b", "2");
            assertEquals(3, v8ValueArray.getLength());
            assertEquals("x", v8ValueArray.getString(0));
            assertEquals("y", v8ValueArray.getString(1));
            assertEquals("z", v8ValueArray.getString(2));
            assertEquals(1, v8ValueArray.getInteger("a"));
            assertEquals("2", v8ValueArray.getString("b"));
            assertEquals("x,y,z", v8ValueArray.toString());
            assertEquals("[object Array]", v8ValueArray.toProtoString());
            assertEquals("[\"x\",\"y\",\"z\"]", v8ValueArray.toJsonString());
            List<Integer> keys = v8ValueArray.getKeys();
            assertEquals(3, keys.size());
            assertEquals(0, keys.get(0));
            assertEquals(1, keys.get(1));
            assertEquals(2, keys.get(2));
        }
    }

    @Test
    public void testNestedArray() throws JavetException {
        try (V8ValueArray outerArray = v8Runtime.getExecutor("[1,2,3]").execute()) {
            assertEquals(3, outerArray.getLength());
            try (V8ValueArray innerArray = v8Runtime.createV8ValueArray()) {
                assertEquals(1, innerArray.push("a"));
                assertEquals(1, innerArray.getLength());
                assertEquals(4, outerArray.push(innerArray));
            }
            assertEquals("[1,2,3,[\"a\"]]", outerArray.toJsonString());
        }
    }

    @Test
    @Tag("performance")
    public void testPerformancePush() throws JavetException {
        final int itemCount = 1000;
        final int iterations = 1000;
        // Test push one by one.
        {
            final long startTime = System.currentTimeMillis();
            for (int i = 0; i < iterations; i++) {
                try (V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray()) {
                    for (int j = 0; j < itemCount; j++) {
                        v8ValueArray.push(1);
                    }
                }
            }
            final long stopTime = System.currentTimeMillis();
            final long tps = itemCount * iterations * 1000 / (stopTime - startTime);
            logger.logInfo("Array push one by one: {0} tps.", tps);
        }
        // Test push by batch.
        {
            final long startTime = System.currentTimeMillis();
            Object[] items = new Object[itemCount];
            Arrays.fill(items, 1);
            for (int i = 0; i < iterations; i++) {
                try (V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray()) {
                    v8ValueArray.push(items);
                }
            }
            final long stopTime = System.currentTimeMillis();
            final long tps = itemCount * iterations * 1000 / (stopTime - startTime);
            logger.logInfo("Array push in a batch: {0} tps.", tps);
        }
    }

    @Test
    public void testPushAndPop() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray()) {
            v8ValueArray.push(
                    null, 1, 1L, 1.23D, true, false, "abc",
                    v8Runtime.createV8ValueZonedDateTime(1234567890L),
                    v8Runtime.createV8ValueUndefined(),
                    v8Runtime.createV8ValueBigInteger("1234567890123456789012345678901234567890"));
            assertEquals(10, v8ValueArray.getLength());
            assertEquals(new BigInteger("1234567890123456789012345678901234567890"), v8ValueArray.popBigInteger());
            assertTrue(v8ValueArray.popUndefined().isUndefined());
            assertEquals(1234567L, v8ValueArray.popZonedDateTime().toInstant().getEpochSecond());
            assertEquals("abc", v8ValueArray.popString());
            assertFalse(v8ValueArray.popBoolean());
            assertTrue(v8ValueArray.popBoolean());
            assertEquals(1.23D, v8ValueArray.popDouble(), 0.001D);
            assertEquals(1L, v8ValueArray.popLong());
            assertEquals(1, v8ValueArray.popInteger());
            assertTrue(v8ValueArray.popNull().isNull());
            assertEquals(0, v8ValueArray.getLength());
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testToClone(boolean referenceCopy) throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor("const x = []; x;").execute()) {
            v8ValueArray.push(1);
            assertEquals("[1]", v8ValueArray.toJsonString());
            try (V8ValueArray clonedV8ValueArray = v8ValueArray.toClone(referenceCopy)) {
                assertEquals("[1]", clonedV8ValueArray.toJsonString());
                assertNotEquals(v8ValueArray.getHandle(), clonedV8ValueArray.getHandle());
                if (referenceCopy) {
                    assertTrue(clonedV8ValueArray.strictEquals(v8ValueArray));
                } else {
                    assertFalse(clonedV8ValueArray.strictEquals(v8ValueArray));
                }
                clonedV8ValueArray.push(2);
                assertEquals("[1,2]", clonedV8ValueArray.toJsonString());
                if (referenceCopy) {
                    assertEquals("[1,2]", v8ValueArray.toJsonString());
                } else {
                    assertEquals("[1]", v8ValueArray.toJsonString());
                }
                assertEquals(v8Runtime, clonedV8ValueArray.getV8Runtime());
            }
        }
    }

    @Test
    public void testUnshiftAndShift() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray()) {
            v8ValueArray.unshift(
                    null, 1, 1L, 1.23D, true, false, "abc",
                    v8Runtime.createV8ValueZonedDateTime(1234567890L),
                    v8Runtime.createV8ValueUndefined(),
                    v8Runtime.createV8ValueBigInteger("1234567890123456789012345678901234567890"));
            assertEquals(10, v8ValueArray.getLength());
            assertTrue(v8ValueArray.shiftNull().isNull());
            assertEquals(1, v8ValueArray.shiftInteger());
            assertEquals(1L, v8ValueArray.shiftLong());
            assertEquals(1.23D, v8ValueArray.shiftDouble(), 0.001D);
            assertTrue(v8ValueArray.shiftBoolean());
            assertFalse(v8ValueArray.shiftBoolean());
            assertEquals("abc", v8ValueArray.shiftString());
            assertEquals(1234567L, v8ValueArray.shiftZonedDateTime().toInstant().getEpochSecond());
            assertTrue(v8ValueArray.shiftUndefined().isUndefined());
            assertEquals(new BigInteger("1234567890123456789012345678901234567890"), v8ValueArray.shiftBigInteger());
            assertEquals(0, v8ValueArray.getLength());
        }
    }
}
