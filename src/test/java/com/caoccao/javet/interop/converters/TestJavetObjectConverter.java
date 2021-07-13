/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.interop.converters;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.entities.JavetEntityFunction;
import com.caoccao.javet.entities.JavetEntityMap;
import com.caoccao.javet.enums.JSFunctionType;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetConverterException;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetDateTimeUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.*;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class TestJavetObjectConverter extends BaseTestJavetRuntime {
    @Test
    public void testAnonymousFunction() throws JavetException {
        String codeString = "const x = {a: 1, b: () => 1}; x;";
        JavetObjectConverter converter = new JavetObjectConverter();
        converter.getConfig().setSkipFunctionInObject(false).setExtractFunctionSourceCode(true);
        Object object = converter.toObject(v8Runtime.getExecutor(codeString).execute(), true);
        assertTrue(object instanceof Map);
        Map map = (Map) object;
        assertEquals(2, map.size());
        assertEquals(1, map.get("a"));
        JavetEntityFunction javetEntityFunction = (JavetEntityFunction) map.get("b");
        assertTrue(javetEntityFunction.getJSFunctionType().isUserDefined());
        assertEquals("() => 1", javetEntityFunction.getSourceCode());
        v8Runtime.resetContext();
        converter.getConfig().setExtractFunctionSourceCode(false);
        object = converter.toObject(v8Runtime.getExecutor(codeString).execute(), true);
        map = (Map) object;
        assertNull(((JavetEntityFunction) map.get("b")).getSourceCode());
        v8Runtime.resetContext();
        converter.getConfig().setSkipFunctionInObject(true);
        object = converter.toObject(v8Runtime.getExecutor(codeString).execute(), true);
        map = (Map) object;
        assertEquals(1, map.size());
        assertEquals(1, map.get("a"));
        v8Runtime.resetContext();
        map.clear();
        javetEntityFunction = new JavetEntityFunction("() => 2");
        javetEntityFunction.setJSFunctionType(JSFunctionType.UserDefined);
        map.put("a", javetEntityFunction);
        try (V8Value v8Value = converter.toV8Value(v8Runtime, map)) {
            v8Runtime.getGlobalObject().set("x", v8Value);
        }
        assertEquals(2, v8Runtime.getExecutor("x.a()").executeInteger());
    }

    @Test
    public void testArray() throws JavetException {
        IJavetConverter converter = new JavetObjectConverter();
        try (V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray()) {
            v8ValueArray.push("abc");
            v8ValueArray.push(123);
            List<Object> list = (List<Object>) converter.toObject(v8ValueArray);
            assertEquals(2, list.size());
            assertEquals("abc", list.get(0));
            assertEquals(123, list.get(1));
        }
        // ArrayList
        try (V8ValueArray v8ValueArray = converter.toV8Value(
                v8Runtime, Arrays.asList("abc", 123))) {
            assertEquals(2, v8ValueArray.getLength());
            assertEquals("abc", v8ValueArray.getString(0));
            assertEquals(123, v8ValueArray.getInteger(1));
        }
        // boolean[]
        try (V8ValueArray v8ValueArray = converter.toV8Value(
                v8Runtime, new boolean[]{true, false})) {
            assertEquals(2, v8ValueArray.getLength());
            assertEquals(true, v8ValueArray.getBoolean(0));
            assertEquals(false, v8ValueArray.getBoolean(1));
        }
        // String[]
        try (V8ValueArray v8ValueArray = converter.toV8Value(
                v8Runtime, new String[]{"abc", "def"})) {
            assertEquals(2, v8ValueArray.getLength());
            assertEquals("abc", v8ValueArray.getString(0));
            assertEquals("def", v8ValueArray.getString(1));
        }
        // ZonedDateTime[]
        try (V8ValueArray v8ValueArray = converter.toV8Value(
                v8Runtime, new ZonedDateTime[]{
                        JavetDateTimeUtils.toZonedDateTime(123L),
                        JavetDateTimeUtils.toZonedDateTime(456L)})) {
            assertEquals(2, v8ValueArray.getLength());
            assertEquals(123L, v8ValueArray.getZonedDateTime(0).toInstant().toEpochMilli());
            assertEquals(456L, v8ValueArray.getZonedDateTime(1).toInstant().toEpochMilli());
        }
        // Object[]
        try (V8ValueArray v8ValueArray = converter.toV8Value(
                v8Runtime, new Object[]{1, "abc"})) {
            assertEquals(2, v8ValueArray.getLength());
            assertEquals(1, v8ValueArray.getInteger(0));
            assertEquals("abc", v8ValueArray.getString(1));
        }
    }

    @Test
    public void testCircularStructure() throws JavetException {
        String codeString = "const a = {x: 1};\n" +
                "var b = {y: a, z: 2};\n" +
                "a.x = b;";
        v8Runtime.getExecutor(codeString).executeVoid();
        try (V8ValueObject v8ValueObject = v8Runtime.getGlobalObject().get("b")) {
            assertEquals(2, v8ValueObject.getInteger("z"));
            try {
                Object b = v8Runtime.toObject(v8ValueObject);
            } catch (JavetConverterException e) {
                assertEquals(JavetError.ConverterCircularStructure, e.getError());
                assertEquals(
                        JavetConverterConfig.DEFAULT_MAX_DEPTH,
                        e.getParameters().get(JavetError.PARAMETER_MAX_DEPTH));
            }
        }
    }

    @Test
    public void testMap() throws JavetException {
        IJavetConverter converter = new JavetObjectConverter();
        try (V8ValueMap v8ValueMap = v8Runtime.createV8ValueMap()) {
            v8ValueMap.set("x", "abc");
            assertEquals("abc", v8ValueMap.getString("x"));
            JavetEntityMap map = (JavetEntityMap) converter.toObject(v8ValueMap);
            assertEquals(1, map.size());
            assertEquals("abc", map.get("x"));
        }
        try (V8ValueMap v8ValueMap = converter.toV8Value(
                v8Runtime, new JavetEntityMap() {{
                    put("x", "abc");
                }})) {
            assertEquals(1, v8ValueMap.getSize());
            assertEquals("abc", v8ValueMap.getString("x"));
        }
    }

    @Test
    public void testObject() throws JavetException {
        IJavetConverter converter = new JavetObjectConverter();
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8ValueObject.set("x", "abc");
            assertEquals("abc", v8ValueObject.getString("x"));
            Map<String, Object> map = (Map<String, Object>) converter.toObject(v8ValueObject);
            assertTrue(map instanceof HashMap);
            assertEquals(1, map.size());
            assertEquals("abc", map.get("x"));
        }
        try (V8ValueObject v8ValueObject = converter.toV8Value(
                v8Runtime, new HashMap<String, Object>() {{
                    put("x", "abc");
                }})) {
            assertEquals("abc", v8ValueObject.getString("x"));
        }
    }

    @Test
    public void testSet() throws JavetException {
        IJavetConverter converter = new JavetObjectConverter();
        try (V8ValueSet v8ValueSet = v8Runtime.createV8ValueSet()) {
            v8ValueSet.add(v8Runtime.createV8ValueString("abc"));
            assertTrue(v8ValueSet.has("abc"));
            Set<Object> set = (Set<Object>) converter.toObject(v8ValueSet);
            assertEquals(1, set.size());
            assertTrue(set.contains("abc"));
        }
        try (V8ValueSet v8ValueSet = converter.toV8Value(
                v8Runtime, new HashSet<Object>(Arrays.asList("a", "b", "c")))) {
            assertEquals(3, v8ValueSet.getSize());
            assertTrue(v8ValueSet.has("a"));
            assertTrue(v8ValueSet.has("b"));
            assertTrue(v8ValueSet.has("c"));
        }
    }

    @Test
    public void testTypedArrayByteArray() throws JavetException {
        IJavetConverter converter = new JavetObjectConverter();
        byte[] bytes = new byte[]{(byte) 0x01, (byte) 0x02, (byte) 0x03};
        try (V8ValueTypedArray v8ValueTypedArray = converter.toV8Value(v8Runtime, bytes)) {
            assertEquals(bytes.length, v8ValueTypedArray.getLength());
            assertEquals(bytes.length, v8ValueTypedArray.getByteLength());
            assertEquals(0, v8ValueTypedArray.getByteOffset());
            assertArrayEquals(bytes, v8ValueTypedArray.toBytes());
        }
        try (V8ValueTypedArray v8ValueTypedArray =
                     v8Runtime.createV8ValueTypedArray(V8ValueReferenceType.Int8Array, bytes.length)) {
            assertTrue(v8ValueTypedArray.fromBytes(bytes));
            byte[] newBytes = (byte[]) converter.toObject(v8ValueTypedArray);
            assertArrayEquals(bytes, newBytes);
        }
    }

    @Test
    public void testTypedArrayDoubleArray() throws JavetException {
        IJavetConverter converter = new JavetObjectConverter();
        double[] doubles = new double[]{1.23D, 2.34D, 3.45D};
        try (V8ValueTypedArray v8ValueTypedArray = converter.toV8Value(v8Runtime, doubles)) {
            assertEquals(doubles.length, v8ValueTypedArray.getLength());
            assertEquals(doubles.length * v8ValueTypedArray.getSizeInBytes(), v8ValueTypedArray.getByteLength());
            assertEquals(0, v8ValueTypedArray.getByteOffset());
            assertArrayEquals(doubles, v8ValueTypedArray.toDoubles());
        }
        try (V8ValueTypedArray v8ValueTypedArray =
                     v8Runtime.createV8ValueTypedArray(V8ValueReferenceType.Float64Array, doubles.length)) {
            assertTrue(v8ValueTypedArray.fromDoubles(doubles));
            double[] newDoubles = (double[]) converter.toObject(v8ValueTypedArray);
            assertArrayEquals(doubles, newDoubles, 0.001D);
        }
    }

    @Test
    public void testTypedArrayFloatArray() throws JavetException {
        IJavetConverter converter = new JavetObjectConverter();
        float[] floats = new float[]{1.23F, 2.34F, 3.45F};
        try (V8ValueTypedArray v8ValueTypedArray = converter.toV8Value(v8Runtime, floats)) {
            assertEquals(floats.length, v8ValueTypedArray.getLength());
            assertEquals(floats.length * v8ValueTypedArray.getSizeInBytes(), v8ValueTypedArray.getByteLength());
            assertEquals(0, v8ValueTypedArray.getByteOffset());
            assertArrayEquals(floats, v8ValueTypedArray.toFloats());
        }
        try (V8ValueTypedArray v8ValueTypedArray =
                     v8Runtime.createV8ValueTypedArray(V8ValueReferenceType.Float32Array, floats.length)) {
            assertTrue(v8ValueTypedArray.fromFloats(floats));
            float[] newFloats = (float[]) converter.toObject(v8ValueTypedArray);
            assertArrayEquals(floats, newFloats, 0.001F);
        }
    }

    @Test
    public void testTypedArrayIntegerArray() throws JavetException {
        IJavetConverter converter = new JavetObjectConverter();
        int[] integers = new int[]{1, 2, 3};
        try (V8ValueTypedArray v8ValueTypedArray = converter.toV8Value(v8Runtime, integers)) {
            assertEquals(integers.length, v8ValueTypedArray.getLength());
            assertEquals(integers.length * v8ValueTypedArray.getSizeInBytes(), v8ValueTypedArray.getByteLength());
            assertEquals(0, v8ValueTypedArray.getByteOffset());
            assertArrayEquals(integers, v8ValueTypedArray.toIntegers());
        }
        try (V8ValueTypedArray v8ValueTypedArray =
                     v8Runtime.createV8ValueTypedArray(V8ValueReferenceType.Int32Array, integers.length)) {
            assertTrue(v8ValueTypedArray.fromIntegers(integers));
            int[] newIntegers = (int[]) converter.toObject(v8ValueTypedArray);
            assertArrayEquals(integers, newIntegers);
        }
    }

    @Test
    public void testTypedArrayLongArray() throws JavetException {
        IJavetConverter converter = new JavetObjectConverter();
        long[] longs = new long[]{1L, 2L, 3L};
        try (V8ValueTypedArray v8ValueTypedArray = converter.toV8Value(v8Runtime, longs)) {
            assertEquals(longs.length, v8ValueTypedArray.getLength());
            assertEquals(longs.length * v8ValueTypedArray.getSizeInBytes(), v8ValueTypedArray.getByteLength());
            assertEquals(0, v8ValueTypedArray.getByteOffset());
            assertArrayEquals(longs, v8ValueTypedArray.toLongs());
        }
        try (V8ValueTypedArray v8ValueTypedArray =
                     v8Runtime.createV8ValueTypedArray(V8ValueReferenceType.BigInt64Array, longs.length)) {
            assertTrue(v8ValueTypedArray.fromLongs(longs));
            long[] newLongs = (long[]) converter.toObject(v8ValueTypedArray);
            assertArrayEquals(longs, newLongs);
        }
    }

    @Test
    public void testTypedArrayShortArray() throws JavetException {
        IJavetConverter converter = new JavetObjectConverter();
        short[] shorts = new short[]{(short) 0x01, (short) 0x02, (short) 0x03};
        try (V8ValueTypedArray v8ValueTypedArray = converter.toV8Value(v8Runtime, shorts)) {
            assertEquals(shorts.length, v8ValueTypedArray.getLength());
            assertEquals(shorts.length * v8ValueTypedArray.getSizeInBytes(), v8ValueTypedArray.getByteLength());
            assertEquals(0, v8ValueTypedArray.getByteOffset());
            assertArrayEquals(shorts, v8ValueTypedArray.toShorts());
        }
        try (V8ValueTypedArray v8ValueTypedArray =
                     v8Runtime.createV8ValueTypedArray(V8ValueReferenceType.Int16Array, shorts.length)) {
            assertTrue(v8ValueTypedArray.fromShorts(shorts));
            short[] newShorts = (short[]) converter.toObject(v8ValueTypedArray);
            assertArrayEquals(shorts, newShorts);
        }
    }
}
