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

/**
 * The type V8 native is the pure interface that defines the JNI C++ implementation.
 * <p>
 * Guidelines:
 * 1. Please keep V8Native as small, simple as possible so that the C++ implementation is minimized.
 * 2. Please make sure V8Native doesn't not reference any other types so that JNI code generation is quick and clean.
 * 3. Please keep the methods in ascending order so that the generated .h file keeps the same order.
 * 4. Please keep all methods be static.
 * 5. Please don't not inject any other non-native code.
 */
final class V8Native {
    private V8Native() {
    }

    native static void add(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    native static Object call(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object receiver, boolean returnResult, Object[] values);

    native static Object cloneV8Value(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static void closeV8Runtime(long v8RuntimeHandle);

    native static void compileOnly(
            long v8RuntimeHandle, String script,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    native static long createCallback(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object v8CallbackContext);

    native static long createV8Runtime(String globalName);

    native static Object createV8Value(long v8RuntimeHandle, int v8ValueType);

    native static boolean delete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    native static Object execute(
            long v8RuntimeHandle, String script, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    native static Object get(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    native static Object getGlobalObject(long v8RuntimeHandle);

    native static int getLength(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static int getSize(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static Object getOwnPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static Object getPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static Object getProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    native static String getVersion();

    native static boolean has(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    native static boolean hasOwnProperty(long v8RuntimeHandle, long v8ValueHandle, int type, Object key);

    native static Object invoke(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            String functionName, boolean returnResult, Object[] values);

    native static void lockV8Runtime(long v8RuntimeHandle);

    native static void removeCallbackHandle(long callbackContextHandle);

    native static void removeReferenceHandle(long referenceHandle);

    native static void requestGarbageCollectionForTesting(long v8RuntimeHandle, boolean fullGC);

    native static void resetV8Runtime(long v8RuntimeHandle, String globalName);

    native static boolean set(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, Object value);

    /**
     * Sets flags.
     * <p>
     * Famous flags:
     * --use_strict     type: bool  default: false
     *
     * @param flags the flags
     */
    native static void setFlags(String flags);

    native static boolean setProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, Object value);

    native static String toProtoString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static String toString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static void unlockV8Runtime(long v8RuntimeHandle);
}
