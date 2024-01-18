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

import com.caoccao.javet.annotations.V8Function;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.jvm.JavetJVMInterceptor;
import com.caoccao.javet.interfaces.IJavetAnonymous;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.JavetProxyConverter;
import com.caoccao.javet.interop.proxy.JavetReflectionObjectFactory;

public class TestAccessTheWholeJVM {

    public static void main(String[] args) throws JavetException, InterruptedException {
        // Step 1: Create a V8 runtime from V8 host in try-with-resource.
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            // Step 2: Create a proxy converter.
            JavetProxyConverter javetProxyConverter = new JavetProxyConverter();
            // Step 3: Enable the dynamic object capability. (Optional)
            javetProxyConverter.getConfig().setReflectionObjectFactory(JavetReflectionObjectFactory.getInstance());
            // Step 4: Set the proxy converter to the V8 runtime.
            v8Runtime.setConverter(javetProxyConverter);
            // Step 5: Create and register the JVM interceptor.
            JavetJVMInterceptor javetJVMInterceptor = new JavetJVMInterceptor(v8Runtime);
            javetJVMInterceptor.register(v8Runtime.getGlobalObject());
            // Step 6: Create package 'java'.
            v8Runtime.getExecutor("let java = javet.package.java").executeVoid();

            // Play with StringBuilder.
            System.out.println(v8Runtime.getExecutor("let sb = new java.lang.StringBuilder();" +
                    "sb.append('abc').append(123);" +
                    "sb.toString();").executeString());
            // Output: abc123
            System.out.println(v8Runtime.getExecutor("java.lang['.name']").executeString());
            // Output: java.lang
            System.out.println(v8Runtime.getExecutor("java.lang.StringBuilder['.name']").executeString());
            // Output: java.lang.StringBuilder
            System.out.println(v8Runtime.getExecutor("java.io['.valid']").executeBoolean());
            // Output: true
            System.out.println(v8Runtime.getExecutor("java.abc['.valid']").executeBoolean());
            // Output: false
            System.out.println(v8Runtime.getExecutor("javet.package.javax.annotation['.name']").executeString());
            // Output: javax.annotation

            // Play with dynamic interface.
            Thread thread = v8Runtime.getExecutor(
                    "let count = 0;" +
                            "let thread = new java.lang.Thread(() => { count++; });" +
                            "thread.start();" +
                            "thread; "
            ).executeObject();
            thread.join();
            System.out.println(v8Runtime.getExecutor("count").executeInteger());
            // Output: 1

            // Play with dynamic object. (Optional)
            IJavetAnonymous anonymous = new IJavetAnonymous() {
                @V8Function
                public void test(DynamicClass dynamicClass) {
                    System.out.println(dynamicClass.add(1, 2));
                }
            };
            v8Runtime.getGlobalObject().set("a", anonymous);
            v8Runtime.getExecutor("a.test({ add: (a, b) => a + b });").executeVoid();
            v8Runtime.getGlobalObject().delete("a");
            // Output: 3

            // Step 7: Dispose everything.
            v8Runtime.getExecutor("java = sb = thread = undefined;").executeVoid();
            // Step 8: Unregister the JVM interceptor.
            javetJVMInterceptor.unregister(v8Runtime.getGlobalObject());
            // Step 9: Enforce the GC to avoid memory leak. (Optional)
            System.gc();
            System.runFinalization();
            System.gc();
            System.runFinalization();
            v8Runtime.lowMemoryNotification();
        }
    }

    public static class DynamicClass {
        public int add(int a, int b) {
            return 0;
        }
    }
}
