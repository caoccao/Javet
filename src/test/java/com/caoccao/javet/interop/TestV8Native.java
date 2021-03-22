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
import com.caoccao.javet.exceptions.JavetV8LockConflictException;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Native extends BaseTestJavet {
    @Test
    public void testGetVersion() {
        String versionString = V8Native.getVersion();
        if (JavetLibLoader.getJSRuntimeType().isNode()) {
            assertEquals(JSRuntimeType.Node.getVersion(), versionString);
        } else if (JavetLibLoader.getJSRuntimeType().isV8()) {
            assertEquals(JSRuntimeType.V8.getVersion(), versionString);
        } else {
            fail(MessageFormat.format(
                    "JS runtime type {0} is not supported.",
                    JavetLibLoader.getJSRuntimeType().getName()));
        }
    }

    @Test
    public void testLockAndUnlock() {
        final long handle = V8Native.createV8Runtime(null);
        try {
            final int iterations = 3;
            for (int i = 0; i < iterations; ++i) {
                assertThrows(JavetV8LockConflictException.class, () -> {
                    V8Native.unlockV8Runtime(handle);
                }, "It should not allow unlock before lock.");
                V8Native.lockV8Runtime(handle);
                assertThrows(JavetV8LockConflictException.class, () -> {
                    V8Native.lockV8Runtime(handle);
                }, "It should not allow double lock.");
                V8Native.unlockV8Runtime(handle);
            }
        } finally {
            V8Native.closeV8Runtime(handle);
        }
    }
}
