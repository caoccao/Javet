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

package com.caoccao.javet.interception.logging;

import com.caoccao.javet.BaseTestJavetRuntime;
import com.caoccao.javet.exceptions.JavetException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestJavetStandardConsoleInterceptor extends BaseTestJavetRuntime {
    protected static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Test
    public void test() throws IOException, JavetException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            try (PrintStream printStream = new PrintStream(byteArrayOutputStream)) {
                JavetStandardConsoleInterceptor interceptor =
                        new JavetStandardConsoleInterceptor(v8Runtime);
                interceptor.setDebug(printStream);
                interceptor.setError(printStream);
                interceptor.setInfo(printStream);
                interceptor.setLog(printStream);
                interceptor.setTrace(printStream);
                interceptor.setWarn(printStream);
                interceptor.register(v8Runtime.getGlobalObject());
                v8Runtime.getExecutor("console.debug('debug');").executeVoid();
                v8Runtime.getExecutor("console.error('error');").executeVoid();
                v8Runtime.getExecutor("console.info('info');").executeVoid();
                v8Runtime.getExecutor("console.log('log');").executeVoid();
                v8Runtime.getExecutor("console.trace('trace');").executeVoid();
                v8Runtime.getExecutor("console.warn('warn');").executeVoid();
                interceptor.unregister(v8Runtime.getGlobalObject());
                assertEquals(
                        String.join(LINE_SEPARATOR, "debug", "error", "info", "log", "trace", "warn"),
                        byteArrayOutputStream.toString(StandardCharsets.UTF_8.name()).trim());
            }
        }
        v8Runtime.lowMemoryNotification();
    }
}
