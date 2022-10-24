/*
 * Copyright (c) 2021-2022. caoccao.com Sam Cao
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

package com.caoccao.javet.interop;

/**
 * The type V8 public native is the pure interface that defines the JNI C++ implementation.
 * It has to be public so that dynamic library loading can work.
 * <p>
 * Guidelines:
 * 1. Please keep V8Native as small, simple as possible so that the C++ implementation is minimized.
 * 2. Please make sure V8Native does not reference any other types so that JNI code generation is quick and clean.
 * 3. Please keep the methods in ascending order so that the generated .h file keeps the same order.
 * 4. Please do not inject any other non-public native code.
 *
 * @since 0.7.0
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
    public native Object contextGet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, int index);

    @Override
    public native int contextGetLength(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean contextIsContextType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, int contextTypeId);

    @Override
    public native boolean contextSetLength(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, int length);

    @Override
    public native void createV8Inspector(long v8RuntimeHandle, Object v8Inspector);

    @Override
    public native long createV8Runtime(Object runtimeOptions);

    @Override
    public native Object createV8Value(long v8RuntimeHandle, int v8ValueType, Object context);

    @Override
    public native boolean delete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native boolean deletePrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String key);

    @Override
    public native boolean equals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    @Override
    public native Object execute(
            long v8RuntimeHandle, String script, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    @Override
    public native boolean functionCanDiscardCompiled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean functionCopyScopeInfoFrom(
            long v8RuntimeHandle,
            long targetV8ValueHandle, int targetV8ValueType,
            long sourceV8ValueHandle, int sourceV8ValueType);

    @Override
    public native Object functionGetContext(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object functionGetScriptSource(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native String functionGetSourceCode(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean functionIsCompiled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean functionSetContext(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object v8Context);

    @Override
    public native boolean functionSetScriptSource(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object scriptSource, boolean cloneScript);

    @Override
    public native boolean functionSetSourceCode(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String sourceCode, boolean cloneScript);

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
    public native Object getPrivateProperty(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String propertyName);

    @Override
    public native Object getProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native Object getPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object getPrototype(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int getSize(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object getV8HeapSpaceStatistics(long v8RuntimeHandle, int allocationSpace);

    @Override
    public native Object getV8HeapStatistics(long v8RuntimeHandle);

    @Override
    public native Object getV8SharedMemoryStatistics();

    @Override
    public native String getVersion();

    @Override
    public native boolean has(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    @Override
    public native boolean hasInternalType(long v8RuntimeHandle, long v8ValueHandle, int internalTypeId);

    @Override
    public native boolean hasOwnProperty(long v8RuntimeHandle, long v8ValueHandle, int type, Object key);

    @Override
    public native boolean hasPendingException(long v8RuntimeHandle);

    @Override
    public native boolean hasPendingMessage(long v8RuntimeHandle);

    @Override
    public native boolean hasPrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int type, String propertyName);

    @Override
    public native boolean hasScheduledException(long v8RuntimeHandle);

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
    public native Object promiseGetPromise(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object promiseGetResult(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int promiseGetState(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean promiseHasHandler(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native void promiseMarkAsHandled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean promiseReject(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    @Override
    public native boolean promiseResolve(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    @Override
    public native Object promiseThen(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            long v8ValueFunctionFulfilledHandle, long v8ValueFunctionRejectedHandle);

    @Override
    public native boolean promoteScheduledException(long v8RuntimeHandle);

    @Override
    public native Object proxyGetHandler(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object proxyGetTarget(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean proxyIsRevoked(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native void proxyRevoke(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native void registerGCEpilogueCallback(long v8RuntimeHandle);

    @Override
    public native void registerGCPrologueCallback(long v8RuntimeHandle);

    @Override
    public native void registerV8Runtime(long v8RuntimeHandle, Object v8Runtime);

    @Override
    public native void removeJNIGlobalRef(long handle);

    @Override
    public native void removeReferenceHandle(long referenceHandle, int referenceType);

    @Override
    public native boolean reportPendingMessages(long v8RuntimeHandle);

    @Override
    public native void requestGarbageCollectionForTesting(long v8RuntimeHandle, boolean fullGC);

    @Override
    public native void resetV8Context(long v8RuntimeHandle, Object runtimeOptions);

    @Override
    public native void resetV8Isolate(long v8RuntimeHandle, Object runtimeOptions);

    @Override
    public native boolean sameValue(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    @Override
    public native Object scriptRun(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean resultRequired);

    @Override
    public native boolean set(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, Object value);

    @Override
    public native boolean setAccessor(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object propertyName, Object getter, Object setter);

    @Override
    public native boolean setPrivateProperty(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String key, Object value);

    @Override
    public native boolean setProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, Object value);

    @Override
    public native boolean setPrototype(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, long v8ValueHandlePrototype);

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
    public native void unregisterGCEpilogueCallback(long v8RuntimeHandle);

    @Override
    public native void unregisterGCPrologueCallback(long v8RuntimeHandle);

    @Override
    public native void v8InspectorSend(long v8RuntimeHandle, String message);
}
