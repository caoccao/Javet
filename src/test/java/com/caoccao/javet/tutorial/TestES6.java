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

package com.caoccao.javet.tutorial;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.utils.JavetOSUtils;
import com.caoccao.javet.values.reference.V8ValueArray;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class TestES6 extends BaseTestJavetRuntime {

    @Test
    public void testES6ArrayFind() throws JavetException {
        File scriptFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/test-es6/test-es6-array-find.js");
        assertEquals(3, v8Runtime.getExecutor(scriptFile).executeInteger());
    }

    @Test
    public void testES6ArrayFindIndex() throws JavetException {
        File scriptFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/test-es6/test-es6-array-find-index.js");
        assertEquals(2, v8Runtime.getExecutor(scriptFile).executeInteger());
    }

    @Test
    public void testES6ArrowFunction() throws JavetException {
        File scriptFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/test-es6/test-es6-arrow-function.js");
        assertEquals(3, v8Runtime.getExecutor(scriptFile).executeInteger());
    }

    @Test
    public void testES6Class() throws JavetException {
        File scriptFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/test-es6/test-es6-class.js");
        assertEquals(3, v8Runtime.getExecutor(scriptFile).executeInteger());
    }

    @Test
    public void testES6DefaultParameterValues() throws JavetException {
        File scriptFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/test-es6/test-es6-default-parameter-values.js");
        assertEquals(3, v8Runtime.getExecutor(scriptFile).executeInteger());
    }

    @Test
    public void testES6FunctionRestParameter() throws JavetException {
        File scriptFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/test-es6/test-es6-function-rest-parameter.js");
        assertEquals(6, v8Runtime.getExecutor(scriptFile).executeInteger());
    }

    @Test
    public void testES6IsFinite() throws JavetException {
        File scriptFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/test-es6/test-es6-is-finite.js");
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor(scriptFile).execute()) {
            assertTrue(v8ValueArray.getBoolean(0));
            assertFalse(v8ValueArray.getBoolean(1));
        }
    }

    @Test
    public void testES6IsNaN() throws JavetException {
        File scriptFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/test-es6/test-es6-is-nan.js");
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor(scriptFile).execute()) {
            assertTrue(v8ValueArray.getBoolean(0));
            assertFalse(v8ValueArray.getBoolean(1));
        }
    }

    @Test
    public void testES6LetConst() throws JavetException {
        File scriptFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/test-es6/test-es6-let-const.js");
        assertEquals(3, v8Runtime.getExecutor(scriptFile).executeInteger());
    }

    @Test
    public void testES6NumberIsInteger() throws JavetException {
        File scriptFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/test-es6/test-es6-number-is-integer.js");
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor(scriptFile).execute()) {
            assertTrue(v8ValueArray.getBoolean(0));
            assertFalse(v8ValueArray.getBoolean(1));
        }
    }

    @Test
    public void testES6NumberIsSafeInteger() throws JavetException {
        File scriptFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/test-es6/test-es6-number-is-safe-integer.js");
        try (V8ValueArray v8ValueArray = v8Runtime.getExecutor(scriptFile).execute()) {
            assertTrue(v8ValueArray.getBoolean(0));
            assertFalse(v8ValueArray.getBoolean(1));
        }
    }

    @Test
    public void testES6Symbol() throws JavetException {
        File scriptFile = new File(
                JavetOSUtils.WORKING_DIRECTORY,
                "scripts/node/test-es6/test-es6-symbol.js");
        assertFalse(v8Runtime.getExecutor(scriptFile).executeBoolean());
    }
}
