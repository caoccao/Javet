/*
 *    Copyright 2021. caoccao.com Sam Cao
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.caoccao.javet.interop;

import org.junit.jupiter.api.Test;

public class TestV8Runtime {
    @Test
    public void testClose() {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
        }
    }

    @Test
    public void testReset() {
        V8Host v8Host = V8Host.getInstance();
        try (V8Runtime v8Runtime = v8Host.createV8Runtime("window")) {
            v8Runtime.reset();
        }
    }
}
