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
import com.caoccao.javet.mock.MockCallbackReceiver;
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
    public void testCallback() throws JavetException, NoSuchMethodException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.lock();
            assertEquals(0, v8Runtime.getCallbackContextCount());
            MockCallbackReceiver mockCallbackReceiver = new MockCallbackReceiver();
            V8CallbackContext v8CallbackContext = v8Runtime.createCallback(
                    v8Runtime.getGlobalObject(), "test", mockCallbackReceiver,
                    MockCallbackReceiver.class.getMethod("test", new Class[]{}));
            assertEquals(1, v8Runtime.getCallbackContextCount());
            assertFalse(mockCallbackReceiver.isCalled());
            v8Runtime.executeVoid("test();");
            assertTrue(mockCallbackReceiver.isCalled());
            v8Runtime.removeCallback(v8CallbackContext);
            assertEquals(0, v8Runtime.getCallbackContextCount());
        }
    }

    @Test
    public void testExecute() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.lock();
            v8Runtime.executeVoid("var a = 1;");
            assertEquals(2, v8Runtime.executeInteger("a + 1"));
        }
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            v8Runtime.lock();
            v8Runtime.executeVoid("var a = 1;");
            assertEquals(2, v8Runtime.executeInteger("a + 1"));
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
    public void testReset() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            v8Runtime.reset();
        }
    }

}
