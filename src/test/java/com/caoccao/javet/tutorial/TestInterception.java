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
import com.caoccao.javet.annotations.V8Property;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.values.reference.V8ValueObject;

public class TestInterception {
    private String name;
    private int value;

    public static void main(String[] args) throws JavetException {
        // Step 1: Create a V8 runtime from V8 host in try-with-resource.
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            // Step 2: Register console.
            JavetStandardConsoleInterceptor javetStandardConsoleInterceptor = new JavetStandardConsoleInterceptor(v8Runtime);
            javetStandardConsoleInterceptor.register(v8Runtime.getGlobalObject());
            // Step 3: Create an interceptor.
            TestInterception testInterceptor = new TestInterception();
            // Step 4: Bind the interceptor to a variable.
            try (V8ValueObject v8ValueObject = v8Runtime.createV8ValueObject()) {
                v8Runtime.getGlobalObject().set("a", v8ValueObject);
                v8ValueObject.bind(testInterceptor);
            }

            // Test property name
            v8Runtime.getExecutor("console.log(`a.name is initially ${a.name}.`);").executeVoid(); // null
            // a.name setter => setName(String name)
            v8Runtime.getExecutor("a.name = 'Javet';").executeVoid();
            // name is changed
            System.out.println("Interceptor name is " + testInterceptor.getName() + "."); // Javet
            // a.name getter => getName()
            v8Runtime.getExecutor("console.log(`a.name is now ${a.name}.`);").executeVoid(); // Javet

            // Test property value
            v8Runtime.getExecutor("console.log(`a.value is initially ${a.value}.`);").executeVoid(); // 0
            // a.value setter => setValue(String value)
            v8Runtime.getExecutor("a.value = 123;").executeVoid();
            // value is changed
            System.out.println("Interceptor value is " + testInterceptor.getValue() + "."); // 123
            // a.value getter => getValue()
            v8Runtime.getExecutor("console.log(`a.value is now ${a.value}.`);").executeVoid(); // 123

            // Test functions
            v8Runtime.getExecutor("console.log(`a.increaseAndGet() is ${a.increaseAndGet()}.`);").executeVoid(); // 124
            v8Runtime.getExecutor("console.log(`a.add(76) is ${a.add(76)}.`);").executeVoid(); // 200

            // Step 5: Delete the interceptor.
            v8Runtime.getGlobalObject().delete("a");
            // Step 6: Unregister console.
            javetStandardConsoleInterceptor.unregister(v8Runtime.getGlobalObject());
            // Step 7: Notify V8 to perform GC. (Optional)
            v8Runtime.lowMemoryNotification();
        }
    }

    @V8Function
    public int add(int delta) {
        value += delta;
        return value;
    }

    @V8Property
    public String getName() {
        return name;
    }

    @V8Property
    public int getValue() {
        return value;
    }

    @V8Function
    public int increaseAndGet() {
        return ++value;
    }

    @V8Property
    public void setName(String name) {
        this.name = name;
    }

    @V8Property
    public void setValue(int value) {
        this.value = value;
    }
}
