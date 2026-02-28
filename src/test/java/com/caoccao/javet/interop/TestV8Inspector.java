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
