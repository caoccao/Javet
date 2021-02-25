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

package com.caoccao.javet.utils.converters;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.entities.JavetEntityMap;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetDateTimeUtils;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueMap;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValueSet;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unchecked")
public class TestJavetObjectConverter extends BaseTestJavetRuntime {
    protected JavetObjectConverter converter;

    public TestJavetObjectConverter() {
        converter = new JavetObjectConverter();
    }

    @Test
    public void testArray() throws JavetException {
        try (V8ValueArray v8ValueArray = v8Runtime.createV8ValueArray()) {
            v8ValueArray.push(new V8ValueString("abc"));
            v8ValueArray.push(new V8ValueInteger(123));
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
        // double[]
        try (V8ValueArray v8ValueArray = converter.toV8Value(
                v8Runtime, new double[]{1.23D, 2.34D})) {
            assertEquals(2, v8ValueArray.getLength());
            assertEquals(1.23D, v8ValueArray.getDouble(0), 0.001D);
            assertEquals(2.34D, v8ValueArray.getDouble(1), 0.001D);
        }
        // float[]
        try (V8ValueArray v8ValueArray = converter.toV8Value(
                v8Runtime, new float[]{1.23F, 2.34F})) {
            assertEquals(2, v8ValueArray.getLength());
            assertEquals(1.23F, v8ValueArray.getFloat(0), 0.001F);
            assertEquals(2.34F, v8ValueArray.getFloat(1), 0.001F);
        }
        // int[]
        try (V8ValueArray v8ValueArray = converter.toV8Value(
                v8Runtime, new int[]{1, 2})) {
            assertEquals(2, v8ValueArray.getLength());
            assertEquals(1, v8ValueArray.getInteger(0));
            assertEquals(2, v8ValueArray.getInteger(1));
        }
        // long[]
        try (V8ValueArray v8ValueArray = converter.toV8Value(
                v8Runtime, new long[]{1L, 2L})) {
            assertEquals(2, v8ValueArray.getLength());
            assertEquals(1L, v8ValueArray.getLong(0));
            assertEquals(2L, v8ValueArray.getLong(1));
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
    public void testMap() throws JavetException {
        try (V8ValueMap v8ValueMap = v8Runtime.createV8ValueMap()) {
            v8ValueMap.set("x", new V8ValueString("abc"));
            assertEquals("abc", v8ValueMap.getString("x"));
            JavetEntityMap map = (JavetEntityMap) converter.toObject(v8ValueMap);
            assertEquals(1, map.size());
            assertEquals("abc", map.get("x"));
        }
        try (V8ValueMap v8ValueMap = converter.toV8Value(
                v8Runtime, new JavetEntityMap() {{put("x", "abc");}})) {
            assertEquals(1, v8ValueMap.getSize());
            assertEquals("abc", v8ValueMap.getString("x"));
        }
    }

    @Test
    public void testObject() throws JavetException {
        try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
            v8ValueObject.set("x", new V8ValueString("abc"));
            assertEquals("abc", v8ValueObject.getString("x"));
            Map<String, Object> map = (Map<String, Object>) converter.toObject(v8ValueObject);
            assertTrue(map instanceof HashMap);
            assertEquals(1, map.size());
            assertEquals("abc", map.get("x"));
        }
        try (V8ValueObject v8ValueObject = converter.toV8Value(
                v8Runtime, new HashMap<String, Object>() {{put("x", "abc");}})) {
            assertEquals("abc", v8ValueObject.getString("x"));
        }
    }

    @Test
    public void testSet() throws JavetException {
        try (V8ValueSet v8ValueSet = v8Runtime.createV8ValueSet()) {
            v8ValueSet.add(new V8ValueString("abc"));
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
}
