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
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            V8Inspector v8Inspector = v8Runtime.getV8Inspector();
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
                V8Inspector v8Inspector = v8Runtime.getV8Inspector();
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

    @Test
    public void testContextResetWithInspector() throws JavetException, TimeoutException, InterruptedException, JsonProcessingException {
        // Verify that resetting the V8 context while an inspector is active does not crash.
        // Previously, contextDestroyed() was not called before closing the old context,
        // leaving the inspector with stale context references.
        if (isNode()) {
            return;
        }
        MockV8InspectorListener listener = new MockV8InspectorListener();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            V8Inspector v8Inspector = v8Runtime.getV8Inspector();
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
    public void testEvaluateValue() throws JavetException, TimeoutException, InterruptedException, JsonProcessingException {
        if (isNode()) {
            // Node has its own protocol which is much more complicated. Javet doesn't test node inspector.
            return;
        }
        MockV8InspectorListener listener = new MockV8InspectorListener();
        V8Inspector v8Inspector;
        try (V8Runtime v8Runtime = v8Host.createV8Runtime()) {
            v8Inspector = v8Runtime.getV8Inspector();
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
