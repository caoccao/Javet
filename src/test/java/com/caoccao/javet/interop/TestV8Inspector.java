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
import com.caoccao.javet.exceptions.JavetException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
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
    public void testEvaluateValue() throws JavetException, TimeoutException, InterruptedException, JsonProcessingException {
        if (v8Host.getJSRuntimeType().isNode()) {
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
                    e.printStackTrace();
                    fail("V8 inspector should not throw exception.");
                }
            });
            thread.start();
            thread.join();
            v8Runtime.getExecutor("const b = 1;").executeVoid();
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
