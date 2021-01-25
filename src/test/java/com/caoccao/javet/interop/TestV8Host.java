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

import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestV8Host {
    @Test
    public void testCreateV8RuntimeWithoutGlobalName() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            assertNotNull(v8Runtime);
            assertTrue(v8Host.isIsolateCreated());
        }
    }

    @Test
    public void testCreateV8RuntimeWithGlobalName() throws JavetException {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            assertNotNull(v8Runtime);
            assertTrue(v8Host.isIsolateCreated());
        }
    }

    @Test
    public void testSetFlags() {
        V8Host v8Host = V8Host.getInstance();
        assertNotNull(v8Host);
        assertTrue(v8Host.isLibLoaded());
        if (!v8Host.isIsolateCreated()) {
            v8Host.setFlags("--use_strict");
        }
    }
}
