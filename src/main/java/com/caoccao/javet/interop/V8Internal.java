/*
 * Copyright (c) 2021-2023. caoccao.com Sam Cao
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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.JSFunctionType;
import com.caoccao.javet.enums.JSScopeType;
import com.caoccao.javet.enums.V8ValueInternalType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.*;

import java.util.Objects;

public final class V8Internal {
    private final V8Runtime v8Runtime;

    V8Internal(V8Runtime v8Runtime) {
        this.v8Runtime = Objects.requireNonNull(v8Runtime);
    }

    public void addReference(IV8ValueReference iV8ValueReference) {
        v8Runtime.addReference(iV8ValueReference);
    }

    public boolean arrayDelete(IV8ValueArray iV8ValueArray, V8Value key) throws JavetException {
        return v8Runtime.arrayDelete(iV8ValueArray, key);
    }

    @CheckReturnValue
    public <T extends V8Value> T arrayGet(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.arrayGet(iV8ValueObject, key);
    }

    public int arrayGetLength(IV8ValueArray iV8ValueArray) throws JavetException {
        return v8Runtime.arrayGetLength(iV8ValueArray);
    }

    public int arrayGetLength(IV8ValueTypedArray iV8ValueTypedArray) throws JavetException {
        return v8Runtime.arrayGetLength(iV8ValueTypedArray);
    }

    public boolean arraySet(IV8ValueArray iV8ValueArray, V8Value key, V8Value value) throws JavetException {
        return v8Runtime.arraySet(iV8ValueArray, key, value);
    }

    public int batchArrayGet(
            IV8ValueArray iV8ValueArray, V8Value[] v8Values, int startIndex, int endIndex)
            throws JavetException {
        return v8Runtime.batchArrayGet(iV8ValueArray, v8Values, startIndex, endIndex);
    }

    public int batchObjectGet(
            IV8ValueObject iV8ValueObject, V8Value[] v8ValueKeys, V8Value[] v8ValueValues, int length)
            throws JavetException {
        return v8Runtime.batchObjectGet(iV8ValueObject, v8ValueKeys, v8ValueValues, length);
    }

    public void clearWeak(IV8ValueReference iV8ValueReference) throws JavetException {
        v8Runtime.clearWeak(iV8ValueReference);
    }

    @CheckReturnValue
    public <T extends V8Value> T cloneV8Value(
            IV8ValueReference iV8ValueReference, boolean referenceCopy)
            throws JavetException {
        return v8Runtime.cloneV8Value(iV8ValueReference, referenceCopy);
    }

    @CheckReturnValue
    public <T extends V8Value> T contextGet(IV8Context iV8Context, int index) throws JavetException {
        return v8Runtime.contextGet(iV8Context, index);
    }

    public int contextGetLength(IV8Context iV8Context) throws JavetException {
        return v8Runtime.contextGetLength(iV8Context);
    }

    public boolean contextIsContextType(IV8Context iV8Context, int contextTypeId) throws JavetException {
        return v8Runtime.contextIsContextType(iV8Context, contextTypeId);
    }

    public boolean contextSetLength(IV8Context iV8Context, int length) throws JavetException {
        return v8Runtime.contextSetLength(iV8Context, length);
    }

    public boolean equals(IV8ValueReference iV8ValueReference1, IV8ValueReference iV8ValueReference2)
            throws JavetException {
        return v8Runtime.equals(iV8ValueReference1, iV8ValueReference2);
    }

    @CheckReturnValue
    public <T extends V8Value> T functionCall(
            IV8ValueObject iV8ValueObject, IV8ValueObject receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        return v8Runtime.functionCall(iV8ValueObject, receiver, returnResult, v8Values);
    }

    @CheckReturnValue
    public <T extends V8Value> T functionCallAsConstructor(
            IV8ValueObject iV8ValueObject, V8Value... v8Values) throws JavetException {
        return v8Runtime.functionCallAsConstructor(iV8ValueObject, v8Values);
    }

    public boolean functionCanDiscardCompiled(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.functionCanDiscardCompiled(iV8ValueFunction);
    }

    public boolean functionCopyScopeInfoFrom(
            IV8ValueFunction targetIV8ValueFunction,
            IV8ValueFunction sourceIV8ValueFunction) throws JavetException {
        return v8Runtime.functionCopyScopeInfoFrom(targetIV8ValueFunction, sourceIV8ValueFunction);
    }

    public boolean functionDiscardCompiled(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.functionDiscardCompiled(iV8ValueFunction);
    }

    public String[] functionGetArguments(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.functionGetArguments(iV8ValueFunction);
    }

    @CheckReturnValue
    public V8Context functionGetContext(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.functionGetContext(iV8ValueFunction);
    }

    @CheckReturnValue
    public IV8ValueArray functionGetInternalProperties(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.functionGetInternalProperties(iV8ValueFunction);
    }

    public JSFunctionType functionGetJSFunctionType(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.functionGetJSFunctionType(iV8ValueFunction);
    }

    public JSScopeType functionGetJSScopeType(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.functionGetJSScopeType(iV8ValueFunction);
    }

    @CheckReturnValue
    public IV8ValueArray functionGetScopeInfos(
            IV8ValueFunction iV8ValueFunction,
            IV8ValueFunction.GetScopeInfosOptions options)
            throws JavetException {
        return v8Runtime.functionGetScopeInfos(iV8ValueFunction, options);
    }

    public IV8ValueFunction.ScriptSource functionGetScriptSource(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.functionGetScriptSource(iV8ValueFunction);
    }

    public String functionGetSourceCode(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.functionGetSourceCode(iV8ValueFunction);
    }

    public boolean functionIsCompiled(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.functionIsCompiled(iV8ValueFunction);
    }

    public boolean functionIsWrapped(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.functionIsWrapped(iV8ValueFunction);
    }

    public boolean functionSetContext(
            IV8ValueFunction iV8ValueFunction, V8Context v8Context) throws JavetException {
        return v8Runtime.functionSetContext(iV8ValueFunction, v8Context);
    }

    public boolean functionSetScriptSource(
            IV8ValueFunction iV8ValueFunction, IV8ValueFunction.ScriptSource scriptSource, boolean cloneScript)
            throws JavetException {
        return v8Runtime.functionSetScriptSource(iV8ValueFunction, scriptSource, cloneScript);
    }

    public boolean functionSetSourceCode(
            IV8ValueFunction iV8ValueFunction, String sourceCode, boolean cloneScript)
            throws JavetException {
        return v8Runtime.functionSetSourceCode(iV8ValueFunction, sourceCode, cloneScript);
    }

    public byte[] getCachedData(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.getCachedData(iV8ValueFunction);
    }

    public byte[] getCachedData(IV8Module iV8Module) throws JavetException {
        return v8Runtime.getCachedData(iV8Module);
    }

    public byte[] getCachedData(IV8Script iV8Script) throws JavetException {
        return v8Runtime.getCachedData(iV8Script);
    }

    public boolean has(IV8ValueObject iV8ValueObject, V8Value value) throws JavetException {
        return v8Runtime.has(iV8ValueObject, value);
    }

    public boolean hasInternalType(IV8ValueObject iV8ValueObject, V8ValueInternalType internalType) {
        return v8Runtime.hasInternalType(iV8ValueObject, internalType);
    }

    public boolean isWeak(IV8ValueReference iV8ValueReference) {
        return v8Runtime.isWeak(iV8ValueReference);
    }

    public boolean mapDelete(IV8ValueMap iV8ValueMap, V8Value key) throws JavetException {
        return v8Runtime.mapDelete(iV8ValueMap, key);
    }

    @CheckReturnValue
    public <T extends V8Value> T mapGet(IV8ValueMap iV8ValueMap, V8Value key) throws JavetException {
        return v8Runtime.mapGet(iV8ValueMap, key);
    }

    public int mapGetSize(IV8ValueMap iV8ValueMap) throws JavetException {
        return v8Runtime.mapGetSize(iV8ValueMap);
    }

    public boolean mapSet(IV8ValueMap iV8ValueMap, V8Value key, V8Value value) throws JavetException {
        return v8Runtime.mapSet(iV8ValueMap, key, value);
    }

    @CheckReturnValue
    public <T extends V8Value> T moduleEvaluate(
            IV8Module iV8Module, boolean resultRequired) throws JavetException {
        return v8Runtime.moduleEvaluate(iV8Module, resultRequired);
    }

    @CheckReturnValue
    public V8ValueError moduleGetException(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleGetException(iV8Module);
    }

    @CheckReturnValue
    public V8ValueObject moduleGetNamespace(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleGetNamespace(iV8Module);
    }

    public int moduleGetScriptId(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleGetScriptId(iV8Module);
    }

    public int moduleGetStatus(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleGetStatus(iV8Module);
    }

    public boolean moduleInstantiate(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleInstantiate(iV8Module);
    }

    public boolean objectDelete(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.objectDelete(iV8ValueObject, key);
    }

    public boolean objectDeletePrivateProperty(IV8ValueObject iV8ValueObject, String propertyName) throws JavetException {
        return v8Runtime.objectDeletePrivateProperty(iV8ValueObject, propertyName);
    }

    @CheckReturnValue
    public <T extends V8Value> T objectGet(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.objectGet(iV8ValueObject, key);
    }

    public int objectGetIdentityHash(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Runtime.objectGetIdentityHash(iV8ValueReference);
    }

    @CheckReturnValue
    public IV8ValueArray objectGetOwnPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        return v8Runtime.objectGetOwnPropertyNames(iV8ValueObject);
    }

    @CheckReturnValue
    public <T extends V8Value> T objectGetPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName)
            throws JavetException {
        return v8Runtime.objectGetPrivateProperty(iV8ValueObject, propertyName);
    }

    @CheckReturnValue
    public <T extends V8Value> T objectGetProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.objectGetProperty(iV8ValueObject, key);
    }

    @CheckReturnValue
    public IV8ValueArray objectGetPropertyNames(IV8ValueObject iV8ValueObject) throws JavetException {
        return v8Runtime.objectGetPropertyNames(iV8ValueObject);
    }

    @CheckReturnValue
    public <T extends IV8ValueObject> T objectGetPrototype(IV8ValueObject iV8ValueObject) throws JavetException {
        return v8Runtime.objectGetPrototype(iV8ValueObject);
    }

    public boolean objectHasOwnProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.objectHasOwnProperty(iV8ValueObject, key);
    }

    public boolean objectHasPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName) throws JavetException {
        return v8Runtime.objectHasPrivateProperty(iV8ValueObject, propertyName);
    }

    @CheckReturnValue
    public <T extends V8Value> T objectInvoke(
            IV8ValueObject iV8ValueObject, String functionName, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        return v8Runtime.objectInvoke(iV8ValueObject, functionName, returnResult, v8Values);
    }

    public boolean objectSet(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        return v8Runtime.objectSet(iV8ValueObject, key, value);
    }

    public boolean objectSetAccessor(
            IV8ValueObject iV8ValueObject,
            V8Value propertyName,
            JavetCallbackContext javetCallbackContextGetter,
            JavetCallbackContext javetCallbackContextSetter) throws JavetException {
        return v8Runtime.objectSetAccessor(
                iV8ValueObject, propertyName, javetCallbackContextGetter, javetCallbackContextSetter);
    }

    public boolean objectSetPrivateProperty(
            IV8ValueObject iV8ValueObject, String propertyName, V8Value propertyValue)
            throws JavetException {
        return v8Runtime.objectSetPrivateProperty(iV8ValueObject, propertyName, propertyValue);
    }

    public boolean objectSetProperty(
            IV8ValueObject iV8ValueObject, V8Value key, V8Value value)
            throws JavetException {
        return v8Runtime.objectSetProperty(iV8ValueObject, key, value);
    }

    public boolean objectSetPrototype(
            IV8ValueObject iV8ValueObject, IV8ValueObject iV8ValueObjectPrototype)
            throws JavetException {
        return v8Runtime.objectSetPrototype(iV8ValueObject, iV8ValueObjectPrototype);
    }

    public String objectToProtoString(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Runtime.objectToProtoString(iV8ValueReference);
    }

    public <T extends V8ValuePromise> T promiseCatch(
            IV8ValuePromise iV8ValuePromise, IV8ValueFunction functionHandle) throws JavetException {
        return v8Runtime.promiseCatch(iV8ValuePromise, functionHandle);
    }

    public V8ValuePromise promiseGetPromise(IV8ValuePromise iV8ValuePromise) throws JavetException {
        return v8Runtime.promiseGetPromise(iV8ValuePromise);
    }

    public <T extends V8Value> T promiseGetResult(IV8ValuePromise iV8ValuePromise) throws JavetException {
        return v8Runtime.promiseGetResult(iV8ValuePromise);
    }

    public int promiseGetState(IV8ValuePromise iV8ValuePromise) {
        return v8Runtime.promiseGetState(iV8ValuePromise);
    }

    public boolean promiseHasHandler(IV8ValuePromise iV8ValuePromise) {
        return v8Runtime.promiseHasHandler(iV8ValuePromise);
    }

    public void promiseMarkAsHandled(IV8ValuePromise iV8ValuePromise) {
        v8Runtime.promiseMarkAsHandled(iV8ValuePromise);
    }

    public boolean promiseReject(V8ValuePromise v8ValuePromise, V8Value v8Value) {
        return v8Runtime.promiseReject(v8ValuePromise, v8Value);
    }

    public boolean promiseResolve(V8ValuePromise v8ValuePromise, V8Value v8Value) {
        return v8Runtime.promiseResolve(v8ValuePromise, v8Value);
    }

    @CheckReturnValue
    public <T extends V8ValuePromise> T promiseThen(
            IV8ValuePromise iV8ValuePromise, IV8ValueFunction functionFulfilledHandle,
            IV8ValueFunction functionRejectedHandle) throws JavetException {
        return v8Runtime.promiseThen(iV8ValuePromise, functionFulfilledHandle, functionRejectedHandle);
    }

    @CheckReturnValue
    public V8ValueObject proxyGetHandler(IV8ValueProxy iV8ValueProxy) throws JavetException {
        return v8Runtime.proxyGetHandler(iV8ValueProxy);
    }

    @CheckReturnValue
    public V8ValueObject proxyGetTarget(IV8ValueProxy iV8ValueProxy) throws JavetException {
        return v8Runtime.proxyGetTarget(iV8ValueProxy);
    }

    public boolean proxyIsRevoked(IV8ValueProxy iV8ValueProxy) throws JavetException {
        return v8Runtime.proxyIsRevoked(iV8ValueProxy);
    }

    public void proxyRevoke(IV8ValueProxy iV8ValueProxy) throws JavetException {
        v8Runtime.proxyRevoke(iV8ValueProxy);
    }

    public void removeReference(IV8ValueReference iV8ValueReference) throws JavetException {
        v8Runtime.removeReference(iV8ValueReference);
    }

    public boolean sameValue(IV8ValueObject iV8ValueObject1, IV8ValueObject iV8ValueObject2) {
        return v8Runtime.sameValue(iV8ValueObject1, iV8ValueObject2);
    }

    public <T extends V8Value> T scriptRun(
            IV8Script iV8Script, boolean resultRequired) throws JavetException {
        return v8Runtime.scriptRun(iV8Script, resultRequired);
    }

    public void setAdd(IV8ValueSet iV8ValueKeySet, V8Value value) throws JavetException {
        v8Runtime.setAdd(iV8ValueKeySet, value);
    }

    public boolean setDelete(IV8ValueSet iV8ValueSet, V8Value key) throws JavetException {
        return v8Runtime.setDelete(iV8ValueSet, key);
    }

    public int setGetSize(IV8ValueSet iV8ValueKeySet) throws JavetException {
        return v8Runtime.setGetSize(iV8ValueKeySet);
    }

    public void setWeak(IV8ValueReference iV8ValueReference) {
        v8Runtime.setWeak(iV8ValueReference);
    }

    public boolean strictEquals(IV8ValueObject iV8ValueObject1, IV8ValueObject iV8ValueObject2) {
        return v8Runtime.strictEquals(iV8ValueObject1, iV8ValueObject2);
    }

    public String toString(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Runtime.toString(iV8ValueReference);
    }
}
