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
    public native void allowCodeGenerationFromStrings(long v8RuntimeHandle, boolean allow);

    @Override
    public native Object arrayBufferCreate(long v8RuntimeHandle, int length);

    @Override
    public native Object arrayBufferCreate(long v8RuntimeHandle, ByteBuffer byteBuffer);

    @Override
    public native Object arrayCreate(long v8RuntimeHandle);

    @Override
    public native int arrayGetLength(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean await(long v8RuntimeHandle, int v8AwaitMode);

    @Override
    public native int batchArrayGet(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object[] v8Values, int startIndex, int endIndex);

    @Override
    public native int batchObjectGet(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object[] v8ValueKeys, Object[] v8ValueValues, int length);

    @Override
    public native Object booleanObjectCreate(long v8RuntimeHandle, boolean booleanValue);

    @Override
    public native Object booleanObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native void clearInternalStatistic();

    @Override
    public native void clearWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object cloneV8Value(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean referenceCopy);

    @Override
    public native void closeV8Runtime(long v8RuntimeHandle);

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
    public native Object doubleObjectCreate(long v8RuntimeHandle, double doubleValue);

    @Override
    public native Object doubleObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean equals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    @Override
    public native Object errorCreate(long v8RuntimeHandle, int v8ValueErrorTypeId, String message);

    @Override
    public native Object functionCall(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object receiver, boolean returnResult, Object[] values);

    @Override
    public native Object functionCallAsConstructor(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object[] values);

    @Override
    public native boolean functionCanDiscardCompiled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object functionCompile(
            long v8RuntimeHandle, String script, byte[] cachedData,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean wasm,
            String[] arguments, Object[] contextExtensions);

    @Override
    public native boolean functionCopyScopeInfoFrom(
            long v8RuntimeHandle,
            long targetV8ValueHandle, int targetV8ValueType,
            long sourceV8ValueHandle, int sourceV8ValueType);

    @Override
    public native Object functionCreate(long v8RuntimeHandle, Object callbackContext);

    @Override
    public native boolean functionDiscardCompiled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native String[] functionGetArguments(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native byte[] functionGetCachedData(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object functionGetContext(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object functionGetInternalProperties(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int functionGetJSFunctionType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int functionGetJSScopeType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object functionGetScopeInfos(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            boolean includeGlobalVariables, boolean includeScopeTypeGlobal);

    @Override
    public native Object functionGetScriptSource(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native String functionGetSourceCode(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean functionIsCompiled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean functionIsWrapped(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean functionSetContext(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object v8Context);

    @Override
    public native boolean functionSetScriptSource(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object scriptSource, boolean cloneScript);

    @Override
    public native boolean functionSetSourceCode(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String sourceCode, boolean cloneScript);

    @Override
    public native Object getGlobalObject(long v8RuntimeHandle);

    @Override
    public native long[] getInternalStatistic();

    @Override
    public native Object getV8HeapSpaceStatistics(long v8RuntimeHandle, int allocationSpace);

    @Override
    public native Object getV8HeapStatistics(long v8RuntimeHandle);

    @Override
    public native Object getV8SharedMemoryStatistics();

    @Override
    public native String getVersion();

    @Override
    public native boolean hasInternalType(long v8RuntimeHandle, long v8ValueHandle, int internalTypeId);

    @Override
    public native boolean hasPendingException(long v8RuntimeHandle);

    @Override
    public native boolean hasPendingMessage(long v8RuntimeHandle);

    @Override
    public native boolean hasScheduledException(long v8RuntimeHandle);

    @Override
    public native void idleNotificationDeadline(long v8RuntimeHandle, long deadlineInMillis);

    @Override
    public native Object integerObjectCreate(long v8RuntimeHandle, int intValue);

    @Override
    public native Object integerObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean isDead(long v8RuntimeHandle);

    @Override
    public native boolean isInUse(long v8RuntimeHandle);

    @Override
    public native boolean isWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean lockV8Runtime(long v8RuntimeHandle);

    @Override
    public native Object longObjectCreate(long v8RuntimeHandle, long longValue);

    @Override
    public native Object longObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native void lowMemoryNotification(long v8RuntimeHandle);

    @Override
    public native Object mapAsArray(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native void mapClear(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object mapCreate(long v8RuntimeHandle);

    @Override
    public native boolean mapDelete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native Object mapGet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native boolean mapGetBoolean(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    @Override
    public native double mapGetDouble(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    @Override
    public native int mapGetInteger(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    @Override
    public native long mapGetLong(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    @Override
    public native int mapGetSize(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native String mapGetString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native boolean mapHas(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    @Override
    public native boolean mapSet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object[] keysAndValues);

    @Override
    public native boolean mapSetBoolean(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean value);

    @Override
    public native boolean mapSetDouble(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, double value);

    @Override
    public native boolean mapSetInteger(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, int value);

    @Override
    public native boolean mapSetLong(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, long value);

    @Override
    public native boolean mapSetNull(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native boolean mapSetString(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, String value);

    @Override
    public native boolean mapSetUndefined(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native Object moduleCompile(
            long v8RuntimeHandle, String script, byte[] cachedData, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    @Override
    public native Object moduleCreate(long v8RuntimeHandle, String name, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object moduleEvaluate(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean resultRequired);

    @Override
    public native Object moduleExecute(
            long v8RuntimeHandle, String script, byte[] cachedData, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM);

    @Override
    public native byte[] moduleGetCachedData(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object moduleGetException(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int moduleGetIdentityHash(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object moduleGetNamespace(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native String moduleGetResourceName(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int moduleGetScriptId(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int moduleGetStatus(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean moduleInstantiate(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean moduleIsSourceTextModule(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean moduleIsSyntheticModule(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object objectCreate(long v8RuntimeHandle);

    @Override
    public native boolean objectDelete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native boolean objectDeletePrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String key);

    @Override
    public native Object objectGet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native boolean objectGetBoolean(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    @Override
    public native double objectGetDouble(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    @Override
    public native int objectGetIdentityHash(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native int objectGetInteger(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    @Override
    public native long objectGetLong(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    @Override
    public native Object objectGetOwnPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object objectGetPrivateProperty(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String propertyName);

    @Override
    public native Object objectGetProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native Object objectGetPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object objectGetPrototype(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native String objectGetString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native boolean objectHas(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    @Override
    public native boolean objectHasOwnProperty(long v8RuntimeHandle, long v8ValueHandle, int type, Object key);

    @Override
    public native boolean objectHasPrivateProperty(
            long v8RuntimeHandle, long v8ValueHandle, int type, String propertyName);

    @Override
    public native Object objectInvoke(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            String functionName, boolean returnResult, Object[] values);

    @Override
    public native boolean objectSet(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object[] keysAndValues);

    @Override
    public native boolean objectSetAccessor(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object propertyName, Object getter, Object setter);

    @Override
    public native boolean objectSetBoolean(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean value);

    @Override
    public native boolean objectSetDouble(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, double value);

    @Override
    public native boolean objectSetInteger(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, int value);

    @Override
    public native boolean objectSetLong(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, long value);

    @Override
    public native boolean objectSetNull(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native boolean objectSetPrivateProperty(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String key, Object value);

    @Override
    public native boolean objectSetProperty(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, Object value);

    @Override
    public native boolean objectSetPrototype(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, long v8ValueHandlePrototype);

    @Override
    public native boolean objectSetString(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, String value);

    @Override
    public native boolean objectSetUndefined(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native String objectToProtoString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object promiseCatch(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, long v8ValueFunctionHandle);

    @Override
    public native Object promiseCreate(long v8RuntimeHandle);

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
    public native Object proxyCreate(long v8RuntimeHandle, Object target);

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
    public native void removeReferenceHandle(long v8RuntimeHandle, long referenceHandle, int referenceType);

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
    public native Object scriptCompile(
            long v8RuntimeHandle, String script, byte[] cachedData, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    @Override
    public native Object scriptExecute(
            long v8RuntimeHandle, String script, byte[] cachedData, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM);

    @Override
    public native byte[] scriptGetCachedData(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native String scriptGetResourceName(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object scriptRun(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean resultRequired);

    @Override
    public native void setAdd(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    @Override
    public native Object setAsArray(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native void setClear(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object setCreate(long v8RuntimeHandle);

    @Override
    public native boolean setDelete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    @Override
    public native int setGetSize(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native boolean setHas(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    @Override
    public native void setWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object objectReference);

    @Override
    public native byte[] snapshotCreate(long v8RuntimeHandle);

    @Override
    public native boolean strictEquals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    @Override
    public native Object stringObjectCreate(long v8RuntimeHandle, String str);

    @Override
    public native Object stringObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object symbolCreate(long v8RuntimeHandle, String description);

    @Override
    public native String symbolDescription(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object symbolObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native Object symbolToObject(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    @Override
    public native void terminateExecution(long v8RuntimeHandle);

    @Override
    public native boolean throwError(long v8RuntimeHandle, int v8ValueErrorType, String message);

    @Override
    public native boolean throwError(long v8RuntimeHandle, Object v8Value);

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
