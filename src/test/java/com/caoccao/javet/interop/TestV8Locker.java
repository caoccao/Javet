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

package com.caoccao.javet.interop;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Locker extends BaseTestJavetRuntime {
    @Test
    public void testExceptionInAcquire() throws JavetException {
        try (V8Locker v8Locker = v8Runtime.getV8Locker()) {
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            try (V8Locker innerV8Locker = v8Runtime.getV8Locker()) {
                fail("Failed to report lock acquisition failure.");
            } catch (JavetException e) {
                assertEquals(JavetError.LockAcquisitionFailure, e.getError(),
                        "Second lock acquisition should fail.");
            }
        }
    }

    @Test
    public void testExceptionInClose() throws JavetException {
        V8Locker v8Locker = v8Runtime.getV8Locker();
        assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
        v8Locker.close();
        assertTrue(v8Locker.isClosed());
        try {
            v8Locker.close();
        } catch (JavetException e) {
            assertEquals(JavetError.LockReleaseFailure, e.getError(),
                    "Second lock release should fail.");
        }
    }
}
