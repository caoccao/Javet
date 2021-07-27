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
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.values.V8Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestJavetProxyConverter extends BaseTestJavetRuntime {
    protected JavetProxyConverter javetProxyConverter;

    public TestJavetProxyConverter() {
        super();
        javetProxyConverter = new JavetProxyConverter();
    }

    @BeforeEach
    @Override
    public void beforeEach() throws JavetException {
        super.beforeEach();
        v8Runtime.setConverter(javetProxyConverter);
    }

    @Test
    public void testFile() throws JavetException {
        File file = new File("/tmp/i-am-not-accessible");
        try (V8Value v8Value = v8Runtime.toV8Value(file)) {
            v8Runtime.getGlobalObject().set("file", v8Value);
        }
        try (V8Value v8Value = v8Runtime.getGlobalObject().get("file")) {
            assertEquals(file, v8Runtime.toObject(v8Value));
        }
        assertEquals(file.exists(), v8Runtime.getExecutor("file.exists()").executeBoolean());
        assertEquals(file.isFile(), v8Runtime.getExecutor("file.isFile()").executeBoolean());
        assertEquals(file.isDirectory(), v8Runtime.getExecutor("file.isDirectory()").executeBoolean());
        assertEquals(file.canRead(), v8Runtime.getExecutor("file.canRead()").executeBoolean());
        assertEquals(file.canWrite(), v8Runtime.getExecutor("file.canWrite()").executeBoolean());
        assertEquals(file.canExecute(), v8Runtime.getExecutor("file.canExecute()").executeBoolean());
        v8Runtime.getGlobalObject().delete("file");
        v8Runtime.lowMemoryNotification();
    }

    @Test
    public void testPath() throws JavetException {
        Path path = new File("/tmp/i-am-not-accessible").toPath();
        try (V8Value v8Value = v8Runtime.toV8Value(path)) {
            v8Runtime.getGlobalObject().set("path", v8Value);
        }
        try (V8Value v8Value = v8Runtime.getGlobalObject().get("path")) {
            assertEquals(path, v8Runtime.toObject(v8Value));
        }
        assertEquals(path.toString(), v8Runtime.getExecutor("path.toString()").executeString());
        Path newPath = v8Runtime.toObject(v8Runtime.getExecutor("path.resolve('abc')").execute(), true);
        assertNotNull(newPath);
        assertEquals(path.resolve("abc").toString(), newPath.toString());
        assertEquals(
                path.resolve("abc").toString(),
                v8Runtime.getExecutor("path.resolve('abc').toString()").executeString());
        v8Runtime.getGlobalObject().delete("path");
        v8Runtime.lowMemoryNotification();
    }
}
