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
import com.caoccao.javet.interop.converters.JavetProxyConverter;

import java.awt.*;
import java.util.regex.Pattern;

public class TestJavaAndJSInterop {
    public static void main(String[] args) throws JavetException {
        // Create a V8 runtime from V8 host in try-with-resource.
        try (V8Runtime v8Runtime = V8Host.getV8Instance().createV8Runtime()) {
            // Set converter to proxy based one to unlock the interoperability.
            v8Runtime.setConverter(new JavetProxyConverter());

            testInjectStaticClass(v8Runtime);
            testInjectEnum(v8Runtime);
            testInjectRegex(v8Runtime);
            testInjectStringBuilder(v8Runtime);

            // Notify V8 to perform GC. (Optional)
            v8Runtime.lowMemoryNotification();
        }
    }

    private static void testInjectEnum(V8Runtime v8Runtime) throws JavetException {
        System.out.println("--- testInjectEnum ---");
        v8Runtime.getGlobalObject().set("Color", Color.class);
        System.out.println(v8Runtime.getExecutor("Color.pink.toString();").executeString());
        System.out.println("The enum in JavaScript is the one in Java: " +
                (Color.pink == (Color) v8Runtime.getExecutor("Color.pink;").executeObject()));
        v8Runtime.getGlobalObject().delete("Color");
        System.out.println();
    }

    private static void testInjectRegex(V8Runtime v8Runtime) throws JavetException {
        System.out.println("--- testInjectRegex ---");
        Pattern pattern = Pattern.compile("^\\d+$");
        v8Runtime.getExecutor("function main(pattern) {\n" +
                "  return [\n" +
                "    pattern.matcher('123').matches(),\n" +
                "    pattern.matcher('abc').matches(),\n" +
                "  ];\n" +
                "}").executeVoid();
        System.out.println(v8Runtime.getGlobalObject().invokeObject("main", pattern).toString());
        System.out.println();
    }

    private static void testInjectStaticClass(V8Runtime v8Runtime) throws JavetException {
        System.out.println("--- testInjectStaticClass ---");
        v8Runtime.getGlobalObject().set("System", System.class);
        v8Runtime.getExecutor("function main() {\n" +
                // Java reference can be directly called in JavaScript.
                "  System.out.println('Hello from Java');\n" +
                // Java reference can be directly assigned to JavaScript variable.
                "  const println = System.out.println;\n" +
                // Java reference can be directly assigned to JavaScript variable.
                "  println('Hello from JavaScript');\n" +
                "}\n" +
                "main();").executeVoid();
        v8Runtime.getGlobalObject().delete("System");
        System.out.println();
    }

    private static void testInjectStringBuilder(V8Runtime v8Runtime) throws JavetException {
        System.out.println("--- testInjectStringBuilder ---");
        v8Runtime.getGlobalObject().set("StringBuilder", StringBuilder.class);
        System.out.println(v8Runtime.getExecutor("function main() {\n" +
                "  return new StringBuilder().append('Hello from StringBuilder').toString();\n" +
                "}\n" +
                "main();").executeString());
        v8Runtime.getGlobalObject().delete("StringBuilder");
        System.out.println();
    }
}
