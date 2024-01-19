/*
 * Copyright (c) 2023-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.tutorial;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.proxy.IJavetDirectProxyHandler;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.V8ValueBoolean;
import com.caoccao.javet.values.primitive.V8ValueInteger;
import com.caoccao.javet.values.primitive.V8ValueString;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Objects;

public class TestJsonNodeInV8 {

    public static void main(String[] args) throws JavetException, JsonProcessingException {
        // Prepare the test script for testing JsonNode API performance in Java flavor.
        final String testScriptJavaFlavor =
                "function testJavaFlavor(node, rounds) {\n" +
                        "  for (let i = 0; i < rounds; i++) {\n" +
                        "    if (node.has('a')) {\n" +
                        "      const a = node.get('a');\n" +
                        "      if (a.isObject() && a.has('b')) {\n" +
                        "        const b = a.get('b');\n" +
                        "        if (b.isObject() && b.has('c')) {\n" +
                        "          const c = b.get('c');\n" +
                        "          if (c.isInt()) {\n" +
                        "            b.put('c', c.asInt() + 1);\n" +
                        "          }\n" +
                        "        }\n" +
                        "      }\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";

        // Prepare the test script for testing JsonNode API performance in JS flavor.
        final String testScriptJSFlavor =
                "function testJSFlavor(node, rounds) {\n" +
                        "  for (let i = 0; i < rounds; i++) {\n" +
                        "    const b = node?.a?.b;\n" +
                        "    if (b?.c !== undefined) {\n" +
                        "      b.c = b.c + 1;\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";

        // Other preparations.
        final String jsonString = "{ \"a\": { \"b\": { \"c\": 0 } } }";
        final ObjectMapper objectMapper = new ObjectMapper();
        final int rounds = 100_000;

        // Create a V8 runtime from V8 host in try-with-resource.
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            // Set converter to proxy based one to unlock the interoperability.
            v8Runtime.setConverter(new JavetProxyConverter());
            v8Runtime.getExecutor(testScriptJavaFlavor).executeVoid();
            v8Runtime.getExecutor(testScriptJSFlavor).executeVoid();

            // Test the Java Flavor.
            test(v8Runtime,
                    "testJavaFlavor",
                    objectMapper.readTree(jsonString),
                    rounds);
            v8Runtime.lowMemoryNotification();
            System.gc();
            System.gc();

            // Test the JS Flavor.
            test(v8Runtime,
                    "testJSFlavor",
                    new JsonNodeWrapper(v8Runtime, objectMapper.readTree(jsonString)),
                    rounds);

            // Notify V8 to perform GC. (Optional)
            v8Runtime.lowMemoryNotification();
        }
    }

    private static void test(
            V8Runtime v8Runtime,
            String testName,
            Object jsonNode,
            int rounds)
            throws JavetException {
        System.out.println("--- " + testName + " ---");
        final long startTime = System.currentTimeMillis();
        v8Runtime.getGlobalObject().invokeVoid(testName, jsonNode, rounds);
        final long stopTime = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (stopTime - startTime) + "ms");
        System.out.println(jsonNode);
        System.out.println();
    }

    /*
     * The key is to implement IJavetDirectProxyHandler
     * which is recognized by the JavetProxyConverter.
     * The JS object injected actually is a JS proxy
     * where all the proxy API calls are redirected
     * to the wrapper class. E.g. proxyGet, proxySet.
     */
    static final class JsonNodeWrapper implements IJavetDirectProxyHandler<Exception> {
        private final JsonNode jsonNode;
        private final V8Runtime v8Runtime;

        public JsonNodeWrapper(V8Runtime v8Runtime, JsonNode jsonNode) {
            this.v8Runtime = Objects.requireNonNull(v8Runtime);
            this.jsonNode = Objects.requireNonNull(jsonNode);
        }

        public JsonNode getJsonNode() {
            return jsonNode;
        }

        @Override
        public V8Runtime getV8Runtime() {
            return v8Runtime;
        }

        @Override
        public V8Value proxyGet(
                V8Value target,
                V8Value property,
                V8Value receiver)
                throws JavetException, Exception {
            if (property instanceof V8ValueString) {
                String name = ((V8ValueString) property).getValue();
                if (jsonNode.has(name)) {
                    JsonNode childJsonNode = jsonNode.get(name);
                    if (childJsonNode.isInt()) {
                        return v8Runtime.createV8ValueInteger(childJsonNode.asInt());
                    } else {
                        return v8Runtime.toV8Value(new JsonNodeWrapper(v8Runtime, childJsonNode));
                    }
                }
            }
            return IJavetDirectProxyHandler.super.proxyGet(target, property, receiver);
        }

        @Override
        public V8ValueBoolean proxySet(
                V8Value target,
                V8Value propertyKey,
                V8Value propertyValue,
                V8Value receiver)
                throws JavetException, Exception {
            if (propertyKey instanceof V8ValueString && propertyValue instanceof V8ValueInteger) {
                String name = ((V8ValueString) propertyKey).getValue();
                int value = ((V8ValueInteger) propertyValue).getValue();
                if (jsonNode.isObject()) {
                    ((ObjectNode) jsonNode).put(name, value);
                    return v8Runtime.createV8ValueBoolean(true);
                }
            }
            return IJavetDirectProxyHandler.super.proxySet(target, propertyKey, propertyValue, receiver);
        }

        @Override
        public String toString() {
            return jsonNode.toString();
        }
    }
}
