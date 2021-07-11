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
 * The type V8 public native is the pure interface that defines the JNI C++ implementation.
 * It has to be public so that dynamic library loading can work.
 * <p>
 * Guidelines:
 * 1. Please keep V8Native as small, simple as possible so that the C++ implementation is minimized.
 * 2. Please make sure V8Native doesn't not reference any other types so that JNI code generation is quick and clean.
 * 3. Please keep the methods in ascending order so that the generated .h file keeps the same order.
 * 4. Please don't not inject any other non-public native code.
 */
class V8Native implements IV8Native {
    V8Native() {
    }

    @Override
    public native void add(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    @Override
    public native void allowCodeGenerationFromStrings(long v8RuntimeHandle, boolean allow);

    @Override
    public native void await(long v8RuntimeHandle);

    @Override
    public native Object call(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object receiver, boolean returnResult, Object[] values);

    @Override
    public native Object callAsConstructor(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object[] values);

    @Override
    public native void clearInternalStatistic();

    @Override
    public native void clearWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object cloneV8Value(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native void closeV8Runtime(long v8RuntimeHandle);

    @Override
    public native Object compile(
            long v8RuntimeHandle, String script, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    @Override
    public native void createV8Inspector(long v8RuntimeHandle, Object v8Inspector);

    @Override
    public native long createV8Runtime(String globalName);

    @Override
    public native Object createV8Value(long v8RuntimeHandle, int v8ValueType, Object context);

    @Override
    public native boolean delete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native boolean equals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    @Override
    public native Object execute(
            long v8RuntimeHandle, String script, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    @Override
    public native Object get(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native Object getGlobalObject(long v8RuntimeHandle);

    @Override
    public native int getIdentityHash(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object getInternalProperties(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native long[] getInternalStatistic();

    @Override
    public native int getJSFunctionType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int getJSScopeType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int getLength(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object getOwnPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object getProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native Object getPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object getPrototype(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int getSize(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native String getSourceCode(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native String getVersion();

    @Override
    public native boolean has(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    @Override
    public native boolean hasOwnProperty(long v8RuntimeHandle, long v8ValueHandle, int type, Object key);

    @Override
    public native void idleNotificationDeadline(long v8RuntimeHandle, long deadlineInMillis);

    @Override
    public native Object invoke(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            String functionName, boolean returnResult, Object[] values);

    @Override
    public native boolean isDead(long v8RuntimeHandle);

    @Override
    public native boolean isInUse(long v8RuntimeHandle);

    @Override
    public native boolean isWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean lockV8Runtime(long v8RuntimeHandle);

    @Override
    public native void lowMemoryNotification(long v8RuntimeHandle);

    @Override
    public native Object moduleEvaluate(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean resultRequired);

    @Override
    public native Object moduleGetException(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object moduleGetNamespace(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int moduleGetScriptId(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int moduleGetStatus(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean moduleInstantiate(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object promiseCatch(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, long v8ValueFunctionHandle);

    @Override
    public native Object promiseGetResult(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int promiseGetState(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean promiseHasHandler(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native void promiseMarkAsHandled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object promiseThen(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            long v8ValueFunctionFulfilledHandle, long v8ValueFunctionRejectedHandle);

    @Override
    public native void registerV8Runtime(long v8RuntimeHandle, Object v8Runtime);

    @Override
    public native void removeJNIGlobalRef(long handle);

    @Override
    public native void removeReferenceHandle(long referenceHandle, int referenceType);

    @Override
    public native void requestGarbageCollectionForTesting(long v8RuntimeHandle, boolean fullGC);

    @Override
    public native void resetV8Context(long v8RuntimeHandle, String globalName);

    @Override
    public native void resetV8Isolate(long v8RuntimeHandle, String globalName);

    @Override
    public native boolean sameValue(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    @Override
    public native Object scriptRun(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean resultRequired);

    @Override
    public native boolean set(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, Object value);

    @Override
    public native boolean setAccessor(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String propertyName, Object getter, Object setter);

    /**
     * Sets flags.
     * <p>
     * Famous flags:
     * --use_strict     type: bool  default: false
     *
     * @param flags the flags
     */
    @Override
    public native void setFlags(String flags);

    @Override
    public native boolean setProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, Object value);

    @Override
    public native boolean setPrototype(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, long v8ValueHandlePrototype);

    @Override
    public native boolean setSourceCode(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String sourceCode);

    @Override
    public native void setWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object objectReference);

    @Override
    public native boolean strictEquals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    @Override
    public native void terminateExecution(long v8RuntimeHandle);

    @Override
    public native String toProtoString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native String toString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean unlockV8Runtime(long v8RuntimeHandle);

    @Override
    public native void v8InspectorSend(long v8RuntimeHandle, String message);
}
