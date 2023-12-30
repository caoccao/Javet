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
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;

public class TestModuleResolver {
    public static void main(String[] args) throws JavetException {
        // Step 1: Create a V8 runtime from V8 host in try-with-resource.
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            // Step 2: Register a custom module resolver.
            v8Runtime.setV8ModuleResolver((runtime, resourceName, v8ModuleReferrer) -> {
                // Step 3: Compile module.js from source code if the resource name matches.
                if ("./module.js".equals(resourceName)) {
                    return runtime.getExecutor("export function test() { return 1; }")
                            .setResourceName(resourceName).compileV8Module();
                } else {
                    return null;
                }
            });
            // Step 4: Import module.js in test.js and expose test() in global context.
            v8Runtime.getExecutor("import { test } from './module.js'; globalThis.test = test;")
                    .setModule(true).setResourceName("./test.js").executeVoid();
            // Step 5: Call test() in global context.
            System.out.println("test() -> " + v8Runtime.getExecutor("test()").executeInteger());
        }
    }
}
