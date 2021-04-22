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

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.enums.JavetPromiseRejectEvent;
import com.caoccao.javet.exceptions.*;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interfaces.IJavetPromiseRejectCallback;
import com.caoccao.javet.interop.executors.IV8Executor;
import com.caoccao.javet.interop.executors.V8PathExecutor;
import com.caoccao.javet.interop.executors.V8StringExecutor;
import com.caoccao.javet.utils.JavetCallbackContext;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.utils.JavetPromiseRejectCallback;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.*;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.IntStream;

@SuppressWarnings("unchecked")
public class V8Runtime implements IJavetClosable, IV8Creatable {
    protected static final long INVALID_HANDLE = 0L;
    protected static final String PROPERTY_DATA_VIEW = "DataView";
    protected static final String DEFAULT_MESSAGE_FORMAT_JAVET_INSPECTOR = "Javet Inspector {0}";
    protected static final int V8_VALUE_BOOLEAN_FALSE_INDEX = 0;
    protected static final int V8_VALUE_BOOLEAN_TRUE_INDEX = 1;
    protected static final int V8_VALUE_NUMBER_LOWER_BOUND = -128; // Inclusive
    protected static final int V8_VALUE_NUMBER_UPPER_BOUND = 128; // Exclusive

    protected V8ValueBoolean[] cachedV8ValueBooleans;
    protected V8ValueInteger[] cachedV8ValueIntegers;
    protected V8ValueLong[] cachedV8ValueLongs;
    protected V8ValueNull cachedV8ValueNull;
    protected V8ValueUndefined cachedV8ValueUndefined;

    /*
     * V8 may not make final callback when V8 context is being recycled.
     * That may results in memory leak.
     * Callback context map is designed for closing that memory leak issue.
     */
    protected Map<Long, JavetCallbackContext> callbackContextMap;
    protected boolean gcScheduled;
    protected String globalName;
    protected long handle;
    protected IJavetLogger logger;
    protected Map<String, IV8Module> v8ModuleMap;
    protected boolean pooled;
    protected IJavetPromiseRejectCallback promiseRejectCallback;
    protected Map<Long, IV8ValueReference> referenceMap;
    protected V8Host v8Host;
    protected IV8Native v8Native;
    protected V8Inspector v8Inspector;

    V8Runtime(V8Host v8Host, long handle, boolean pooled, IV8Native v8Native, String globalName) {
        assert handle != 0;
        callbackContextMap = new TreeMap<>();
        gcScheduled = false;
        this.globalName = globalName;
        this.handle = handle;
        logger = new JavetDefaultLogger(getClass().getName());
        v8ModuleMap = new HashMap<>();
        this.pooled = pooled;
        promiseRejectCallback = new JavetPromiseRejectCallback(logger);
        referenceMap = new TreeMap<>();
        this.v8Host = v8Host;
        this.v8Native = v8Native;
        v8Inspector = null;
        initializeV8ValueCache();
    }

    public void add(IV8ValueSet iV8ValueKeySet, V8Value value) throws JavetException {
        decorateV8Value(value);
        v8Native.add(handle, iV8ValueKeySet.getHandle(), iV8ValueKeySet.getType().getId(), value);
    }

    public void addV8Module(IV8Module iV8Module) {
        v8ModuleMap.put(iV8Module.getResourceName(), iV8Module);
    }

    public void addReference(IV8ValueReference iV8ValueReference) {
        referenceMap.put(iV8ValueReference.getHandle(), iV8ValueReference);
    }

    public void allowEval(boolean allow) {
        v8Native.allowCodeGenerationFromStrings(handle, allow);
    }

    public void await() {
        v8Native.await(handle);
    }

    public <T extends V8Value> T call(
            IV8ValueObject iV8ValueObject, IV8ValueObject receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        decorateV8Values(v8Values);
        return decorateV8Value((T) v8Native.call(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), receiver, returnResult, v8Values));
    }

    public <T extends V8Value> T callAsConstructor(
            IV8ValueObject iV8ValueObject, V8Value... v8Values) throws JavetException {
        decorateV8Values(v8Values);
        return decorateV8Value((T) v8Native.callAsConstructor(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), v8Values));
    }

    public void clearWeak(IV8ValueReference iV8ValueReference) throws JavetException {
        v8Native.clearWeak(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    public <T extends V8Value> T cloneV8Value(IV8ValueReference iV8ValueReference) throws JavetException {
        return decorateV8Value((T) v8Native.cloneV8Value(
                handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId()));
    }

    @Override
    public void close() throws JavetException {
        if (pooled) {
            close(false);
        } else {
            close(true);
        }
    }

    public void close(boolean forceClose) throws JavetException {
        if (handle != INVALID_HANDLE && forceClose) {
            removeAllReferences();
            v8Host.closeV8Runtime(this);
            handle = INVALID_HANDLE;
        }
    }

    public V8Script compileScript(String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) throws JavetException {
        v8ScriptOrigin.setModule(false);
        return decorateV8Value((V8Script) v8Native.compile(
                handle, scriptString, resultRequired, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule()));
    }

    public V8Module compileV8Module(String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) throws JavetException {
        v8ScriptOrigin.setModule(true);
        if (v8ScriptOrigin.getResourceName() == null) {
            throw new JavetV8DataModuleNameEmptyException();
        }
        V8Module v8Module = decorateV8Value((V8Module) v8Native.compile(
                handle, scriptString, resultRequired, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule()));
        v8Module.setResourceName(v8ScriptOrigin.getResourceName());
        v8ModuleMap.put(v8Module.getResourceName(), v8Module);
        return v8Module;
    }

    public boolean containsV8Module(String resourceName) {
        return v8ModuleMap.containsKey(resourceName);
    }

    @Override
    public V8ValueArray createV8ValueArray() throws JavetException {
        return decorateV8Value((V8ValueArray) v8Native.createV8Value(
                handle, V8ValueReferenceType.Array.getId(), null));
    }

    @Override
    public V8ValueArrayBuffer createV8ValueArrayBuffer(int length) throws JavetException {
        return decorateV8Value((V8ValueArrayBuffer) v8Native.createV8Value(
                handle, V8ValueReferenceType.ArrayBuffer.getId(), createV8ValueInteger(length)));
    }

    @Override
    public V8ValueBoolean createV8ValueBoolean(boolean booleanValue) throws JavetException {
        return booleanValue ?
                cachedV8ValueBooleans[V8_VALUE_BOOLEAN_TRUE_INDEX] :
                cachedV8ValueBooleans[V8_VALUE_BOOLEAN_FALSE_INDEX];
    }

    @Override
    public V8ValueDataView createV8ValueDataView(V8ValueArrayBuffer v8ValueArrayBuffer) throws JavetException {
        Objects.requireNonNull(v8ValueArrayBuffer);
        try (V8ValueFunction v8ValueFunction = getGlobalObject().get(PROPERTY_DATA_VIEW)) {
            return v8ValueFunction.callAsConstructor(v8ValueArrayBuffer);
        }
    }

    @Override
    public V8ValueFunction createV8ValueFunction(JavetCallbackContext javetCallbackContext) throws JavetException {
        Objects.requireNonNull(javetCallbackContext);
        V8ValueFunction v8ValueFunction = decorateV8Value((V8ValueFunction) v8Native.createV8Value(
                handle, V8ValueReferenceType.Function.getId(), javetCallbackContext));
        callbackContextMap.put(javetCallbackContext.getHandle(), javetCallbackContext);
        return v8ValueFunction;
    }

    @Override
    public V8ValueInteger createV8ValueInteger(int integerValue) throws JavetException {
        if (integerValue >= V8_VALUE_NUMBER_LOWER_BOUND && integerValue < V8_VALUE_NUMBER_UPPER_BOUND) {
            return cachedV8ValueIntegers[integerValue - V8_VALUE_NUMBER_LOWER_BOUND];
        }
        return decorateV8Value(new V8ValueInteger(integerValue));
    }

    @Override
    public V8ValueLong createV8ValueLong(long longValue) throws JavetException {
        if (longValue >= V8_VALUE_NUMBER_LOWER_BOUND && longValue < V8_VALUE_NUMBER_UPPER_BOUND) {
            return cachedV8ValueLongs[(int) longValue - V8_VALUE_NUMBER_LOWER_BOUND];
        }
        return decorateV8Value(new V8ValueLong(longValue));
    }

    @Override
    public V8ValueMap createV8ValueMap() throws JavetException {
        return decorateV8Value((V8ValueMap) v8Native.createV8Value(handle, V8ValueReferenceType.Map.getId(), null));
    }

    @Override
    public V8ValueNull createV8ValueNull() {
        return cachedV8ValueNull;
    }

    @Override
    public V8ValueObject createV8ValueObject() throws JavetException {
        return decorateV8Value((V8ValueObject) v8Native.createV8Value(handle, V8ValueReferenceType.Object.getId(), null));
    }

    @Override
    public V8ValueSet createV8ValueSet() throws JavetException {
        return decorateV8Value((V8ValueSet) v8Native.createV8Value(handle, V8ValueReferenceType.Set.getId(), null));
    }

    @Override
    public V8ValueTypedArray createV8ValueTypedArray(V8ValueReferenceType type, int length) throws JavetException {
        try (V8ValueFunction v8ValueFunction = getGlobalObject().get(type.getName())) {
            return v8ValueFunction.callAsConstructor(createV8ValueInteger(length));
        }
    }

    @Override
    public V8ValueUndefined createV8ValueUndefined() {
        return cachedV8ValueUndefined;
    }

    public <T extends V8Value> T decorateV8Value(T v8Value) throws JavetException {
        if (v8Value != null) {
            if (v8Value.getV8Runtime() == null) {
                v8Value.setV8Runtime(this);
            } else if (v8Value.getV8Runtime() != this) {
                throw new JavetV8RuntimeAlreadyRegisteredException();
            }
        }
        return v8Value;
    }

    public <T extends V8Value> int decorateV8Values(T... v8Values) throws JavetException {
        if (v8Values != null && v8Values.length > 0) {
            for (T v8Value : v8Values) {
                decorateV8Value(v8Value);
            }
            return v8Values.length;
        }
        return 0;
    }

    public boolean delete(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        decorateV8Value(key);
        return v8Native.delete(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key);
    }

    public boolean equals(IV8ValueReference iV8ValueReference1, IV8ValueReference iV8ValueReference2)
            throws JavetException {
        return v8Native.equals(handle, iV8ValueReference1.getHandle(), iV8ValueReference2.getHandle());
    }

    public <T extends V8Value> T execute(
            String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) throws JavetException {
        return decorateV8Value((T) v8Native.execute(
                handle, scriptString, resultRequired, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule()));
    }

    public <T extends V8Value> T get(
            IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return decorateV8Value((T) v8Native.get(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key));
    }

    public int getCallbackContextCount() {
        return callbackContextMap.size();
    }

    public String getGlobalName() {
        return globalName;
    }

    public void setGlobalName(String globalName) {
        this.globalName = globalName;
    }

    public IV8Executor getExecutor(File scriptFile) throws JavetException {
        return getExecutor(scriptFile.toPath());
    }

    public IV8Executor getExecutor(Path scriptPath) throws JavetException {
        return new V8PathExecutor(this, scriptPath);
    }

    public IV8Executor getExecutor(String scriptString) {
        return new V8StringExecutor(this, scriptString);
    }

    public int getIdentityHash(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Native.getIdentityHash(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    public IJavetLogger getLogger() {
        return logger;
    }

    public void setLogger(IJavetLogger logger) {
        this.logger = logger;
    }

    public V8ValueGlobalObject getGlobalObject() throws JavetException {
        return decorateV8Value((V8ValueGlobalObject) v8Native.getGlobalObject(handle));
    }

    public long getHandle() {
        return handle;
    }

    public JSRuntimeType getJSRuntimeType() {
        return JSRuntimeType.V8;
    }

    public int getLength(IV8ValueArray iV8ValueArray) throws JavetException {
        return v8Native.getLength(handle, iV8ValueArray.getHandle(), iV8ValueArray.getType().getId());
    }

    public int getLength(IV8ValueTypedArray iV8ValueTypedArray) throws JavetException {
        return v8Native.getLength(handle, iV8ValueTypedArray.getHandle(), iV8ValueTypedArray.getType().getId());
    }

    public IV8ValueArray getOwnPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        return decorateV8Value((V8ValueArray) v8Native.getOwnPropertyNames(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId()));
    }

    public IJavetPromiseRejectCallback getPromiseRejectCallback() {
        return promiseRejectCallback;
    }

    public void setPromiseRejectCallback(IJavetPromiseRejectCallback promiseRejectCallback) {
        Objects.requireNonNull(promiseRejectCallback);
        this.promiseRejectCallback = promiseRejectCallback;
    }

    public <T extends V8Value> T getProperty(
            IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        decorateV8Value(key);
        return decorateV8Value((T) v8Native.getProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key));
    }

    public IV8ValueArray getPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        return decorateV8Value((V8ValueArray) v8Native.getPropertyNames(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId()));
    }

    public int getReferenceCount() {
        return referenceMap.size();
    }

    public int getSize(IV8ValueKeyContainer iV8ValueKeyContainer) throws JavetException {
        return v8Native.getSize(handle, iV8ValueKeyContainer.getHandle(), iV8ValueKeyContainer.getType().getId());
    }

    public V8Inspector getV8Inspector() {
        return getV8Inspector(MessageFormat.format(DEFAULT_MESSAGE_FORMAT_JAVET_INSPECTOR, Long.toString(handle)));
    }

    public V8Inspector getV8Inspector(String name) {
        if (v8Inspector == null) {
            v8Inspector = new V8Inspector(this, name, v8Native);
        }
        return v8Inspector;
    }

    public V8Locker getV8Locker() throws JavetV8LockConflictException {
        return new V8Locker(this, v8Native);
    }

    public IV8Module getV8Module(String resourceName) {
        return v8ModuleMap.get(resourceName);
    }

    public int getV8ModuleCount() {
        return v8ModuleMap.size();
    }

    public String getVersion() {
        return v8Native.getVersion();
    }

    public boolean has(IV8ValueObject iV8ValueObject, V8Value value) throws JavetException {
        decorateV8Value(value);
        return v8Native.has(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), value);
    }

    public boolean hasOwnProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        decorateV8Value(key);
        return v8Native.hasOwnProperty(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key);
    }

    protected void initializeV8ValueCache() {
        try {
            cachedV8ValueNull = decorateV8Value(new V8ValueNull());
            cachedV8ValueUndefined = decorateV8Value(new V8ValueUndefined());
            cachedV8ValueBooleans = new V8ValueBoolean[]{
                    decorateV8Value(new V8ValueBoolean(false)),
                    decorateV8Value(new V8ValueBoolean(true))};
            cachedV8ValueIntegers = new V8ValueInteger[V8_VALUE_NUMBER_UPPER_BOUND - V8_VALUE_NUMBER_LOWER_BOUND];
            cachedV8ValueLongs = new V8ValueLong[V8_VALUE_NUMBER_UPPER_BOUND - V8_VALUE_NUMBER_LOWER_BOUND];
            IntStream.range(V8_VALUE_NUMBER_LOWER_BOUND, V8_VALUE_NUMBER_UPPER_BOUND).forEach(i -> {
                try {
                    cachedV8ValueIntegers[i - V8_VALUE_NUMBER_LOWER_BOUND] = decorateV8Value(new V8ValueInteger(i));
                    cachedV8ValueLongs[i - V8_VALUE_NUMBER_LOWER_BOUND] = decorateV8Value(new V8ValueLong(i));
                } catch (JavetException e) {
                    logger.logError(e, e.getMessage());
                }
            });
        } catch (JavetException e) {
            logger.logError(e, e.getMessage());
        }
    }

    public <T extends V8Value> T invoke(
            IV8ValueObject iV8ValueObject, String functionName, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        decorateV8Values(v8Values);
        return decorateV8Value((T) v8Native.invoke(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), functionName, returnResult, v8Values));
    }

    public boolean isDead() {
        return v8Native.isDead(handle);
    }

    public boolean isGCScheduled() {
        return gcScheduled;
    }

    public void setGCScheduled(boolean gcScheduled) {
        this.gcScheduled = gcScheduled;
    }

    /**
     * Idle notification deadline.
     * <p>
     * Note: This API is the recommended one that notifies V8 to perform GC.
     *
     * @param deadlineInMillis the deadline in millis
     */
    public void idleNotificationDeadline(long deadlineInMillis) {
        if (handle != INVALID_HANDLE && deadlineInMillis > 0) {
            v8Native.idleNotificationDeadline(handle, deadlineInMillis);
        }
    }

    public boolean isInUse() {
        return v8Native.isInUse(handle);
    }

    public boolean isPooled() {
        return pooled;
    }

    public boolean isWeak(IV8ValueReference iV8ValueReference) {
        return v8Native.isWeak(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    /**
     * Send low memory notification to current V8 isolate.
     */
    public void lowMemoryNotification() {
        if (handle != INVALID_HANDLE) {
            v8Native.lowMemoryNotification(handle);
        }
    }

    public <T extends V8Value> T moduleEvaluate(
            IV8Module iV8Module, boolean resultRequired) throws JavetException {
        return decorateV8Value((T) v8Native.moduleEvaluate(
                handle, iV8Module.getHandle(), iV8Module.getType().getId(), resultRequired));
    }

    public V8ValueError moduleGetException(IV8Module iV8Module) throws JavetException {
        return decorateV8Value((V8ValueError) v8Native.moduleGetException(handle, iV8Module.getHandle(), iV8Module.getType().getId()));
    }

    public V8ValueObject moduleGetNamespace(IV8Module iV8Module) throws JavetException {
        return decorateV8Value((V8ValueObject) v8Native.moduleGetNamespace(
                handle, iV8Module.getHandle(), iV8Module.getType().getId()));
    }

    public int moduleGetScriptId(IV8Module iV8Module) throws JavetException {
        return v8Native.moduleGetScriptId(handle, iV8Module.getHandle(), iV8Module.getType().getId());
    }

    public int moduleGetStatus(IV8Module iV8Module) throws JavetException {
        return v8Native.moduleGetStatus(handle, iV8Module.getHandle(), iV8Module.getType().getId());
    }

    public boolean moduleInstantiate(IV8Module iV8Module) throws JavetException {
        return v8Native.moduleInstantiate(handle, iV8Module.getHandle(), iV8Module.getType().getId());
    }

    public <T extends V8ValuePromise> T promiseCatch(
            IV8ValuePromise iV8ValuePromise, IV8ValueFunction functionHandle) throws JavetException {
        return decorateV8Value((T) v8Native.promiseCatch(
                handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId(), functionHandle.getHandle()));
    }

    public <T extends V8Value> T promiseGetResult(IV8ValuePromise iV8ValuePromise) throws JavetException {
        return decorateV8Value((T) v8Native.promiseGetResult(
                handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId()));
    }

    public int promiseGetState(IV8ValuePromise iV8ValuePromise) {
        return v8Native.promiseGetState(handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId());
    }

    public boolean promiseHasHandler(IV8ValuePromise iV8ValuePromise) {
        return v8Native.promiseHasHandler(handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId());
    }

    public void promiseMarkAsHandled(IV8ValuePromise iV8ValuePromise) {
        v8Native.promiseMarkAsHandled(handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId());
    }

    public <T extends V8ValuePromise> T promiseThen(
            IV8ValuePromise iV8ValuePromise, IV8ValueFunction functionFulfilledHandle,
            IV8ValueFunction functionRejectedHandle) throws JavetException {
        return decorateV8Value((T) v8Native.promiseThen(
                handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId(),
                functionFulfilledHandle.getHandle(),
                functionRejectedHandle == null ? 0L : functionRejectedHandle.getHandle()));
    }

    protected void removeAllReferences() throws JavetException {
        removeReferences();
        removeCallbackContexts();
        removeV8Modules();
        v8Inspector = null;
    }

    public void removeCallbackContext(long handle) {
        callbackContextMap.remove(handle);
    }

    protected void removeCallbackContexts() {
        if (!callbackContextMap.isEmpty()) {
            final int callbackContextCount = callbackContextMap.size();
            for (long handle : callbackContextMap.keySet()) {
                removeJNIGlobalRef(handle);
            }
            logger.logWarn("{0} V8 callback context object(s) not recycled.",
                    Integer.toString(callbackContextCount));
            callbackContextMap.clear();
        }
    }

    public void removeJNIGlobalRef(long handle) {
        if (handle != INVALID_HANDLE) {
            v8Native.removeJNIGlobalRef(handle);
        }
    }

    public void removeReference(IV8ValueReference iV8ValueReference) {
        final long referenceHandle = iV8ValueReference.getHandle();
        if (referenceMap.containsKey(referenceHandle)) {
            final int referenceType = iV8ValueReference.getType().getId();
            if (referenceType == V8ValueReferenceType.Module.getId()) {
                removeV8Module((IV8Module) iV8ValueReference);
            }
            v8Native.removeReferenceHandle(referenceHandle, referenceType);
            referenceMap.remove(referenceHandle);
        }
        if (gcScheduled) {
            lowMemoryNotification();
            gcScheduled = false;
        }
    }

    protected void removeReferences() throws JavetException {
        if (!referenceMap.isEmpty()) {
            final int referenceCount = referenceMap.size();
            int weakReferenceCount = 0;
            for (IV8ValueReference iV8ValueReference : new ArrayList<>(referenceMap.values())) {
                if (iV8ValueReference instanceof IV8ValueObject) {
                    IV8ValueObject iV8ValueObject = (IV8ValueObject) iV8ValueReference;
                    if (iV8ValueObject.isWeak()) {
                        ++weakReferenceCount;
                    }
                }
                iV8ValueReference.close(true);
            }
            if (weakReferenceCount < referenceCount) {
                logger.logWarn("{0} V8 object(s) not recycled, {1} weak.",
                        Integer.toString(referenceCount), Integer.toString(weakReferenceCount));
            } else {
                logger.logDebug("{0} V8 object(s) not recycled, {1} weak.",
                        Integer.toString(referenceCount), Integer.toString(weakReferenceCount));
            }
            referenceMap.clear();
        }
    }

    public void removeV8Module(String resourceName) {
        v8ModuleMap.remove(resourceName);
    }

    public void removeV8Module(IV8Module iV8Module) {
        v8ModuleMap.remove(iV8Module.getResourceName());
    }

    protected void removeV8Modules() {
        if (!v8ModuleMap.isEmpty()) {
            logger.logWarn("{0} V8 module(s) not recycled.", Integer.toString(v8ModuleMap.size()));
            for (IV8Module iV8Module : v8ModuleMap.values()) {
                logger.logWarn("  V8 module: {0}", iV8Module.getResourceName());
            }
        }
        v8ModuleMap.clear();
    }

    /**
     * Requests GC for testing.
     * Note: --expose_gc must be set.
     *
     * @param fullGC true = Full GC, false = Minor GC
     */
    public void requestGarbageCollectionForTesting(boolean fullGC) {
        v8Native.requestGarbageCollectionForTesting(handle, fullGC);
    }

    /**
     * Reset V8 context.
     * This is a light-weight and recommended reset.
     *
     * @return the self
     * @throws JavetException the javet exception
     */
    public V8Runtime resetContext() throws JavetException {
        removeAllReferences();
        v8Native.resetV8Context(handle, globalName);
        return this;
    }

    /**
     * Reset V8 isolate.
     * This is a heavy reset. Please avoid using it in performance sensitive scenario.
     *
     * @return the self
     * @throws JavetException the javet exception
     */
    public V8Runtime resetIsolate() throws JavetException {
        removeAllReferences();
        v8Native.resetV8Isolate(handle, globalName);
        return this;
    }

    protected void receivePromiseRejectCallback(int event, V8ValuePromise promise, V8Value value) {
        try {
            decorateV8Values(promise, value);
            promiseRejectCallback.callback(JavetPromiseRejectEvent.parse(event), promise, value);
        } catch (Throwable t) {
            logger.logError(t, "Failed to process promise reject callback {0}.", event);
        } finally {
            JavetResourceUtils.safeClose(promise, value);
        }
    }

    public boolean sameValue(IV8ValueObject iV8ValueObject1, IV8ValueObject iV8ValueObject2) {
        return v8Native.sameValue(handle, iV8ValueObject1.getHandle(), iV8ValueObject2.getHandle());
    }

    public <T extends V8Value> T scriptRun(
            IV8Script iV8Script, boolean resultRequired) throws JavetException {
        return decorateV8Value((T) v8Native.scriptRun(
                handle, iV8Script.getHandle(), iV8Script.getType().getId(), resultRequired));
    }

    public boolean set(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        decorateV8Values(key, value);
        return v8Native.set(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key, value);
    }

    public boolean setProperty(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        decorateV8Values(key, value);
        return v8Native.setProperty(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key, value);
    }

    public void setWeak(IV8ValueReference iV8ValueReference) {
        v8Native.setWeak(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId(), iV8ValueReference);
    }

    public boolean strictEquals(IV8ValueObject iV8ValueObject1, IV8ValueObject iV8ValueObject2) {
        return v8Native.strictEquals(handle, iV8ValueObject1.getHandle(), iV8ValueObject2.getHandle());
    }

    /**
     * Terminate execution.
     * <p>
     * Forcefully terminate the current thread of JavaScript execution
     * in the given isolate.
     * <p>
     * This method can be used by any thread even if that thread has not
     * acquired the V8 lock with a Locker object.
     */
    public void terminateExecution() {
        v8Native.terminateExecution(handle);
    }

    public String toProtoString(IV8ValueReference iV8ValueReference) throws JavetV8RuntimeAlreadyClosedException {
        return v8Native.toProtoString(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    public String toString(IV8ValueReference iV8ValueReference) throws JavetV8RuntimeAlreadyClosedException {
        return v8Native.toString(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }
}
