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

import com.caoccao.javet.BaseTestJavet;
import com.caoccao.javet.enums.JSRuntimeType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestV8Native extends BaseTestJavet {
    protected IV8Native v8Native;

    public TestV8Native() {
        super();
        v8Native = v8Host.getV8Native();
    }

    @Test
    public void testGetVersion() {
        assertEquals(JSRuntimeType.Node.getVersion(), V8Host.getNodeInstance().getV8Native().getVersion());
        assertEquals(JSRuntimeType.V8.getVersion(), V8Host.getV8Instance().getV8Native().getVersion());
        Arrays.stream(JSRuntimeType.values()).forEach(jsRuntimeType -> assertEquals(
                jsRuntimeType.getVersion(),
                Objects.requireNonNull(V8Host.getInstance(jsRuntimeType)).getV8Native().getVersion()));
    }

    @Test
    public void testLockAndUnlock() {
        final long handle = v8Native.createV8Runtime(JSRuntimeType.V8.getRuntimeOptions());
        try {
            final int iterations = 3;
            for (int i = 0; i < iterations; ++i) {
                assertFalse(v8Native.unlockV8Runtime(handle), "It should not allow unlock before lock.");
                v8Native.lockV8Runtime(handle);
                assertFalse(v8Native.lockV8Runtime(handle), "It should not allow double lock.");
                v8Native.unlockV8Runtime(handle);
            }
        } finally {
            v8Native.closeV8Runtime(handle);
        }
    }
}
