/*
 *   Copyright (c) 2021. caoccao.com Sam Cao
 *   All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.caoccao.javet.tutorial;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Host;
import com.caoccao.javet.interop.V8Runtime;

public class HelloJavet {

    public static void main(String[] args) throws JavetException {
        HelloJavet helloJavet = new HelloJavet();
        helloJavet.printHelloJavet();
        helloJavet.printOnePlusOne();
    }

    public void printHelloJavet() throws JavetException {
        // Step 1: Create a V8 runtime from V8 host in try resource.
        try (V8Runtime v8Runtime = V8Host.getInstance().createV8Runtime()) {
            // Step 2: Request a lock.
            v8Runtime.lock();
            // Step 3: Execute a string as JavaScript code and print the result to console.
            System.out.println(v8Runtime.executeString("'Hello Javet'")); // Hello Javet
            // Step 4: Resource including the lock is recycled automatically at the end of the try resource block.
        }
    }

    public void printOnePlusOne() throws JavetException {
        // Step 1: Create a V8 runtime from V8 host in try resource.
        try (V8Runtime v8Runtime = V8Host.getInstance().createV8Runtime()) {
            // Step 2: Request a lock.
            v8Runtime.lock();
            // Step 3: Execute a string as JavaScript code and print the result to console.
            System.out.println(v8Runtime.executeInteger("1 + 1")); // 2
            // Step 4: Resource including the lock is recycled automatically at the end of the try resource block.
        }
    }
}
