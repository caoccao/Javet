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

import java.util.*;

public class TestObjectConverter {
    public static void main(String[] args) throws JavetException {
        // Create a V8 runtime from V8 host in try-with-resource.
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            testArray(v8Runtime);
            v8Runtime.resetContext();
            testList(v8Runtime);
            v8Runtime.resetContext();
            testMap(v8Runtime);
            v8Runtime.resetContext();
        }
    }

    private static void testArray(V8Runtime v8Runtime) throws JavetException {
        // Create a string array in JVM.
        String[] stringArray = new String[]{"a", "b", "c"};
        // Bind that string array to a JavaScript variable x.
        v8Runtime.getGlobalObject().set("x", stringArray);
        // Print the JSON representation of the JavaScript variable x.
        System.out.println(v8Runtime.getExecutor("JSON.stringify(x);").executeString());
        // Output: ["a","b","c"]

        // Create a string array in V8.
        v8Runtime.getExecutor("const y = ['a', 'b', 'c'];").executeVoid();
        // Get that string array.
        List<String> stringList = v8Runtime.getExecutor("y;").executeObject();
        // Assert the returned string list.
        System.out.println(Arrays.equals(stringArray, stringList.toArray(new String[0])));
        // Output: true
    }

    private static void testList(V8Runtime v8Runtime) throws JavetException {
        // Create a string list in JVM.
        List<String> stringList = new ArrayList<String>();
        stringList.add("a");
        stringList.add("b");
        stringList.add("c");
        // Bind that string list to a JavaScript variable x.
        v8Runtime.getGlobalObject().set("x", stringList);
        // Print the JSON representation of the JavaScript variable x.
        System.out.println(v8Runtime.getExecutor("JSON.stringify(x);").executeString());
        // Output: ["a","b","c"]
    }

    private static void testMap(V8Runtime v8Runtime) throws JavetException {
        // Create a map in JVM.
        Map<String, Object> mapX = new HashMap<String, Object>() {{
            put("a", 1);
            put("b", true);
            put("c", "s");
        }};
        // Bind that map to a JavaScript variable x.
        v8Runtime.getGlobalObject().set("x", mapX);
        // Print the JSON representation of the JavaScript variable x.
        System.out.println(v8Runtime.getExecutor("JSON.stringify(x);").executeString());
        // Output: {"a":1,"b":true,"c":"s"}

        // Create an object in V8.
        v8Runtime.getExecutor("const y = {'a': 1, 'b': true, 'c': 's'};").executeVoid();
        // Get that string array.
        Map<String, Object> mapY = v8Runtime.getExecutor("y;").executeObject();
        // Assert the returned map.
        System.out.println(mapX.equals(mapY));
        // Output: true
    }
}
