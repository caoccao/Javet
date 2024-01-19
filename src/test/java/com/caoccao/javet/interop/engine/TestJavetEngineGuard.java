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

package com.caoccao.javet.interop.engine;

import com.caoccao.javet.BaseTestJavetPool;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetTerminatedException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.utils.JavetDateTimeUtils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TestJavetEngineGuard extends BaseTestJavetPool {
    @Test
    public void testTermination() throws JavetException {
        // Get an engine from the pool as usual.
        try (IJavetEngine<?> iJavetEngine = javetEnginePool.getEngine()) {
            V8Runtime v8Runtime = iJavetEngine.getV8Runtime();
            // Get a guard from the engine and apply try-with-resource pattern.
            try (IJavetEngineGuard iJavetEngineGuard = iJavetEngine.getGuard(1)) {
                iJavetEngineGuard.enableInDebugMode();
                v8Runtime.getExecutor("while (true) {}").executeVoid();
                // That infinite loop will be terminated in 10 seconds by the guard.
            } catch (JavetTerminatedException e) {
                // JavetTerminatedException will be thrown to mark that.
                assertFalse(e.isContinuable());
            }
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger(),
                    "The V8 runtime is not dead and is still able to execute code afterwards.");
        }
    }

    @Test
    public void testWithoutTermination() throws JavetException {
        final long timeoutMillis = 10000;
        ZonedDateTime startZonedDateTime = JavetDateTimeUtils.getUTCNow();
        try (IJavetEngine<?> iJavetEngine = javetEnginePool.getEngine()) {
            try (IJavetEngineGuard iJavetEngineGuard = iJavetEngine.getGuard(timeoutMillis)) {
                V8Runtime v8Runtime = iJavetEngine.getV8Runtime();
                assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            }
        }
        ZonedDateTime endZonedDateTime = JavetDateTimeUtils.getUTCNow();
        Duration duration = Duration.between(startZonedDateTime, endZonedDateTime);
        assertTrue(duration.toMillis() < timeoutMillis);
    }
}
