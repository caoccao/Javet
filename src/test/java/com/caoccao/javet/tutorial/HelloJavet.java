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

package com.caoccao.javet.tutorial;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.NodeRuntime;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.engine.IJavetEngine;
import com.caoccao.javet.interop.engine.IJavetEnginePool;
import com.caoccao.javet.interop.engine.JavetEnginePool;

public class HelloJavet {

    public static void main(String[] args) throws JavetException {
        HelloJavet helloJavet = new HelloJavet();
        helloJavet.printHelloJavet();
        helloJavet.printOnePlusOne();
        helloJavet.playWithPoolAndConsole();
    }

    public void playWithPoolAndConsole() throws JavetException {
        // Create a Javet engine pool.
        try (IJavetEnginePool<V8Runtime> javetEnginePool = new JavetEnginePool<>()) {
            // Get a Javet engine from the pool.
            try (IJavetEngine<V8Runtime> javetEngine = javetEnginePool.getEngine()) {
                // Get a V8 runtime from the engine.
                V8Runtime v8Runtime = javetEngine.getV8Runtime();
                // Create a Javet console interceptor.
                JavetStandardConsoleInterceptor javetConsoleInterceptor =
                        new JavetStandardConsoleInterceptor(v8Runtime);
                // Register the Javet console to V8 global object.
                javetConsoleInterceptor.register(v8Runtime.getGlobalObject());
                // V8 console log is redirected to JVM console log.
                v8Runtime.getExecutor("console.log('Hello Javet from Pool');").executeVoid();
                // Unregister the Javet console to V8 global object.
                javetConsoleInterceptor.unregister(v8Runtime.getGlobalObject());
                // close() is not necessary because the Javet engine pool handles that.
                v8Runtime.lowMemoryNotification();
                // Force V8 to GC.
            }
        }
    }

    public void printHelloJavet() throws JavetException {
        // Step 1: Create a V8 runtime from V8 host in try-with-resource.
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            // Step 2: Execute a string as JavaScript code and print the result to console.
            System.out.println(v8Runtime.getExecutor("'Hello Javet'").executeString()); // Hello Javet
            // Step 3: Resource is recycled automatically at the end of the try-with-resource block.
        }
    }

    public void printOnePlusOne() throws JavetException {
        // Step 1: Create a Node runtime from V8 host in try-with-resource.
        try (NodeRuntime nodeRuntime = V8Host.getNodeInstance().createV8Runtime()) {
            // Step 2: Execute a string as JavaScript code and print the result to console.
            System.out.println("1 + 1 = " + nodeRuntime.getExecutor("1 + 1").executeInteger()); // 2
            // Step 3: Resource is recycled automatically at the end of the try-with-resource block.
        }
    }
}
