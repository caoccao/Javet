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

    public void add(IV8ValueSet iV8ValueKeySet, V8Value value) throws JavetException {
        v8Runtime.add(iV8ValueKeySet, value);
    }

    public void addReference(IV8ValueReference iV8ValueReference) {
        v8Runtime.addReference(iV8ValueReference);
    }

    @CheckReturnValue
    public <T extends V8Value> T call(
            IV8ValueObject iV8ValueObject, IV8ValueObject receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        return v8Runtime.call(iV8ValueObject, receiver, returnResult, v8Values);
    }

    @CheckReturnValue
    public <T extends V8Value> T callAsConstructor(
            IV8ValueObject iV8ValueObject, V8Value... v8Values) throws JavetException {
        return v8Runtime.callAsConstructor(iV8ValueObject, v8Values);
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

    public boolean delete(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.delete(iV8ValueObject, key);
    }

    public boolean deletePrivateProperty(IV8ValueObject iV8ValueObject, String propertyName) throws JavetException {
        return v8Runtime.deletePrivateProperty(iV8ValueObject, propertyName);
    }

    public boolean equals(IV8ValueReference iV8ValueReference1, IV8ValueReference iV8ValueReference2)
            throws JavetException {
        return v8Runtime.equals(iV8ValueReference1, iV8ValueReference2);
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

    @CheckReturnValue
    public V8Context functionGetContext(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.functionGetContext(iV8ValueFunction);
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

    @CheckReturnValue
    public <T extends V8Value> T get(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.get(iV8ValueObject, key);
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

    public int getIdentityHash(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Runtime.getIdentityHash(iV8ValueReference);
    }

    @CheckReturnValue
    public IV8ValueArray getInternalProperties(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.getInternalProperties(iV8ValueFunction);
    }

    public JSFunctionType getJSFunctionType(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.getJSFunctionType(iV8ValueFunction);
    }

    public JSScopeType getJSScopeType(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.getJSScopeType(iV8ValueFunction);
    }

    public int getLength(IV8ValueArray iV8ValueArray) throws JavetException {
        return v8Runtime.getLength(iV8ValueArray);
    }

    public int getLength(IV8ValueTypedArray iV8ValueTypedArray) throws JavetException {
        return v8Runtime.getLength(iV8ValueTypedArray);
    }

    @CheckReturnValue
    public IV8ValueArray getOwnPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        return v8Runtime.getOwnPropertyNames(iV8ValueObject);
    }

    @CheckReturnValue
    public <T extends V8Value> T getPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName)
            throws JavetException {
        return v8Runtime.getPrivateProperty(iV8ValueObject, propertyName);
    }

    @CheckReturnValue
    public <T extends V8Value> T getProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.getProperty(iV8ValueObject, key);
    }

    @CheckReturnValue
    public IV8ValueArray getPropertyNames(IV8ValueObject iV8ValueObject) throws JavetException {
        return v8Runtime.getPropertyNames(iV8ValueObject);
    }

    @CheckReturnValue
    public <T extends IV8ValueObject> T getPrototype(IV8ValueObject iV8ValueObject) throws JavetException {
        return v8Runtime.getPrototype(iV8ValueObject);
    }

    public int getSize(IV8ValueKeyContainer iV8ValueKeyContainer) throws JavetException {
        return v8Runtime.getSize(iV8ValueKeyContainer);
    }

    public boolean has(IV8ValueObject iV8ValueObject, V8Value value) throws JavetException {
        return v8Runtime.has(iV8ValueObject, value);
    }

    public boolean hasInternalType(IV8ValueObject iV8ValueObject, V8ValueInternalType internalType) {
        return v8Runtime.hasInternalType(iV8ValueObject, internalType);
    }

    public boolean hasOwnProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.hasOwnProperty(iV8ValueObject, key);
    }

    public boolean hasPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName) throws JavetException {
        return v8Runtime.hasPrivateProperty(iV8ValueObject, propertyName);
    }

    @CheckReturnValue
    public <T extends V8Value> T invoke(
            IV8ValueObject iV8ValueObject, String functionName, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        return v8Runtime.invoke(iV8ValueObject, functionName, returnResult, v8Values);
    }

    public boolean isWeak(IV8ValueReference iV8ValueReference) {
        return v8Runtime.isWeak(iV8ValueReference);
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

    public boolean set(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        return v8Runtime.set(iV8ValueObject, key, value);
    }

    public boolean setAccessor(
            IV8ValueObject iV8ValueObject,
            V8Value propertyName,
            JavetCallbackContext javetCallbackContextGetter,
            JavetCallbackContext javetCallbackContextSetter) throws JavetException {
        return v8Runtime.setAccessor(
                iV8ValueObject, propertyName, javetCallbackContextGetter, javetCallbackContextSetter);
    }

    public boolean setPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName, V8Value propertyValue)
            throws JavetException {
        return v8Runtime.setPrivateProperty(iV8ValueObject, propertyName, propertyValue);
    }

    public boolean setProperty(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        return v8Runtime.setProperty(iV8ValueObject, key, value);
    }

    public boolean setPrototype(
            IV8ValueObject iV8ValueObject, IV8ValueObject iV8ValueObjectPrototype) throws JavetException {
        return v8Runtime.setPrototype(iV8ValueObject, iV8ValueObjectPrototype);
    }

    public void setWeak(IV8ValueReference iV8ValueReference) {
        v8Runtime.setWeak(iV8ValueReference);
    }

    public boolean strictEquals(IV8ValueObject iV8ValueObject1, IV8ValueObject iV8ValueObject2) {
        return v8Runtime.strictEquals(iV8ValueObject1, iV8ValueObject2);
    }

    public String toProtoString(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Runtime.toProtoString(iV8ValueReference);
    }

    public String toString(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Runtime.toString(iV8ValueReference);
    }
}
