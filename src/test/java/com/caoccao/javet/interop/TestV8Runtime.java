/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.interop;

import com.caoccao.javet.BaseTestJavet;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetV8RuntimeLockConflictException;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.primitive.V8ValueUndefined;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Runtime extends BaseTestJavet {

    @Test
    public void testClose() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
        }
    }

    @Test
    public void testExecute() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.lock();
            v8Runtime.getExecutor("var a = 1;").executeVoid();
            assertEquals(2, v8Runtime.getExecutor("a + 1").executeInteger());
        }
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            v8Runtime.lock();
            v8Runtime.getExecutor("var a = 1;").executeVoid();
            assertEquals(2, v8Runtime.getExecutor("a + 1").executeInteger());
            try (V8ValueObject window = v8Runtime.createV8ValueObject()) {
                v8Runtime.getGlobalObject().set("window", window);
                window.set("x", new V8ValueString("1"));
            }
            assertEquals("1", v8Runtime.getExecutor("window.x;").executeString());
        }
    }

    @Test
    public void testLockAndUnlock() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            final int iterations = 3;
            for (int i = 0; i < iterations; ++i) {
                assertThrows(JavetV8RuntimeLockConflictException.class, () -> {
                    v8Runtime.unlock();
                }, "It should not allow unlock before lock.");
                v8Runtime.lock();
                // It should not allow double lock.
                v8Runtime.lock();
                v8Runtime.unlock();
            }
        }
    }

    @Test
    public void testResetContext() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            v8Runtime.lock();
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            v8Runtime.getGlobalObject().set("a", new V8ValueString("1"));
            v8Runtime.resetContext();
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            assertTrue(v8Runtime.getGlobalObject().get("a") instanceof V8ValueUndefined);
            v8Runtime.unlock();
        }
    }

    @Test
    public void testResetIsolate() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            v8Runtime.lock();
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            v8Runtime.getGlobalObject().set("a", new V8ValueString("1"));
            v8Runtime.unlock();
            v8Runtime.resetIsolate();
            v8Runtime.lock();
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            assertTrue(v8Runtime.getGlobalObject().get("a") instanceof V8ValueUndefined);
            v8Runtime.unlock();
        }
    }
}
