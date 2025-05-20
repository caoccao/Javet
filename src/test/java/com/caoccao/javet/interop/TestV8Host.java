/*
 * Copyright (c) 2021-2025. caoccao.com Sam Cao
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
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.options.V8RuntimeOptions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Host extends BaseTestJavet {
    @Test
    public void testAllRuntimes() {
        Stream.of(JSRuntimeType.values())
                .filter(type -> {
                    File libFile = BaseTestJavet.getLibFile(type);
                    return libFile != null && libFile.exists();
                })
                .forEach(type -> {
                    try (V8Runtime v8Runtime = V8Host.getInstance(type).createV8Runtime()) {
                        assertNotNull(v8Runtime);
                        assertEquals(type, v8Runtime.getJSRuntimeType());
                    } catch (JavetException e) {
                        fail(e);
                    }
                });
    }

    @Test
    public void testCreateV8RuntimeWithGlobalName() throws JavetException {
        if (isV8()) {
            V8RuntimeOptions runtimeOptions = v8Host.getJSRuntimeType().getRuntimeOptions();
            runtimeOptions.setGlobalName("window");
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(runtimeOptions)) {
                assertNotNull(v8Runtime);
                assertTrue(v8Host.isIsolateCreated());
            }
        }
    }

    @Test
    public void testCreateV8RuntimeWithoutGlobalName() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            assertNotNull(v8Runtime);
            assertTrue(v8Host.isIsolateCreated());
        }
    }

    @Test
    public void testLogJSRuntimeType() {
        JSRuntimeType jsRuntimeType = v8Host.getJSRuntimeType();
        logger.logInfo("JS runtime type is {0}, version is {1}.",
                jsRuntimeType.getName(), jsRuntimeType.getVersion());
    }
}
