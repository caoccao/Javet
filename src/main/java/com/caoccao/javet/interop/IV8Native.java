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
 * The interface V8 native.
 *
 * @since 0.8.0
 */
public interface IV8Native {

    void add(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    void allowCodeGenerationFromStrings(long v8RuntimeHandle, boolean allow);

    void await(long v8RuntimeHandle);

    Object call(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object receiver, boolean returnResult, Object[] values);

    Object callAsConstructor(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object[] values);

    void clearInternalStatistic();

    void clearWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object cloneV8Value(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    void closeV8Runtime(long v8RuntimeHandle);

    Object compile(
            long v8RuntimeHandle, String script, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    void createV8Inspector(long v8RuntimeHandle, Object v8Inspector);

    long createV8Runtime(Object runtimeOptions);

    Object createV8Value(long v8RuntimeHandle, int v8ValueType, Object context);

    boolean delete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    boolean deletePrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String key);

    boolean equals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    Object execute(
            long v8RuntimeHandle, String script, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    void functionCopyScopeInfoFrom(
            long v8RuntimeHandle,
            long targetV8ValueHandle, int targetV8ValueType,
            long sourceV8ValueHandle, int sourceV8ValueType);

    String functionGetSourceCode(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean functionSetSourceCode(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String sourceCode);

    Object get(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    Object getGlobalObject(long v8RuntimeHandle);

    int getIdentityHash(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object getInternalProperties(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    long[] getInternalStatistic();

    int getJSFunctionType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    int getJSScopeType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    int getLength(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object getOwnPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object getPrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String propertyName);

    Object getProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    Object getPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object getPrototype(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    int getSize(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object getV8HeapSpaceStatistics(long v8RuntimeHandle, int allocationSpace);

    Object getV8HeapStatistics(long v8RuntimeHandle);

    Object getV8SharedMemoryStatistics();

    String getVersion();

    boolean has(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    boolean hasInternalType(long v8RuntimeHandle, long v8ValueHandle, int internalTypeId);

    boolean hasOwnProperty(long v8RuntimeHandle, long v8ValueHandle, int type, Object key);

    boolean hasPendingException(long v8RuntimeHandle);

    boolean hasPendingMessage(long v8RuntimeHandle);

    boolean hasPrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int type, String propertyName);

    boolean hasScheduledException(long v8RuntimeHandle);

    void idleNotificationDeadline(long v8RuntimeHandle, long deadlineInMillis);

    Object invoke(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            String functionName, boolean returnResult, Object[] values);

    boolean isDead(long v8RuntimeHandle);

    boolean isInUse(long v8RuntimeHandle);

    boolean isWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean lockV8Runtime(long v8RuntimeHandle);

    void lowMemoryNotification(long v8RuntimeHandle);

    Object moduleEvaluate(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean resultRequired);

    Object moduleGetException(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object moduleGetNamespace(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    int moduleGetScriptId(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    int moduleGetStatus(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean moduleInstantiate(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object promiseCatch(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, long v8ValueFunctionHandle);

    Object promiseGetPromise(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object promiseGetResult(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    int promiseGetState(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean promiseHasHandler(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    void promiseMarkAsHandled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean promiseReject(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    boolean promiseResolve(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    Object promiseThen(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            long v8ValueFunctionFulfilledHandle, long v8ValueFunctionRejectedHandle);

    boolean promoteScheduledException(long v8RuntimeHandle);

    Object proxyGetHandler(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object proxyGetTarget(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean proxyIsRevoked(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    void proxyRevoke(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    void registerGCEpilogueCallback(long v8RuntimeHandle);

    void registerGCPrologueCallback(long v8RuntimeHandle);

    void registerV8Runtime(long v8RuntimeHandle, Object v8Runtime);

    void removeJNIGlobalRef(long handle);

    void removeReferenceHandle(long referenceHandle, int referenceType);

    boolean reportPendingMessages(long v8RuntimeHandle);

    void requestGarbageCollectionForTesting(long v8RuntimeHandle, boolean fullGC);

    void resetV8Context(long v8RuntimeHandle, Object runtimeOptions);

    void resetV8Isolate(long v8RuntimeHandle, Object runtimeOptions);

    boolean sameValue(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    Object scriptRun(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean resultRequired);

    boolean set(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, Object value);

    boolean setAccessor(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object propertyName, Object getter, Object setter);

    boolean setPrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String key, Object value);

    boolean setProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, Object value);

    boolean setPrototype(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, long v8ValueHandlePrototype);

    void setWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object objectReference);

    boolean strictEquals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    void terminateExecution(long v8RuntimeHandle);

    String toProtoString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    String toString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean unlockV8Runtime(long v8RuntimeHandle);

    void unregisterGCEpilogueCallback(long v8RuntimeHandle);

    void unregisterGCPrologueCallback(long v8RuntimeHandle);

    void v8InspectorSend(long v8RuntimeHandle, String message);
}
