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

final class V8Native {
    private V8Native() {
    }

    native static void closeV8Runtime(long v8RuntimeHandle);

    native static long createV8Runtime(String globalName);

    native static Object execute(
            long v8RuntimeHandle, String script, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    native static String getVersion();

    native static void resetV8Runtime(long v8RuntimeHandle, String globalName);

    /**
     * Sets flags.
     * <p>
     * Famous flags:
     * --use_strict     type: bool  default: false
     *
     * @param flags the flags
     */
    native static void setFlags(String flags);
}
