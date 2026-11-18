/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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
import com.caoccao.javet.enums.V8MicrotasksPolicy;
import com.caoccao.javet.enums.V8RuntimeTerminationMode;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetExecutionException;
import com.caoccao.javet.interop.callback.IJavetGCCallback;
import com.caoccao.javet.interop.callback.IJavetMicrotasksCompletedCallback;
import com.caoccao.javet.interop.callback.IJavetNearHeapLimitCallback;
import com.caoccao.javet.interop.options.RuntimeOptions;
import com.caoccao.javet.interop.options.V8RuntimeOptions;
import com.caoccao.javet.mock.MockNearHeapLimitCallback;
import com.caoccao.javet.utils.SimpleList;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8ValuePromise;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.caoccao.javet.values.reference.V8ValuePromise;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Runtime extends BaseTestJavet {
    /**
     * Assert the reset removes the GC callbacks and the microtasks completed callbacks,
     * and the callbacks added after the reset are registered to the native isolate again.
     *
     * @param v8Runtime    the V8 runtime
     * @param isolateReset true : reset the isolate, false : reset the context
     */
    private void assertResetRemovesCallbacks(V8Runtime v8Runtime, boolean isolateReset) throws JavetException {
        AtomicInteger gcEpilogueCounter1 = new AtomicInteger();
        AtomicInteger gcEpilogueCounter2 = new AtomicInteger();
        AtomicInteger gcPrologueCounter1 = new AtomicInteger();
        AtomicInteger gcPrologueCounter2 = new AtomicInteger();
        AtomicInteger microtasksCompletedCounter1 = new AtomicInteger();
        AtomicInteger microtasksCompletedCounter2 = new AtomicInteger();
        IJavetGCCallback gcEpilogueCallback1 = (v8GCTypes, v8GCCallbackFlags) -> gcEpilogueCounter1.incrementAndGet();
        IJavetGCCallback gcEpilogueCallback2 = (v8GCTypes, v8GCCallbackFlags) -> gcEpilogueCounter2.incrementAndGet();
        IJavetGCCallback gcPrologueCallback1 = (v8GCTypes, v8GCCallbackFlags) -> gcPrologueCounter1.incrementAndGet();
        IJavetGCCallback gcPrologueCallback2 = (v8GCTypes, v8GCCallbackFlags) -> gcPrologueCounter2.incrementAndGet();
        IJavetMicrotasksCompletedCallback microtasksCompletedCallback1 = microtasksCompletedCounter1::incrementAndGet;
        IJavetMicrotasksCompletedCallback microtasksCompletedCallback2 = microtasksCompletedCounter2::incrementAndGet;
        // The callbacks are called before the reset.
        v8Runtime.addGCEpilogueCallback(gcEpilogueCallback1);
        v8Runtime.addGCPrologueCallback(gcPrologueCallback1);
        v8Runtime.addMicrotasksCompletedCallback(microtasksCompletedCallback1);
        v8Runtime.lowMemoryNotification();
        v8Runtime.performMicrotaskCheckpoint();
        assertTrue(gcEpilogueCounter1.get() > 0);
        assertTrue(gcPrologueCounter1.get() > 0);
        assertTrue(microtasksCompletedCounter1.get() > 0);
        // The reset removes the callbacks.
        if (isolateReset) {
            v8Runtime.resetIsolate();
        } else {
            v8Runtime.resetContext();
        }
        assertTrue(v8Runtime.gcEpilogueCallbacks.isEmpty());
        assertTrue(v8Runtime.gcPrologueCallbacks.isEmpty());
        assertTrue(v8Runtime.microtasksCompletedCallbacks.isEmpty());
        final int gcEpilogueCount1 = gcEpilogueCounter1.get();
        final int gcPrologueCount1 = gcPrologueCounter1.get();
        final int microtasksCompletedCount1 = microtasksCompletedCounter1.get();
        v8Runtime.lowMemoryNotification();
        v8Runtime.performMicrotaskCheckpoint();
        assertEquals(gcEpilogueCount1, gcEpilogueCounter1.get());
        assertEquals(gcPrologueCount1, gcPrologueCounter1.get());
        assertEquals(microtasksCompletedCount1, microtasksCompletedCounter1.get());
        // The callbacks added after the reset are called.
        v8Runtime.addGCEpilogueCallback(gcEpilogueCallback2);
        v8Runtime.addGCPrologueCallback(gcPrologueCallback2);
        v8Runtime.addMicrotasksCompletedCallback(microtasksCompletedCallback2);
        v8Runtime.lowMemoryNotification();
        v8Runtime.performMicrotaskCheckpoint();
        assertTrue(gcEpilogueCounter2.get() > 0);
        assertTrue(gcPrologueCounter2.get() > 0);
        assertTrue(microtasksCompletedCounter2.get() > 0);
        assertEquals(gcEpilogueCount1, gcEpilogueCounter1.get());
        assertEquals(gcPrologueCount1, gcPrologueCounter1.get());
        assertEquals(microtasksCompletedCount1, microtasksCompletedCounter1.get());
        // The callbacks added after the reset can be removed.
        v8Runtime.removeGCEpilogueCallback(gcEpilogueCallback2);
        v8Runtime.removeGCPrologueCallback(gcPrologueCallback2);
        v8Runtime.removeMicrotasksCompletedCallback(microtasksCompletedCallback2);
        final int gcEpilogueCount2 = gcEpilogueCounter2.get();
        final int gcPrologueCount2 = gcPrologueCounter2.get();
        final int microtasksCompletedCount2 = microtasksCompletedCounter2.get();
        v8Runtime.lowMemoryNotification();
        v8Runtime.performMicrotaskCheckpoint();
        assertEquals(gcEpilogueCount2, gcEpilogueCounter2.get());
        assertEquals(gcPrologueCount2, gcPrologueCounter2.get());
        assertEquals(microtasksCompletedCount2, microtasksCompletedCounter2.get());
    }

    /**
     * Create Node.js snapshot blobs that Node.js rejects without aborting the process.
     *
     * @param snapshotBlob the valid snapshot blob
     * @return the invalid snapshot blobs by description
     */
    private Map<String, byte[]> createInvalidNodeSnapshotBlobs(byte[] snapshotBlob) throws JavetException {
        String nodeVersion;
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            nodeVersion = v8Runtime.getExecutor("process.versions.node").executeString();
        }
        Map<String, byte[]> invalidSnapshotBlobs = new LinkedHashMap<>();
        // An empty blob is treated as no blob by RuntimeOptions, so the shortest blob is used.
        invalidSnapshotBlobs.put("a too short blob", new byte[]{1, 2, 3});
        byte[] invalidMagicSnapshotBlob = snapshotBlob.clone();
        invalidMagicSnapshotBlob[0] = (byte) ~invalidMagicSnapshotBlob[0];
        invalidSnapshotBlobs.put("invalid magic bytes", invalidMagicSnapshotBlob);
        invalidSnapshotBlobs.put("a truncated blob", Arrays.copyOf(snapshotBlob, snapshotBlob.length / 2));
        // The Node.js version is stored in the metadata at the beginning of the blob.
        byte[] nodeVersionBytes = nodeVersion.getBytes(StandardCharsets.US_ASCII);
        final int lastSearchIndex = Math.min(64, snapshotBlob.length - nodeVersionBytes.length);
        int nodeVersionIndex = -1;
        for (int i = 0; i <= lastSearchIndex && nodeVersionIndex < 0; ++i) {
            if (Arrays.equals(nodeVersionBytes, Arrays.copyOfRange(snapshotBlob, i, i + nodeVersionBytes.length))) {
                nodeVersionIndex = i;
            }
        }
        assertTrue(nodeVersionIndex >= 0, "Node.js version " + nodeVersion + " is not found in the snapshot metadata.");
        byte[] incompatibleSnapshotBlob = snapshotBlob.clone();
        incompatibleSnapshotBlob[nodeVersionIndex] = (byte) (incompatibleSnapshotBlob[nodeVersionIndex] == '9' ? '8' : '9');
        invalidSnapshotBlobs.put("another Node.js version", incompatibleSnapshotBlob);
        return invalidSnapshotBlobs;
    }

    /**
     * Create a Node.js snapshot blob with function add.
     *
     * @return the snapshot blob
     */
    private byte[] createNodeSnapshotBlob() throws JavetException {
        RuntimeOptions<?> options = v8Host.getJSRuntimeType().getRuntimeOptions();
        options.setCreateSnapshotEnabled(true);
        try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
            v8Runtime.getExecutor("const add = (a, b) => a + b;").executeVoid();
            byte[] snapshotBlob = v8Runtime.createSnapshot();
            assertNotNull(snapshotBlob);
            assertTrue(snapshotBlob.length > 0);
            return snapshotBlob;
        }
    }

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
        if (isV8()) {
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
    public void testLowMemoryNotification() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.lowMemoryNotification();
        }
    }

    @Test
    public void testMicrotasksCompletedCallback() throws JavetException {
        final String pendJob = "globalThis.a = 0; Promise.resolve().then(() => { globalThis.a = 1; });";
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            AtomicInteger counter1 = new AtomicInteger();
            AtomicInteger counter2 = new AtomicInteger();
            IJavetMicrotasksCompletedCallback callback1 = counter1::incrementAndGet;
            IJavetMicrotasksCompletedCallback callback2 = counter2::incrementAndGet;
            if (isV8()) {
                // Explicit makes the checkpoints, and therefore the callbacks, deterministic.
                v8Runtime.setMicrotasksPolicy(V8MicrotasksPolicy.Explicit);
                v8Runtime.addMicrotasksCompletedCallback(callback1);
                assertEquals(0, counter1.get());

                // The callback is triggered even if the microtask queue is empty.
                v8Runtime.performMicrotaskCheckpoint();
                assertEquals(1, counter1.get());

                v8Runtime.getExecutor(pendJob).executeVoid();
                assertEquals(
                        1, counter1.get(),
                        "The script execution is not supposed to trigger a checkpoint under Explicit.");
                v8Runtime.performMicrotaskCheckpoint();
                assertEquals(2, counter1.get());
                assertEquals(1, v8Runtime.getExecutor("globalThis.a").executeInteger());

                // await() performs a checkpoint in the V8 mode.
                v8Runtime.await();
                assertEquals(3, counter1.get());

                // Every registered callback is invoked.
                v8Runtime.addMicrotasksCompletedCallback(callback2);
                v8Runtime.performMicrotaskCheckpoint();
                assertEquals(4, counter1.get());
                assertEquals(1, counter2.get());

                // Removing one callback leaves the other one registered.
                v8Runtime.removeMicrotasksCompletedCallback(callback1);
                v8Runtime.performMicrotaskCheckpoint();
                assertEquals(4, counter1.get());
                assertEquals(2, counter2.get());

                // Removing the last callback unregisters the native callback.
                v8Runtime.removeMicrotasksCompletedCallback(callback2);
                v8Runtime.performMicrotaskCheckpoint();
                assertEquals(4, counter1.get());
                assertEquals(2, counter2.get());

                v8Runtime.setMicrotasksPolicy(V8MicrotasksPolicy.Auto);
            } else {
                // Node.js drives the checkpoints itself, so only the invocation is asserted.
                v8Runtime.addMicrotasksCompletedCallback(callback1);
                v8Runtime.getExecutor(pendJob).executeVoid();
                v8Runtime.await();
                assertEquals(1, v8Runtime.getExecutor("globalThis.a").executeInteger());
                assertTrue(counter1.get() > 0, "The callback is supposed to be invoked.");
                v8Runtime.removeMicrotasksCompletedCallback(callback1);
                final int count = counter1.get();
                v8Runtime.getExecutor(pendJob).executeVoid();
                v8Runtime.await();
                assertEquals(count, counter1.get(), "The callback is supposed to be removed.");
            }
            assertThrows(NullPointerException.class, () -> v8Runtime.addMicrotasksCompletedCallback(null));
            assertThrows(NullPointerException.class, () -> v8Runtime.removeMicrotasksCompletedCallback(null));
        }
    }

    @Test
    public void testMicrotasksDiagnostics() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            // Nothing is running outside a checkpoint.
            assertFalse(v8Runtime.isRunningMicrotasks());
            assertEquals(0, v8Runtime.getMicrotasksScopeDepth());

            // V8 fires the microtasks completed callback before it leaves the checkpoint.
            AtomicBoolean runningInCallback = new AtomicBoolean();
            AtomicInteger depthInCallback = new AtomicInteger(-1);
            IJavetMicrotasksCompletedCallback callback = () -> {
                runningInCallback.set(v8Runtime.isRunningMicrotasks());
                depthInCallback.set(v8Runtime.getMicrotasksScopeDepth());
            };
            v8Runtime.addMicrotasksCompletedCallback(callback);
            try {
                v8Runtime.performMicrotaskCheckpoint();
            } finally {
                v8Runtime.removeMicrotasksCompletedCallback(callback);
            }
            assertTrue(runningInCallback.get(), "The microtasks are supposed to be running in the callback.");
            assertEquals(0, depthInCallback.get(), "Javet never creates a v8::MicrotasksScope.");
            assertFalse(v8Runtime.isRunningMicrotasks());

            // A promise job that calls back into Java observes the same state.
            AtomicBoolean runningInJob = new AtomicBoolean();
            try (V8ValuePromise v8ValuePromiseResolver = v8Runtime.createV8ValuePromise();
                 V8ValuePromise v8ValuePromise = v8ValuePromiseResolver.getPromise()) {
                assertTrue(v8ValuePromise.register(new IV8ValuePromise.IListener() {
                    @Override
                    public void onCatch(V8Value v8Value) {
                    }

                    @Override
                    public void onFulfilled(V8Value v8Value) {
                        runningInJob.set(v8Runtime.isRunningMicrotasks());
                    }

                    @Override
                    public void onRejected(V8Value v8Value) {
                    }
                }));
                assertTrue(v8ValuePromiseResolver.resolve(1));
                v8Runtime.await();
                assertTrue(runningInJob.get(), "The microtasks are supposed to be running in the promise job.");
            } finally {
                v8Runtime.lowMemoryNotification();
            }
            assertFalse(v8Runtime.isRunningMicrotasks());
        }
    }

    @Test
    public void testMicrotasksPolicy() throws JavetException {
        final String codeString = "globalThis.a = 0; Promise.resolve().then(() => { globalThis.a = 1; });";
        if (isV8()) {
            try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
                assertEquals(V8MicrotasksPolicy.Auto, v8Runtime.getMicrotasksPolicy());
                // Under Auto, V8 drains the queue when the JS call depth drops to zero.
                v8Runtime.getExecutor(codeString).executeVoid();
                assertEquals(1, v8Runtime.getExecutor("globalThis.a").executeInteger());

                v8Runtime.setMicrotasksPolicy(V8MicrotasksPolicy.Explicit);
                assertEquals(V8MicrotasksPolicy.Explicit, v8Runtime.getMicrotasksPolicy());
                v8Runtime.getExecutor(codeString).executeVoid();
                assertEquals(
                        0, v8Runtime.getExecutor("globalThis.a").executeInteger(),
                        "The promise job is supposed to stay pending under Explicit.");
                v8Runtime.performMicrotaskCheckpoint();
                assertEquals(1, v8Runtime.getExecutor("globalThis.a").executeInteger());

                // await() drains the microtask queue in the V8 mode as well.
                v8Runtime.getExecutor(codeString).executeVoid();
                assertEquals(0, v8Runtime.getExecutor("globalThis.a").executeInteger());
                assertFalse(v8Runtime.await());
                assertEquals(1, v8Runtime.getExecutor("globalThis.a").executeInteger());

                // A promise resolved from Java stays pending under Explicit too.
                try (V8ValuePromise v8ValuePromiseResolver = v8Runtime.createV8ValuePromise();
                     V8ValuePromise v8ValuePromise = v8ValuePromiseResolver.getPromise();
                     V8ValueObject v8ValueGlobalObject = v8Runtime.getGlobalObject()) {
                    v8ValueGlobalObject.set("p", v8ValuePromise);
                    v8Runtime.getExecutor("globalThis.b = 0; p.then(() => { globalThis.b = 1; });").executeVoid();
                    assertTrue(v8ValuePromiseResolver.resolve(1));
                    assertEquals(0, v8Runtime.getExecutor("globalThis.b").executeInteger());
                    v8Runtime.performMicrotaskCheckpoint();
                    assertEquals(1, v8Runtime.getExecutor("globalThis.b").executeInteger());
                    v8ValueGlobalObject.delete("p");
                }

                v8Runtime.setMicrotasksPolicy(V8MicrotasksPolicy.Auto);
                assertEquals(V8MicrotasksPolicy.Auto, v8Runtime.getMicrotasksPolicy());
            }
        } else {
            try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
                // Node.js sets the policy to Explicit and drains the queue by itself.
                assertEquals(V8MicrotasksPolicy.Explicit, v8Runtime.getMicrotasksPolicy());
                v8Runtime.getExecutor(codeString).executeVoid();
                v8Runtime.await();
                assertEquals(1, v8Runtime.getExecutor("globalThis.a").executeInteger());
            }
        }
    }

    @Test
    public void testMicrotasksPolicyNotSupported() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            // Scoped requires a v8::MicrotasksScope which Javet never creates.
            // In the Node.js mode the policy belongs to Node.js, so every value is rejected.
            List<V8MicrotasksPolicy> rejectedPolicies = isV8()
                    ? SimpleList.of(V8MicrotasksPolicy.Scoped)
                    : SimpleList.of(V8MicrotasksPolicy.values());
            for (V8MicrotasksPolicy microtasksPolicy : rejectedPolicies) {
                JavetException javetException = assertThrows(
                        JavetException.class,
                        () -> v8Runtime.setMicrotasksPolicy(microtasksPolicy),
                        microtasksPolicy.getName() + " is supposed to be rejected.");
                assertEquals(JavetError.NotSupported.getCode(), javetException.getError().getCode());
            }
            assertThrows(NullPointerException.class, () -> v8Runtime.setMicrotasksPolicy(null));
        }
    }

    @Test
    public void testNearHeapLimitCallbackWithIncreasingHeapLimit() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            assertNull(v8Runtime.getNearHeapLimitCallback());
            MockNearHeapLimitCallback callback = new MockNearHeapLimitCallback();
            v8Runtime.setNearHeapLimitCallback(callback);
            assertEquals(callback, v8Runtime.getNearHeapLimitCallback());
            v8Runtime.getExecutor("[[... new Array(50000000).keys()],[... new Array(50000000).keys()],[... new Array(50000000).keys()],[... new Array(50000000).keys()]]").executeVoid();
            v8Runtime.setNearHeapLimitCallback(null);
            assertTrue(callback.isCallbackCalled());
            assertTrue(callback.isGetDefaultHeapLimitCalled());
            assertNull(v8Runtime.getNearHeapLimitCallback());
        }
    }

    @Test
    public void testNearHeapLimitCallbackWithReplacement() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            MockNearHeapLimitCallback callback1 = new MockNearHeapLimitCallback();
            MockNearHeapLimitCallback callback2 = new MockNearHeapLimitCallback();
            v8Runtime.setNearHeapLimitCallback(callback1);
            // V8 aborts the process if the native callback is registered again.
            v8Runtime.setNearHeapLimitCallback(callback2);
            assertEquals(callback2, v8Runtime.getNearHeapLimitCallback());
            v8Runtime.getExecutor("[[... new Array(50000000).keys()],[... new Array(50000000).keys()],[... new Array(50000000).keys()],[... new Array(50000000).keys()]]").executeVoid();
            v8Runtime.setNearHeapLimitCallback(null);
            assertFalse(callback1.isCallbackCalled());
            assertFalse(callback1.isGetDefaultHeapLimitCalled());
            assertTrue(callback2.isCallbackCalled());
            assertTrue(callback2.isGetDefaultHeapLimitCalled());
            assertNull(v8Runtime.getNearHeapLimitCallback());
        }
    }

    @Test
    public void testNearHeapLimitCallbackWithReset() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            MockNearHeapLimitCallback callback = new MockNearHeapLimitCallback();
            // resetContext() keeps the isolate, while resetIsolate() recreates it.
            for (boolean isolateReset : new boolean[]{false, true}) {
                // Setting the same callback after a reset registers the native callback again.
                // V8 aborts the process if the previous reset didn't unregister the native callback.
                v8Runtime.setNearHeapLimitCallback(callback);
                assertEquals(callback, v8Runtime.getNearHeapLimitCallback());
                callback.setGetDefaultHeapLimitCalled(false);
                if (isolateReset) {
                    v8Runtime.resetIsolate();
                } else {
                    v8Runtime.resetContext();
                }
                assertTrue(callback.isGetDefaultHeapLimitCalled());
                assertNull(v8Runtime.getNearHeapLimitCallback());
            }
            // The same callback is called by the new isolate.
            v8Runtime.setNearHeapLimitCallback(callback);
            v8Runtime.getExecutor("[[... new Array(50000000).keys()],[... new Array(50000000).keys()],[... new Array(50000000).keys()],[... new Array(50000000).keys()]]").executeVoid();
            v8Runtime.setNearHeapLimitCallback(null);
            assertTrue(callback.isCallbackCalled());
            assertNull(v8Runtime.getNearHeapLimitCallback());
        }
    }

    @ParameterizedTest
    @EnumSource(V8RuntimeTerminationMode.class)
    public void testNearHeapLimitCallbackWithTerminateExecution(V8RuntimeTerminationMode mode) {
        AtomicBoolean callbackInvoked = new AtomicBoolean(false);
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.setNearHeapLimitCallback((currentHeapLimit, initialHeapLimit) -> {
                callbackInvoked.set(true);
                assertEquals(IJavetNearHeapLimitCallback.INITIAL_HEAP_LIMIT, initialHeapLimit);
                // The callback is called again with the raised heap limit if the termination comes late.
                assertTrue(currentHeapLimit >= IJavetNearHeapLimitCallback.INITIAL_HEAP_LIMIT);
                v8Runtime.terminateExecution(mode);
                return currentHeapLimit * 2;
            });
            v8Runtime.getExecutor("[[... new Array(50000000).keys()],[... new Array(50000000).keys()],[... new Array(50000000).keys()],[... new Array(50000000).keys()]]").executeVoid();
        } catch (JavetException e) {
            assertEquals(JavetError.ExecutionTerminated.getCode(), e.getError().getCode());
            assertEquals("Execution is terminated and continuable is false", e.getMessage());
        }
        assertTrue(callbackInvoked.get(), "NearHeapLimitCallback was not invoked");
    }

    @Test
    public void testNodeSnapshot() throws JavetException {
        if (isNode()) {
            RuntimeOptions<?> options = v8Host.getJSRuntimeType().getRuntimeOptions();
            options.setCreateSnapshotEnabled(true);
            byte[] snapshotBlob;
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                v8Runtime.getExecutor("const add = (a, b) => a + b;").executeVoid();
                assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
                snapshotBlob = v8Runtime.createSnapshot();
                assertNotNull(snapshotBlob);
                assertTrue(snapshotBlob.length > 0);
            }
            // Restore from the snapshot.
            options.setCreateSnapshotEnabled(false).setSnapshotBlob(snapshotBlob);
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
            }
            options.setSnapshotBlob(null);
        }
    }

    @Test
    public void testNodeSnapshotWithInvalidBlob() throws JavetException {
        if (isNode()) {
            byte[] snapshotBlob = createNodeSnapshotBlob();
            RuntimeOptions<?> options = v8Host.getJSRuntimeType().getRuntimeOptions();
            final int v8RuntimeCount = v8Host.getV8RuntimeCount();
            for (Map.Entry<String, byte[]> entry : createInvalidNodeSnapshotBlobs(snapshotBlob).entrySet()) {
                options.setSnapshotBlob(entry.getValue());
                try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                    fail("Failed to report runtime create failure with " + entry.getKey() + ".");
                } catch (JavetException e) {
                    assertEquals(JavetError.RuntimeCreateFailure.getCode(), e.getError().getCode(), entry.getKey());
                }
                assertEquals(v8RuntimeCount, v8Host.getV8RuntimeCount(), entry.getKey());
            }
            // The failures must not affect the ordinary runtimes and the valid snapshot.
            options.setSnapshotBlob(null);
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            }
            options.setSnapshotBlob(snapshotBlob);
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
            }
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
        if (isNode()) {
            try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
                assertFalse(v8Runtime.hasException());
                assertFalse(v8Runtime.hasPendingMessage());
                assertFalse(v8Runtime.reportPendingMessages());
            }
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
    public void testResetContextWithInvalidNodeSnapshot() throws JavetException {
        if (isNode()) {
            byte[] snapshotBlob = createNodeSnapshotBlob();
            RuntimeOptions<?> options = v8Host.getJSRuntimeType().getRuntimeOptions();
            final int v8RuntimeCount = v8Host.getV8RuntimeCount();
            for (Map.Entry<String, byte[]> entry : createInvalidNodeSnapshotBlobs(snapshotBlob).entrySet()) {
                options.setSnapshotBlob(snapshotBlob);
                try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                    assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
                    // A snapshot runtime recreates its isolate from the current runtime options.
                    options.setSnapshotBlob(entry.getValue());
                    try {
                        v8Runtime.resetContext();
                        fail("Failed to report runtime reset failure with " + entry.getKey() + ".");
                    } catch (JavetException e) {
                        assertEquals(JavetError.RuntimeResetFailure.getCode(), e.getError().getCode(), entry.getKey());
                    }
                    assertTrue(v8Runtime.isClosed(), entry.getKey());
                    assertEquals(v8RuntimeCount, v8Host.getV8RuntimeCount(), entry.getKey());
                    // The closed runtime ignores further resets.
                    v8Runtime.resetContext();
                }
            }
            // The failures must not affect the ordinary runtimes.
            options.setSnapshotBlob(null);
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                v8Runtime.resetContext();
                assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
            }
        }
    }

    @Test
    public void testResetContextWithNodeSnapshotCreation() throws JavetException {
        if (isNode()) {
            RuntimeOptions<?> options = v8Host.getJSRuntimeType().getRuntimeOptions();
            options.setCreateSnapshotEnabled(true);
            byte[] snapshotBlob;
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                for (int i = 0; i < 3; ++i) {
                    v8Runtime.getExecutor(
                            "globalThis.transientValue = 1; const transientBinding = 2;").executeVoid();
                    v8Runtime.resetContext();
                    assertEquals("undefined", v8Runtime.getExecutor("typeof transientValue").executeString());
                    assertEquals("undefined", v8Runtime.getExecutor("typeof transientBinding").executeString());
                    assertEquals("test.js", v8Runtime.getExecutor(
                            "require('node:path').basename('/tmp/test.js')").executeString());
                }
                // The replacement setup must still be able to create a usable snapshot.
                v8Runtime.getExecutor("const add = (a, b) => a + b;").executeVoid();
                snapshotBlob = v8Runtime.createSnapshot();
                assertNotNull(snapshotBlob);
                assertTrue(snapshotBlob.length > 0);
            }
            options.setCreateSnapshotEnabled(false).setSnapshotBlob(snapshotBlob);
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
                assertEquals("undefined", v8Runtime.getExecutor("typeof transientValue").executeString());
                assertEquals("undefined", v8Runtime.getExecutor("typeof transientBinding").executeString());
            }
            options.setSnapshotBlob(null);
        }
    }

    @Test
    public void testResetContextWithNodeSnapshotRestoration() throws JavetException {
        if (isNode()) {
            RuntimeOptions<?> options = v8Host.getJSRuntimeType().getRuntimeOptions();
            options.setCreateSnapshotEnabled(true);
            byte[] snapshotBlob;
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                v8Runtime.getExecutor(
                        "globalThis.snapshotValue = 41; const add = (a, b) => a + b;").executeVoid();
                snapshotBlob = v8Runtime.createSnapshot();
                assertNotNull(snapshotBlob);
                assertTrue(snapshotBlob.length > 0);
            }
            options.setCreateSnapshotEnabled(false).setSnapshotBlob(snapshotBlob);
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                assertEquals(41, v8Runtime.getExecutor("snapshotValue").executeInteger());
                for (int i = 0; i < 3; ++i) {
                    v8Runtime.getExecutor(
                            "snapshotValue = 99; globalThis.transientValue = 1;").executeVoid();
                    v8Runtime.resetContext();
                    assertEquals(41, v8Runtime.getExecutor("snapshotValue").executeInteger());
                    assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
                    assertEquals("undefined", v8Runtime.getExecutor("typeof transientValue").executeString());
                    assertEquals("undefined", v8Runtime.getExecutor("typeof timerValue").executeString());
                    assertEquals("test.js", v8Runtime.getExecutor(
                            "require('node:path').basename('/tmp/test.js')").executeString());
                    // Verify that the recreated Node environment and event loop work.
                    v8Runtime.getExecutor(
                            "globalThis.timerValue = 0;" +
                                    "setTimeout(() => { globalThis.timerValue = add(40, 2); }, 0);").executeVoid();
                    v8Runtime.await();
                    assertEquals(42, v8Runtime.getExecutor("timerValue").executeInteger());
                }
            }
            options.setSnapshotBlob(null);
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
    public void testResetIsolateWithInvalidNodeSnapshot() throws JavetException {
        if (isNode()) {
            byte[] snapshotBlob = createNodeSnapshotBlob();
            RuntimeOptions<?> options = v8Host.getJSRuntimeType().getRuntimeOptions();
            final int v8RuntimeCount = v8Host.getV8RuntimeCount();
            for (Map.Entry<String, byte[]> entry : createInvalidNodeSnapshotBlobs(snapshotBlob).entrySet()) {
                options.setSnapshotBlob(null);
                try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                    assertEquals(2, v8Runtime.getExecutor("1 + 1").executeInteger());
                    // The isolate is recreated from the current runtime options.
                    options.setSnapshotBlob(entry.getValue());
                    try {
                        v8Runtime.resetIsolate();
                        fail("Failed to report runtime reset failure with " + entry.getKey() + ".");
                    } catch (JavetException e) {
                        assertEquals(JavetError.RuntimeResetFailure.getCode(), e.getError().getCode(), entry.getKey());
                    }
                    assertTrue(v8Runtime.isClosed(), entry.getKey());
                    assertEquals(v8RuntimeCount, v8Host.getV8RuntimeCount(), entry.getKey());
                    // The closed runtime ignores further resets.
                    v8Runtime.resetIsolate();
                }
            }
            // The failures must not affect the valid snapshot.
            options.setSnapshotBlob(snapshotBlob);
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                v8Runtime.resetIsolate();
                assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
            }
        }
    }

    @Test
    public void testResetWithCallbacks() throws JavetException {
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            assertResetRemovesCallbacks(v8Runtime, false);
            assertResetRemovesCallbacks(v8Runtime, true);
        }
        if (isNode()) {
            // resetContext() recreates the isolate of a Node.js snapshot runtime.
            RuntimeOptions<?> options = v8Host.getJSRuntimeType().getRuntimeOptions();
            options.setSnapshotBlob(createNodeSnapshotBlob());
            try (V8Runtime v8Runtime = v8Host.createV8Runtime(options)) {
                assertResetRemovesCallbacks(v8Runtime, false);
                assertEquals(3, v8Runtime.getExecutor("add(1, 2)").executeInteger());
            }
            options.setSnapshotBlob(null);
        }
    }

    @Test
    public void testV8Snapshot() throws JavetException {
        if (isV8()) {
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
