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
import com.caoccao.javet.enums.V8GCCallbackFlags;
import com.caoccao.javet.enums.V8GCType;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interop.callback.IJavetGCCallback;
import com.caoccao.javet.interop.options.RuntimeOptions;
import com.caoccao.javet.interop.options.V8RuntimeOptions;
import com.caoccao.javet.utils.SimpleList;
import com.caoccao.javet.values.reference.V8ValueObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Runtime extends BaseTestJavet {
    @Test
    public void testAllowEval() throws JavetException {
        List<String> codeStrings = SimpleList.of(
                "(() => eval('1'))()",
                "(() => Function('return 1')())()");
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.allowEval(true);
            for (String codeString : codeStrings) {
                assertEquals(1, v8Runtime.getExecutor(codeString).executeInteger());
            }
            v8Runtime.allowEval(false);
            try {
                for (String codeString : codeStrings) {
                    v8Runtime.getExecutor(codeString).executeInteger();
                }
                fail("Failed to disallow eval().");
            } catch (JavetExecutionException e) {
                assertEquals(
                        "EvalError: Code generation from strings disallowed for this context",
                        e.getScriptingError().getDetailedMessage());
            }
        }
    }

    @Test
    public void testClose() throws JavetException {
        V8Runtime danglingV8Runtime;
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            assertFalse(v8Runtime.isClosed());
            danglingV8Runtime = v8Runtime;
        }
        assertTrue(danglingV8Runtime.isClosed());
    }

    @Test
    public void testExecuteScript() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.getExecutor("var a = 1;").executeVoid();
            assertEquals(2, v8Runtime.getExecutor("a + 1").executeInteger());
        }
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.getExecutor("var a = 1;").executeVoid();
            assertEquals(2, v8Runtime.getExecutor("a + 1").executeInteger());
            try (V8ValueObject window = v8Runtime.createV8ValueObject()) {
                v8Runtime.getGlobalObject().set("window", window);
                window.set("x", "1");
            }
        }
    }

    @Test
    public void testGCCallback() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            List<EnumSet<V8GCType>> gcEpilogueCallbackV8GCTypeEnumSets = new ArrayList<>();
            List<EnumSet<V8GCCallbackFlags>> gcEpilogueCallbackV8GCCallbackFlagsEnumSets = new ArrayList<>();
            IJavetGCCallback gcEpilogueCallback = (v8GCTypeEnumSet, v8GCCallbackFlagsEnumSet) -> {
                gcEpilogueCallbackV8GCTypeEnumSets.add(v8GCTypeEnumSet);
                gcEpilogueCallbackV8GCCallbackFlagsEnumSets.add(v8GCCallbackFlagsEnumSet);
            };
            List<EnumSet<V8GCType>> gcPrologueCallbackV8GCTypeEnumSets = new ArrayList<>();
            List<EnumSet<V8GCCallbackFlags>> gcPrologueCallbackV8GCCallbackFlagsEnumSets = new ArrayList<>();
            IJavetGCCallback gcPrologueCallback = (v8GCTypeEnumSet, v8GCCallbackFlagsEnumSet) -> {
                gcPrologueCallbackV8GCTypeEnumSets.add(v8GCTypeEnumSet);
                gcPrologueCallbackV8GCCallbackFlagsEnumSets.add(v8GCCallbackFlagsEnumSet);
            };
            v8Runtime.addGCEpilogueCallback(gcEpilogueCallback);
            v8Runtime.addGCPrologueCallback(gcPrologueCallback);
            v8Runtime.getGlobalObject().set("a", 1);
            v8Runtime.getGlobalObject().delete("a");
            v8Runtime.lowMemoryNotification();
            assertEquals(2, gcEpilogueCallbackV8GCTypeEnumSets.size());
            assertEquals(2, gcEpilogueCallbackV8GCCallbackFlagsEnumSets.size());
            assertEquals(2, gcPrologueCallbackV8GCTypeEnumSets.size());
            assertEquals(2, gcPrologueCallbackV8GCCallbackFlagsEnumSets.size());
            v8Runtime.removeGCEpilogueCallback(gcEpilogueCallback);
            v8Runtime.removeGCPrologueCallback(gcPrologueCallback);
        }
    }

    @Test
    public void testGlobalName() throws JavetException {
        if (v8Host.getJSRuntimeType().isV8()) {
            V8RuntimeOptions runtimeOptions = v8Host.getJSRuntimeType().getRuntimeOptions();
            runtimeOptions.setGlobalName("window");
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(runtimeOptions)) {
                assertFalse(v8Runtime.getExecutor("typeof window == 'undefined';").executeBoolean());
                assertFalse(v8Runtime.getExecutor("typeof globalThis == 'undefined';").executeBoolean());
                v8Runtime.getExecutor("var a = 1;").executeVoid();
                assertEquals(1, v8Runtime.getExecutor("window.a;").executeInteger());
                v8Runtime.getExecutor("window.b = 2").executeVoid();
                v8Runtime.getExecutor("globalThis.b = 2").executeVoid();
                assertEquals(2, v8Runtime.getGlobalObject().getInteger("b"));
                runtimeOptions.setGlobalName("globalThis");
                v8Runtime.resetContext();
                assertFalse(v8Runtime.getExecutor("typeof globalThis == 'undefined';").executeBoolean());
                v8Runtime.getExecutor("var a = 1;").executeVoid();
                assertEquals(1, v8Runtime.getExecutor("globalThis.a;").executeInteger());
                v8Runtime.getExecutor("globalThis.b = 2").executeVoid();
                assertEquals(2, v8Runtime.getGlobalObject().getInteger("b"));
                runtimeOptions.setGlobalName(null);
                v8Runtime.resetContext();
                assertTrue(v8Runtime.getExecutor("typeof window == 'undefined';").executeBoolean());
                assertTrue(v8Runtime.getExecutor("typeof global == 'undefined';").executeBoolean());
                assertFalse(v8Runtime.getExecutor("typeof globalThis == 'undefined';").executeBoolean());
            }
        }
    }

    @Test
    public void testIdleNotificationDeadline() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.idleNotificationDeadline(1000);
        }
    }

    @Test
    public void testLowMemoryNotification() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testOutOfMemoryException() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            // The following line triggers the OOM which will let JVM to core dump.
            // v8Runtime.getExecutor("let a = [... new Array (1000000000). keys()];").executeVoid();
        }
    }

    @Test
    public void testPending() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            assertFalse(v8Runtime.hasPendingException());
            assertFalse(v8Runtime.hasPendingMessage());
            assertFalse(v8Runtime.hasScheduledException());
            assertFalse(v8Runtime.promoteScheduledException());
            assertFalse(v8Runtime.reportPendingMessages());
        }
    }

    @Test
    public void testResetContext() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            v8Runtime.getGlobalObject().set("a", "1");
            v8Runtime.resetContext();
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            assertTrue(v8Runtime.getGlobalObject().get("a").isUndefined());
        }
    }

    @Test
    public void testResetIsolate() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            v8Runtime.getGlobalObject().set("a", "1");
            v8Runtime.resetIsolate();
            assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            assertTrue(v8Runtime.getGlobalObject().get("a").isUndefined());
        }
    }

    @Test
    public void testSnapshot() throws JavetException {
        if (v8Host.getJSRuntimeType().isV8()) {
            RuntimeOptions<?> options = v8Host.getJSRuntimeType().getRuntimeOptions();
            // Set create snapshot enabled.
            options.setCreateSnapshotEnabled(true);
            byte[] snapshotBlob1;
            byte[] snapshotBlob2;
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                // Prepare function add.
                v8Runtime.getExecutor("const add = (a, b) => a + b;").executeVoid();
                assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
                // Create a snapshot with function add.
                snapshotBlob1 = v8Runtime.createSnapshot();
                assertNotNull(snapshotBlob1);
                assertTrue(snapshotBlob1.length > 0);
                // Test the runtime is still usable after the snapshot is created.
                assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
            }
            // Set the snapshot blob.
            options.setSnapshotBlob(snapshotBlob1);
            for (int i = 0; i < 5; ++i) {
                // Create a new V8 runtime with the snapshot 5 times.
                try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                    // Test the function add.
                    assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
                }
            }
            // Create a new V8 runtime with the snapshot.
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                // Test the function add.
                assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
                // Prepare function subtract.
                v8Runtime.getExecutor("const subtract = (a, b) => a - b;").executeVoid();
                // Create a new snapshot with function add and subtract.
                snapshotBlob2 = v8Runtime.createSnapshot();
                assertNotNull(snapshotBlob2);
                assertTrue(snapshotBlob2.length > 0);
            }
            // Set the new snapshot blob.
            options.setSnapshotBlob(snapshotBlob2);
            for (int i = 0; i < 5; ++i) {
                // Create a new V8 runtime with the snapshot 5 times.
                try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                    // Test the function add and subtract.
                    assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
                    assertEquals(1, v8Runtime.getExecutor("subtract(3, 2)").executeInteger());
                }
            }
            options.setCreateSnapshotEnabled(false).setSnapshotBlob(null);
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                v8Runtime.createSnapshot();
                fail("Failed to report create snapshot disabled");
            } catch (JavetException e) {
                assertEquals(JavetError.RuntimeCreateSnapshotDisabled.getCode(), e.getError().getCode());
            }
        }
    }
}
