/*
 * Copyright (c) 2021-2026. caoccao.com Sam Cao
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

    /**
     * Allow or disallow eval() and new Function() in the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param allow           whether to allow code generation from strings
     */
    void allowCodeGenerationFromStrings(long v8RuntimeHandle, boolean allow);

    /**
     * Create a new ArrayBuffer with the given length.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param length          the length of the array buffer
     * @return the result
     */
    Object arrayBufferCreate(long v8RuntimeHandle, int length);

    /**
     * Create a new ArrayBuffer backed by the given ByteBuffer.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param byteBuffer      the byte buffer
     * @return the result
     */
    Object arrayBufferCreate(long v8RuntimeHandle, ByteBuffer byteBuffer);

    /**
     * Create a new empty Array.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return the result
     */
    Object arrayCreate(long v8RuntimeHandle);

    /**
     * Get the length of an Array.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the length
     */
    int arrayGetLength(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Pump the message loop with the given await mode.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8AwaitMode     the V8 await mode
     * @return true if successful
     */
    boolean await(long v8RuntimeHandle, int v8AwaitMode);

    /**
     * Batch get elements from an Array within the given index range.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param v8Values        the V8 values array
     * @param startIndex      the start index
     * @param endIndex        the end index
     * @return the number of items processed
     */
    int batchArrayGet(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object[] v8Values, int startIndex, int endIndex);

    /**
     * Batch get properties from an Object by keys.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param v8ValueKeys     the V8 value keys
     * @param v8ValueValues   the V8 value values
     * @param length          the length
     * @return the number of items processed
     */
    int batchObjectGet(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object[] v8ValueKeys, Object[] v8ValueValues, int length);

    /**
     * Create a Boolean object wrapper.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param booleanValue    the boolean value
     * @return the result
     */
    Object booleanObjectCreate(long v8RuntimeHandle, boolean booleanValue);

    /**
     * Get the primitive value of a Boolean object.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object booleanObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Cancel a pending terminate execution request.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     */
    void cancelTerminateExecution(long v8RuntimeHandle);

    /**
     * Clear internal statistics.
     */
    void clearInternalStatistic();

    /**
     * Clear the weak reference for a V8 value.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     */
    void clearWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Clone a V8 value, optionally as a reference copy.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param referenceCopy   whether to create a reference copy
     * @return the result
     */
    Object cloneV8Value(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean referenceCopy);

    /**
     * Close and dispose a V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     */
    void closeV8Runtime(long v8RuntimeHandle);

    /**
     * Get a value from a Context at the given index.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param index           the index
     * @return the result
     */
    Object contextGet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, int index);

    /**
     * Get the length of a Context.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the length
     */
    int contextGetLength(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Check if a Context matches the given context type.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param contextTypeId   the context type ID
     * @return true if the context matches the type
     */
    boolean contextIsContextType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, int contextTypeId);

    /**
     * Set the length of a Context.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param length          the length
     * @return true if successful
     */
    boolean contextSetLength(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, int length);

    /**
     * Create a V8 inspector session.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8Inspector     the V8 inspector object
     * @param name            the name
     * @param waitForDebugger whether to wait for debugger
     * @return the inspector session ID
     */
    int createV8Inspector(long v8RuntimeHandle, Object v8Inspector, String name, boolean waitForDebugger);

    /**
     * Create a new V8 runtime with the given options.
     *
     * @param runtimeOptions the runtime options
     * @return the V8 runtime handle
     */
    long createV8Runtime(Object runtimeOptions);

    /**
     * Create a Number object wrapper from a double.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param doubleValue     the double value
     * @return the result
     */
    Object doubleObjectCreate(long v8RuntimeHandle, double doubleValue);

    /**
     * Get the primitive value of a Number object created from a double.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object doubleObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Check if two V8 values are equal (==).
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle1  the first V8 value handle
     * @param v8ValueHandle2  the second V8 value handle
     * @return true if the values are equal
     */
    boolean equals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    /**
     * Create a new Error object with the given type and message.
     *
     * @param v8RuntimeHandle    the V8 runtime handle
     * @param v8ValueErrorTypeId the V8 value error type ID
     * @param message            the message
     * @return the result
     */
    Object errorCreate(long v8RuntimeHandle, int v8ValueErrorTypeId, String message);

    /**
     * Call a Function with the given receiver and arguments.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param receiver        the receiver object
     * @param returnResult    whether to return the result
     * @param values          the argument values
     * @return the result
     */
    Object functionCall(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object receiver, boolean returnResult, Object[] values);

    /**
     * Call a Function as a constructor with the given arguments.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param values          the argument values
     * @return the result
     */
    Object functionCallAsConstructor(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object[] values);

    /**
     * Check if a Function's compiled code can be discarded.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return true if the compiled code can be discarded
     */
    boolean functionCanDiscardCompiled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Compile a Function from source code with optional cached data.
     *
     * @param v8RuntimeHandle      the V8 runtime handle
     * @param script               the script source code
     * @param cachedData           the cached data
     * @param resourceName         the resource name
     * @param resourceLineOffset   the resource line offset
     * @param resourceColumnOffset the resource column offset
     * @param scriptId             the script ID
     * @param wasm                 whether the script is WebAssembly
     * @param arguments            the argument names
     * @param contextExtensions    the context extensions
     * @return the result
     */
    Object functionCompile(
            long v8RuntimeHandle, String script, byte[] cachedData,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean wasm,
            String[] arguments, Object[] contextExtensions);

    /**
     * Copy scope info from a source Function to a target Function.
     *
     * @param v8RuntimeHandle      the V8 runtime handle
     * @param targetV8ValueHandle  the target V8 value handle
     * @param targetV8ValueType    the target V8 value type
     * @param sourceV8ValueHandle  the source V8 value handle
     * @param sourceV8ValueType    the source V8 value type
     * @return true if successful
     */
    boolean functionCopyScopeInfoFrom(
            long v8RuntimeHandle,
            long targetV8ValueHandle, int targetV8ValueType,
            long sourceV8ValueHandle, int sourceV8ValueType);

    /**
     * Create a new Function from a callback context.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param callbackContext the callback context
     * @return the result
     */
    Object functionCreate(long v8RuntimeHandle, Object callbackContext);

    /**
     * Discard the compiled code of a Function.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return true if successful
     */
    boolean functionDiscardCompiled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the argument names of a Function.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the argument names
     */
    String[] functionGetArguments(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the cached compiled data of a Function.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the cached data
     */
    byte[] functionGetCachedData(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the context of a Function.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object functionGetContext(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the internal properties of a Function.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object functionGetInternalProperties(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the JS function type of a Function.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the JS function type
     */
    int functionGetJSFunctionType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the JS scope type of a Function.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the JS scope type
     */
    int functionGetJSScopeType(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the scope info objects of a Function.
     *
     * @param v8RuntimeHandle        the V8 runtime handle
     * @param v8ValueHandle          the V8 value handle
     * @param v8ValueType            the V8 value type
     * @param includeGlobalVariables whether to include global variables
     * @param includeScopeTypeGlobal whether to include global scope type
     * @return the result
     */
    Object functionGetScopeInfos(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            boolean includeGlobalVariables, boolean includeScopeTypeGlobal);

    /**
     * Get the script source object of a Function.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object functionGetScriptSource(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the source code string of a Function.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the source code string
     */
    String functionGetSourceCode(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Check if a Function is compiled.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return true if the function is compiled
     */
    boolean functionIsCompiled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Check if a Function is wrapped.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return true if the function is wrapped
     */
    boolean functionIsWrapped(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Set the context of a Function.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param v8Context       the V8 context
     * @return true if successful
     */
    boolean functionSetContext(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object v8Context);

    /**
     * Set the script source object of a Function.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param scriptSource    the script source object
     * @param cloneScript     whether to clone the script
     * @return true if successful
     */
    boolean functionSetScriptSource(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object scriptSource, boolean cloneScript);

    /**
     * Set the source code string of a Function.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param sourceCode      the source code string
     * @param cloneScript     whether to clone the script
     * @return true if successful
     */
    boolean functionSetSourceCode(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String sourceCode, boolean cloneScript);

    /**
     * Get the global object of the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return the result
     */
    Object getGlobalObject(long v8RuntimeHandle);

    /**
     * Get internal statistics.
     *
     * @return the statistics
     */
    long[] getInternalStatistic();

    /**
     * Get the priority of the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return the priority
     */
    int getPriority(long v8RuntimeHandle);

    /**
     * Get V8 heap space statistics for the given allocation space.
     *
     * @param v8RuntimeHandle  the V8 runtime handle
     * @param v8AllocationSpace the V8 allocation space
     * @return the result
     */
    Object getV8HeapSpaceStatistics(long v8RuntimeHandle, Object v8AllocationSpace);

    /**
     * Get V8 heap statistics.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return the result
     */
    Object getV8HeapStatistics(long v8RuntimeHandle);

    /**
     * Get V8 shared memory statistics.
     *
     * @return the result
     */
    Object getV8SharedMemoryStatistics();

    /**
     * Get the V8 engine version string.
     *
     * @return the version string
     */
    String getVersion();

    /**
     * Check if the V8 runtime has a pending exception.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return true if there is a pending exception
     */
    boolean hasException(long v8RuntimeHandle);

    /**
     * Check if a V8 value has the given internal type.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param internalTypeId  the internal type ID
     * @return true if the value has the given internal type
     */
    boolean hasInternalType(long v8RuntimeHandle, long v8ValueHandle, int internalTypeId);

    /**
     * Check if the V8 runtime has a pending message.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return true if there is a pending message
     */
    boolean hasPendingMessage(long v8RuntimeHandle);

    /**
     * Create a Number object wrapper from an integer.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param intValue        the integer value
     * @return the result
     */
    Object integerObjectCreate(long v8RuntimeHandle, int intValue);

    /**
     * Get the primitive value of a Number object created from an integer.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object integerObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Check if battery saver mode is enabled for the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return true if battery saver mode is enabled
     */
    boolean isBatterySaverModeEnabled(long v8RuntimeHandle);

    /**
     * Check if the V8 runtime is dead.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return true if the runtime is dead
     */
    boolean isDead(long v8RuntimeHandle);

    /**
     * Check if efficiency mode is enabled for the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return true if efficiency mode is enabled
     */
    boolean isEfficiencyModeEnabled(long v8RuntimeHandle);

    /**
     * Check if JavaScript execution is being terminated.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return true if execution is being terminated
     */
    boolean isExecutionTerminating(long v8RuntimeHandle);

    /**
     * Check if V8 internationalization support is enabled.
     *
     * @return true if internationalization is enabled
     */
    boolean isI18nEnabled();

    /**
     * Check if the V8 runtime is currently in use.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return true if the runtime is in use
     */
    boolean isInUse(long v8RuntimeHandle);

    /**
     * Check if memory saver mode is enabled for the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return true if memory saver mode is enabled
     */
    boolean isMemorySaverModeEnabled(long v8RuntimeHandle);

    /**
     * Check if a V8 value is a weak reference.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return true if the value is a weak reference
     */
    boolean isWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Lock the V8 runtime to the current thread.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return true if successful
     */
    boolean lockV8Runtime(long v8RuntimeHandle);

    /**
     * Create a BigInt object wrapper from a long.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param longValue       the long value
     * @return the result
     */
    Object longObjectCreate(long v8RuntimeHandle, long longValue);

    /**
     * Get the primitive value of a BigInt object.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object longObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Notify V8 of low memory conditions to trigger garbage collection.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     */
    void lowMemoryNotification(long v8RuntimeHandle);

    /**
     * Convert a Map to an array of key-value pairs.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object mapAsArray(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Remove all entries from a Map.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     */
    void mapClear(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Create a new empty Map.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return the result
     */
    Object mapCreate(long v8RuntimeHandle);

    /**
     * Delete an entry from a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return true if successful
     */
    boolean mapDelete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    /**
     * Get a value from a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return the result
     */
    Object mapGet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    /**
     * Get a boolean value from a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param primitiveFlags  the primitive flags
     * @return the boolean value
     */
    boolean mapGetBoolean(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    /**
     * Get a double value from a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param primitiveFlags  the primitive flags
     * @return the double value
     */
    double mapGetDouble(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    /**
     * Get an integer value from a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param primitiveFlags  the primitive flags
     * @return the integer value
     */
    int mapGetInteger(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    /**
     * Get a long value from a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param primitiveFlags  the primitive flags
     * @return the long value
     */
    long mapGetLong(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    /**
     * Get the number of entries in a Map.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the size
     */
    int mapGetSize(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get a string value from a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return the string value
     */
    String mapGetString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    /**
     * Check if a Map contains the given key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param value           the value
     * @return true if the key or value exists
     */
    boolean mapHas(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    /**
     * Set multiple key-value pairs in a Map.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param keysAndValues   the keys and values array
     * @return true if successful
     */
    boolean mapSet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object[] keysAndValues);

    /**
     * Set a boolean value in a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param value           the value
     * @return true if successful
     */
    boolean mapSetBoolean(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean value);

    /**
     * Set a double value in a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param value           the value
     * @return true if successful
     */
    boolean mapSetDouble(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, double value);

    /**
     * Set an integer value in a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param value           the value
     * @return true if successful
     */
    boolean mapSetInteger(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, int value);

    /**
     * Set a long value in a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param value           the value
     * @return true if successful
     */
    boolean mapSetLong(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, long value);

    /**
     * Set a null value in a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return true if successful
     */
    boolean mapSetNull(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    /**
     * Set a string value in a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param value           the value
     * @return true if successful
     */
    boolean mapSetString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, String value);

    /**
     * Set an undefined value in a Map by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return true if successful
     */
    boolean mapSetUndefined(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    /**
     * Compile a Module from source code with optional cached data.
     *
     * @param v8RuntimeHandle      the V8 runtime handle
     * @param script               the script source code
     * @param cachedData           the cached data
     * @param returnResult         whether to return the result
     * @param resourceName         the resource name
     * @param resourceLineOffset   the resource line offset
     * @param resourceColumnOffset the resource column offset
     * @param scriptId             the script ID
     * @param isWASM               whether the script is WebAssembly
     * @param isModule             whether the script is a module
     * @return the result
     */
    Object moduleCompile(
            long v8RuntimeHandle, String script, byte[] cachedData, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    /**
     * Create a synthetic Module with the given name and export values.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param name            the name
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object moduleCreate(long v8RuntimeHandle, String name, long v8ValueHandle, int v8ValueType);

    /**
     * Evaluate a Module and optionally return the result.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param resultRequired  whether the result is required
     * @return the result
     */
    Object moduleEvaluate(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean resultRequired);

    /**
     * Compile and execute a Module from source code.
     *
     * @param v8RuntimeHandle      the V8 runtime handle
     * @param script               the script source code
     * @param cachedData           the cached data
     * @param returnResult         whether to return the result
     * @param resourceName         the resource name
     * @param resourceLineOffset   the resource line offset
     * @param resourceColumnOffset the resource column offset
     * @param scriptId             the script ID
     * @param isWASM               whether the script is WebAssembly
     * @return the result
     */
    Object moduleExecute(
            long v8RuntimeHandle, String script, byte[] cachedData, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM);

    /**
     * Get the cached compiled data of a Module.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the cached data
     */
    byte[] moduleGetCachedData(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the exception from a Module if it is in an errored state.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object moduleGetException(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the identity hash of a Module.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the identity hash
     */
    int moduleGetIdentityHash(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the namespace object of a Module.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object moduleGetNamespace(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the resource name of a Module.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the resource name
     */
    String moduleGetResourceName(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the script ID of a Module.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the script ID
     */
    int moduleGetScriptId(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the status of a Module.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the status
     */
    int moduleGetStatus(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Instantiate a Module, resolving its dependencies.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return true if successful
     */
    boolean moduleInstantiate(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Check if a Module is a source text module.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return true if the module is a source text module
     */
    boolean moduleIsSourceTextModule(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Check if a Module is a synthetic module.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return true if the module is a synthetic module
     */
    boolean moduleIsSyntheticModule(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Create a new empty Object.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return the result
     */
    Object objectCreate(long v8RuntimeHandle);

    /**
     * Delete a property from an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return true if successful
     */
    boolean objectDelete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    /**
     * Delete a private property from an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return true if successful
     */
    boolean objectDeletePrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String key);

    /**
     * Get a property value from an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return the result
     */
    Object objectGet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    /**
     * Get a boolean property value from an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param primitiveFlags  the primitive flags
     * @return the boolean value
     */
    boolean objectGetBoolean(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    /**
     * Get a double property value from an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param primitiveFlags  the primitive flags
     * @return the double value
     */
    double objectGetDouble(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    /**
     * Get the identity hash of an Object.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the identity hash
     */
    int objectGetIdentityHash(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get an integer property value from an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param primitiveFlags  the primitive flags
     * @return the integer value
     */
    int objectGetInteger(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    /**
     * Get a long property value from an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param primitiveFlags  the primitive flags
     * @return the long value
     */
    long objectGetLong(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean[] primitiveFlags);

    /**
     * Get the own property names of an Object.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object objectGetOwnPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get a private property value from an Object by name.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param propertyName    the property name
     * @return the result
     */
    Object objectGetPrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String propertyName);

    /**
     * Get a property value from an Object including its prototype chain.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return the result
     */
    Object objectGetProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    /**
     * Get all property names of an Object including inherited ones.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object objectGetPropertyNames(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the prototype of an Object.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object objectGetPrototype(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get a string property value from an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return the string value
     */
    String objectGetString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    /**
     * Check if an Object has the given key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param value           the value
     * @return true if the key or value exists
     */
    boolean objectHas(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    /**
     * Check if an Object has the given own property.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param type            the V8 value type
     * @param key             the key
     * @return true if the key or value exists
     */
    boolean objectHasOwnProperty(long v8RuntimeHandle, long v8ValueHandle, int type, Object key);

    /**
     * Check if an Object has the given private property.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param type            the V8 value type
     * @param propertyName    the property name
     * @return true if the key or value exists
     */
    boolean objectHasPrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int type, String propertyName);

    /**
     * Invoke a named method on an Object with the given arguments.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param functionName    the function name
     * @param returnResult    whether to return the result
     * @param values          the argument values
     * @return the result
     */
    Object objectInvoke(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            String functionName, boolean returnResult, Object[] values);

    /**
     * Check if an Object is frozen.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @return true if the object is frozen
     */
    boolean objectIsFrozen(long v8RuntimeHandle, long v8ValueHandle);

    /**
     * Check if an Object is sealed.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @return true if the object is sealed
     */
    boolean objectIsSealed(long v8RuntimeHandle, long v8ValueHandle);

    /**
     * Set multiple key-value pairs on an Object.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param keysAndValues   the keys and values array
     * @return true if successful
     */
    boolean objectSet(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object[] keysAndValues);

    /**
     * Set a property accessor (getter/setter) on an Object.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param propertyName    the property name
     * @param getter          the getter callback
     * @param setter          the setter callback
     * @return true if successful
     */
    boolean objectSetAccessor(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            Object propertyName, Object getter, Object setter);

    /**
     * Set a boolean property on an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param value           the value
     * @return true if successful
     */
    boolean objectSetBoolean(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, boolean value);

    /**
     * Set a double property on an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param value           the value
     * @return true if successful
     */
    boolean objectSetDouble(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, double value);

    /**
     * Set an integer property on an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param value           the value
     * @return true if successful
     */
    boolean objectSetInteger(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, int value);

    /**
     * Set a long property on an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param value           the value
     * @return true if successful
     */
    boolean objectSetLong(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, long value);

    /**
     * Set a null property on an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return true if successful
     */
    boolean objectSetNull(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    /**
     * Set a private property on an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param value           the value
     * @return true if successful
     */
    boolean objectSetPrivateProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, String key, Object value);

    /**
     * Set a property on an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param value           the value
     * @return true if successful
     */
    boolean objectSetProperty(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, Object value);

    /**
     * Set the prototype of an Object.
     *
     * @param v8RuntimeHandle        the V8 runtime handle
     * @param v8ValueHandle          the V8 value handle
     * @param v8ValueType            the V8 value type
     * @param v8ValueHandlePrototype the prototype V8 value handle
     * @return true if successful
     */
    boolean objectSetPrototype(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, long v8ValueHandlePrototype);

    /**
     * Set a string property on an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @param value           the value
     * @return true if successful
     */
    boolean objectSetString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key, String value);

    /**
     * Set an undefined property on an Object by key.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return true if successful
     */
    boolean objectSetUndefined(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    /**
     * Convert an Object to its proto string representation.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the string representation
     */
    String objectToProtoString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Register a catch handler on a Promise.
     *
     * @param v8RuntimeHandle      the V8 runtime handle
     * @param v8ValueHandle        the V8 value handle
     * @param v8ValueType          the V8 value type
     * @param v8ValueFunctionHandle the V8 value function handle
     * @return the result
     */
    Object promiseCatch(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, long v8ValueFunctionHandle);

    /**
     * Create a new Promise with its resolver.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return the result
     */
    Object promiseCreate(long v8RuntimeHandle);

    /**
     * Get the Promise from a Promise resolver.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object promiseGetPromise(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the result value of a settled Promise.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object promiseGetResult(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the state of a Promise (pending, fulfilled, or rejected).
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the state
     */
    int promiseGetState(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Check if a Promise has a registered handler.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return true if the promise has a handler
     */
    boolean promiseHasHandler(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Mark a Promise as handled to suppress unhandled rejection warnings.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     */
    void promiseMarkAsHandled(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Reject a Promise with the given value.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param value           the value
     * @return true if successful
     */
    boolean promiseReject(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    /**
     * Resolve a Promise with the given value.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param value           the value
     * @return true if successful
     */
    boolean promiseResolve(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    /**
     * Register fulfilled and rejected handlers on a Promise.
     *
     * @param v8RuntimeHandle                the V8 runtime handle
     * @param v8ValueHandle                  the V8 value handle
     * @param v8ValueType                    the V8 value type
     * @param v8ValueFunctionFulfilledHandle the fulfilled function handle
     * @param v8ValueFunctionRejectedHandle  the rejected function handle
     * @return the result
     */
    Object promiseThen(
            long v8RuntimeHandle, long v8ValueHandle, int v8ValueType,
            long v8ValueFunctionFulfilledHandle, long v8ValueFunctionRejectedHandle);

    /**
     * Create a new Proxy with the given target.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param target          the target object
     * @return the result
     */
    Object proxyCreate(long v8RuntimeHandle, Object target);

    /**
     * Get the handler of a Proxy.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object proxyGetHandler(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the target of a Proxy.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object proxyGetTarget(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Check if a Proxy has been revoked.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return true if the proxy is revoked
     */
    boolean proxyIsRevoked(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Revoke a Proxy, making it no longer usable.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     */
    void proxyRevoke(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Register a GC epilogue callback for the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     */
    void registerGCEpilogueCallback(long v8RuntimeHandle);

    /**
     * Register a GC prologue callback for the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     */
    void registerGCPrologueCallback(long v8RuntimeHandle);

    /**
     * Register a near-heap-limit callback for the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     */
    void registerNearHeapLimitCallback(long v8RuntimeHandle);

    /**
     * Register a V8 runtime with its Java object reference.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8Runtime       the V8 runtime object
     */
    void registerV8Runtime(long v8RuntimeHandle, Object v8Runtime);

    /**
     * Remove a JNI global reference.
     *
     * @param handle the handle
     */
    void removeJNIGlobalRef(long handle);

    /**
     * Remove a raw pointer of the given type.
     *
     * @param handle          the handle
     * @param rawPointerTypeId the raw pointer type ID
     */
    void removeRawPointer(long handle, int rawPointerTypeId);

    /**
     * Remove a V8 reference handle.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param referenceHandle the reference handle
     * @param referenceType   the reference type
     */
    void removeReferenceHandle(long v8RuntimeHandle, long referenceHandle, int referenceType);

    /**
     * Report pending messages in the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return true if successful
     */
    boolean reportPendingMessages(long v8RuntimeHandle);

    /**
     * Request garbage collection for testing purposes.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param fullGC          whether to perform a full garbage collection
     */
    void requestGarbageCollectionForTesting(long v8RuntimeHandle, boolean fullGC);

    /**
     * Reset the V8 context with the given options.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param runtimeOptions  the runtime options
     */
    void resetV8Context(long v8RuntimeHandle, Object runtimeOptions);

    /**
     * Reset the V8 isolate with the given options.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param runtimeOptions  the runtime options
     */
    void resetV8Isolate(long v8RuntimeHandle, Object runtimeOptions);

    /**
     * Check if two V8 values are the same value (Object.is semantics).
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle1  the first V8 value handle
     * @param v8ValueHandle2  the second V8 value handle
     * @return true if the values are the same
     */
    boolean sameValue(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    /**
     * Compile a Script from source code with optional cached data.
     *
     * @param v8RuntimeHandle      the V8 runtime handle
     * @param script               the script source code
     * @param cachedData           the cached data
     * @param returnResult         whether to return the result
     * @param resourceName         the resource name
     * @param resourceLineOffset   the resource line offset
     * @param resourceColumnOffset the resource column offset
     * @param scriptId             the script ID
     * @param isWASM               whether the script is WebAssembly
     * @param isModule             whether the script is a module
     * @return the result
     */
    Object scriptCompile(
            long v8RuntimeHandle, String script, byte[] cachedData, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM, boolean isModule);

    /**
     * Compile and execute a Script from source code.
     *
     * @param v8RuntimeHandle      the V8 runtime handle
     * @param script               the script source code
     * @param cachedData           the cached data
     * @param returnResult         whether to return the result
     * @param resourceName         the resource name
     * @param resourceLineOffset   the resource line offset
     * @param resourceColumnOffset the resource column offset
     * @param scriptId             the script ID
     * @param isWASM               whether the script is WebAssembly
     * @return the result
     */
    Object scriptExecute(
            long v8RuntimeHandle, String script, byte[] cachedData, boolean returnResult,
            String resourceName, int resourceLineOffset, int resourceColumnOffset,
            int scriptId, boolean isWASM);

    /**
     * Get the cached compiled data of a Script.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the cached data
     */
    byte[] scriptGetCachedData(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the resource name of a Script.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the resource name
     */
    String scriptGetResourceName(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Run a compiled Script and optionally return the result.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param resultRequired  whether the result is required
     * @return the result
     */
    Object scriptRun(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, boolean resultRequired);

    /**
     * Add a value to a Set.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param value           the value
     */
    void setAdd(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    /**
     * Convert a Set to an Array.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object setAsArray(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Enable or disable battery saver mode for the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param enabled         whether to enable the mode
     */
    void setBatterySaverModeEnabled(long v8RuntimeHandle, boolean enabled);

    /**
     * Remove all entries from a Set.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     */
    void setClear(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Create a new empty Set.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return the result
     */
    Object setCreate(long v8RuntimeHandle);

    /**
     * Delete a value from a Set.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param key             the key
     * @return true if successful
     */
    boolean setDelete(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object key);

    /**
     * Get the number of entries in a Set.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the size
     */
    int setGetSize(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Check if a Set contains the given value.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param value           the value
     * @return true if the key or value exists
     */
    boolean setHas(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object value);

    /**
     * Enable or disable memory saver mode for the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param enabled         whether to enable the mode
     */
    void setMemorySaverModeEnabled(long v8RuntimeHandle, boolean enabled);

    /**
     * Set the priority of the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param priority        the priority value
     */
    void setPriority(long v8RuntimeHandle, int priority);

    /**
     * Set a V8 value as a weak reference with an associated object reference.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @param objectReference the object reference
     */
    void setWeak(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType, Object objectReference);

    /**
     * Create a V8 heap snapshot.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return the snapshot data
     */
    byte[] snapshotCreate(long v8RuntimeHandle);

    /**
     * Check if two V8 values are strictly equal (===).
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle1  the first V8 value handle
     * @param v8ValueHandle2  the second V8 value handle
     * @return true if the values are strictly equal
     */
    boolean strictEquals(long v8RuntimeHandle, long v8ValueHandle1, long v8ValueHandle2);

    /**
     * Create a String object wrapper.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param str             the string value
     * @return the result
     */
    Object stringObjectCreate(long v8RuntimeHandle, String str);

    /**
     * Get the primitive value of a String object.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object stringObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Create a new Symbol with the given description.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param description     the symbol description
     * @return the result
     */
    Object symbolCreate(long v8RuntimeHandle, String description);

    /**
     * Get the description of a Symbol.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the description string
     */
    String symbolDescription(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Get the primitive value of a Symbol object wrapper.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object symbolObjectValueOf(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Convert a Symbol to its Object wrapper.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the result
     */
    Object symbolToObject(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Terminate JavaScript execution in the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     */
    void terminateExecution(long v8RuntimeHandle);

    /**
     * Throw an error of the given type and message in V8.
     *
     * @param v8RuntimeHandle  the V8 runtime handle
     * @param v8ValueErrorType the V8 value error type
     * @param message          the message
     * @return true if successful
     */
    boolean throwError(long v8RuntimeHandle, int v8ValueErrorType, String message);

    /**
     * Throw the given V8 value as an error in V8.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8Value         the V8 value to throw
     * @return true if successful
     */
    boolean throwError(long v8RuntimeHandle, Object v8Value);

    /**
     * Convert a V8 value to its string representation.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param v8ValueHandle   the V8 value handle
     * @param v8ValueType     the V8 value type
     * @return the string representation
     */
    String toString(long v8RuntimeHandle, long v8ValueHandle, int v8ValueType);

    /**
     * Unlock the V8 runtime from the current thread.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @return true if successful
     */
    boolean unlockV8Runtime(long v8RuntimeHandle);

    /**
     * Unregister the GC epilogue callback from the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     */
    void unregisterGCEpilogueCallback(long v8RuntimeHandle);

    /**
     * Unregister the GC prologue callback from the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     */
    void unregisterGCPrologueCallback(long v8RuntimeHandle);

    /**
     * Unregister the near-heap-limit callback from the V8 runtime.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param heapLimit       the heap limit
     */
    void unregisterNearHeapLimitCallback(long v8RuntimeHandle, long heapLimit);

    /**
     * Break the program in the V8 inspector.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param sessionId       the inspector session ID
     * @param breakReason     the break reason
     * @param breakDetails    the break details
     */
    void v8InspectorBreakProgram(long v8RuntimeHandle, int sessionId, String breakReason, String breakDetails);

    /**
     * Cancel a scheduled pause on the next statement in the V8 inspector.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param sessionId       the inspector session ID
     */
    void v8InspectorCancelPauseOnNextStatement(long v8RuntimeHandle, int sessionId);

    /**
     * Close a V8 inspector session.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param sessionId       the inspector session ID
     */
    void v8InspectorCloseSession(long v8RuntimeHandle, int sessionId);

    /**
     * Evaluate an expression in the V8 inspector session.
     *
     * @param v8RuntimeHandle     the V8 runtime handle
     * @param sessionId           the inspector session ID
     * @param expression          the expression to evaluate
     * @param includeCommandLineAPI whether to include command line API
     * @return the result
     */
    Object v8InspectorEvaluate(long v8RuntimeHandle, int sessionId, String expression, boolean includeCommandLineAPI);

    /**
     * Schedule a pause on the next statement in the V8 inspector.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param sessionId       the inspector session ID
     * @param breakReason     the break reason
     * @param breakDetails    the break details
     */
    void v8InspectorSchedulePauseOnNextStatement(long v8RuntimeHandle, int sessionId, String breakReason, String breakDetails);

    /**
     * Send a protocol message to the V8 inspector session.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param sessionId       the inspector session ID
     * @param message         the message
     */
    void v8InspectorSend(long v8RuntimeHandle, int sessionId, String message);

    /**
     * Set whether to skip all pauses in the V8 inspector session.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     * @param sessionId       the inspector session ID
     * @param skip            whether to skip all pauses
     */
    void v8InspectorSetSkipAllPauses(long v8RuntimeHandle, int sessionId, boolean skip);

    /**
     * Wait for a debugger to attach to the V8 inspector.
     *
     * @param v8RuntimeHandle the V8 runtime handle
     */
    void v8InspectorWaitForDebugger(long v8RuntimeHandle);
}
