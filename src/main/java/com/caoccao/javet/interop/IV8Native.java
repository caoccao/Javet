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

package com.caoccao.javet.interop;

import java.nio.ByteBuffer;

/**
 * The interface V8 native.
 *
 * @since 0.8.0
 */
public interface IV8Native {

    void allowCodeGenerationFromStrings(long v8RuntimeHandle, boolean allow);

    Object arrayBufferCreate(long v8RuntimeHandle, int length);

    Object arrayBufferCreate(long v8RuntimeHandle, ByteBuffer byteBuffer);

    Object arrayCreate(long v8RuntimeHandle);

    int arrayGetLength(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean await(long v8RuntimeHandle, int v8AwaitMode);

    int batchArrayGet(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object[] v8Values, int startIndex, int endIndex);

    int batchObjectGet(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object[] v8ValueKeys, Object[] v8ValueValues, int length);

    Object booleanObjectCreate(long v8RuntimeHandle, boolean booleanValue);

    Object booleanObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    void clearInternalStatistic();

    void clearWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object cloneV8Value(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean referenceCopy);

    void closeV8Runtime(long v8RuntimeHandle);

    Object contextGet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, int index);

    int contextGetLength(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean contextIsContextType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, int contextTypeId);

    boolean contextSetLength(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, int length);

    void createV8Inspector(long v8RuntimeHandle, Object v8Inspector);

    long createV8Runtime(Object runtimeOptions);

    Object doubleObjectCreate(long v8RuntimeHandle, double doubleValue);

    Object doubleObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean equals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    Object errorCreate(long v8RuntimeHandle, int v8ValueErrorTypeId, String message);

    Object functionCall(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object receiver, boolean returnResult, Object[] values);

    Object functionCallAsConstructor(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object[] values);

    boolean functionCanDiscardCompiled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object functionCompile(
            long v8RuntimeHandle, String script, byte[] cachedData,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean wasm,
            String[] arguments, Object[] contextExtensions);

    boolean functionCopyScopeInfoFrom(
            long v8RuntimeHandle,
            long targetV8ValueHandle, int targetV8ValueType,
            long sourceV8ValueHandle, int sourceV8ValueType);

    Object functionCreate(long v8RuntimeHandle, Object callbackContext);

    boolean functionDiscardCompiled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    String[] functionGetArguments(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    byte[] functionGetCachedData(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object functionGetContext(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object functionGetInternalProperties(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    int functionGetJSFunctionType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    int functionGetJSScopeType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object functionGetScopeInfos(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            boolean includeGlobalVariables, boolean includeScopeTypeGlobal);

    Object functionGetScriptSource(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    String functionGetSourceCode(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean functionIsCompiled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean functionIsWrapped(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean functionSetContext(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object v8Context);

    boolean functionSetScriptSource(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object scriptSource, boolean cloneScript);

    boolean functionSetSourceCode(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String sourceCode, boolean cloneScript);

    Object getGlobalObject(long v8RuntimeHandle);

    long[] getInternalStatistic();

    Object getV8HeapSpaceStatistics(long v8RuntimeHandle, int allocationSpace);

    Object getV8HeapStatistics(long v8RuntimeHandle);

    Object getV8SharedMemoryStatistics();

    String getVersion();

    boolean hasInternalType(long v8RuntimeHandle, long v8ValueHandle, int internalTypeId);

    boolean hasPendingException(long v8RuntimeHandle);

    boolean hasPendingMessage(long v8RuntimeHandle);

    boolean hasScheduledException(long v8RuntimeHandle);

    void idleNotificationDeadline(long v8RuntimeHandle, long deadlineInMillis);

    Object integerObjectCreate(long v8RuntimeHandle, int intValue);

    Object integerObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean isDead(long v8RuntimeHandle);

    boolean isInUse(long v8RuntimeHandle);

    boolean isWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean lockV8Runtime(long v8RuntimeHandle);

    Object longObjectCreate(long v8RuntimeHandle, long longValue);

    Object longObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    void lowMemoryNotification(long v8RuntimeHandle);

    Object mapAsArray(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    void mapClear(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object mapCreate(long v8RuntimeHandle);

    boolean mapDelete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    Object mapGet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    boolean mapGetBoolean(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    double mapGetDouble(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    int mapGetInteger(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    long mapGetLong(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    int mapGetSize(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    String mapGetString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    boolean mapHas(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    boolean mapSet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object[] keysAndValues);

    boolean mapSetBoolean(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean value);

    boolean mapSetDouble(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, double value);

    boolean mapSetInteger(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, int value);

    boolean mapSetLong(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, long value);

    boolean mapSetNull(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    boolean mapSetString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, String value);

    boolean mapSetUndefined(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    Object moduleCompile(
            long v8RuntimeHandle, String script, byte[] cachedData, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    Object moduleCreate(long v8RuntimeHandle, String name, long v8ValueHandle, int v8ValueType);

    Object moduleEvaluate(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean resultRequired);

    Object moduleExecute(
            long v8RuntimeHandle, String script, byte[] cachedData, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM);

    byte[] moduleGetCachedData(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object moduleGetException(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object moduleGetNamespace(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    int moduleGetScriptId(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    int moduleGetStatus(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean moduleInstantiate(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean moduleIsSourceTextModule(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean moduleIsSyntheticModule(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object objectCreate(long v8RuntimeHandle);

    boolean objectDelete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    boolean objectDeletePrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String key);

    Object objectGet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    boolean objectGetBoolean(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    double objectGetDouble(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    int objectGetIdentityHash(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    int objectGetInteger(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    long objectGetLong(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    Object objectGetOwnPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object objectGetPrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String propertyName);

    Object objectGetProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    Object objectGetPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object objectGetPrototype(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    String objectGetString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    boolean objectHas(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    boolean objectHasOwnProperty(long v8RuntimeHandle, long v8ValueHandle, int type, Object key);

    boolean objectHasPrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int type, String propertyName);

    Object objectInvoke(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            String functionName, boolean returnResult, Object[] values);

    boolean objectSet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object[] keysAndValues);

    boolean objectSetAccessor(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object propertyName, Object getter, Object setter);

    boolean objectSetBoolean(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean value);

    boolean objectSetDouble(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, double value);

    boolean objectSetInteger(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, int value);

    boolean objectSetLong(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, long value);

    boolean objectSetNull(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    boolean objectSetPrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String key, Object value);

    boolean objectSetProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, Object value);

    boolean objectSetPrototype(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, long v8ValueHandlePrototype);

    boolean objectSetString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, String value);

    boolean objectSetUndefined(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    String objectToProtoString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object promiseCatch(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, long v8ValueFunctionHandle);

    Object promiseCreate(long v8RuntimeHandle);

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

    Object proxyCreate(long v8RuntimeHandle, Object target);

    Object proxyGetHandler(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object proxyGetTarget(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean proxyIsRevoked(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    void proxyRevoke(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    void registerGCEpilogueCallback(long v8RuntimeHandle);

    void registerGCPrologueCallback(long v8RuntimeHandle);

    void registerV8Runtime(long v8RuntimeHandle, Object v8Runtime);

    void removeJNIGlobalRef(long handle);

    void removeReferenceHandle(long v8RuntimeHandle, long referenceHandle, int referenceType);

    boolean reportPendingMessages(long v8RuntimeHandle);

    void requestGarbageCollectionForTesting(long v8RuntimeHandle, boolean fullGC);

    void resetV8Context(long v8RuntimeHandle, Object runtimeOptions);

    void resetV8Isolate(long v8RuntimeHandle, Object runtimeOptions);

    boolean sameValue(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    Object scriptCompile(
            long v8RuntimeHandle, String script, byte[] cachedData, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    Object scriptExecute(
            long v8RuntimeHandle, String script, byte[] cachedData, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM);

    byte[] scriptGetCachedData(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object scriptRun(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean resultRequired);

    void setAdd(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    Object setAsArray(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    void setClear(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object setCreate(long v8RuntimeHandle);

    boolean setDelete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    int setGetSize(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean setHas(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    void setWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object objectReference);

    byte[] snapshotCreate(long v8RuntimeHandle);

    boolean strictEquals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    Object stringObjectCreate(long v8RuntimeHandle, String str);

    Object stringObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object symbolCreate(long v8RuntimeHandle, String description);

    String symbolDescription(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object symbolObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    Object symbolToObject(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    void terminateExecution(long v8RuntimeHandle);

    boolean throwError(long v8RuntimeHandle, int v8ValueErrorType, String message);

    boolean throwError(long v8RuntimeHandle, Object v8Value);

    String toString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    boolean unlockV8Runtime(long v8RuntimeHandle);

    void unregisterGCEpilogueCallback(long v8RuntimeHandle);

    void unregisterGCPrologueCallback(long v8RuntimeHandle);

    void v8InspectorSend(long v8RuntimeHandle, String message);
}
