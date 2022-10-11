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
import com.caoccao.javet.enums.*;
import com.caoccao.javet.exceptions.JavetError;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IEnumBitset;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interfaces.IJavetLogger;
import com.caoccao.javet.interfaces.IV8ModuleResolver;
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
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.*;
import com.caoccao.javet.values.reference.builtin.V8ValueBuiltInSymbol;

import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

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
    /**
     * The Default converter.
     *
     * @since 0.8.5
     */
    static final IJavetConverter DEFAULT_CONVERTER = new JavetObjectConverter();
    /**
     * The Default message format javet inspector.
     *
     * @since 0.7.3
     */
    static final String DEFAULT_MESSAGE_FORMAT_JAVET_INSPECTOR = "Javet Inspector {0}";
    /**
     * The Invalid handle.
     *
     * @since 0.7.0
     */
    static final long INVALID_HANDLE = 0L;
    /**
     * The Property data view.
     *
     * @since 0.7.2
     */
    static final String PROPERTY_DATA_VIEW = "DataView";
    /**
     * The V8 value boolean false index.
     *
     * @since 0.7.4
     */
    static final int V8_VALUE_BOOLEAN_FALSE_INDEX = 0;
    /**
     * The V8 value boolean true index.
     *
     * @since 0.7.4
     */
    static final int V8_VALUE_BOOLEAN_TRUE_INDEX = 1;
    /**
     * The V8 value number lower bound.
     *
     * @since 0.7.4
     */
    static final int V8_VALUE_NUMBER_LOWER_BOUND = -128; // Inclusive
    /**
     * The V8 value number upper bound.
     *
     * @since 0.7.4
     */
    static final int V8_VALUE_NUMBER_UPPER_BOUND = 128; // Exclusive
    /**
     * The Callback context lock.
     *
     * @since 0.9.12
     */
    final Object callbackContextLock;
    /**
     * The Callback context map.
     * <p>
     * V8 may not make final callback when V8 context is being recycled.
     * That may result in memory leak.
     * Callback context map is designed for closing that memory leak issue.
     *
     * @since 0.8.3
     */
    final Map<Long, JavetCallbackContext> callbackContextMap;
    /**
     * The GC epilogue callbacks.
     *
     * @since 1.0.3
     */
    final List<IJavetGCCallback> gcEpilogueCallbacks;
    /**
     * The GC prologue callbacks.
     *
     * @since 1.0.3
     */
    final List<IJavetGCCallback> gcPrologueCallbacks;
    /**
     * The Reference lock.
     *
     * @since 0.9.12
     */
    final Object referenceLock;
    /**
     * The Reference map.
     *
     * @since 0.7.0
     */
    final Map<Long, IV8ValueReference> referenceMap;
    /**
     * The Runtime options.
     *
     * @since 1.0.0
     */
    final RuntimeOptions<?> runtimeOptions;
    /**
     * The V8 host.
     *
     * @since 0.7.0
     */
    final V8Host v8Host;
    /**
     * The V8 internal.
     *
     * @since 1.0.2
     */
    final V8Internal v8Internal;
    /**
     * The V8 module lock.
     *
     * @since 0.9.12
     */
    final Object v8ModuleLock;
    /**
     * The V8 module map.
     *
     * @since 0.9.12
     */
    final Map<String, IV8Module> v8ModuleMap;
    /**
     * The Cached V8 value booleans.
     *
     * @since 0.7.4
     */
    V8ValueBoolean[] cachedV8ValueBooleans;
    /**
     * The Cached V8 value integers.
     *
     * @since 0.7.4
     */
    V8ValueInteger[] cachedV8ValueIntegers;
    /**
     * The Cached V8 value longs.
     *
     * @since 0.7.4
     */
    V8ValueLong[] cachedV8ValueLongs;
    /**
     * The Cached V8 value null.
     *
     * @since 0.7.4
     */
    V8ValueNull cachedV8ValueNull;
    /**
     * The Cached V8 value undefined.
     *
     * @since 0.7.4
     */
    V8ValueUndefined cachedV8ValueUndefined;
    /**
     * The Converter.
     *
     * @since 0.7.0
     */
    IJavetConverter converter;
    /**
     * The GC scheduled.
     *
     * @since 0.8.3
     */
    boolean gcScheduled;
    /**
     * The Handle.
     *
     * @since 0.7.0
     */
    long handle;
    /**
     * The Logger.
     *
     * @since 0.7.0
     */
    IJavetLogger logger;
    /**
     * The Pooled.
     *
     * @since 0.7.0
     */
    boolean pooled;
    /**
     * The Promise reject callback.
     *
     * @since 0.8.3
     */
    IJavetPromiseRejectCallback promiseRejectCallback;
    /**
     * The V8 inspector.
     *
     * @since 0.7.3
     */
    V8Inspector v8Inspector;
    /**
     * The V8 module resolver.
     *
     * @since 0.9.3
     */
    IV8ModuleResolver v8ModuleResolver;
    /**
     * The V8 native.
     *
     * @since 0.7.0
     */
    IV8Native v8Native;

    /**
     * Instantiates a new V8 runtime.
     *
     * @param v8Host         the V8 host
     * @param handle         the handle
     * @param pooled         the pooled
     * @param v8Native       the V8 native
     * @param runtimeOptions the runtime options
     * @since 0.7.0
     */
    V8Runtime(V8Host v8Host, long handle, boolean pooled, IV8Native v8Native, RuntimeOptions<?> runtimeOptions) {
        assert handle != 0;
        callbackContextLock = new Object();
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
        referenceLock = new Object();
        referenceMap = new HashMap<>();
        this.v8Host = Objects.requireNonNull(v8Host);
        v8Inspector = null;
        this.v8Native = Objects.requireNonNull(v8Native);
        v8ModuleLock = new Object();
        v8ModuleMap = new HashMap<>();
        v8ModuleResolver = null;
        v8Internal = new V8Internal(this);
        initializeV8ValueCache();
    }

    /**
     * Add a value to a set.
     *
     * @param iV8ValueSet the V8 value set
     * @param value       the value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    void add(IV8ValueSet iV8ValueSet, V8Value value) throws JavetException {
        v8Native.add(handle, iV8ValueSet.getHandle(), iV8ValueSet.getType().getId(), value);
    }

    /**
     * Add GC epilogue callback.
     *
     * @param iJavetGCCallback the javet GC callback
     * @since 1.0.3
     */
    public void addGCEpilogueCallback(IJavetGCCallback iJavetGCCallback) {
        synchronized (gcEpilogueCallbacks) {
            boolean registered = !gcEpilogueCallbacks.isEmpty();
            gcEpilogueCallbacks.add(Objects.requireNonNull(iJavetGCCallback));
            if (!registered) {
                v8Native.registerGCEpilogueCallback(handle);
            }
        }
    }

    /**
     * Add GC prologue callback.
     *
     * @param iJavetGCCallback the javet GC callback
     * @since 1.0.3
     */
    public void addGCPrologueCallback(IJavetGCCallback iJavetGCCallback) {
        synchronized (gcPrologueCallbacks) {
            boolean registered = !gcPrologueCallbacks.isEmpty();
            gcPrologueCallbacks.add(Objects.requireNonNull(iJavetGCCallback));
            if (!registered) {
                v8Native.registerGCPrologueCallback(handle);
            }
        }
    }

    /**
     * Add reference.
     *
     * @param iV8ValueReference the V8 value reference
     * @since 1.0.3
     */
    void addReference(IV8ValueReference iV8ValueReference) {
        synchronized (referenceLock) {
            referenceMap.put(iV8ValueReference.getHandle(), iV8ValueReference);
        }
    }

    /**
     * Add V8 module.
     *
     * @param iV8Module the V8 module
     * @since 0.9.1
     */
    public void addV8Module(IV8Module iV8Module) {
        synchronized (v8ModuleLock) {
            v8ModuleMap.put(iV8Module.getResourceName(), iV8Module);
        }
    }

    /**
     * Allow eval().
     *
     * @param allow true : allow eval(), false : disallow eval()
     * @since 0.8.0
     */
    public void allowEval(boolean allow) {
        v8Native.allowCodeGenerationFromStrings(handle, allow);
    }

    /**
     * Await tells the V8 runtime to pump the message loop in a non-blocking manner.
     *
     * @since 0.8.0
     */
    public void await() {
        v8Native.await(handle);
    }

    /**
     * Call a function.
     * <p>
     * It is similar to JavaScript built-in functions apply() or call().
     *
     * @param <T>            the type parameter
     * @param iV8ValueObject the V8 value object
     * @param receiver       the receiver
     * @param returnResult   the return result
     * @param v8Values       the V8 values
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    <T extends V8Value> T call(
            IV8ValueObject iV8ValueObject,
            IV8ValueObject receiver,
            boolean returnResult,
            V8Value... v8Values)
            throws JavetException {
        return (T) v8Native.call(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), receiver, returnResult, v8Values);
    }

    /**
     * Call a function as a constructor.
     *
     * @param <T>            the type parameter
     * @param iV8ValueObject the V8 value object
     * @param v8Values       the V8 values
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    @CheckReturnValue
    <T extends V8Value> T callAsConstructor(
            IV8ValueObject iV8ValueObject, V8Value... v8Values) throws JavetException {
        return (T) v8Native.callAsConstructor(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), v8Values);
    }

    /**
     * Set a reference to a strong reference.
     *
     * @param iV8ValueReference the V8 value reference
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @SuppressWarnings("RedundantThrows")
    void clearWeak(IV8ValueReference iV8ValueReference) throws JavetException {
        v8Native.clearWeak(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    /**
     * Clone a V8 value.
     *
     * @param <T>               the type parameter
     * @param iV8ValueReference the V8 value reference
     * @return the cloned V8 value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    <T extends V8Value> T cloneV8Value(IV8ValueReference iV8ValueReference) throws JavetException {
        return (T) v8Native.cloneV8Value(
                handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    @Override
    public void close() throws JavetException {
        close(!pooled);
    }

    /**
     * Close by a force close flag.
     *
     * @param forceClose the force close
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public void close(boolean forceClose) throws JavetException {
        if (!isClosed() && forceClose) {
            removeAllReferences();
            v8Host.closeV8Runtime(this);
            handle = INVALID_HANDLE;
            v8Native = null;
        }
    }

    /**
     * Compile a V8 module and add that V8 module to the internal V8 module map.
     *
     * @param scriptString   the script string
     * @param v8ScriptOrigin the V8 script origin
     * @param resultRequired the result required
     * @return the compiled V8 module or null
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
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
            v8Module = (V8Module) result;
            v8Module.setResourceName(v8ScriptOrigin.getResourceName());
            addV8Module(v8Module);
        }
        return v8Module;
    }

    /**
     * Compile a V8 script.
     *
     * @param scriptString   the script string
     * @param v8ScriptOrigin the V8 script origin
     * @param resultRequired the result required
     * @return the V8 script
     * @throws JavetException the javet exception
     * @since 0.9.13
     */
    @CheckReturnValue
    public V8Script compileV8Script(String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired)
            throws JavetException {
        v8ScriptOrigin.setModule(false);
        return (V8Script) v8Native.compile(
                handle, scriptString, resultRequired, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule());
    }

    /**
     * Contains a V8 module by resource name.
     *
     * @param resourceName the resource name
     * @return true : yes, false : no
     * @since 0.8.1
     */
    public boolean containsV8Module(String resourceName) {
        synchronized (v8ModuleLock) {
            return v8ModuleMap.containsKey(resourceName);
        }
    }

    @Override
    @CheckReturnValue
    public V8ValueArray createV8ValueArray() throws JavetException {
        return (V8ValueArray) v8Native.createV8Value(
                handle, V8ValueReferenceType.Array.getId(), null);
    }

    @Override
    @CheckReturnValue
    public V8ValueArrayBuffer createV8ValueArrayBuffer(int length) throws JavetException {
        return (V8ValueArrayBuffer) v8Native.createV8Value(
                handle, V8ValueReferenceType.ArrayBuffer.getId(), createV8ValueInteger(length));
    }

    @Override
    @CheckReturnValue
    public V8ValueArrayBuffer createV8ValueArrayBuffer(ByteBuffer byteBuffer) throws JavetException {
        Objects.requireNonNull(byteBuffer);
        assert byteBuffer.isDirect() : "Byte buffer must be direct.";
        return (V8ValueArrayBuffer) v8Native.createV8Value(
                handle, V8ValueReferenceType.ArrayBuffer.getId(), byteBuffer);
    }

    @Override
    public V8ValueBigInteger createV8ValueBigInteger(BigInteger bigInteger) throws JavetException {
        return new V8ValueBigInteger(this, bigInteger);
    }

    @Override
    public V8ValueBigInteger createV8ValueBigInteger(String bigIntegerValue) throws JavetException {
        return new V8ValueBigInteger(this, bigIntegerValue);
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
        return new V8ValueDouble(this, doubleValue);
    }

    @Override
    @CheckReturnValue
    public V8ValueFunction createV8ValueFunction(JavetCallbackContext javetCallbackContext) throws JavetException {
        V8ValueFunction v8ValueFunction = (V8ValueFunction) v8Native.createV8Value(
                handle, V8ValueReferenceType.Function.getId(), Objects.requireNonNull(javetCallbackContext));
        synchronized (callbackContextLock) {
            callbackContextMap.put(javetCallbackContext.getHandle(), javetCallbackContext);
        }
        return v8ValueFunction;
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
        return new V8ValueInteger(this, integerValue);
    }

    @Override
    @CheckReturnValue
    public V8ValueLong createV8ValueLong(long longValue) throws JavetException {
        if (longValue >= V8_VALUE_NUMBER_LOWER_BOUND && longValue < V8_VALUE_NUMBER_UPPER_BOUND) {
            return cachedV8ValueLongs[(int) longValue - V8_VALUE_NUMBER_LOWER_BOUND];
        }
        return new V8ValueLong(this, longValue);
    }

    @Override
    @CheckReturnValue
    public V8ValueMap createV8ValueMap() throws JavetException {
        return (V8ValueMap) v8Native.createV8Value(
                handle, V8ValueReferenceType.Map.getId(), null);
    }

    @Override
    public V8ValueNull createV8ValueNull() {
        return cachedV8ValueNull;
    }

    @Override
    @CheckReturnValue
    public V8ValueObject createV8ValueObject() throws JavetException {
        return (V8ValueObject) v8Native.createV8Value(
                handle, V8ValueReferenceType.Object.getId(), null);
    }

    @Override
    @CheckReturnValue
    public V8ValuePromise createV8ValuePromise() throws JavetException {
        return (V8ValuePromise) v8Native.createV8Value(
                handle, V8ValueReferenceType.Promise.getId(), null);
    }

    @Override
    @CheckReturnValue
    public V8ValueProxy createV8ValueProxy(V8ValueObject v8ValueObject) throws JavetException {
        return (V8ValueProxy) v8Native.createV8Value(
                handle, V8ValueReferenceType.Proxy.getId(), v8ValueObject);
    }

    @Override
    @CheckReturnValue
    public V8ValueSet createV8ValueSet() throws JavetException {
        return (V8ValueSet) v8Native.createV8Value(
                handle, V8ValueReferenceType.Set.getId(), null);
    }

    @Override
    public V8ValueString createV8ValueString(String str) throws JavetException {
        return new V8ValueString(this, str);
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
            return (V8ValueSymbol) v8Native.createV8Value(
                    handle, V8ValueReferenceType.Symbol.getId(), description);
        }
    }

    @Override
    @CheckReturnValue
    public V8ValueTypedArray createV8ValueTypedArray(V8ValueReferenceType type, int length) throws JavetException {
        try (V8ValueFunction v8ValueFunction = getGlobalObject().get(type.getName())) {
            return v8ValueFunction.callAsConstructor(createV8ValueInteger(length));
        }
    }

    @Override
    public V8ValueUndefined createV8ValueUndefined() {
        return cachedV8ValueUndefined;
    }

    @Override
    public V8ValueZonedDateTime createV8ValueZonedDateTime(long jsTimestamp) throws JavetException {
        return new V8ValueZonedDateTime(this, jsTimestamp);
    }

    @Override
    public V8ValueZonedDateTime createV8ValueZonedDateTime(ZonedDateTime zonedDateTime) throws JavetException {
        return new V8ValueZonedDateTime(this, zonedDateTime);
    }

    /**
     * Delete a key from an object.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @return true : deleted, false : key is not found
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean delete(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Native.delete(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key);
    }

    /**
     * Delete a private property from an object.
     *
     * @param iV8ValueObject the V8 value object
     * @param propertyName   the property name
     * @return true : deleted, false : key is not found
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    boolean deletePrivateProperty(IV8ValueObject iV8ValueObject, String propertyName) throws JavetException {
        return v8Native.deletePrivateProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), propertyName);
    }

    /**
     * Equals tells whether 2 references are reference equal or not.
     *
     * @param iV8ValueReference1 the V8 value reference 1
     * @param iV8ValueReference2 the V8 value reference 2
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    boolean equals(IV8ValueReference iV8ValueReference1, IV8ValueReference iV8ValueReference2)
            throws JavetException {
        return v8Native.equals(handle, iV8ValueReference1.getHandle(), iV8ValueReference2.getHandle());
    }

    /**
     * Execute a script or module.
     *
     * @param <T>            the type parameter
     * @param scriptString   the script string
     * @param v8ScriptOrigin the V8 script origin
     * @param resultRequired the result required
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    public <T extends V8Value> T execute(
            String scriptString, V8ScriptOrigin v8ScriptOrigin, boolean resultRequired) throws JavetException {
        return (T) v8Native.execute(
                handle, scriptString, resultRequired, v8ScriptOrigin.getResourceName(),
                v8ScriptOrigin.getResourceLineOffset(), v8ScriptOrigin.getResourceColumnOffset(),
                v8ScriptOrigin.getScriptId(), v8ScriptOrigin.isWasm(), v8ScriptOrigin.isModule());
    }

    /**
     * Gets source code from a function.
     *
     * @param iV8ValueFunction the V8 value function
     * @return the source code
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    @SuppressWarnings("RedundantThrows")
    String functionGetSourceCode(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Native.functionGetSourceCode(handle, iV8ValueFunction.getHandle(), iV8ValueFunction.getType().getId());
    }

    /**
     * Sets source code of a function.
     *
     * @param iV8ValueFunction the V8 value function
     * @param sourceCode       the source code
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    @SuppressWarnings("RedundantThrows")
    boolean functionSetSourceCode(IV8ValueFunction iV8ValueFunction, String sourceCode) throws JavetException {
        return v8Native.functionSetSourceCode(
                handle, iV8ValueFunction.getHandle(), iV8ValueFunction.getType().getId(), sourceCode);
    }

    /**
     * Get a property from an object by a property key.
     *
     * @param <T>            the type parameter
     * @param iV8ValueObject the V8 value object
     * @param key            the property key
     * @return the property value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    <T extends V8Value> T get(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return (T) v8Native.get(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key);
    }

    /**
     * Gets a callback context by a handle.
     *
     * @param handle the handle
     * @return the callback context
     * @since 0.9.12
     */
    public JavetCallbackContext getCallbackContext(long handle) {
        synchronized (callbackContextLock) {
            return callbackContextMap.get(handle);
        }
    }

    /**
     * Gets callback context count.
     *
     * @return the callback context count
     * @since 0.9.12
     */
    public int getCallbackContextCount() {
        return callbackContextMap.size();
    }

    /**
     * Gets converter.
     *
     * @return the converter
     * @since 0.8.5
     */
    public IJavetConverter getConverter() {
        return converter;
    }

    /**
     * Gets an executor by {@link File}.
     *
     * @param scriptFile the script file
     * @return the executor
     * @throws JavetException the javet exception
     * @since 0.8.4
     */
    public IV8Executor getExecutor(File scriptFile) throws JavetException {
        return new V8FileExecutor(this, scriptFile);
    }

    /**
     * Gets an executor by {@link Path}.
     *
     * @param scriptPath the script path
     * @return the executor
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    public IV8Executor getExecutor(Path scriptPath) throws JavetException {
        return new V8PathExecutor(this, scriptPath);
    }

    /**
     * Gets an executor by a script string.
     *
     * @param scriptString the script string
     * @return the executor
     * @since 0.8.0
     */
    public IV8Executor getExecutor(String scriptString) {
        return new V8StringExecutor(this, scriptString);
    }

    /**
     * Gets the global object. By default, it is <code>globalThis</code>.
     *
     * @return the global object
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueGlobalObject getGlobalObject() throws JavetException {
        return (V8ValueGlobalObject) v8Native.getGlobalObject(handle);
    }

    /**
     * Gets the internal handle that represents the current V8 runtime in JNI.
     *
     * @return the handle
     * @since 0.7.0
     */
    public long getHandle() {
        return handle;
    }

    /**
     * Gets the internal identity hash by a reference object.
     *
     * @param iV8ValueReference the V8 value reference
     * @return the identity hash
     * @throws JavetException the javet exception
     * @since 0.9.1
     */
    @SuppressWarnings("RedundantThrows")
    int getIdentityHash(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Native.getIdentityHash(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    /**
     * Gets internal properties from a function.
     * <p>
     * This is experimental only.
     *
     * @param iV8ValueFunction the V8 value function
     * @return the internal properties
     * @throws JavetException the javet exception
     * @since 0.8.8
     */
    @CheckReturnValue
    IV8ValueArray getInternalProperties(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return (V8ValueArray) v8Native.getInternalProperties(
                handle, iV8ValueFunction.getHandle(), iV8ValueFunction.getType().getId());
    }

    /**
     * Gets the JS function type from a function.
     *
     * @param iV8ValueFunction the V8 value function
     * @return the JS function type
     * @since 0.8.8
     */
    JSFunctionType getJSFunctionType(IV8ValueFunction iV8ValueFunction) {
        return JSFunctionType.parse(v8Native.getJSFunctionType(
                handle, iV8ValueFunction.getHandle(), iV8ValueFunction.getType().getId()));
    }

    /**
     * Gets the JS runtime type.
     *
     * @return the JS runtime type
     * @since 0.9.1
     */
    public JSRuntimeType getJSRuntimeType() {
        return JSRuntimeType.V8;
    }

    /**
     * Gets js scope type.
     *
     * @param iV8ValueFunction the V8 value function
     * @return the js scope type
     * @since 0.8.8
     */
    JSScopeType getJSScopeType(IV8ValueFunction iV8ValueFunction) {
        return JSScopeType.parse(v8Native.getJSScopeType(
                handle, iV8ValueFunction.getHandle(), iV8ValueFunction.getType().getId()));
    }

    /**
     * Gets length from an array.
     *
     * @param iV8ValueArray the V8 value array
     * @return the length
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    int getLength(IV8ValueArray iV8ValueArray) throws JavetException {
        return v8Native.getLength(handle, iV8ValueArray.getHandle(), iV8ValueArray.getType().getId());
    }

    /**
     * Gets length from a typed array.
     *
     * @param iV8ValueTypedArray the V8 value typed array
     * @return the length
     * @throws JavetException the javet exception
     * @since 0.8.4
     */
    int getLength(IV8ValueTypedArray iV8ValueTypedArray) throws JavetException {
        return v8Native.getLength(handle, iV8ValueTypedArray.getHandle(), iV8ValueTypedArray.getType().getId());
    }

    /**
     * Gets the internal logger.
     *
     * @return the logger
     * @since 0.9.1
     */
    public IJavetLogger getLogger() {
        return logger;
    }

    /**
     * Gets own property names from an object.
     *
     * @param iV8ValueObject the V8 value object
     * @return the own property names
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    IV8ValueArray getOwnPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        return (V8ValueArray) v8Native.getOwnPropertyNames(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId());
    }

    /**
     * Gets a private property from an object by a property name.
     *
     * @param <T>            the type parameter
     * @param iV8ValueObject the V8 value object
     * @param propertyName   the property name
     * @return the private property
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    @CheckReturnValue
    <T extends V8Value> T getPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName)
            throws JavetException {
        return (T) v8Native.getPrivateProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), propertyName);
    }

    /**
     * Gets promise reject callback.
     *
     * @return the promise reject callback
     * @since 0.8.3
     */
    public IJavetPromiseRejectCallback getPromiseRejectCallback() {
        return promiseRejectCallback;
    }

    /**
     * Gets a property from an object by a property key.
     *
     * @param <T>            the type parameter
     * @param iV8ValueObject the V8 value object
     * @param key            the property key
     * @return the property
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    <T extends V8Value> T getProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return (T) v8Native.getProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key);
    }

    /**
     * Gets property names from an object.
     *
     * @param iV8ValueObject the V8 value object
     * @return the property names
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    IV8ValueArray getPropertyNames(IV8ValueObject iV8ValueObject) throws JavetException {
        return (V8ValueArray) v8Native.getPropertyNames(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId());
    }

    /**
     * Gets prototype from an object.
     *
     * @param <T>            the type parameter
     * @param iV8ValueObject the V8 value object
     * @return the prototype
     * @throws JavetException the javet exception
     * @since 0.9.4
     */
    @CheckReturnValue
    <T extends IV8ValueObject> T getPrototype(IV8ValueObject iV8ValueObject) throws JavetException {
        return (T) v8Native.getPrototype(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId());
    }

    /**
     * Gets the internal reference count.
     *
     * @return the reference count
     * @since 0.7.0
     */
    public int getReferenceCount() {
        return referenceMap.size();
    }

    /**
     * Gets the runtime options.
     *
     * @return the runtime options
     * @since 1.0.0
     */
    public RuntimeOptions<?> getRuntimeOptions() {
        return runtimeOptions;
    }

    /**
     * Gets size from a key container.
     *
     * @param iV8ValueKeyContainer the V8 value key container
     * @return the size
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    int getSize(IV8ValueKeyContainer iV8ValueKeyContainer) throws JavetException {
        return v8Native.getSize(handle, iV8ValueKeyContainer.getHandle(), iV8ValueKeyContainer.getType().getId());
    }

    /**
     * Gets V8 heap space statistics by an allocation space.
     *
     * @param v8AllocationSpace the V8 allocation space
     * @return the V8 heap space statistics
     * @since 1.0.4
     */
    public V8HeapSpaceStatistics getV8HeapSpaceStatistics(V8AllocationSpace v8AllocationSpace) {
        Objects.requireNonNull(v8AllocationSpace);
        return ((V8HeapSpaceStatistics) v8Native.getV8HeapSpaceStatistics(handle, v8AllocationSpace.getIndex()))
                .setAllocationSpace(v8AllocationSpace);
    }

    /**
     * Gets V8 heap statistics.
     *
     * @return the V8 heap statistics
     * @since 1.0.0
     */
    public V8HeapStatistics getV8HeapStatistics() {
        return (V8HeapStatistics) v8Native.getV8HeapStatistics(handle);
    }

    /**
     * Gets V8 inspector.
     *
     * @return the V8 inspector
     * @since 0.7.3
     */
    public V8Inspector getV8Inspector() {
        return getV8Inspector(MessageFormat.format(DEFAULT_MESSAGE_FORMAT_JAVET_INSPECTOR, Long.toString(handle)));
    }

    /**
     * Gets V8 inspector by name.
     *
     * @param name the name
     * @return the V8 inspector
     * @since 0.7.3
     */
    public V8Inspector getV8Inspector(String name) {
        if (v8Inspector == null) {
            v8Inspector = new V8Inspector(this, name, v8Native);
        }
        return v8Inspector;
    }

    /**
     * Gets V8 internal.
     *
     * @return the V8 internal
     * @since 1.0.2
     */
    public V8Internal getV8Internal() {
        return v8Internal;
    }

    /**
     * Gets V8 locker.
     * <p>
     * The V8 locker is for maximizing the performance by explicitly acquire and release
     * the lock. It should not be called in regular case because V8 runtime is thread-safe.
     *
     * @return the V8 locker
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    public V8Locker getV8Locker() throws JavetException {
        return new V8Locker(this, v8Native);
    }

    /**
     * Gets V8 module by the resource name and module referrer.
     * <p>
     * It is the callback function that handles the callback from JNI.
     * The return V8 module tells V8 how to resolve an ESM request.
     *
     * @param resourceName     the resource name
     * @param v8ModuleReferrer the V8 module referrer
     * @return the V8 module
     * @throws JavetException the javet exception
     * @since 0.9.3
     */
    @CheckReturnValue
    IV8Module getV8Module(String resourceName, IV8Module v8ModuleReferrer) throws JavetException {
        if (resourceName != null && resourceName.length() > 0) {
            synchronized (v8ModuleLock) {
                if (v8ModuleMap.containsKey(resourceName)) {
                    return v8ModuleMap.get(resourceName);
                }
            }
            if (v8ModuleResolver != null) {
                return v8ModuleResolver.resolve(this, resourceName, v8ModuleReferrer);
            }
        }
        return null;
    }

    /**
     * Gets V8 module count.
     *
     * @return the V8 module count
     * @since 0.8.1
     */
    public int getV8ModuleCount() {
        return v8ModuleMap.size();
    }

    /**
     * Gets V8 module resolver.
     *
     * @return the V8 module resolver
     * @since 0.9.3
     */
    public IV8ModuleResolver getV8ModuleResolver() {
        return v8ModuleResolver;
    }

    /**
     * Gets V8 scope.
     *
     * @return the V8 scope
     * @since 0.9.14
     */
    public V8Scope getV8Scope() {
        return new V8Scope(this);
    }

    /**
     * Gets V8 shared memory statistics.
     *
     * @return the V8 shared memory statistics
     * @since 1.0.0
     */
    public V8SharedMemoryStatistics getV8SharedMemoryStatistics() {
        return v8Host.getV8SharedMemoryStatistics();
    }

    /**
     * Gets the V8 version.
     *
     * @return the version
     * @since 0.8.2
     */
    public String getVersion() {
        return v8Native.getVersion();
    }

    /**
     * Has a property in an object.
     *
     * @param iV8ValueObject the V8 value object
     * @param value          the value
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    boolean has(IV8ValueObject iV8ValueObject, V8Value value) throws JavetException {
        return v8Native.has(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), value);
    }

    /**
     * Has internal type.
     *
     * @param iV8ValueObject the V8 value object
     * @param internalType   the internal type
     * @return true : yes, false : no
     * @since 0.9.13
     */
    boolean hasInternalType(IV8ValueObject iV8ValueObject, V8ValueInternalType internalType) {
        return v8Native.hasInternalType(
                handle, iV8ValueObject.getHandle(), Objects.requireNonNull(internalType).getId());
    }

    /**
     * Has own property in an object by a property key.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the property key
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 0.7.2
     */
    boolean hasOwnProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Native.hasOwnProperty(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key);
    }

    /**
     * Has pending exception.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 1.0.4
     */
    public boolean hasPendingException() throws JavetException {
        return v8Native.hasPendingException(handle);
    }

    /**
     * Has pending message.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 1.0.4
     */
    public boolean hasPendingMessage() throws JavetException {
        return v8Native.hasPendingMessage(handle);
    }

    /**
     * Has private property in an object by a property name.
     *
     * @param iV8ValueObject the V8 value object
     * @param propertyName   the property name
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    boolean hasPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName) throws JavetException {
        return v8Native.hasPrivateProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), propertyName);
    }

    /**
     * Has scheduled exception.
     *
     * @return true : yes, false : no
     * @throws JavetException the javet exception
     * @since 1.0.4
     */
    public boolean hasScheduledException() throws JavetException {
        return v8Native.hasScheduledException(handle);
    }

    /**
     * Idle notification deadline tells V8 to perform GC in the given millis.
     *
     * @param deadlineInMillis the deadline in millis
     * @since 0.9.1
     */
    public void idleNotificationDeadline(long deadlineInMillis) {
        if (!isClosed() && deadlineInMillis > 0) {
            v8Native.idleNotificationDeadline(handle, deadlineInMillis);
        }
    }

    /**
     * Initialize V8 value cache.
     *
     * @since 0.7.4
     */
    void initializeV8ValueCache() {
        try {
            cachedV8ValueNull = new V8ValueNull(this);
            cachedV8ValueUndefined = new V8ValueUndefined(this);
            cachedV8ValueBooleans = new V8ValueBoolean[]{
                    new V8ValueBoolean(this, false),
                    new V8ValueBoolean(this, true)};
            cachedV8ValueIntegers = new V8ValueInteger[V8_VALUE_NUMBER_UPPER_BOUND - V8_VALUE_NUMBER_LOWER_BOUND];
            cachedV8ValueLongs = new V8ValueLong[V8_VALUE_NUMBER_UPPER_BOUND - V8_VALUE_NUMBER_LOWER_BOUND];
            for (int i = V8_VALUE_NUMBER_LOWER_BOUND; i < V8_VALUE_NUMBER_UPPER_BOUND; ++i) {
                try {
                    cachedV8ValueIntegers[i - V8_VALUE_NUMBER_LOWER_BOUND] = new V8ValueInteger(this, i);
                    cachedV8ValueLongs[i - V8_VALUE_NUMBER_LOWER_BOUND] = new V8ValueLong(this, i);
                } catch (JavetException e) {
                    logger.logError(e, e.getMessage());
                }
            }
        } catch (JavetException e) {
            logger.logError(e, e.getMessage());
        }
    }

    /**
     * Invoke a function by a function name.
     *
     * @param <T>            the type parameter
     * @param iV8ValueObject the V8 value object
     * @param functionName   the function name
     * @param returnResult   the return result
     * @param v8Values       the V8 values
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @CheckReturnValue
    <T extends V8Value> T invoke(
            IV8ValueObject iV8ValueObject, String functionName, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        return (T) v8Native.invoke(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), functionName, returnResult, v8Values);
    }

    @Override
    public boolean isClosed() {
        return handle == INVALID_HANDLE;
    }

    /**
     * Returns whether the V8 runtime is dead or not.
     *
     * @return true : dead, false : alive
     * @since 0.7.2
     */
    public boolean isDead() {
        return v8Native.isDead(handle);
    }

    /**
     * Returns whether the GC is scheduled or not.
     *
     * @return true : scheduled, false: not scheduled
     * @since 0.8.3
     */
    public boolean isGCScheduled() {
        return gcScheduled;
    }

    /**
     * Returns whether the V8 runtime is in use or not.
     *
     * @return the boolean
     * @since 0.7.2
     */
    public boolean isInUse() {
        return v8Native.isInUse(handle);
    }

    /**
     * Returns whether the V8 runtime is managed by a pool or not.
     *
     * @return true : yes, false : no
     * @since 0.7.0
     */
    public boolean isPooled() {
        return pooled;
    }

    /**
     * Returns whether the reference is weak or not.
     *
     * @param iV8ValueReference the V8 value reference
     * @return true : weak, false : strong
     * @since 0.7.0
     */
    boolean isWeak(IV8ValueReference iV8ValueReference) {
        return v8Native.isWeak(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    /**
     * Send low memory notification to current V8 isolate.
     *
     * @since 0.8.3
     */
    public void lowMemoryNotification() {
        if (!isClosed()) {
            v8Native.lowMemoryNotification(handle);
        }
    }

    /**
     * Evaluate a module.
     *
     * @param <T>            the type parameter
     * @param iV8Module      the V8 module
     * @param resultRequired the result required
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    <T extends V8Value> T moduleEvaluate(
            IV8Module iV8Module, boolean resultRequired) throws JavetException {
        return (T) v8Native.moduleEvaluate(
                handle, iV8Module.getHandle(), iV8Module.getType().getId(), resultRequired);
    }

    /**
     * Gets an error from a module.
     *
     * @param iV8Module the V8 module
     * @return the V8 value error
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    @CheckReturnValue
    V8ValueError moduleGetException(IV8Module iV8Module) throws JavetException {
        return (V8ValueError) v8Native.moduleGetException(
                handle, iV8Module.getHandle(), iV8Module.getType().getId());
    }

    /**
     * Gets the namespace from a module.
     *
     * @param iV8Module the V8 module
     * @return the namespace
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    V8ValueObject moduleGetNamespace(IV8Module iV8Module) throws JavetException {
        return (V8ValueObject) v8Native.moduleGetNamespace(
                handle, iV8Module.getHandle(), iV8Module.getType().getId());
    }

    /**
     * Gets the script ID from a module.
     *
     * @param iV8Module the V8 module
     * @return the script ID
     * @throws JavetException the javet exception
     * @since 0.8.4
     */
    @SuppressWarnings("RedundantThrows")
    int moduleGetScriptId(IV8Module iV8Module) throws JavetException {
        return v8Native.moduleGetScriptId(handle, iV8Module.getHandle(), iV8Module.getType().getId());
    }

    /**
     * Gets the status from a module.
     *
     * @param iV8Module the V8 module
     * @return the status
     * @throws JavetException the javet exception
     * @since 0.8.4
     */
    @SuppressWarnings("RedundantThrows")
    int moduleGetStatus(IV8Module iV8Module) throws JavetException {
        return v8Native.moduleGetStatus(handle, iV8Module.getHandle(), iV8Module.getType().getId());
    }

    /**
     * Instantiate a module.
     * <p>
     * It may return an exception or false if the module state is invalid.
     *
     * @param iV8Module the V8 module
     * @return true : success, false : false
     * @throws JavetException the javet exception
     * @since 0.8.4
     */
    @SuppressWarnings("RedundantThrows")
    boolean moduleInstantiate(IV8Module iV8Module) throws JavetException {
        return v8Native.moduleInstantiate(handle, iV8Module.getHandle(), iV8Module.getType().getId());
    }

    /**
     * Call Promise.catch().
     *
     * @param <T>             the type parameter
     * @param iV8ValuePromise the V8 value promise
     * @param functionHandle  the function handle
     * @return the V8 value promise
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    <T extends V8ValuePromise> T promiseCatch(
            IV8ValuePromise iV8ValuePromise, IV8ValueFunction functionHandle) throws JavetException {
        return (T) v8Native.promiseCatch(
                handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId(), functionHandle.getHandle());
    }

    /**
     * Gets a promise from a promise resolver.
     *
     * @param iV8ValuePromise the V8 value promise
     * @return the V8 value promise
     * @throws JavetException the javet exception
     * @since 0.9.8
     */
    @CheckReturnValue
    V8ValuePromise promiseGetPromise(IV8ValuePromise iV8ValuePromise) throws JavetException {
        return (V8ValuePromise) v8Native.promiseGetPromise(
                handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId());
    }

    /**
     * Gets the result from a promise.
     *
     * @param <T>             the type parameter
     * @param iV8ValuePromise the V8 value promise
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    <T extends V8Value> T promiseGetResult(IV8ValuePromise iV8ValuePromise) throws JavetException {
        return (T) v8Native.promiseGetResult(
                handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId());
    }

    /**
     * Gets the state from a promise.
     *
     * @param iV8ValuePromise the V8 value promise
     * @return the state
     * @since 0.8.4
     */
    int promiseGetState(IV8ValuePromise iV8ValuePromise) {
        return v8Native.promiseGetState(handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId());
    }

    /**
     * Returns whether a promise has a handler or not.
     *
     * @param iV8ValuePromise the V8 value promise
     * @return true : yes, false : false
     * @since 0.8.4
     */
    boolean promiseHasHandler(IV8ValuePromise iV8ValuePromise) {
        return v8Native.promiseHasHandler(handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId());
    }

    /**
     * Mark a promise as handled.
     *
     * @param iV8ValuePromise the V8 value promise
     * @since 0.8.4
     */
    void promiseMarkAsHandled(IV8ValuePromise iV8ValuePromise) {
        v8Native.promiseMarkAsHandled(handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId());
    }

    /**
     * Rejects a promise by a value.
     *
     * @param v8ValuePromise the V8 value promise
     * @param v8Value        the V8 value
     * @return true : rejected, false : not rejected
     * @since 0.8.4
     */
    boolean promiseReject(V8ValuePromise v8ValuePromise, V8Value v8Value) {
        return v8Native.promiseReject(
                handle, v8ValuePromise.getHandle(), v8ValuePromise.getType().getId(), v8Value);
    }

    /**
     * Call Promise.resolve() by a value.
     *
     * @param v8ValuePromise the V8 value promise
     * @param v8Value        the V8 value
     * @return true : resolved, false : not resolved
     * @since 0.9.8
     */
    boolean promiseResolve(V8ValuePromise v8ValuePromise, V8Value v8Value) {
        return v8Native.promiseResolve(
                handle, v8ValuePromise.getHandle(), v8ValuePromise.getType().getId(), v8Value);
    }

    /**
     * Call Promise.then() by a function fulfilled handle and a function rejected handle.
     *
     * @param <T>                     the type parameter
     * @param iV8ValuePromise         the V8 value promise
     * @param functionFulfilledHandle the function fulfilled handle
     * @param functionRejectedHandle  the function rejected handle
     * @return the result promise
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    <T extends V8ValuePromise> T promiseThen(
            IV8ValuePromise iV8ValuePromise, IV8ValueFunction functionFulfilledHandle,
            IV8ValueFunction functionRejectedHandle) throws JavetException {
        return (T) v8Native.promiseThen(
                handle, iV8ValuePromise.getHandle(), iV8ValuePromise.getType().getId(),
                functionFulfilledHandle.getHandle(),
                functionRejectedHandle == null ? 0L : functionRejectedHandle.getHandle());
    }

    /**
     * Promote scheduled exception.
     *
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 1.0.4
     */
    public boolean promoteScheduledException() throws JavetException {
        return v8Native.promoteScheduledException(handle);
    }

    /**
     * Gets a handler from a proxy
     *
     * @param iV8ValueProxy the V8 value proxy
     * @return the handler
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    @CheckReturnValue
    V8ValueObject proxyGetHandler(IV8ValueProxy iV8ValueProxy) throws JavetException {
        return (V8ValueObject) v8Native.proxyGetHandler(
                handle, iV8ValueProxy.getHandle(), iV8ValueProxy.getType().getId());
    }

    /**
     * Gets the target from a proxy.
     *
     * @param iV8ValueProxy the V8 value proxy
     * @return the target
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    @CheckReturnValue
    V8ValueObject proxyGetTarget(IV8ValueProxy iV8ValueProxy) throws JavetException {
        return (V8ValueObject) v8Native.proxyGetTarget(
                handle, iV8ValueProxy.getHandle(), iV8ValueProxy.getType().getId());
    }

    /**
     * Returns whether a proxy is revoked or not.
     *
     * @param iV8ValueProxy the V8 value proxy
     * @return true : revoked, false : not revoked
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    boolean proxyIsRevoked(IV8ValueProxy iV8ValueProxy) throws JavetException {
        return v8Native.proxyIsRevoked(handle, iV8ValueProxy.getHandle(), iV8ValueProxy.getType().getId());
    }

    /**
     * Revokes a proxy.
     *
     * @param iV8ValueProxy the V8 value proxy
     * @throws JavetException the javet exception
     * @since 0.9.6
     */
    void proxyRevoke(IV8ValueProxy iV8ValueProxy) throws JavetException {
        v8Native.proxyRevoke(handle, iV8ValueProxy.getHandle(), iV8ValueProxy.getType().getId());
    }

    /**
     * Receives the GC epilogue callback from JNI.
     *
     * @param v8GCTypeValue          the V8 GC type value
     * @param v8GCCallbackFlagsValue the V8 GC callback flags value
     * @since 1.0.3
     */
    void receiveGCEpilogueCallback(int v8GCTypeValue, int v8GCCallbackFlagsValue) {
        final EnumSet<V8GCType> enumSetV8GCType = IEnumBitset.getEnumSet(v8GCTypeValue, V8GCType.class);
        final EnumSet<V8GCCallbackFlags> enumSetV8GCCallbackFlags = IEnumBitset.getEnumSet(
                v8GCCallbackFlagsValue, V8GCCallbackFlags.class, V8GCCallbackFlags.NoGCCallbackFlags);
        for (IJavetGCCallback iJavetGCCallback : gcEpilogueCallbacks) {
            iJavetGCCallback.callback(enumSetV8GCType, enumSetV8GCCallbackFlags);
        }
    }

    /**
     * Receives the GC prologue callback from JNI.
     *
     * @param v8GCTypeValue          the V8 GC type value
     * @param v8GCCallbackFlagsValue the V8 GC callback flags value
     * @since 1.0.3
     */
    void receiveGCPrologueCallback(int v8GCTypeValue, int v8GCCallbackFlagsValue) {
        final EnumSet<V8GCType> enumSetV8GCType = IEnumBitset.getEnumSet(v8GCTypeValue, V8GCType.class);
        final EnumSet<V8GCCallbackFlags> enumSetV8GCCallbackFlags = IEnumBitset.getEnumSet(
                v8GCCallbackFlagsValue, V8GCCallbackFlags.class, V8GCCallbackFlags.NoGCCallbackFlags);
        for (IJavetGCCallback iJavetGCCallback : gcPrologueCallbacks) {
            iJavetGCCallback.callback(enumSetV8GCType, enumSetV8GCCallbackFlags);
        }
    }

    /**
     * Receives the promise reject callback from JNI.
     *
     * @param event   the event
     * @param promise the promise
     * @param value   the value
     * @since 0.9.1
     */
    void receivePromiseRejectCallback(int event, V8ValuePromise promise, V8Value value) {
        try {
            promiseRejectCallback.callback(JavetPromiseRejectEvent.parse(event), promise, value);
        } catch (Throwable t) {
            logger.logError(t, "Failed to process promise reject callback {0}.", event);
        } finally {
            JavetResourceUtils.safeClose(promise, value);
        }
    }

    /**
     * Remove all references.
     *
     * @throws JavetException the javet exception
     * @since 0.8.3
     */
    void removeAllReferences() throws JavetException {
        removeReferences();
        removeCallbackContexts();
        removeV8Modules();
        v8Inspector = null;
    }

    /**
     * Remove a callback context by a handle.
     *
     * @param handle the handle
     * @since 0.8.3
     */
    public void removeCallbackContext(long handle) {
        synchronized (callbackContextLock) {
            callbackContextMap.remove(handle);
        }
    }

    /**
     * Remove callback contexts.
     *
     * @since 0.8.3
     */
    void removeCallbackContexts() {
        synchronized (callbackContextLock) {
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
    }

    /**
     * Remove a GC epilogue callback.
     *
     * @param iJavetGCCallback the javet GC callback
     * @since 1.0.3
     */
    public void removeGCEpilogueCallback(IJavetGCCallback iJavetGCCallback) {
        synchronized (gcEpilogueCallbacks) {
            gcEpilogueCallbacks.remove(Objects.requireNonNull(iJavetGCCallback));
            if (gcEpilogueCallbacks.isEmpty()) {
                v8Native.unregisterGCEpilogueCallback(handle);
            }
        }
    }

    /**
     * Remove a GC prologue callback.
     *
     * @param iJavetGCCallback the javet GC callback
     * @since 1.0.3
     */
    public void removeGCPrologueCallback(IJavetGCCallback iJavetGCCallback) {
        synchronized (gcPrologueCallbacks) {
            gcPrologueCallbacks.remove(Objects.requireNonNull(iJavetGCCallback));
            if (gcPrologueCallbacks.isEmpty()) {
                v8Native.unregisterGCPrologueCallback(handle);
            }
        }
    }

    /**
     * Remove a JNI global ref by a handle.
     *
     * @param handle the handle
     * @since 0.8.0
     */
    void removeJNIGlobalRef(long handle) {
        if (!isClosed()) {
            v8Native.removeJNIGlobalRef(handle);
        }
    }

    /**
     * Remove reference.
     *
     * @param iV8ValueReference the V8 value reference
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @SuppressWarnings("RedundantThrows")
    void removeReference(IV8ValueReference iV8ValueReference) throws JavetException {
        final long referenceHandle = iV8ValueReference.getHandle();
        synchronized (referenceLock) {
            if (referenceMap.containsKey(referenceHandle)) {
                final int referenceType = iV8ValueReference.getType().getId();
                if (referenceType == V8ValueReferenceType.Module.getId()) {
                    removeV8Module((IV8Module) iV8ValueReference);
                }
                v8Native.removeReferenceHandle(referenceHandle, referenceType);
                referenceMap.remove(referenceHandle);
            }
        }
        if (gcScheduled) {
            lowMemoryNotification();
            gcScheduled = false;
        }
    }

    /**
     * Remove all references.
     *
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    void removeReferences() throws JavetException {
        synchronized (referenceLock) {
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
        }
    }

    /**
     * Remove a V8 module by a resource name and force close flag.
     *
     * @param resourceName the resource name
     * @param forceClose   the force close
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    public void removeV8Module(String resourceName, boolean forceClose) throws JavetException {
        IV8Module iV8Module;
        synchronized (v8ModuleLock) {
            iV8Module = v8ModuleMap.remove(resourceName);
        }
        if (forceClose && iV8Module != null) {
            iV8Module.close(true);
        }
    }

    /**
     * Remove a V8 module by a resource name.
     *
     * @param resourceName the resource name
     * @throws JavetException the javet exception
     * @since 0.9.4
     */
    public void removeV8Module(String resourceName) throws JavetException {
        removeV8Module(resourceName, false);
    }

    /**
     * Remove a V8 module.
     *
     * @param iV8Module the V8 module
     * @throws JavetException the javet exception
     * @since 0.9.4
     */
    public void removeV8Module(IV8Module iV8Module) throws JavetException {
        removeV8Module(iV8Module, false);
    }

    /**
     * Remove a V8 module by a force close flag.
     *
     * @param iV8Module  the V8 module
     * @param forceClose the force close
     * @throws JavetException the javet exception
     * @since 0.9.4
     */
    public void removeV8Module(IV8Module iV8Module, boolean forceClose) throws JavetException {
        removeV8Module(iV8Module.getResourceName(), forceClose);
    }

    /**
     * Remove all V8 modules.
     *
     * @throws JavetException the javet exception
     * @since 0.9.3
     */
    public void removeV8Modules() throws JavetException {
        removeV8Modules(false);
    }

    /**
     * Remove all V8 modules by a force close flag.
     *
     * @param forceClose the force close
     * @throws JavetException the javet exception
     * @since 0.8.3
     */
    public void removeV8Modules(boolean forceClose) throws JavetException {
        synchronized (v8ModuleLock) {
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
        }
    }

    /**
     * Report pending messages.
     *
     * @return true : reported, false : not reported
     * @throws JavetException the javet exception
     * @since 1.0.4
     */
    public boolean reportPendingMessages() throws JavetException {
        return v8Native.reportPendingMessages(handle);
    }

    /**
     * Requests GC for testing.
     * Note: --expose_gc must be set.
     *
     * @param fullGC true : Full GC, false : Minor GC
     * @since 0.8.0
     */
    public void requestGarbageCollectionForTesting(boolean fullGC) {
        v8Native.requestGarbageCollectionForTesting(handle, fullGC);
    }

    /**
     * Resets the V8 context.
     * <p>
     * This is a light-weight and recommended reset.
     *
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public void resetContext() throws JavetException {
        removeAllReferences();
        v8Native.resetV8Context(handle, runtimeOptions);
    }

    /**
     * Resets the V8 isolate.
     * <p>
     * This is a heavy reset. Please avoid using it in performance sensitive scenario.
     *
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    @SuppressWarnings("UnusedReturnValue")
    public void resetIsolate() throws JavetException {
        removeAllReferences();
        v8Native.resetV8Isolate(handle, runtimeOptions);
    }

    /**
     * Tests whether 2 objects are the same in value.
     *
     * @param iV8ValueObject1 the V8 value object 1
     * @param iV8ValueObject2 the V8 value object 2
     * @return true : same, false : not same
     * @since 0.8.4
     */
    boolean sameValue(IV8ValueObject iV8ValueObject1, IV8ValueObject iV8ValueObject2) {
        return v8Native.sameValue(handle, iV8ValueObject1.getHandle(), iV8ValueObject2.getHandle());
    }

    /**
     * Run a script.
     *
     * @param <T>            the type parameter
     * @param iV8Script      the V8 script
     * @param resultRequired the result required
     * @return the result
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    @CheckReturnValue
    <T extends V8Value> T scriptRun(
            IV8Script iV8Script, boolean resultRequired) throws JavetException {
        return (T) v8Native.scriptRun(
                handle, iV8Script.getHandle(), iV8Script.getType().getId(), resultRequired);
    }

    /**
     * Sets a property by a key
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @param value          the value
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean set(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        return v8Native.set(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key, value);
    }

    /**
     * Sets accessor (getter / setter) by a property name.
     *
     * @param iV8ValueObject             the V8 value object
     * @param propertyName               the property name
     * @param javetCallbackContextGetter the javet callback context getter
     * @param javetCallbackContextSetter the javet callback context setter
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 0.8.9
     */
    @SuppressWarnings("RedundantThrows")
    boolean setAccessor(
            IV8ValueObject iV8ValueObject,
            V8Value propertyName,
            JavetCallbackContext javetCallbackContextGetter,
            JavetCallbackContext javetCallbackContextSetter) throws JavetException {
        assert (propertyName instanceof V8ValueString || propertyName instanceof V8ValueSymbol);
        boolean isAccessorSet = v8Native.setAccessor(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(),
                propertyName, javetCallbackContextGetter, javetCallbackContextSetter);
        synchronized (callbackContextLock) {
            if (javetCallbackContextGetter != null && javetCallbackContextGetter.isValid()) {
                callbackContextMap.put(javetCallbackContextGetter.getHandle(), javetCallbackContextGetter);
            }
            if (javetCallbackContextSetter != null && javetCallbackContextSetter.isValid()) {
                callbackContextMap.put(javetCallbackContextSetter.getHandle(), javetCallbackContextSetter);
            }
        }
        return isAccessorSet;
    }

    /**
     * Sets converter.
     *
     * @param converter the converter
     * @since 0.9.1
     */
    public void setConverter(IJavetConverter converter) {
        Objects.requireNonNull(converter);
        this.converter = converter;
    }

    /**
     * Sets GC scheduled.
     *
     * @param gcScheduled the GC scheduled
     * @since 0.9.1
     */
    public void setGCScheduled(boolean gcScheduled) {
        this.gcScheduled = gcScheduled;
    }

    /**
     * Sets logger.
     *
     * @param logger the logger
     * @since 0.9.1
     */
    public void setLogger(IJavetLogger logger) {
        this.logger = logger;
    }

    /**
     * Sets a private property.
     *
     * @param iV8ValueObject the V8 value object
     * @param propertyName   the property name
     * @param propertyValue  the property value
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 0.9.12
     */
    boolean setPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName, V8Value propertyValue)
            throws JavetException {
        return v8Native.setPrivateProperty(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), propertyName, propertyValue);
    }

    /**
     * Sets promise reject callback.
     *
     * @param promiseRejectCallback the promise reject callback
     * @since 0.9.1
     */
    public void setPromiseRejectCallback(IJavetPromiseRejectCallback promiseRejectCallback) {
        Objects.requireNonNull(promiseRejectCallback);
        this.promiseRejectCallback = promiseRejectCallback;
    }

    /**
     * Sets property.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @param value          the value
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    boolean setProperty(IV8ValueObject iV8ValueObject, V8Value key, V8Value value) throws JavetException {
        return v8Native.setProperty(handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(), key, value);
    }

    /**
     * Sets prototype.
     *
     * @param iV8ValueObject          the V8 value object
     * @param iV8ValueObjectPrototype the V8 value object prototype
     * @return true : success, false : failure
     * @throws JavetException the javet exception
     * @since 0.9.4
     */
    boolean setPrototype(
            IV8ValueObject iV8ValueObject, IV8ValueObject iV8ValueObjectPrototype) throws JavetException {
        return v8Native.setPrototype(
                handle, iV8ValueObject.getHandle(), iV8ValueObject.getType().getId(),
                iV8ValueObjectPrototype.getHandle());
    }

    /**
     * Sets V8 module resolver.
     *
     * @param v8ModuleResolver the V8 module resolver
     * @since 0.9.3
     */
    public void setV8ModuleResolver(IV8ModuleResolver v8ModuleResolver) {
        this.v8ModuleResolver = v8ModuleResolver;
    }

    /**
     * Sets a reference to weak.
     *
     * @param iV8ValueReference the V8 value reference
     * @since 0.8.4
     */
    void setWeak(IV8ValueReference iV8ValueReference) {
        v8Native.setWeak(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId(), iV8ValueReference);
    }

    /**
     * Tests whether 2 objects are strict equal.
     *
     * @param iV8ValueObject1 the V8 value object 1
     * @param iV8ValueObject2 the V8 value object 2
     * @return true : strict equal, false: not strict equal
     * @since 0.8.4
     */
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
     *
     * @since 0.8.0
     */
    public void terminateExecution() {
        v8Native.terminateExecution(handle);
    }

    @Override
    public <T, V extends V8Value> T toObject(V v8Value) throws JavetException {
        return (T) converter.toObject(v8Value);
    }

    /**
     * Gets a proto string from a reference.
     *
     * @param iV8ValueReference the V8 value reference
     * @return the proto string
     * @throws JavetException the javet exception
     * @since 0.8.4
     */
    String toProtoString(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Native.toProtoString(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    /**
     * Call toString().
     *
     * @param iV8ValueReference the V8 value reference
     * @return the string
     * @throws JavetException the javet exception
     * @since 0.8.0
     */
    String toString(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Native.toString(handle, iV8ValueReference.getHandle(), iV8ValueReference.getType().getId());
    }

    @Override
    @CheckReturnValue
    public <T, V extends V8Value> V toV8Value(T object) throws JavetException {
        return converter.toV8Value(this, object);
    }
}
