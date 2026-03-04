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
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.values.reference.IV8ValueObject;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestV8Inspector extends BaseTestJavet {
    protected AtomicInteger atomicInteger;

    public TestV8Inspector() {
        super();
        atomicInteger = new AtomicInteger();
    }

    @BeforeEach
    public void beforeEach() {
        atomicInteger.set(0);
    }

    @Test
    public void testBreakProgram() throws JavetException, InterruptedException, JsonProcessingException {
        // Verify that breakProgram() triggers an immediate Debugger.paused
        // notification when called during active JavaScript execution.
        // A Javet direct-call callback is registered as a JS function. When
        // JS calls the function, the Java callback invokes breakProgram()
        // on the V8 execution thread (which has JS frames on the stack).
        // V8 pauses immediately, the main thread sends Debugger.resume,
        // and execution completes.
        if (isNode()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        CountDownLatch pausedLatch = new CountDownLatch(1);
        CountDownLatch completedLatch = new CountDownLatch(1);
        MockV8InspectorListener listener = new MockV8InspectorListener() {
            @Override
            public void receiveNotification(String message) {
                super.receiveNotification(message);
                try {
                    JsonNode node = objectMapper.readTree(message);
                    if (node.has("method") && "Debugger.paused".equals(node.get("method").asText())) {
                        pausedLatch.countDown();
                    }
                } catch (JsonProcessingException e) {
                    // ignore
                }
            }
        };
        try (V8Runtime v8Runtime = v8Host.createV8Runtime();
             V8Inspector v8Inspector = v8Runtime.createV8Inspector("break-program-test")) {
            v8Inspector.addListeners(listener);
            v8Inspector.sendRequest("{\"id\":1,\"method\":\"Debugger.enable\"}");
            // Register a Java callback that calls breakProgram() when invoked from JS.
            JavetCallbackContext callbackContext = new JavetCallbackContext(
                    "triggerBreak", JavetCallbackType.DirectCallNoThisAndNoResult,
                    (IJavetDirectCallable.NoThisAndNoResult<Exception>) (v8Values) ->
                            v8Inspector.breakProgram("embedder-break", "{}"));
            try (V8ValueFunction triggerFn = v8Runtime.createV8ValueFunction(callbackContext)) {
                v8Runtime.getGlobalObject().set("triggerBreak", triggerFn);
            }
            Thread executionThread = new Thread(() -> {
                try {
                    // triggerBreak() calls breakProgram() on the V8 execution thread.
                    // V8 pauses immediately, then resumes after Debugger.resume.
                    v8Runtime.getExecutor("triggerBreak();\nconst y = 2;")
                            .setResourceName("break-prog.js")
                            .executeVoid();
                    completedLatch.countDown();
                } catch (JavetException e) {
                    fail("Execution should not throw: " + e.getMessage());
                }
            });
            executionThread.start();
            assertTrue(pausedLatch.await(5, TimeUnit.SECONDS),
                    "Should receive Debugger.paused from breakProgram()");
            // Resume to let execution complete.
            v8Inspector.sendRequest("{\"id\":2,\"method\":\"Debugger.resume\"}");
            assertTrue(completedLatch.await(5, TimeUnit.SECONDS),
                    "Execution should complete after resume");
            executionThread.join(5000);
            assertFalse(executionThread.isAlive());
            v8Runtime.getGlobalObject().delete("triggerBreak");
        }
    }

    @Test
    public void testBreakpointHitAndResume() throws JavetException, InterruptedException, JsonProcessingException {
        if (isNode()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        CountDownLatch pausedLatch = new CountDownLatch(1);
        CountDownLatch resumedLatch = new CountDownLatch(1);
        // Listener that detects Debugger.paused and signals the latch.
        MockV8InspectorListener listener = new MockV8InspectorListener() {
            @Override
            public void receiveNotification(String message) {
                super.receiveNotification(message);
                try {
                    JsonNode node = objectMapper.readTree(message);
                    if (node.has("method") && "Debugger.paused".equals(node.get("method").asText())) {
                        pausedLatch.countDown();
                    }
                    if (node.has("method") && "Debugger.resumed".equals(node.get("method").asText())) {
                        resumedLatch.countDown();
                    }
                } catch (JsonProcessingException e) {
                    // ignore
                }
            }
        };
        try (V8Runtime v8Runtime = v8Host.createV8Runtime();
             V8Inspector v8Inspector = v8Runtime.createV8Inspector("bp-test")) {
            assertNotNull(v8Inspector);
            v8Inspector.addListeners(listener);
            // Enable debugger and set breakpoint on line 2 of the script.
            v8Inspector.sendRequest("{\"id\":1,\"method\":\"Debugger.enable\"}");
            // Set breakpoint on line 1 (0-based) of a script that will be compiled with URL "test.js".
            v8Inspector.sendRequest("{\"id\":2,\"method\":\"Debugger.setBreakpointByUrl\",\"params\":{\"lineNumber\":1,\"url\":\"test.js\",\"columnNumber\":0,\"condition\":\"\"}}");
            // Execute JavaScript on a separate thread because execution will pause at the breakpoint.
            Thread executionThread = new Thread(() -> {
                try {
                    // Line 0: const x = 1;
                    // Line 1: const y = x + 2;  <-- breakpoint here
                    // Line 2: const z = y + 3;
                    v8Runtime.getExecutor("const x = 1;\nconst y = x + 2;\nconst z = y + 3;")
                            .setResourceName("test.js")
                            .executeVoid();
                } catch (JavetException e) {
                    e.printStackTrace(System.err);
                    fail("Execution should not throw exception.");
                }
            });
            executionThread.start();
            // Wait for the Debugger.paused notification.
            assertTrue(pausedLatch.await(5, TimeUnit.SECONDS), "Should receive Debugger.paused notification");
            // Verify the paused notification contains breakpoint hit reason.
            boolean foundPausedNotification = false;
            for (String notification : listener.getNotifications()) {
                JsonNode node = objectMapper.readTree(notification);
                if (node.has("method") && "Debugger.paused".equals(node.get("method").asText())) {
                    foundPausedNotification = true;
                    assertTrue(node.has("params"));
                    break;
                }
            }
            assertTrue(foundPausedNotification, "Debugger.paused notification should be present");
            // Send Debugger.resume from this thread (while V8 is paused on the execution thread).
            v8Inspector.sendRequest("{\"id\":3,\"method\":\"Debugger.resume\"}");
            // Wait for execution to complete (it should resume after our resume command).
            executionThread.join(5000);
            assertFalse(executionThread.isAlive(), "Execution thread should have completed after resume");
            // Verify that Debugger.resumed notification was received.
            assertTrue(resumedLatch.await(5, TimeUnit.SECONDS), "Should receive Debugger.resumed notification");
        }
    }

    @Test
    public void testBreakpointHitAndResumeMultipleCycles() throws JavetException, InterruptedException, JsonProcessingException {
        // Stress-test the cross-thread pause flag visibility by doing multiple breakpoint-hit-and-resume cycles.
        // This exercises the std::atomic<bool> runningMessageLoop flag under concurrent access.
        if (isNode()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            for (int cycle = 0; cycle < 5; cycle++) {
                CountDownLatch pausedLatch = new CountDownLatch(1);
                CountDownLatch resumedLatch = new CountDownLatch(1);
                MockV8InspectorListener listener = new MockV8InspectorListener() {
                    @Override
                    public void receiveNotification(String message) {
                        super.receiveNotification(message);
                        try {
                            JsonNode node = objectMapper.readTree(message);
                            if (node.has("method")) {
                                String method = node.get("method").asText();
                                if ("Debugger.paused".equals(method)) {
                                    pausedLatch.countDown();
                                } else if ("Debugger.resumed".equals(method)) {
                                    resumedLatch.countDown();
                                }
                            }
                        } catch (JsonProcessingException e) {
                            // ignore
                        }
                    }
                };
                try (V8Inspector v8Inspector = v8Runtime.createV8Inspector("bp-cycle-" + cycle)) {
                    v8Inspector.addListeners(listener);
                    int baseId = cycle * 10;
                    v8Inspector.sendRequest("{\"id\":" + (baseId + 1) + ",\"method\":\"Debugger.enable\"}");
                    v8Inspector.sendRequest("{\"id\":" + (baseId + 2) + ",\"method\":\"Debugger.setBreakpointByUrl\","
                            + "\"params\":{\"lineNumber\":1,\"url\":\"cycle" + cycle + ".js\",\"columnNumber\":0,\"condition\":\"\"}}");
                    final int c = cycle;
                    Thread executionThread = new Thread(() -> {
                        try {
                            v8Runtime.getExecutor("const x" + c + " = 1;\nconst y" + c + " = 2;\nconst z" + c + " = 3;")
                                    .setResourceName("cycle" + c + ".js")
                                    .executeVoid();
                        } catch (JavetException e) {
                            fail("Execution should not throw: " + e.getMessage());
                        }
                    });
                    executionThread.start();
                    assertTrue(pausedLatch.await(5, TimeUnit.SECONDS),
                            "Cycle " + cycle + ": should receive Debugger.paused");
                    v8Inspector.sendRequest("{\"id\":" + (baseId + 3) + ",\"method\":\"Debugger.resume\"}");
                    executionThread.join(5000);
                    assertFalse(executionThread.isAlive(),
                            "Cycle " + cycle + ": execution thread should have completed");
                    assertTrue(resumedLatch.await(5, TimeUnit.SECONDS),
                            "Cycle " + cycle + ": should receive Debugger.resumed");
                    v8Inspector.sendRequest("{\"id\":" + (baseId + 4) + ",\"method\":\"Debugger.disable\"}");
                    v8Inspector.removeListeners(listener);
                }
            }
        }
    }

    @Test
    public void testCancelPauseOnNextStatement() throws JavetException, InterruptedException {
        // Verify that cancelPauseOnNextStatement() cancels a previously scheduled pause.
        if (isNode()) {
            return;
        }
        CountDownLatch pausedLatch = new CountDownLatch(1);
        MockV8InspectorListener listener = new MockV8InspectorListener() {
            @Override
            public void receiveNotification(String message) {
                super.receiveNotification(message);
                if (message.contains("Debugger.paused")) {
                    pausedLatch.countDown();
                }
            }
        };
        try (V8Runtime v8Runtime = v8Host.createV8Runtime();
             V8Inspector v8Inspector = v8Runtime.createV8Inspector("cancel-pause-test")) {
            v8Inspector.addListeners(listener);
            v8Inspector.sendRequest("{\"id\":1,\"method\":\"Debugger.enable\"}");
            // Schedule a pause, then immediately cancel it.
            v8Inspector.schedulePauseOnNextStatement("test", "{}");
            v8Inspector.cancelPauseOnNextStatement();
            // Execute JavaScript — it should NOT pause.
            v8Runtime.getExecutor("const a = 1; const b = 2;").executeVoid();
            // Give a short window for any pause notification to arrive.
            assertFalse(pausedLatch.await(500, TimeUnit.MILLISECONDS),
                    "Should NOT receive Debugger.paused after cancellation");
        }
    }

    @Test
    public void testCloseSession() throws JavetException, InterruptedException, JsonProcessingException, TimeoutException {
        // Verify that closing one session does not affect other sessions.
        if (isNode()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        MockV8InspectorListener listener1 = new MockV8InspectorListener();
        MockV8InspectorListener listener2 = new MockV8InspectorListener();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.getExecutor("const val = 99;").executeVoid();
            try (V8Inspector session1 = v8Runtime.createV8Inspector("close-test-1");
                 V8Inspector session2 = v8Runtime.createV8Inspector("close-test-2")) {
                session1.addListeners(listener1);
                session2.addListeners(listener2);
                // Verify both work.
                session1.sendRequest("{\"id\":1,\"method\":\"Runtime.enable\"}");
                session2.sendRequest("{\"id\":1,\"method\":\"Runtime.enable\"}");
                runAndWait(1000, () -> listener1.getResponses().size() >= 1);
                runAndWait(1000, () -> listener2.getResponses().size() >= 1);
                // Close session 1.
                session1.close();
                assertTrue(session1.isClosed());
                assertFalse(session2.isClosed());
                // Session 2 should still work.
                listener2.getResponses().clear();
                session2.sendRequest("{\"id\":2,\"method\":\"Runtime.evaluate\","
                        + "\"params\":{\"expression\":\"val\",\"replMode\":true}}");
                runAndWait(1000, () -> listener2.getResponses().size() >= 1);
                JsonNode resp = objectMapper.readTree(listener2.getResponses().get(0));
                assertEquals(99, resp.get("result").get("result").get("value").asInt(),
                        "Session 2 should still evaluate correctly after session 1 is closed");
                // Sending on closed session should be silently ignored.
                int prevSize = listener1.getResponses().size();
                session1.sendRequest("{\"id\":99,\"method\":\"Runtime.evaluate\","
                        + "\"params\":{\"expression\":\"1+1\"}}");
                Thread.sleep(200);
                assertEquals(prevSize, listener1.getResponses().size(),
                        "Closed session should not receive responses");
            }
        }
    }

    @Test
    public void testContextResetWithInspector() throws JavetException, TimeoutException, InterruptedException, JsonProcessingException {
        // Verify that resetting the V8 context while an inspector is active does not crash.
        // Previously, contextDestroyed() was not called before closing the old context,
        // leaving the inspector with stale context references.
        if (isNode()) {
            return;
        }
        MockV8InspectorListener listener = new MockV8InspectorListener();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime();
             V8Inspector v8Inspector = v8Runtime.createV8Inspector("ctx-reset")) {
            v8Inspector.addListeners(listener);
            // Evaluate a variable in the original context.
            v8Runtime.getExecutor("const a = 10;").executeVoid();
            v8Inspector.sendRequest("{\"id\":1,\"method\":\"Runtime.enable\"}");
            v8Inspector.sendRequest("{\"id\":2,\"method\":\"Runtime.evaluate\","
                    + "\"params\":{\"expression\":\"a\",\"replMode\":true}}");
            runAndWait(1000, () -> listener.getResponses().size() >= 2);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(listener.getResponses().get(1));
            assertEquals(10, jsonNode.get("result").get("result").get("value").asInt());
            // Reset the context. This should call contextDestroyed then contextCreated internally.
            listener.getResponses().clear();
            listener.getNotifications().clear();
            v8Runtime.resetContext();
            // After reset, old variables are gone. Define a new one and evaluate via inspector.
            v8Runtime.getExecutor("const b = 20;").executeVoid();
            v8Inspector.sendRequest("{\"id\":3,\"method\":\"Runtime.enable\"}");
            v8Inspector.sendRequest("{\"id\":4,\"method\":\"Runtime.evaluate\","
                    + "\"params\":{\"expression\":\"b\",\"replMode\":true}}");
            runAndWait(1000, () -> listener.getResponses().size() >= 2);
            JsonNode jsonNode2 = objectMapper.readTree(listener.getResponses().get(1));
            assertTrue(jsonNode2.has("result"));
            JsonNode resultResult = jsonNode2.get("result").get("result");
            assertEquals("number", resultResult.get("type").asText());
            assertEquals(20, resultResult.get("value").asInt());
        }
    }

    @Test
    public void testDirectEvaluate() throws JavetException {
        // Verify that the direct evaluate() method returns a V8 value
        // without going through CDP JSON serialization.
        if (isNode()) {
            return;
        }
        try (V8Runtime v8Runtime = v8Host.createV8Runtime();
             V8Inspector v8Inspector = v8Runtime.createV8Inspector("direct-eval-test")) {
            v8Runtime.getExecutor("const directVal = 42;").executeVoid();
            // Evaluate a simple expression.
            try (V8ValueInteger result = v8Inspector.evaluate("directVal", false)) {
                assertNotNull(result, "evaluate() should return a non-null result");
                assertEquals(42, result.getValue(), "Should evaluate to 42");
            }
            // Evaluate a string expression.
            try (V8ValueString strResult = v8Inspector.evaluate("'hello'", false)) {
                assertNotNull(strResult, "evaluate() should return a non-null result for strings");
                assertEquals("hello", strResult.getValue());
            }
            // Evaluate an object expression.
            try (V8ValueObject objResult = v8Inspector.evaluate("({a: 1, b: 2})", false)) {
                assertNotNull(objResult, "evaluate() should return a non-null result for objects");
                assertEquals(1, objResult.getInteger("a"));
                assertEquals(2, objResult.getInteger("b"));
            }
            // Evaluate an expression that throws — should return the exception value.
            V8Value errorResult = v8Inspector.evaluate("throw new Error('test error')", false);
            if (errorResult != null) {
                errorResult.close();
            }
            // Evaluate on a closed session should return null.
            v8Inspector.close();
            V8Value nullResult = v8Inspector.evaluate("1 + 1", false);
            assertNull(nullResult, "evaluate() on closed session should return null");
        }
    }

    @Test
    public void testEvaluateValue() throws JavetException, TimeoutException, InterruptedException, JsonProcessingException {
        if (isNode()) {
            // Node has its own protocol which is much more complicated. Javet doesn't test node inspector.
            return;
        }
        MockV8InspectorListener listener = new MockV8InspectorListener();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime();
             V8Inspector v8Inspector = v8Runtime.createV8Inspector("eval-test")) {
            assertNotNull(v8Inspector);
            v8Inspector.addListeners(listener);
            Thread thread = new Thread(() -> {
                try {
                    v8Runtime.getExecutor("const a = 3;").executeVoid();
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Runtime.enable\"}");
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Runtime.runIfWaitingForDebugger\",\"params\":{}}");
                    v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet() + ",\"method\":\"Runtime.evaluate\",\"params\":{\"expression\":\"a\",\"includeCommandLineAPI\":true,\"generatePreview\":true,\"userGesture\":false,\"awaitPromise\":false,\"throwOnSideEffect\":true,\"timeout\":500,\"disableBreaks\":true,\"replMode\":true}}");
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    fail("V8 inspector should not throw exception.");
                }
            });
            thread.start();
            thread.join();
            runAndWait(1000, () -> atomicInteger.get() == listener.getResponses().size());
            assertEquals(1, listener.getContextGroupIds().size());
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> responses = listener.getResponses();
            JsonNode jsonNode = objectMapper.readTree(responses.get(atomicInteger.get() - 1));
            assertTrue(jsonNode.has("id"));
            assertEquals(atomicInteger.get(), jsonNode.get("id").asInt());
            assertTrue(jsonNode.has("result"));
            JsonNode jsonNodeResult = jsonNode.get("result");
            assertTrue(jsonNodeResult.has("result"));
            JsonNode jsonNodeResultResult = jsonNodeResult.get("result");
            assertTrue(jsonNodeResultResult.has("type"));
            assertEquals("number", jsonNodeResultResult.get("type").asText());
            assertTrue(jsonNodeResultResult.has("value"));
            assertEquals(3, jsonNodeResultResult.get("value").asInt());
        }
        assertEquals(atomicInteger.get(), listener.getResponses().size());
    }

    @Test
    public void testInstallAdditionalCommandLineAPI() throws JavetException, InterruptedException, JsonProcessingException, TimeoutException {
        // Verify that installAdditionalCommandLineAPI is called when Runtime.evaluate
        // uses includeCommandLineAPI:true, and that properties set on the command-line
        // API object are available during evaluation.
        if (isNode()) {
            return;
        }
        AtomicInteger callbackCount = new AtomicInteger(0);
        MockV8InspectorListener listener = new MockV8InspectorListener() {
            @Override
            public void installAdditionalCommandLineAPI(IV8ValueObject commandLineAPI) {
                callbackCount.incrementAndGet();
                try {
                    commandLineAPI.set("$myHelper", 42);
                } catch (JavetException e) {
                    fail("Should not throw: " + e.getMessage());
                }
            }
        };
        ObjectMapper objectMapper = new ObjectMapper();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime();
             V8Inspector v8Inspector = v8Runtime.createV8Inspector("cmdline-api-test")) {
            v8Inspector.addListeners(listener);
            // Enable Runtime domain.
            v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet()
                    + ",\"method\":\"Runtime.enable\"}");
            // Evaluate with includeCommandLineAPI:true to trigger installAdditionalCommandLineAPI.
            v8Inspector.sendRequest("{\"id\":" + atomicInteger.incrementAndGet()
                    + ",\"method\":\"Runtime.evaluate\","
                    + "\"params\":{\"expression\":\"$myHelper\","
                    + "\"includeCommandLineAPI\":true,"
                    + "\"replMode\":true}}");
            runAndWait(1000, () -> listener.getResponses().size() >= 2);
            // The callback should have been invoked at least once.
            assertTrue(callbackCount.get() > 0,
                    "installAdditionalCommandLineAPI should have been called");
            // The evaluation response should contain the custom helper value.
            String evalResponse = listener.getResponses().get(1);
            JsonNode jsonNode = objectMapper.readTree(evalResponse);
            JsonNode result = jsonNode.get("result").get("result");
            assertEquals("number", result.get("type").asText());
            assertEquals(42, result.get("value").asInt(),
                    "$myHelper should resolve to 42 from the command-line API");
        }
    }

    @Test
    public void testMultipleInspectorSessions() throws JavetException, InterruptedException, JsonProcessingException, TimeoutException {
        // Verify that two independent sessions on the same runtime each receive
        // their own responses and can evaluate independently.
        if (isNode()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        MockV8InspectorListener listener1 = new MockV8InspectorListener();
        MockV8InspectorListener listener2 = new MockV8InspectorListener();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Runtime.getExecutor("const a = 10; const b = 20;").executeVoid();
            // Create two independent sessions.
            try (V8Inspector session1 = v8Runtime.createV8Inspector("session-1");
                 V8Inspector session2 = v8Runtime.createV8Inspector("session-2")) {
                assertNotNull(session1);
                assertNotNull(session2);
                assertNotSame(session1, session2);
                assertTrue(session1.getSessionId() != session2.getSessionId(),
                        "Sessions should have different IDs");
                session1.addListeners(listener1);
                session2.addListeners(listener2);
                // Enable Runtime on both sessions.
                session1.sendRequest("{\"id\":1,\"method\":\"Runtime.enable\"}");
                session2.sendRequest("{\"id\":1,\"method\":\"Runtime.enable\"}");
                // Evaluate "a" on session 1.
                session1.sendRequest("{\"id\":2,\"method\":\"Runtime.evaluate\","
                        + "\"params\":{\"expression\":\"a\",\"replMode\":true}}");
                runAndWait(1000, () -> listener1.getResponses().size() >= 2);
                // Evaluate "b" on session 2.
                session2.sendRequest("{\"id\":2,\"method\":\"Runtime.evaluate\","
                        + "\"params\":{\"expression\":\"b\",\"replMode\":true}}");
                runAndWait(1000, () -> listener2.getResponses().size() >= 2);
                // Verify session 1 got value 10 (a).
                JsonNode resp1 = objectMapper.readTree(listener1.getResponses().get(1));
                assertEquals(10, resp1.get("result").get("result").get("value").asInt(),
                        "Session 1 should evaluate 'a' = 10");
                // Verify session 2 got value 20 (b).
                JsonNode resp2 = objectMapper.readTree(listener2.getResponses().get(1));
                assertEquals(20, resp2.get("result").get("result").get("value").asInt(),
                        "Session 2 should evaluate 'b' = 20");
                // Verify responses are independent (session 1 doesn't have session 2's responses).
                assertEquals(2, listener1.getResponses().size(),
                        "Session 1 should only have its own responses");
                assertEquals(2, listener2.getResponses().size(),
                        "Session 2 should only have its own responses");
            }
        }
    }

    @Test
    public void testMultipleSessionsBreakpoint() throws JavetException, InterruptedException, JsonProcessingException {
        // Verify that when two sessions both enable the debugger and one sets a breakpoint,
        // both sessions see the Debugger.paused notification when the breakpoint is hit.
        if (isNode()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        CountDownLatch pausedLatch1 = new CountDownLatch(1);
        CountDownLatch pausedLatch2 = new CountDownLatch(1);
        CountDownLatch completedLatch = new CountDownLatch(1);
        MockV8InspectorListener listener1 = new MockV8InspectorListener() {
            @Override
            public void receiveNotification(String message) {
                super.receiveNotification(message);
                try {
                    JsonNode node = objectMapper.readTree(message);
                    if (node.has("method") && "Debugger.paused".equals(node.get("method").asText())) {
                        pausedLatch1.countDown();
                    }
                } catch (JsonProcessingException e) {
                    // ignore
                }
            }
        };
        MockV8InspectorListener listener2 = new MockV8InspectorListener() {
            @Override
            public void receiveNotification(String message) {
                super.receiveNotification(message);
                try {
                    JsonNode node = objectMapper.readTree(message);
                    if (node.has("method") && "Debugger.paused".equals(node.get("method").asText())) {
                        pausedLatch2.countDown();
                    }
                } catch (JsonProcessingException e) {
                    // ignore
                }
            }
        };
        try (V8Runtime v8Runtime = v8Host.createV8Runtime();
             V8Inspector session1 = v8Runtime.createV8Inspector("multi-bp-1");
             V8Inspector session2 = v8Runtime.createV8Inspector("multi-bp-2")) {
            session1.addListeners(listener1);
            session2.addListeners(listener2);
            // Enable debugger on both sessions.
            session1.sendRequest("{\"id\":1,\"method\":\"Debugger.enable\"}");
            session2.sendRequest("{\"id\":1,\"method\":\"Debugger.enable\"}");
            // Set breakpoint via session 1 only.
            session1.sendRequest("{\"id\":2,\"method\":\"Debugger.setBreakpointByUrl\","
                    + "\"params\":{\"lineNumber\":1,\"url\":\"multi-bp.js\",\"columnNumber\":0,\"condition\":\"\"}}");
            Thread executionThread = new Thread(() -> {
                try {
                    v8Runtime.getExecutor("const x = 1;\nconst y = 2;\nconst z = 3;")
                            .setResourceName("multi-bp.js")
                            .executeVoid();
                    completedLatch.countDown();
                } catch (JavetException e) {
                    fail("Execution should not throw: " + e.getMessage());
                }
            });
            executionThread.start();
            // Both sessions should see Debugger.paused.
            assertTrue(pausedLatch1.await(5, TimeUnit.SECONDS),
                    "Session 1 should see Debugger.paused");
            assertTrue(pausedLatch2.await(5, TimeUnit.SECONDS),
                    "Session 2 should see Debugger.paused");
            // Resume from session 2.
            session2.sendRequest("{\"id\":2,\"method\":\"Debugger.resume\"}");
            assertTrue(completedLatch.await(5, TimeUnit.SECONDS),
                    "Execution should complete after resume");
            executionThread.join(5000);
            assertFalse(executionThread.isAlive());
        }
    }

    @Test
    public void testSetSkipAllPauses() throws JavetException, InterruptedException {
        // Verify that setSkipAllPauses(true) prevents breakpoints from firing.
        if (isNode()) {
            return;
        }
        CountDownLatch pausedLatch = new CountDownLatch(1);
        MockV8InspectorListener listener = new MockV8InspectorListener() {
            @Override
            public void receiveNotification(String message) {
                super.receiveNotification(message);
                if (message.contains("Debugger.paused")) {
                    pausedLatch.countDown();
                }
            }
        };
        try (V8Runtime v8Runtime = v8Host.createV8Runtime();
             V8Inspector v8Inspector = v8Runtime.createV8Inspector("skip-pauses-test")) {
            v8Inspector.addListeners(listener);
            v8Inspector.sendRequest("{\"id\":1,\"method\":\"Debugger.enable\"}");
            v8Inspector.sendRequest("{\"id\":2,\"method\":\"Debugger.setBreakpointByUrl\","
                    + "\"params\":{\"lineNumber\":1,\"url\":\"skip.js\",\"columnNumber\":0,\"condition\":\"\"}}");
            // Skip all pauses — the breakpoint should not fire.
            v8Inspector.setSkipAllPauses(true);
            v8Runtime.getExecutor("const s1 = 1;\nconst s2 = 2;\nconst s3 = 3;")
                    .setResourceName("skip.js")
                    .executeVoid();
            // Give a short window for any pause notification to arrive.
            assertFalse(pausedLatch.await(500, TimeUnit.MILLISECONDS),
                    "Should NOT receive Debugger.paused when skip is enabled");
            // Re-enable pauses.
            v8Inspector.setSkipAllPauses(false);
        }
    }

    @Test
    public void testWaitForDebugger() throws JavetException, InterruptedException, JsonProcessingException {
        // Verify that an inspector created with waitForDebugger=true blocks execution
        // until Runtime.runIfWaitingForDebugger is sent, and that the
        // runIfWaitingForDebugger callback is invoked.
        if (isNode()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        CountDownLatch waitingLatch = new CountDownLatch(1);
        CountDownLatch completedLatch = new CountDownLatch(1);
        MockV8InspectorListener listener = new MockV8InspectorListener() {
            @Override
            public void runIfWaitingForDebugger(int contextGroupId) {
                super.runIfWaitingForDebugger(contextGroupId);
                waitingLatch.countDown();
            }
        };
        try (V8Runtime v8Runtime = v8Host.createV8Runtime();
             // Create inspector with waitForDebugger=true.
             V8Inspector v8Inspector = v8Runtime.createV8Inspector("test-wait", true)) {
            assertNotNull(v8Inspector);
            v8Inspector.addListeners(listener);
            // On a separate thread: wait for debugger, then execute JavaScript.
            Thread executionThread = new Thread(() -> {
                try {
                    // This blocks until Runtime.runIfWaitingForDebugger is processed.
                    v8Inspector.waitForDebugger();
                    v8Runtime.getExecutor("const result = 42;").executeVoid();
                    completedLatch.countDown();
                } catch (JavetException e) {
                    e.printStackTrace(System.err);
                    fail("Execution should not throw exception.");
                }
            });
            executionThread.start();
            // Give the execution thread time to enter the waiting loop.
            Thread.sleep(200);
            // The execution thread should be blocked (not completed yet).
            assertEquals(1, completedLatch.getCount(), "Execution thread should still be waiting");
            // Send Runtime.runIfWaitingForDebugger from the main thread.
            v8Inspector.sendRequest("{\"id\":1,\"method\":\"Runtime.runIfWaitingForDebugger\"}");
            // The callback should have been invoked.
            assertTrue(waitingLatch.await(5, TimeUnit.SECONDS),
                    "runIfWaitingForDebugger callback should be invoked");
            // The execution thread should now complete.
            assertTrue(completedLatch.await(5, TimeUnit.SECONDS),
                    "Execution thread should complete after debugger is released");
            executionThread.join(5000);
            assertFalse(executionThread.isAlive(), "Execution thread should have finished");
            // Verify the callback received the correct context group ID.
            assertFalse(listener.getContextGroupIds().isEmpty(),
                    "runIfWaitingForDebugger should have been called");
            assertEquals(1, listener.getContextGroupIds().get(0));
        }
    }

    @Test
    public void testWaitForDebuggerWithBreakpointAfter() throws JavetException, InterruptedException, JsonProcessingException {
        // Verify that after releasing the debugger wait, setting a breakpoint
        // and hitting it still works correctly (tests interaction between
        // waitForDebugger and runMessageLoopOnPause).
        if (isNode()) {
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        CountDownLatch waitingLatch = new CountDownLatch(1);
        CountDownLatch pausedLatch = new CountDownLatch(1);
        CountDownLatch resumedLatch = new CountDownLatch(1);
        CountDownLatch completedLatch = new CountDownLatch(1);
        MockV8InspectorListener listener = new MockV8InspectorListener() {
            @Override
            public void runIfWaitingForDebugger(int contextGroupId) {
                super.runIfWaitingForDebugger(contextGroupId);
                waitingLatch.countDown();
            }

            @Override
            public void receiveNotification(String message) {
                super.receiveNotification(message);
                try {
                    JsonNode node = objectMapper.readTree(message);
                    if (node.has("method")) {
                        String method = node.get("method").asText();
                        if ("Debugger.paused".equals(method)) {
                            pausedLatch.countDown();
                        } else if ("Debugger.resumed".equals(method)) {
                            resumedLatch.countDown();
                        }
                    }
                } catch (JsonProcessingException e) {
                    // ignore
                }
            }
        };
        try (V8Runtime v8Runtime = v8Host.createV8Runtime();
             V8Inspector v8Inspector = v8Runtime.createV8Inspector("test-wait-bp", true)) {
            v8Inspector.addListeners(listener);
            Thread executionThread = new Thread(() -> {
                try {
                    // Wait for debugger before executing anything.
                    v8Inspector.waitForDebugger();
                    // Now execute script with a breakpoint set on line 1.
                    v8Runtime.getExecutor("const a = 1;\nconst b = a + 2;\nconst c = b + 3;")
                            .setResourceName("wait-bp.js")
                            .executeVoid();
                    completedLatch.countDown();
                } catch (JavetException e) {
                    e.printStackTrace(System.err);
                    fail("Execution should not throw: " + e.getMessage());
                }
            });
            executionThread.start();
            // Give time for the thread to enter the wait loop.
            Thread.sleep(200);
            // Enable debugger and set breakpoint while the execution thread is waiting.
            v8Inspector.sendRequest("{\"id\":1,\"method\":\"Debugger.enable\"}");
            v8Inspector.sendRequest("{\"id\":2,\"method\":\"Debugger.setBreakpointByUrl\","
                    + "\"params\":{\"lineNumber\":1,\"url\":\"wait-bp.js\",\"columnNumber\":0,\"condition\":\"\"}}");
            // Release the debugger wait.
            v8Inspector.sendRequest("{\"id\":3,\"method\":\"Runtime.runIfWaitingForDebugger\"}");
            assertTrue(waitingLatch.await(5, TimeUnit.SECONDS),
                    "runIfWaitingForDebugger callback should be invoked");
            // Now execution should start and hit the breakpoint at line 1.
            assertTrue(pausedLatch.await(5, TimeUnit.SECONDS),
                    "Should receive Debugger.paused notification");
            // Resume execution.
            v8Inspector.sendRequest("{\"id\":4,\"method\":\"Debugger.resume\"}");
            assertTrue(resumedLatch.await(5, TimeUnit.SECONDS),
                    "Should receive Debugger.resumed notification");
            assertTrue(completedLatch.await(5, TimeUnit.SECONDS),
                    "Execution thread should finish after resume");
            executionThread.join(5000);
            assertFalse(executionThread.isAlive(), "Execution thread should have completed");
        }
    }

    static class MockV8InspectorListener implements IV8InspectorListener {
        private final List<Integer> contextGroupIds;
        private final List<String> notifications;
        private final List<String> requests;
        private final List<String> responses;

        public MockV8InspectorListener() {
            contextGroupIds = new ArrayList<>();
            notifications = new ArrayList<>();
            requests = new ArrayList<>();
            responses = new ArrayList<>();
        }

        @Override
        public void flushProtocolNotifications() {
            notifications.clear();
        }

        public List<Integer> getContextGroupIds() {
            return contextGroupIds;
        }

        public List<String> getNotifications() {
            return notifications;
        }

        public List<String> getRequests() {
            return requests;
        }

        public List<String> getResponses() {
            return responses;
        }

        @Override
        public void receiveNotification(String message) {
            notifications.add(message);
        }

        @Override
        public void receiveResponse(String message) {
            responses.add(message);
        }

        @Override
        public void runIfWaitingForDebugger(int contextGroupId) {
            contextGroupIds.add(contextGroupId);
        }

        @Override
        public void sendRequest(String message) {
            requests.add(message);
        }
    }
}
