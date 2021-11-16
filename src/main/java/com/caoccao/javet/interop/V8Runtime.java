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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.*;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.JavetOutOfMemoryException;
import com.caoccao.javet.interfaces.IEnumBitset;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interfaces.IV8ModuleResolver;
import com.caoccao.javet.interop.binding.BindingContext;
import com.caoccao.javet.interop.callback.IJavetGCCallback;
import com.caoccao.javet.interop.callback.IJavetPromiseRejectCallback;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetPromiseRejectCallback;
import com.caoccao.javet.interop.converters.IJavetConverter;
import com.caoccao.javet.interop.converters.JavetObjectConverter;
import com.caoccao.javet.interop.executors.IV8Executor;
import com.caoccao.javet.interop.executors.V8FileExecutor;
import com.caoccao.javet.interop.executors.V8PathExecutor;
import com.caoccao.javet.interop.executors.V8StringExecutor;
import com.caoccao.javet.interop.monitoring.V8HeapSpaceStatistics;
import com.caoccao.javet.interop.monitoring.V8HeapStatistics;
import com.caoccao.javet.interop.monitoring.V8SharedMemoryStatistics;
import com.caoccao.javet.interop.options.RuntimeOptions;
import com.caoccao.javet.utils.JavetDefaultLogger;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.IV8Value;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.*;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The representation of a V8 isolate (and V8 context).
 * <p>
 * Javet simplifies the V8 runtime model to 1 runtime - 1 isolate - 1 context,
 * though in V8 1 isolate can host multiple contexts.
 * <p>
 * V8 runtime exposes many useful methods and callbacks that allow low level
 * interaction with the V8 isolate or context.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public class V8Runtime implements IJavetClosable, IV8Creatable, IV8Convertible {
    protected static final IJavetConverter DEFAULT_CONVERTER = new JavetObjectConverter();
    protected static final String DEFAULT_MESSAGE_FORMAT_JAVET_INSPECTOR = "Javet Inspector {0}";
    protected static final long INVALID_HANDLE = 0L;
    protected static final String PROPERTY_DATA_VIEW = "DataView";
    protected static final int V8_VALUE_BOOLEAN_FALSE_INDEX = 0;
    protected static final int V8_VALUE_BOOLEAN_TRUE_INDEX = 1;
    protected static final int V8_VALUE_NUMBER_LOWER_BOUND = -128; // Inclusive
    protected static final int V8_VALUE_NUMBER_UPPER_BOUND = 128; // Exclusive
    protected Map<Class<?>, BindingContext> bindingContextWeakHashMap;
    protected V8ValueBoolean[] cachedV8ValueBooleans;
    protected V8ValueInteger[] cachedV8ValueIntegers;
    protected V8ValueLong[] cachedV8ValueLongs;
    protected V8ValueNull cachedV8ValueNull;
    protected V8ValueUndefined cachedV8ValueUndefined;
    protected ReadWriteLock callbackContextLock;
    /**
     * The Callback context map.
     * <p>
     * V8 may not make final callback when V8 context is being recycled.
     * That may result in memory leak.
     * Callback context map is designed for closing that memory leak issue.
     *
     * @since 0.8.3
     */
    protected Map<Long, JavetCallbackContext> callbackContextMap;
    protected IJavetConverter converter;
    protected List<IJavetGCCallback> gcEpilogueCallbacks;
    protected List<IJavetGCCallback> gcPrologueCallbacks;
    protected boolean gcScheduled;
    protected long handle;
    protected IJavetLogger logger;
    protected boolean pooled;
    protected IJavetPromiseRejectCallback promiseRejectCallback;
    protected ReadWriteLock referenceLock;
    protected Map<Long, IV8ValueReference> referenceMap;
    protected RuntimeOptions<?> runtimeOptions;
    protected V8Host v8Host;
    protected V8Inspector v8Inspector;
    protected V8Internal v8Internal;
    protected ReadWriteLock v8ModuleLock;
    protected Map<String, IV8Module> v8ModuleMap;
    protected IV8ModuleResolver v8ModuleResolver;
    protected IV8Native v8Native;

    V8Runtime(V8Host v8Host, long handle, boolean pooled, IV8Native v8Native, RuntimeOptions<?> runtimeOptions) {
        assert handle != 0;
        bindingContextWeakHashMap = Collections.synchronizedMap(new WeakHashMap<>());
        callbackContextLock = new ReentrantReadWriteLock();
        callbackContextMap = new HashMap<>();
        converter = DEFAULT_CONVERTER;
        gcEpilogueCallbacks = new CopyOnWriteArrayList<>();
        gcPrologueCallbacks = new CopyOnWriteArrayList<>();
        gcScheduled = false;
        this.runtimeOptions = Objects.requireNonNull(runtimeOptions);
        this.handle = handle;
        logger = new JavetDefaultLogger(getClass().getName());
        this.pooled = pooled;
        promiseRejectCallback = new JavetPromiseRejectCallback(logger);
        referenceLock = new ReentrantReadWriteLock();
        referenceMap = new HashMap<>();
        this.v8Host = Objects.requireNonNull(v8Host);
        v8Inspector = null;
        this.v8Native = Objects.requireNonNull(v8Native);
        v8ModuleLock = new ReentrantReadWriteLock();
        v8ModuleMap = new HashMap<>();
        v8ModuleResolver = null;
        v8Internal = new V8Internal(this);
        initializeV8ValueCache();
    }

    void add(IV8ValueSet iV8ValueKeySet, V8Value value) throws JavetException {
        decorateV8Value(value);
        v8Native.add(handle, iV8ValueKeySet.getHandle(), iV8ValueKeySet.getType().getId(), value);
    }

    public void addGCEpilogueCallback(IJavetGCCallback iJavetGCCallback) {
        synchronized (gcEpilogueCallbacks) {
            boolean registered = !gcEpilogueCallbacks.isEmpty();
            gcEpilogueCallbacks.add(Objects.requireNonNull(iJavetGCCallback));
            if (!registered) {
                v8Native.registerGCEpilogueCallback(handle);
            }
        }
    }

    public void addGCPrologueCallback(IJavetGCCallback iJavetGCCallback) {
        synchronized (gcPrologueCallbacks) {
            boolean registered = !gcPrologueCallbacks.isEmpty();
            gcPrologueCallbacks.add(Objects.requireNonNull(iJavetGCCallback));
            if (!registered) {
                v8Native.registerGCPrologueCallback(handle);
            }
        }
    }

    void addReference(IV8ValueReference iV8ValueReference) {
        Lock readLock = referenceLock.readLock();
        try {
            readLock.lock();
            referenceMap.put(iV8ValueReference.getHandle(), iV8ValueReference);
        } finally {
            readLock.unlock();
        }
    }

    public void addV8Module(IV8Module iV8Module) {
        Lock readLock = v8ModuleLock.readLock();
        try {
            readLock.lock();
            v8ModuleMap.put(iV8Module.getResourceName(), iV8Module);
        } finally {
            readLock.unlock();
        }
    }

    public void allowEval(boolean allow) {
        v8Native.allowCodeGenerationFromStrings(handle, allow);
    }

    public void await() {
        v8Native.await(handle);
    }

    @CheckReturnValue
    <T extends V8Value> T call(
            IV8ValueObject iV8ValueObject, IV8ValueObject receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        decorateV8Values(v8Values);
        return decorateV8Value((T) v8Native.call(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), receiver, returnResult, v8Values));
    }

    @CheckReturnValue
    <T extends V8Value> T callAsConstructor(
            IV8ValueObject iV8ValueObject, V8Value... v8Values) throws JavetException {
        decorateV8Values(v8Values);
        return decorateV8Value((T) v8Native.callAsConstructor(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), v8Values));
    }

    @SuppressWarnings("RedundantThrows")
    void clearWeak(IV8ValueReference iV8ValueReference) throws JavetException {
        v8Native.clearWeak(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    @CheckReturnValue
    <T extends V8Value> T cloneV8Value(IV8ValueReference iV8ValueReference) throws JavetException {
        return decorateV8Value((T) v8Native.cloneV8Value(
                handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId()));
    }

    @Override
    public void close() throws JavetException {
        close(!pooled);
    }

    public void close(boolean forceClose) throws JavetException {
        if (!isClosed() && forceClose) {
            removeAllReferences();
            v8Host.closeV8Runtime(this);
            handle = INVALID_HANDLE;
            v8Native = null;
        }
    }

    @CheckReturnValue
    public V8Module compileV8Module(String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired)
            throws JavetException {
        v8ScriptOrigin.setModule(true);
        if (v8ScriptOrigin.getResourceName() == null || v8ScriptOrigin.getResourceName().length() == 0) {
            throw new JavetException(JavetError.ModuleNameEmpty);
        }
        Object result = v8Native.compile(
                handle, scriptString, resultRequired, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule());
        V8Module v8Module = null;
        if (resultRequired && result instanceof V8Module) {
            v8Module = decorateV8Value((V8Module) result);
            v8Module.setResourceName(v8ScriptOrigin.getResourceName());
            addV8Module(v8Module);
        }
        return v8Module;
    }

    @CheckReturnValue
    public V8Script compileV8Script(String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired)
            throws JavetException {
        v8ScriptOrigin.setModule(false);
        return decorateV8Value((V8Script) v8Native.compile(
                handle, scriptString, resultRequired, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule()));
    }

    public boolean containsV8Module(String resourceName) {
        Lock readLock = v8ModuleLock.readLock();
        try {
            readLock.lock();
            return v8ModuleMap.containsKey(resourceName);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    @CheckReturnValue
    public V8ValueArray createV8ValueArray() throws JavetException {
        return decorateV8Value((V8ValueArray) v8Native.createV8Value(
                handle, V8ValueReferenceType.Array.getId(), null));
    }

    @Override
    @CheckReturnValue
    public V8ValueArrayBuffer createV8ValueArrayBuffer(int length) throws JavetException {
        return decorateV8Value((V8ValueArrayBuffer) v8Native.createV8Value(
                handle, V8ValueReferenceType.ArrayBuffer.getId(), createV8ValueInteger(length)));
    }

    @Override
    @SuppressWarnings("RedundantThrows")
    public V8ValueBoolean createV8ValueBoolean(boolean booleanValue) throws JavetException {
        return booleanValue ?
                cachedV8ValueBooleans[V8_VALUE_BOOLEAN_TRUE_INDEX] :
                cachedV8ValueBooleans[V8_VALUE_BOOLEAN_FALSE_INDEX];
    }

    @Override
    @CheckReturnValue
    public V8ValueDataView createV8ValueDataView(V8ValueArrayBuffer v8ValueArrayBuffer) throws JavetException {
        Objects.requireNonNull(v8ValueArrayBuffer);
        try (V8ValueFunction v8ValueFunction = getGlobalObject().get(PROPERTY_DATA_VIEW)) {
            return v8ValueFunction.callAsConstructor(v8ValueArrayBuffer);
        }
    }

    @Override
    public V8ValueDouble createV8ValueDouble(double doubleValue) throws JavetException {
        return decorateV8Value(new V8ValueDouble(doubleValue));
    }

    @Override
    @CheckReturnValue
    public V8ValueFunction createV8ValueFunction(JavetCallbackContext javetCallbackContext) throws JavetException {
        Objects.requireNonNull(javetCallbackContext);
        try {
            V8ValueFunction v8ValueFunction = decorateV8Value((V8ValueFunction) v8Native.createV8Value(
                    handle, V8ValueReferenceType.Function.getId(), javetCallbackContext));
            Lock writeLock = callbackContextLock.writeLock();
            try {
                writeLock.lock();
                callbackContextMap.put(javetCallbackContext.getHandle(), javetCallbackContext);
            } finally {
                writeLock.unlock();
            }
            return v8ValueFunction;
        } catch (JavetOutOfMemoryException e) {
            logger.error(getV8HeapSpaceStatistics(V8AllocationSpace.RO_SPACE).toString());
            logger.error(getV8HeapSpaceStatistics(V8AllocationSpace.OLD_SPACE).toString());
            logger.error(getV8HeapSpaceStatistics(V8AllocationSpace.CODE_SPACE).toString());
            logger.error(getV8HeapSpaceStatistics(V8AllocationSpace.MAP_SPACE).toString());
            logger.error(getV8HeapSpaceStatistics(V8AllocationSpace.LO_SPACE).toString());
            logger.error(getV8HeapSpaceStatistics(V8AllocationSpace.CODE_LO_SPACE).toString());
            logger.error(getV8HeapSpaceStatistics(V8AllocationSpace.NEW_LO_SPACE).toString());
            logger.error(getV8HeapSpaceStatistics(V8AllocationSpace.NEW_SPACE).toString());
            throw e;
        }
    }

    @Override
    @CheckReturnValue
    public V8ValueFunction createV8ValueFunction(String codeString) throws JavetException {
        return getExecutor(codeString).execute();
    }

    @Override
    public V8ValueInteger createV8ValueInteger(int integerValue) throws JavetException {
        if (integerValue >= V8_VALUE_NUMBER_LOWER_BOUND && integerValue < V8_VALUE_NUMBER_UPPER_BOUND) {
            return cachedV8ValueIntegers[integerValue - V8_VALUE_NUMBER_LOWER_BOUND];
        }
        return decorateV8Value(new V8ValueInteger(integerValue));
    }

    @Override
    @CheckReturnValue
    public V8ValueLong createV8ValueLong(long longValue) throws JavetException {
        if (longValue >= V8_VALUE_NUMBER_LOWER_BOUND && longValue < V8_VALUE_NUMBER_UPPER_BOUND) {
            return cachedV8ValueLongs[(int) longValue - V8_VALUE_NUMBER_LOWER_BOUND];
        }
        return decorateV8Value(new V8ValueLong(longValue));
    }

    @Override
    @CheckReturnValue
    public V8ValueMap createV8ValueMap() throws JavetException {
        return decorateV8Value((V8ValueMap) v8Native.createV8Value(
                handle, V8ValueReferenceType.Map.getId(), null));
    }

    @Override
    public V8ValueNull createV8ValueNull() {
        return cachedV8ValueNull;
    }

    @Override
    @CheckReturnValue
    public V8ValueObject createV8ValueObject() throws JavetException {
        return decorateV8Value((V8ValueObject) v8Native.createV8Value(
                handle, V8ValueReferenceType.Object.getId(), null));
    }

    @Override
    @CheckReturnValue
    public V8ValuePromise createV8ValuePromise() throws JavetException {
        return decorateV8Value((V8ValuePromise) v8Native.createV8Value(
                handle, V8ValueReferenceType.Promise.getId(), null));
    }

    @Override
    @CheckReturnValue
    public V8ValueProxy createV8ValueProxy(V8ValueObject v8ValueObject) throws JavetException {
        return decorateV8Value((V8ValueProxy) v8Native.createV8Value(
                handle, V8ValueReferenceType.Proxy.getId(), v8ValueObject));
    }

    @Override
    @CheckReturnValue
    public V8ValueSet createV8ValueSet() throws JavetException {
        return decorateV8Value((V8ValueSet) v8Native.createV8Value(
                handle, V8ValueReferenceType.Set.getId(), null));
    }

    @Override
    public V8ValueString createV8ValueString(String str) throws JavetException {
        return decorateV8Value(new V8ValueString(str));
    }

    @Override
    @CheckReturnValue
    public V8ValueSymbol createV8ValueSymbol(String description, boolean global) throws JavetException {
        Objects.requireNonNull(description);
        assert description.length() > 0;
        if (global) {
            try (V8ValueBuiltInSymbol v8ValueBuiltInSymbol = getGlobalObject().getBuiltInSymbol()) {
                return v8ValueBuiltInSymbol._for(description);
            }
        } else {
            return decorateV8Value((V8ValueSymbol) v8Native.createV8Value(
                    handle, V8ValueReferenceType.Symbol.getId(), description));
        }
    }

    @Override
    @CheckReturnValue
    public V8ValueTypedArray createV8ValueTypedArray(V8ValueReferenceType type, int length) throws JavetException {
        try (V8ValueFunction v8ValueFunction = getGlobalObject().get(type.getName())) {
            return v8ValueFunction.callAsConstructor(length);
        }
    }

    @Override
    public V8ValueUndefined createV8ValueUndefined() {
        return cachedV8ValueUndefined;
    }

    @Override
    public V8ValueZonedDateTime createV8ValueZonedDateTime(long jsTimestamp) throws JavetException {
        return decorateV8Value(new V8ValueZonedDateTime(jsTimestamp));
    }

    @Override
    public V8ValueZonedDateTime createV8ValueZonedDateTime(ZonedDateTime zonedDateTime) throws JavetException {
        return decorateV8Value(new V8ValueZonedDateTime(zonedDateTime));
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T extends IV8Value> T decorateV8Value(T v8Value) throws JavetException {
        if (v8Value != null) {
            if (v8Value.getV8Runtime() == null) {
                v8Value.setV8Runtime(this);
            } else if (v8Value.getV8Runtime() != this) {
                throw new JavetException(JavetError.RuntimeAlreadyRegistered);
            }
        }
        return v8Value;
    }

    @SuppressWarnings("UnusedReturnValue")
    public <T extends IV8Value> int decorateV8Values(T... v8Values) throws JavetException {
        if (v8Values != null && v8Values.length > 0) {
            for (T v8Value : v8Values) {
                decorateV8Value(v8Value);
            }
            return v8Values.length;
        }
        return 0;
    }

    boolean delete(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        decorateV8Value(key);
        return v8Native.delete(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key);
    }

    boolean deletePrivateProperty(IV8ValueObject iV8ValueObject, String propertyName) throws JavetException {
        return v8Native.deletePrivateProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), propertyName);
    }

    boolean equals(IV8ValueReference iV8ValueReference1, IV8ValueReference iV8ValueReference2)
            throws JavetException {
        return v8Native.equals(handle, iV8ValueReference1.getHandle(), iV8ValueReference2.getHandle());
    }

    @CheckReturnValue
    public <T extends V8Value> T execute(
            String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) throws JavetException {
        return decorateV8Value((T) v8Native.execute(
                handle, scriptString, resultRequired, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule()));
    }

    void gcEpilogueCallback(int v8GCTypeValue, int v8GCCallbackFlagsValue) {
        final EnumSet<V8GCType> enumSetV8GCType = IEnumBitset.getEnumSet(v8GCTypeValue, V8GCType.class);
        final EnumSet<V8GCCallbackFlags> enumSetV8GCCallbackFlags = IEnumBitset.getEnumSet(
                v8GCCallbackFlagsValue, V8GCCallbackFlags.class, V8GCCallbackFlags.NoGCCallbackFlags);
        for (IJavetGCCallback iJavetGCCallback : gcEpilogueCallbacks) {
            iJavetGCCallback.callback(enumSetV8GCType, enumSetV8GCCallbackFlags);
        }
    }

    void gcPrologueCallback(int v8GCTypeValue, int v8GCCallbackFlagsValue) {
        final EnumSet<V8GCType> enumSetV8GCType = IEnumBitset.getEnumSet(v8GCTypeValue, V8GCType.class);
        final EnumSet<V8GCCallbackFlags> enumSetV8GCCallbackFlags = IEnumBitset.getEnumSet(
                v8GCCallbackFlagsValue, V8GCCallbackFlags.class, V8GCCallbackFlags.NoGCCallbackFlags);
        for (IJavetGCCallback iJavetGCCallback : gcPrologueCallbacks) {
            iJavetGCCallback.callback(enumSetV8GCType, enumSetV8GCCallbackFlags);
        }
    }

    @CheckReturnValue
    <T extends V8Value> T get(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return decorateV8Value((T) v8Native.get(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key));
    }

    public Map<Class<?>, BindingContext> getBindingContextWeakHashMap() {
        return bindingContextWeakHashMap;
    }

    public JavetCallbackContext getCallbackContext(long handle) {
        Lock readLock = callbackContextLock.readLock();
        try {
            readLock.lock();
            return callbackContextMap.get(handle);
        } finally {
            readLock.unlock();
        }
    }

    public int getCallbackContextCount() {
        Lock readLock = callbackContextLock.readLock();
        try {
            readLock.lock();
            return callbackContextMap.size();
        } finally {
            readLock.unlock();
        }
    }

    public IJavetConverter getConverter() {
        return converter;
    }

    public IV8Executor getExecutor(File scriptFile) throws JavetException {
        return new V8FileExecutor(this, scriptFile);
    }

    public IV8Executor getExecutor(Path scriptPath) throws JavetException {
        return new V8PathExecutor(this, scriptPath);
    }

    public IV8Executor getExecutor(String scriptString) {
        return new V8StringExecutor(this, scriptString);
    }

    public V8ValueGlobalObject getGlobalObject() throws JavetException {
        return decorateV8Value((V8ValueGlobalObject) v8Native.getGlobalObject(handle));
    }

    public long getHandle() {
        return handle;
    }

    @SuppressWarnings("RedundantThrows")
    int getIdentityHash(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Native.getIdentityHash(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    @CheckReturnValue
    IV8ValueArray getInternalProperties(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return decorateV8Value((V8ValueArray) v8Native.getInternalProperties(
                handle, iV8ValueFunction.getHandle(), iV8ValueFunction.getType().getId()));
    }

    JSFunctionType getJSFunctionType(IV8ValueFunction iV8ValueFunction) {
        return JSFunctionType.parse(v8Native.getJSFunctionType(
                handle, iV8ValueFunction.getHandle(), iV8ValueFunction.getType().getId()));
    }

    public JSRuntimeType getJSRuntimeType() {
        return JSRuntimeType.V8;
    }

    JSScopeType getJSScopeType(IV8ValueFunction iV8ValueFunction) {
        return JSScopeType.parse(v8Native.getJSScopeType(
                handle, iV8ValueFunction.getHandle(), iV8ValueFunction.getType().getId()));
    }

    int getLength(IV8ValueArray iV8ValueArray) throws JavetException {
        return v8Native.getLength(handle, iV8ValueArray.getHandle(), iV8ValueArray.getType().getId());
    }

    int getLength(IV8ValueTypedArray iV8ValueTypedArray) throws JavetException {
        return v8Native.getLength(handle, iV8ValueTypedArray.getHandle(), iV8ValueTypedArray.getType().getId());
    }

    public IJavetLogger getLogger() {
        return logger;
    }

    @CheckReturnValue
    IV8ValueArray getOwnPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        return decorateV8Value((V8ValueArray) v8Native.getOwnPropertyNames(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId()));
    }

    @CheckReturnValue
    <T extends V8Value> T getPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName)
            throws JavetException {
        return decorateV8Value((T) v8Native.getPrivateProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), propertyName));
    }

    public IJavetPromiseRejectCallback getPromiseRejectCallback() {
        return promiseRejectCallback;
    }

    @CheckReturnValue
    <T extends V8Value> T getProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        decorateV8Value(key);
        return decorateV8Value((T) v8Native.getProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key));
    }

    @CheckReturnValue
    IV8ValueArray getPropertyNames(IV8ValueObject iV8ValueObject) throws JavetException {
        return decorateV8Value((V8ValueArray) v8Native.getPropertyNames(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId()));
    }

    @CheckReturnValue
    <T extends IV8ValueObject> T getPrototype(IV8ValueObject iV8ValueObject) throws JavetException {
        return decorateV8Value((T) v8Native.getPrototype(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId()));
    }

    public int getReferenceCount() {
        Lock readLock = referenceLock.readLock();
        try {
            readLock.lock();
            return referenceMap.size();
        } finally {
            readLock.unlock();
        }
    }

    public RuntimeOptions<?> getRuntimeOptions() {
        return runtimeOptions;
    }

    int getSize(IV8ValueKeyContainer iV8ValueKeyContainer) throws JavetException {
        return v8Native.getSize(handle, iV8ValueKeyContainer.getHandle(), iV8ValueKeyContainer.getType().getId());
    }

    @SuppressWarnings("RedundantThrows")
    String getSourceCode(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Native.getSourceCode(handle, iV8ValueFunction.getHandle(), iV8ValueFunction.getType().getId());
    }

    public V8HeapSpaceStatistics getV8HeapSpaceStatistics(V8AllocationSpace v8AllocationSpace) {
        Objects.requireNonNull(v8AllocationSpace);
        return ((V8HeapSpaceStatistics) v8Native.getV8HeapSpaceStatistics(handle, v8AllocationSpace.getIndex()))
                .setAllocationSpace(v8AllocationSpace);
    }

    public V8HeapStatistics getV8HeapStatistics() {
        return (V8HeapStatistics) v8Native.getV8HeapStatistics(handle);
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

    public V8Internal getV8Internal() {
        return v8Internal;
    }

    @CheckReturnValue
    public V8Locker getV8Locker() throws JavetException {
        return new V8Locker(this, v8Native);
    }

    @CheckReturnValue
    IV8Module getV8Module(String resourceName, IV8Module v8ModuleReferrer) throws JavetException {
        if (resourceName != null && resourceName.length() > 0) {
            decorateV8Value(v8ModuleReferrer);
            Lock readLock = v8ModuleLock.readLock();
            try {
                readLock.lock();
                if (v8ModuleMap.containsKey(resourceName)) {
                    return v8ModuleMap.get(resourceName);
                }
            } finally {
                readLock.unlock();
            }
            if (v8ModuleResolver != null) {
                return v8ModuleResolver.resolve(this, resourceName, v8ModuleReferrer);
            }
        }
        return null;
    }

    public int getV8ModuleCount() {
        Lock readLock = v8ModuleLock.readLock();
        try {
            readLock.lock();
            return v8ModuleMap.size();
        } finally {
            readLock.unlock();
        }
    }

    public IV8ModuleResolver getV8ModuleResolver() {
        return v8ModuleResolver;
    }

    public V8Scope getV8Scope() {
        return new V8Scope(this);
    }

    public V8SharedMemoryStatistics getV8SharedMemoryStatistics() {
        return (V8SharedMemoryStatistics) v8Native.getV8SharedMemoryStatistics();
    }

    public String getVersion() {
        return v8Native.getVersion();
    }

    boolean has(IV8ValueObject iV8ValueObject, V8Value value) throws JavetException {
        decorateV8Value(value);
        return v8Native.has(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), value);
    }

    boolean hasInternalType(IV8ValueObject iV8ValueObject, V8ValueInternalType internalType) {
        return v8Native.hasInternalType(
                handle, iV8ValueObject.getHandle(), Objects.requireNonNull(internalType).getId());
    }

    boolean hasOwnProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        decorateV8Value(key);
        return v8Native.hasOwnProperty(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key);
    }

    public boolean hasPendingException() throws JavetException {
        return v8Native.hasPendingException(handle);
    }

    public boolean hasPendingMessage() throws JavetException {
        return v8Native.hasPendingMessage(handle);
    }

    public boolean hasScheduledException() throws JavetException {
        return v8Native.hasScheduledException(handle);
    }

    boolean hasPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName) throws JavetException {
        return v8Native.hasPrivateProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), propertyName);
    }

    /**
     * Idle notification deadline.
     * <p>
     * Note: This API is the recommended one that notifies V8 to perform GC.
     *
     * @param deadlineInMillis the deadline in millis
     */
    public void idleNotificationDeadline(long deadlineInMillis) {
        if (!isClosed() && deadlineInMillis > 0) {
            v8Native.idleNotificationDeadline(handle, deadlineInMillis);
        }
    }

    void initializeV8ValueCache() {
        try {
            cachedV8ValueNull = decorateV8Value(new V8ValueNull());
            cachedV8ValueUndefined = decorateV8Value(new V8ValueUndefined());
            cachedV8ValueBooleans = new V8ValueBoolean[]{
                    decorateV8Value(new V8ValueBoolean(false)),
                    decorateV8Value(new V8ValueBoolean(true))};
            cachedV8ValueIntegers = new V8ValueInteger[V8_VALUE_NUMBER_UPPER_BOUND - V8_VALUE_NUMBER_LOWER_BOUND];
            cachedV8ValueLongs = new V8ValueLong[V8_VALUE_NUMBER_UPPER_BOUND - V8_VALUE_NUMBER_LOWER_BOUND];
            for (int i = V8_VALUE_NUMBER_LOWER_BOUND; i < V8_VALUE_NUMBER_UPPER_BOUND; ++i) {
                try {
                    cachedV8ValueIntegers[i - V8_VALUE_NUMBER_LOWER_BOUND] = decorateV8Value(new V8ValueInteger(i));
                    cachedV8ValueLongs[i - V8_VALUE_NUMBER_LOWER_BOUND] = decorateV8Value(new V8ValueLong(i));
                } catch (JavetException e) {
                    logger.logError(e, e.getMessage());
                }
            }
        } catch (JavetException e) {
            logger.logError(e, e.getMessage());
        }
    }

    @CheckReturnValue
    <T extends V8Value> T invoke(
            IV8ValueObject iV8ValueObject, String functionName, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        decorateV8Values(v8Values);
        return decorateV8Value((T) v8Native.invoke(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), functionName, returnResult, v8Values));
    }

    @Override
    public boolean isClosed() {
        return handle == INVALID_HANDLE;
    }

    public boolean isDead() {
        return v8Native.isDead(handle);
    }

    public boolean isGCScheduled() {
        return gcScheduled;
    }

    public boolean isInUse() {
        return v8Native.isInUse(handle);
    }

    public boolean isPooled() {
        return pooled;
    }

    boolean isWeak(IV8ValueReference iV8ValueReference) {
        return v8Native.isWeak(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    /**
     * Send low memory notification to current V8 isolate.
     */
    public void lowMemoryNotification() {
        if (!isClosed()) {
            v8Native.lowMemoryNotification(handle);
        }
    }

    @CheckReturnValue
    <T extends V8Value> T moduleEvaluate(
            IV8Module iV8Module, boolean resultRequired) throws JavetException {
        return decorateV8Value((T) v8Native.moduleEvaluate(
                handle, iV8Module.getHandle(), iV8Module.getType().getId(), resultRequired));
    }

    @CheckReturnValue
    V8ValueError moduleGetException(IV8Module iV8Module) throws JavetException {
        return decorateV8Value((V8ValueError) v8Native.moduleGetException(
                handle, iV8Module.getHandle(), iV8Module.getType().getId()));
    }

    @CheckReturnValue
    V8ValueObject moduleGetNamespace(IV8Module iV8Module) throws JavetException {
        return decorateV8Value((V8ValueObject) v8Native.moduleGetNamespace(
                handle, iV8Module.getHandle(), iV8Module.getType().getId()));
    }

    @SuppressWarnings("RedundantThrows")
    int moduleGetScriptId(IV8Module iV8Module) throws JavetException {
        return v8Native.moduleGetScriptId(handle, iV8Module.getHandle(), iV8Module.getType().getId());
    }

    @SuppressWarnings("RedundantThrows")
    int moduleGetStatus(IV8Module iV8Module) throws JavetException {
        return v8Native.moduleGetStatus(handle, iV8Module.getHandle(), iV8Module.getType().getId());
    }

    @SuppressWarnings("RedundantThrows")
    boolean moduleInstantiate(IV8Module iV8Module) throws JavetException {
        return v8Native.moduleInstantiate(handle, iV8Module.getHandle(), iV8Module.getType().getId());
    }

    @CheckReturnValue
    <T extends V8ValuePromise> T promiseCatch(
            IV8ValuePromise iV8ValuePromise, IV8ValueFunction functionHandle) throws JavetException {
        return decorateV8Value((T) v8Native.promiseCatch(
                handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId(), functionHandle.getHandle()));
    }

    @CheckReturnValue
    V8ValuePromise promiseGetPromise(IV8ValuePromise iV8ValuePromise) throws JavetException {
        return decorateV8Value((V8ValuePromise) v8Native.promiseGetPromise(
                handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId()));
    }

    @CheckReturnValue
    <T extends V8Value> T promiseGetResult(IV8ValuePromise iV8ValuePromise) throws JavetException {
        return decorateV8Value((T) v8Native.promiseGetResult(
                handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId()));
    }

    int promiseGetState(IV8ValuePromise iV8ValuePromise) {
        return v8Native.promiseGetState(handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId());
    }

    boolean promiseHasHandler(IV8ValuePromise iV8ValuePromise) {
        return v8Native.promiseHasHandler(handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId());
    }

    void promiseMarkAsHandled(IV8ValuePromise iV8ValuePromise) {
        v8Native.promiseMarkAsHandled(handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId());
    }

    boolean promiseReject(V8ValuePromise v8ValuePromise, V8Value v8Value) {
        return v8Native.promiseReject(
                handle, v8ValuePromise.getHandle(), v8ValuePromise.getType().getId(), v8Value);
    }

    boolean promiseResolve(V8ValuePromise v8ValuePromise, V8Value v8Value) {
        return v8Native.promiseResolve(
                handle, v8ValuePromise.getHandle(), v8ValuePromise.getType().getId(), v8Value);
    }

    @CheckReturnValue
    <T extends V8ValuePromise> T promiseThen(
            IV8ValuePromise iV8ValuePromise, IV8ValueFunction functionFulfilledHandle,
            IV8ValueFunction functionRejectedHandle) throws JavetException {
        return decorateV8Value((T) v8Native.promiseThen(
                handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId(),
                functionFulfilledHandle.getHandle(),
                functionRejectedHandle == null ? 0L : functionRejectedHandle.getHandle()));
    }

    @CheckReturnValue
    V8ValueObject proxyGetHandler(IV8ValueProxy iV8ValueProxy) throws JavetException {
        return decorateV8Value((V8ValueObject) v8Native.proxyGetHandler(
                handle, iV8ValueProxy.getHandle(), iV8ValueProxy.getType().getId()));
    }

    @CheckReturnValue
    V8ValueObject proxyGetTarget(IV8ValueProxy iV8ValueProxy) throws JavetException {
        return decorateV8Value((V8ValueObject) v8Native.proxyGetTarget(
                handle, iV8ValueProxy.getHandle(), iV8ValueProxy.getType().getId()));
    }

    boolean proxyIsRevoked(IV8ValueProxy iV8ValueProxy) throws JavetException {
        return v8Native.proxyIsRevoked(handle, iV8ValueProxy.getHandle(), iV8ValueProxy.getType().getId());
    }

    void proxyRevoke(IV8ValueProxy iV8ValueProxy) throws JavetException {
        v8Native.proxyRevoke(handle, iV8ValueProxy.getHandle(), iV8ValueProxy.getType().getId());
    }

    void receivePromiseRejectCallback(int event, V8ValuePromise promise, V8Value value) {
        try {
            decorateV8Values(promise, value);
            promiseRejectCallback.callback(JavetPromiseRejectEvent.parse(event), promise, value);
        } catch (Throwable t) {
            logger.logError(t, "Failed to process promise reject callback {0}.", event);
        } finally {
            JavetResourceUtils.safeClose(promise, value);
        }
    }

    void removeAllReferences() throws JavetException {
        removeReferences();
        removeCallbackContexts();
        removeV8Modules();
        v8Inspector = null;
    }

    public void removeCallbackContext(long handle) {
        Lock writeLock = callbackContextLock.writeLock();
        try {
            writeLock.lock();
            callbackContextMap.remove(handle);
        } finally {
            writeLock.unlock();
        }
    }

    void removeCallbackContexts() {
        Lock writeLock = callbackContextLock.writeLock();
        try {
            writeLock.lock();
            if (!callbackContextMap.isEmpty()) {
                final int callbackContextCount = callbackContextMap.size();
                for (long handle : callbackContextMap.keySet()) {
                    removeJNIGlobalRef(handle);
                }
                logger.logWarn("{0} V8 callback context object(s) not recycled.",
                        Integer.toString(callbackContextCount));
                callbackContextMap.clear();
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void removeGCEpilogueCallback(IJavetGCCallback iJavetGCCallback) {
        synchronized (gcEpilogueCallbacks) {
            gcEpilogueCallbacks.remove(Objects.requireNonNull(iJavetGCCallback));
            if (gcEpilogueCallbacks.isEmpty()) {
                v8Native.unregisterGCEpilogueCallback(handle);
            }
        }
    }

    public void removeGCPrologueCallback(IJavetGCCallback iJavetGCCallback) {
        synchronized (gcPrologueCallbacks) {
            gcPrologueCallbacks.remove(Objects.requireNonNull(iJavetGCCallback));
            if (gcPrologueCallbacks.isEmpty()) {
                v8Native.unregisterGCPrologueCallback(handle);
            }
        }
    }

    void removeJNIGlobalRef(long handle) {
        if (!isClosed()) {
            v8Native.removeJNIGlobalRef(handle);
        }
    }

    @SuppressWarnings("RedundantThrows")
    void removeReference(IV8ValueReference iV8ValueReference) throws JavetException {
        final long referenceHandle = iV8ValueReference.getHandle();
        Lock writeLock = referenceLock.writeLock();
        try {
            writeLock.lock();
            if (referenceMap.containsKey(referenceHandle)) {
                final int referenceType = iV8ValueReference.getType().getId();
                if (referenceType == V8ValueReferenceType.Module.getId()) {
                    removeV8Module((IV8Module) iV8ValueReference);
                }
                v8Native.removeReferenceHandle(referenceHandle, referenceType);
                referenceMap.remove(referenceHandle);
            }
        } finally {
            writeLock.unlock();
        }
        if (gcScheduled) {
            lowMemoryNotification();
            gcScheduled = false;
        }
    }

    void removeReferences() throws JavetException {
        Lock writeLock = referenceLock.writeLock();
        try {
            writeLock.lock();
            if (!referenceMap.isEmpty()) {
                final int referenceCount = getReferenceCount();
                final int v8ModuleCount = getV8ModuleCount();
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
                if (v8ModuleCount + weakReferenceCount < referenceCount) {
                    logger.logWarn("{0} V8 object(s) not recycled, {1} weak, {2} module(s).",
                            Integer.toString(referenceCount),
                            Integer.toString(weakReferenceCount),
                            Integer.toString(v8ModuleCount));
                } else {
                    logger.logDebug("{0} V8 object(s) not recycled, {1} weak, {2} module(s).",
                            Integer.toString(referenceCount),
                            Integer.toString(weakReferenceCount),
                            Integer.toString(v8ModuleCount));
                }
                referenceMap.clear();
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void removeV8Module(String resourceName, boolean forceClose) throws JavetException {
        IV8Module iV8Module;
        Lock writeLock = v8ModuleLock.writeLock();
        try {
            writeLock.lock();
            iV8Module = v8ModuleMap.remove(resourceName);
        } finally {
            writeLock.unlock();
        }
        if (forceClose && iV8Module != null) {
            iV8Module.close(true);
        }
    }

    public void removeV8Module(String resourceName) throws JavetException {
        removeV8Module(resourceName, false);
    }

    public void removeV8Module(IV8Module iV8Module) throws JavetException {
        removeV8Module(iV8Module, false);
    }

    public void removeV8Module(IV8Module iV8Module, boolean forceClose) throws JavetException {
        removeV8Module(iV8Module.getResourceName(), forceClose);
    }

    public void removeV8Modules() throws JavetException {
        removeV8Modules(false);
    }

    public void removeV8Modules(boolean forceClose) throws JavetException {
        Lock writeLock = v8ModuleLock.writeLock();
        try {
            writeLock.lock();
            if (!v8ModuleMap.isEmpty()) {
                logger.logWarn("{0} V8 module(s) not recycled.", Integer.toString(v8ModuleMap.size()));
                for (IV8Module iV8Module : v8ModuleMap.values()) {
                    logger.logWarn("  V8 module: {0}", iV8Module.getResourceName());
                    if (forceClose) {
                        iV8Module.close(true);
                    }
                }
                v8ModuleMap.clear();
            }
        } finally {
            writeLock.unlock();
        }
    }

    public boolean promoteScheduledException() throws JavetException {
        return v8Native.promoteScheduledException(handle);
    }

    public boolean reportPendingMessages() throws JavetException {
        return v8Native.reportPendingMessages(handle);
    }

    /**
     * Requests GC for testing.
     * Note: --expose_gc must be set.
     *
     * @param fullGC true = Full GC, false = Minor GC
     * @return the self
     * @since 0.8.0
     */
    public V8Runtime requestGarbageCollectionForTesting(boolean fullGC) {
        v8Native.requestGarbageCollectionForTesting(handle, fullGC);
        return this;
    }

    /**
     * Reset V8 context.
     * This is a light-weight and recommended reset.
     *
     * @return the self
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8Runtime resetContext() throws JavetException {
        removeAllReferences();
        v8Native.resetV8Context(handle, runtimeOptions);
        return this;
    }

    /**
     * Reset V8 isolate.
     * This is a heavy reset. Please avoid using it in performance sensitive scenario.
     *
     * @return the self
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @SuppressWarnings("UnusedReturnValue")
    public V8Runtime resetIsolate() throws JavetException {
        removeAllReferences();
        v8Native.resetV8Isolate(handle, runtimeOptions);
        return this;
    }

    boolean sameValue(IV8ValueObject iV8ValueObject1, IV8ValueObject iV8ValueObject2) {
        return v8Native.sameValue(handle, iV8ValueObject1.getHandle(), iV8ValueObject2.getHandle());
    }

    @CheckReturnValue
    <T extends V8Value> T scriptRun(
            IV8Script iV8Script, boolean resultRequired) throws JavetException {
        return decorateV8Value((T) v8Native.scriptRun(
                handle, iV8Script.getHandle(), iV8Script.getType().getId(), resultRequired));
    }

    boolean set(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        decorateV8Values(key, value);
        return v8Native.set(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key, value);
    }

    @SuppressWarnings("RedundantThrows")
    boolean setAccessor(
            IV8ValueObject iV8ValueObject,
            V8Value propertyName,
            JavetCallbackContext javetCallbackContextGetter,
            JavetCallbackContext javetCallbackContextSetter) throws JavetException {
        assert (propertyName instanceof V8ValueString || propertyName instanceof V8ValueSymbol);
        boolean isAccessorSet = v8Native.setAccessor(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(),
                propertyName, javetCallbackContextGetter, javetCallbackContextSetter);
        Lock writeLock = callbackContextLock.writeLock();
        try {
            writeLock.lock();
            if (javetCallbackContextGetter != null && javetCallbackContextGetter.isValid()) {
                callbackContextMap.put(javetCallbackContextGetter.getHandle(), javetCallbackContextGetter);
            }
            if (javetCallbackContextSetter != null && javetCallbackContextSetter.isValid()) {
                callbackContextMap.put(javetCallbackContextSetter.getHandle(), javetCallbackContextSetter);
            }
        } finally {
            writeLock.unlock();
        }
        return isAccessorSet;
    }

    public void setConverter(IJavetConverter converter) {
        Objects.requireNonNull(converter);
        this.converter = converter;
    }

    public void setGCScheduled(boolean gcScheduled) {
        this.gcScheduled = gcScheduled;
    }

    public void setLogger(IJavetLogger logger) {
        this.logger = logger;
    }

    boolean setPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName, V8Value propertyValue)
            throws JavetException {
        decorateV8Value(propertyValue);
        return v8Native.setPrivateProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), propertyName, propertyValue);
    }

    public void setPromiseRejectCallback(IJavetPromiseRejectCallback promiseRejectCallback) {
        Objects.requireNonNull(promiseRejectCallback);
        this.promiseRejectCallback = promiseRejectCallback;
    }

    boolean setProperty(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        decorateV8Values(key, value);
        return v8Native.setProperty(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key, value);
    }

    boolean setPrototype(
            IV8ValueObject iV8ValueObject, IV8ValueObject iV8ValueObjectPrototype) throws JavetException {
        decorateV8Value(iV8ValueObjectPrototype);
        return v8Native.setPrototype(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(),
                iV8ValueObjectPrototype.getHandle());
    }

    @SuppressWarnings("RedundantThrows")
    boolean setSourceCode(IV8ValueFunction iV8ValueFunction, String sourceCode) throws JavetException {
        return v8Native.setSourceCode(
                handle, iV8ValueFunction.getHandle(), iV8ValueFunction.getType().getId(), sourceCode);
    }

    public void setV8ModuleResolver(IV8ModuleResolver v8ModuleResolver) {
        this.v8ModuleResolver = v8ModuleResolver;
    }

    void setWeak(IV8ValueReference iV8ValueReference) {
        v8Native.setWeak(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId(), iV8ValueReference);
    }

    boolean strictEquals(IV8ValueObject iV8ValueObject1, IV8ValueObject iV8ValueObject2) {
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

    @Override
    public <T, V extends V8Value> T toObject(V v8Value) throws JavetException {
        return (T) converter.toObject(v8Value);
    }

    String toProtoString(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Native.toProtoString(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    String toString(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Native.toString(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    @Override
    @CheckReturnValue
    public <T, V extends V8Value> V toV8Value(T object) throws JavetException {
        return converter.toV8Value(this, object);
    }
}
