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

    native static void allowCodeGenerationFromStrings(long v8RuntimeHandle, boolean allow);

    native static Object call(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object receiver, boolean returnResult, Object[] values);

    native static Object callAsConstructor(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object[] values);

    native static void clearWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static Object cloneV8Value(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static void closeV8Runtime(long v8RuntimeHandle);

    native static Object compile(
            long v8RuntimeHandle, String script, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    native static void createV8Inspector(long v8RuntimeHandle, Object v8Inspector);

    native static long createV8Runtime(String globalName);

    native static Object createV8Value(long v8RuntimeHandle, int v8ValueType, Object context);

    native static boolean delete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    native static boolean equals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    native static Object execute(
            long v8RuntimeHandle, String script, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    native static Object get(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    native static Object getGlobalObject(long v8RuntimeHandle);

    native static int getIdentityHash(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

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

    native static boolean isDead(long v8RuntimeHandle);

    native static boolean isInUse(long v8RuntimeHandle);

    native static boolean isWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static void lockV8Runtime(long v8RuntimeHandle);

    native static Object moduleEvaluate(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean resultRequired);

    native static Object moduleGetException(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static int moduleGetScriptId(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static int moduleGetStatus(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static boolean moduleInstantiate(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static Object promiseCatch(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, long v8ValueFunctionHandle);

    native static Object promiseGetResult(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static int promiseGetState(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static boolean promiseHasHandler(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static void promiseMarkAsHandled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static Object promiseThen(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            long v8ValueFunctionFulfilledHandle, long v8ValueFunctionRejectedHandle);

    native static void registerV8Runtime(long v8RuntimeHandle, Object v8Runtime);

    native static void removeJNIGlobalRef(long handle);

    native static void removeReferenceHandle(long referenceHandle, int referenceType);

    native static void requestGarbageCollectionForTesting(long v8RuntimeHandle, boolean fullGC);

    native static void resetV8Context(long v8RuntimeHandle, String globalName);

    native static void resetV8Isolate(long v8RuntimeHandle, String globalName);

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

    native static void setWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object objectReference);

    native static boolean sameValue(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    native static boolean strictEquals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    native static void terminateExecution(long v8RuntimeHandle);

    native static String toProtoString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static String toString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    native static void unlockV8Runtime(long v8RuntimeHandle);

    native static void v8InspectorSend(long v8RuntimeHandle, String message);
}
