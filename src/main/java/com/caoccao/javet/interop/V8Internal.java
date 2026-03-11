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

import com.caoccao.javet.annotations.CheckReturnValue;
import com.caoccao.javet.enums.JSFunctionType;
import com.caoccao.javet.enums.JSScopeType;
import com.caoccao.javet.enums.V8ValueInternalType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.*;

import java.util.Objects;

/**
 * The V8 internal API that delegates operations to the V8 runtime.
 */
public final class V8Internal {
    /** The V8 runtime. */
    private final V8Runtime v8Runtime;

    V8Internal(V8Runtime v8Runtime) {
        this.v8Runtime = Objects.requireNonNull(v8Runtime);
    }

    /**
     * Adds a reference to the V8 runtime.
     *
     * @param iV8ValueReference the V8 value reference
     */
    public void addReference(IV8ValueReference iV8ValueReference) {
        v8Runtime.addReference(iV8ValueReference);
    }

    /**
     * Gets the length of a V8 array.
     *
     * @param iV8ValueArray the V8 value array
     * @return the array length
     * @throws JavetException if a V8 error occurs
     */
    public int arrayGetLength(IV8ValueArray iV8ValueArray) throws JavetException {
        return v8Runtime.arrayGetLength(iV8ValueArray);
    }

    /**
     * Gets the length of a V8 typed array.
     *
     * @param iV8ValueTypedArray the V8 value typed array
     * @return the typed array length
     * @throws JavetException if a V8 error occurs
     */
    public int arrayGetLength(IV8ValueTypedArray iV8ValueTypedArray) throws JavetException {
        return v8Runtime.arrayGetLength(iV8ValueTypedArray);
    }

    /**
     * Batch gets values from a V8 array.
     *
     * @param iV8ValueArray the V8 value array
     * @param v8Values      the output V8 values
     * @param startIndex    the start index
     * @param endIndex      the end index
     * @return the number of values retrieved
     * @throws JavetException if a V8 error occurs
     */
    public int batchArrayGet(
            IV8ValueArray iV8ValueArray, V8Value[] v8Values, int startIndex, int endIndex)
            throws JavetException {
        return v8Runtime.batchArrayGet(iV8ValueArray, v8Values, startIndex, endIndex);
    }

    /**
     * Batch gets keys and values from a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @param v8ValueKeys    the output V8 value keys
     * @param v8ValueValues  the output V8 value values
     * @param length         the length
     * @return the number of key-value pairs retrieved
     * @throws JavetException if a V8 error occurs
     */
    public int batchObjectGet(
            IV8ValueObject iV8ValueObject, V8Value[] v8ValueKeys, V8Value[] v8ValueValues, int length)
            throws JavetException {
        return v8Runtime.batchObjectGet(iV8ValueObject, v8ValueKeys, v8ValueValues, length);
    }

    /**
     * Gets the primitive boolean value of a V8 boolean object.
     *
     * @param v8ValueBooleanObject the V8 value boolean object
     * @return the V8 value boolean
     */
    public V8ValueBoolean booleanObjectValueOf(V8ValueBooleanObject v8ValueBooleanObject) {
        return v8Runtime.booleanObjectValueOf(v8ValueBooleanObject);
    }

    /**
     * Clears the weak reference state of a V8 value reference.
     *
     * @param iV8ValueReference the V8 value reference
     * @throws JavetException if a V8 error occurs
     */
    public void clearWeak(IV8ValueReference iV8ValueReference) throws JavetException {
        v8Runtime.clearWeak(iV8ValueReference);
    }

    /**
     * Clones a V8 value.
     *
     * @param <T>                 the type of V8 value
     * @param iV8ValueReference   the V8 value reference to clone
     * @param referenceCopy       whether to perform a reference copy
     * @return the cloned V8 value
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public <T extends V8Value> T cloneV8Value(
            IV8ValueReference iV8ValueReference, boolean referenceCopy)
            throws JavetException {
        return v8Runtime.cloneV8Value(iV8ValueReference, referenceCopy);
    }

    /**
     * Gets a value from a V8 context by index.
     *
     * @param <T>        the type of V8 value
     * @param iV8Context the V8 context
     * @param index      the index
     * @return the V8 value
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public <T extends V8Value> T contextGet(IV8Context iV8Context, int index) throws JavetException {
        return v8Runtime.contextGet(iV8Context, index);
    }

    /**
     * Gets the length of a V8 context.
     *
     * @param iV8Context the V8 context
     * @return the context length
     * @throws JavetException if a V8 error occurs
     */
    public int contextGetLength(IV8Context iV8Context) throws JavetException {
        return v8Runtime.contextGetLength(iV8Context);
    }

    /**
     * Checks whether a V8 context matches the given context type ID.
     *
     * @param iV8Context    the V8 context
     * @param contextTypeId the context type ID
     * @return true if the context matches the type
     * @throws JavetException if a V8 error occurs
     */
    public boolean contextIsContextType(IV8Context iV8Context, int contextTypeId) throws JavetException {
        return v8Runtime.contextIsContextType(iV8Context, contextTypeId);
    }

    /**
     * Sets the length of a V8 context.
     *
     * @param iV8Context the V8 context
     * @param length     the length
     * @return true if the length was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean contextSetLength(IV8Context iV8Context, int length) throws JavetException {
        return v8Runtime.contextSetLength(iV8Context, length);
    }

    /**
     * Gets the primitive number value of a V8 double object.
     *
     * @param v8ValueDoubleObject the V8 value double object
     * @return the V8 value number
     */
    public V8ValueNumber<?> doubleObjectValueOf(V8ValueDoubleObject v8ValueDoubleObject) {
        return v8Runtime.doubleObjectValueOf(v8ValueDoubleObject);
    }

    /**
     * Tests whether two V8 value references are equal.
     *
     * @param iV8ValueReference1 the first V8 value reference
     * @param iV8ValueReference2 the second V8 value reference
     * @return true if the references are equal
     * @throws JavetException if a V8 error occurs
     */
    public boolean equals(IV8ValueReference iV8ValueReference1, IV8ValueReference iV8ValueReference2)
            throws JavetException {
        return v8Runtime.equals(iV8ValueReference1, iV8ValueReference2);
    }

    /**
     * Calls a V8 function.
     *
     * @param <T>             the type of return value
     * @param iV8ValueObject  the V8 value object representing the function
     * @param receiver        the receiver (this) for the call
     * @param returnResult    whether to return the result
     * @param v8Values        the arguments
     * @return the result of the function call
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public <T extends V8Value> T functionCall(
            IV8ValueObject iV8ValueObject, V8Value receiver, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        return v8Runtime.functionCall(iV8ValueObject, receiver, returnResult, v8Values);
    }

    /**
     * Calls a V8 function as a constructor.
     *
     * @param <T>            the type of return value
     * @param iV8ValueObject the V8 value object representing the constructor
     * @param v8Values       the arguments
     * @return the constructed V8 value
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public <T extends V8Value> T functionCallAsConstructor(
            IV8ValueObject iV8ValueObject, V8Value... v8Values) throws JavetException {
        return v8Runtime.functionCallAsConstructor(iV8ValueObject, v8Values);
    }

    /**
     * Checks whether a V8 function can discard its compiled code.
     *
     * @param iV8ValueFunction the V8 value function
     * @return true if the compiled code can be discarded
     */
    public boolean functionCanDiscardCompiled(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.functionCanDiscardCompiled(iV8ValueFunction);
    }

    /**
     * Copies scope info from one V8 function to another.
     *
     * @param targetIV8ValueFunction the target V8 value function
     * @param sourceIV8ValueFunction the source V8 value function
     * @return true if the scope info was copied successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean functionCopyScopeInfoFrom(
            IV8ValueFunction targetIV8ValueFunction,
            IV8ValueFunction sourceIV8ValueFunction) throws JavetException {
        return v8Runtime.functionCopyScopeInfoFrom(targetIV8ValueFunction, sourceIV8ValueFunction);
    }

    /**
     * Discards the compiled code of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @return true if the compiled code was discarded successfully
     */
    public boolean functionDiscardCompiled(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.functionDiscardCompiled(iV8ValueFunction);
    }

    /**
     * Gets the argument names of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @return the argument names
     * @throws JavetException if a V8 error occurs
     */
    public String[] functionGetArguments(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.functionGetArguments(iV8ValueFunction);
    }

    /**
     * Gets the cached data of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @return the cached data as a byte array
     * @throws JavetException if a V8 error occurs
     */
    public byte[] functionGetCachedData(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.functionGetCachedData(iV8ValueFunction);
    }

    /**
     * Gets the context of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @return the V8 context
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public V8Context functionGetContext(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.functionGetContext(iV8ValueFunction);
    }

    /**
     * Gets the internal properties of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @return the internal properties as a V8 value array
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public IV8ValueArray functionGetInternalProperties(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.functionGetInternalProperties(iV8ValueFunction);
    }

    /**
     * Gets the JS function type of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @return the JS function type
     */
    public JSFunctionType functionGetJSFunctionType(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.functionGetJSFunctionType(iV8ValueFunction);
    }

    /**
     * Gets the JS scope type of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @return the JS scope type
     */
    public JSScopeType functionGetJSScopeType(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.functionGetJSScopeType(iV8ValueFunction);
    }

    /**
     * Gets the scope infos of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @param options          the options for getting scope infos
     * @return the scope infos as a V8 value array
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public IV8ValueArray functionGetScopeInfos(
            IV8ValueFunction iV8ValueFunction,
            IV8ValueFunction.GetScopeInfosOptions options)
            throws JavetException {
        return v8Runtime.functionGetScopeInfos(iV8ValueFunction, options);
    }

    /**
     * Gets the script source of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @return the script source
     * @throws JavetException if a V8 error occurs
     */
    public IV8ValueFunction.ScriptSource functionGetScriptSource(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.functionGetScriptSource(iV8ValueFunction);
    }

    /**
     * Gets the source code of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @return the source code
     * @throws JavetException if a V8 error occurs
     */
    public String functionGetSourceCode(IV8ValueFunction iV8ValueFunction) throws JavetException {
        return v8Runtime.functionGetSourceCode(iV8ValueFunction);
    }

    /**
     * Checks whether a V8 function is compiled.
     *
     * @param iV8ValueFunction the V8 value function
     * @return true if the function is compiled
     */
    public boolean functionIsCompiled(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.functionIsCompiled(iV8ValueFunction);
    }

    /**
     * Checks whether a V8 function is wrapped.
     *
     * @param iV8ValueFunction the V8 value function
     * @return true if the function is wrapped
     */
    public boolean functionIsWrapped(IV8ValueFunction iV8ValueFunction) {
        return v8Runtime.functionIsWrapped(iV8ValueFunction);
    }

    /**
     * Sets the context of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @param v8Context        the V8 context
     * @return true if the context was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean functionSetContext(
            IV8ValueFunction iV8ValueFunction, V8Context v8Context) throws JavetException {
        return v8Runtime.functionSetContext(iV8ValueFunction, v8Context);
    }

    /**
     * Sets the script source of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @param scriptSource     the script source
     * @param cloneScript      whether to clone the script
     * @return true if the script source was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean functionSetScriptSource(
            IV8ValueFunction iV8ValueFunction, IV8ValueFunction.ScriptSource scriptSource, boolean cloneScript)
            throws JavetException {
        return v8Runtime.functionSetScriptSource(iV8ValueFunction, scriptSource, cloneScript);
    }

    /**
     * Sets the source code of a V8 function.
     *
     * @param iV8ValueFunction the V8 value function
     * @param sourceCode       the source code
     * @param cloneScript      whether to clone the script
     * @return true if the source code was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean functionSetSourceCode(
            IV8ValueFunction iV8ValueFunction, String sourceCode, boolean cloneScript)
            throws JavetException {
        return v8Runtime.functionSetSourceCode(iV8ValueFunction, sourceCode, cloneScript);
    }

    /**
     * Checks whether a V8 object has the given internal type.
     *
     * @param iV8ValueObject the V8 value object
     * @param internalType   the V8 value internal type
     * @return true if the object has the internal type
     */
    public boolean hasInternalType(IV8ValueObject iV8ValueObject, V8ValueInternalType internalType) {
        return v8Runtime.hasInternalType(iV8ValueObject, internalType);
    }

    /**
     * Gets the primitive integer value of a V8 integer object.
     *
     * @param v8ValueIntegerObject the V8 value integer object
     * @return the V8 value integer
     * @throws JavetException if a V8 error occurs
     */
    public V8ValueInteger integerObjectValueOf(V8ValueIntegerObject v8ValueIntegerObject) throws JavetException {
        return v8Runtime.integerObjectValueOf(v8ValueIntegerObject);
    }

    /**
     * Checks whether a V8 value reference is weak.
     *
     * @param iV8ValueReference the V8 value reference
     * @return true if the reference is weak
     */
    public boolean isWeak(IV8ValueReference iV8ValueReference) {
        return v8Runtime.isWeak(iV8ValueReference);
    }

    /**
     * Gets the primitive long value of a V8 long object.
     *
     * @param v8ValueLongObject the V8 value long object
     * @return the V8 value long
     * @throws JavetException if a V8 error occurs
     */
    public V8ValueLong longObjectValueOf(V8ValueLongObject v8ValueLongObject) throws JavetException {
        return v8Runtime.longObjectValueOf(v8ValueLongObject);
    }

    /**
     * Converts a V8 map to a V8 array.
     *
     * @param iV8ValueMap the V8 value map
     * @return the V8 value array
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public V8ValueArray mapAsArray(IV8ValueMap iV8ValueMap) throws JavetException {
        return v8Runtime.mapAsArray(iV8ValueMap);
    }

    /**
     * Clears all entries from a V8 map.
     *
     * @param iV8ValueMap the V8 value map
     */
    public void mapClear(IV8ValueMap iV8ValueMap) {
        v8Runtime.mapClear(iV8ValueMap);
    }

    /**
     * Deletes an entry from a V8 map by key.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @return true if the entry was deleted
     * @throws JavetException if a V8 error occurs
     */
    public boolean mapDelete(IV8ValueMap iV8ValueMap, V8Value key) throws JavetException {
        return v8Runtime.mapDelete(iV8ValueMap, key);
    }

    /**
     * Gets a value from a V8 map by key.
     *
     * @param <T>         the type of V8 value
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @return the V8 value
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public <T extends V8Value> T mapGet(IV8ValueMap iV8ValueMap, V8Value key) throws JavetException {
        return v8Runtime.mapGet(iV8ValueMap, key);
    }

    /**
     * Gets a boolean value from a V8 map by key.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @return the boolean value, or null
     * @throws JavetException if a V8 error occurs
     */
    public Boolean mapGetBoolean(
            IV8ValueMap iV8ValueMap, V8Value key)
            throws JavetException {
        return v8Runtime.mapGetBoolean(iV8ValueMap, key);
    }

    /**
     * Gets a double value from a V8 map by key.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @return the double value, or null
     * @throws JavetException if a V8 error occurs
     */
    public Double mapGetDouble(
            IV8ValueMap iV8ValueMap, V8Value key)
            throws JavetException {
        return v8Runtime.mapGetDouble(iV8ValueMap, key);
    }

    /**
     * Gets an integer value from a V8 map by key.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @return the integer value, or null
     * @throws JavetException if a V8 error occurs
     */
    public Integer mapGetInteger(
            IV8ValueMap iV8ValueMap, V8Value key)
            throws JavetException {
        return v8Runtime.mapGetInteger(iV8ValueMap, key);
    }

    /**
     * Gets a long value from a V8 map by key.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @return the long value, or null
     * @throws JavetException if a V8 error occurs
     */
    public Long mapGetLong(
            IV8ValueMap iV8ValueMap, V8Value key)
            throws JavetException {
        return v8Runtime.mapGetLong(iV8ValueMap, key);
    }

    /**
     * Gets the size of a V8 map.
     *
     * @param iV8ValueMap the V8 value map
     * @return the map size
     * @throws JavetException if a V8 error occurs
     */
    public int mapGetSize(IV8ValueMap iV8ValueMap) throws JavetException {
        return v8Runtime.mapGetSize(iV8ValueMap);
    }

    /**
     * Gets a string value from a V8 map by key.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @return the string value, or null
     * @throws JavetException if a V8 error occurs
     */
    public String mapGetString(
            IV8ValueMap iV8ValueMap, V8Value key)
            throws JavetException {
        return v8Runtime.mapGetString(iV8ValueMap, key);
    }

    /**
     * Checks whether a V8 map contains the given value.
     *
     * @param iV8ValueMap the V8 value map
     * @param value       the value to check
     * @return true if the map contains the value
     * @throws JavetException if a V8 error occurs
     */
    public boolean mapHas(IV8ValueMap iV8ValueMap, V8Value value) throws JavetException {
        return v8Runtime.mapHas(iV8ValueMap, value);
    }

    /**
     * Sets key-value pairs in a V8 map.
     *
     * @param iV8ValueMap the V8 value map
     * @param v8Values    the key-value pairs
     * @return true if the pairs were set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean mapSet(IV8ValueMap iV8ValueMap, V8Value... v8Values) throws JavetException {
        return v8Runtime.mapSet(iV8ValueMap, v8Values);
    }

    /**
     * Sets a boolean value in a V8 map.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @param value       the boolean value
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean mapSetBoolean(
            IV8ValueMap iV8ValueMap, V8Value key, boolean value)
            throws JavetException {
        return v8Runtime.mapSetBoolean(iV8ValueMap, key, value);
    }

    /**
     * Sets a double value in a V8 map.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @param value       the double value
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean mapSetDouble(
            IV8ValueMap iV8ValueMap, V8Value key, double value)
            throws JavetException {
        return v8Runtime.mapSetDouble(iV8ValueMap, key, value);
    }

    /**
     * Sets an integer value in a V8 map.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @param value       the integer value
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean mapSetInteger(
            IV8ValueMap iV8ValueMap, V8Value key, int value)
            throws JavetException {
        return v8Runtime.mapSetInteger(iV8ValueMap, key, value);
    }

    /**
     * Sets a long value in a V8 map.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @param value       the long value
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean mapSetLong(
            IV8ValueMap iV8ValueMap, V8Value key, long value)
            throws JavetException {
        return v8Runtime.mapSetLong(iV8ValueMap, key, value);
    }

    /**
     * Sets a null value in a V8 map.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean mapSetNull(IV8ValueMap iV8ValueMap, V8Value key) throws JavetException {
        return v8Runtime.mapSetNull(iV8ValueMap, key);
    }

    /**
     * Sets a string value in a V8 map.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @param value       the string value
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean mapSetString(
            IV8ValueMap iV8ValueMap, V8Value key, String value)
            throws JavetException {
        return v8Runtime.mapSetString(iV8ValueMap, key, value);
    }

    /**
     * Sets an undefined value in a V8 map.
     *
     * @param iV8ValueMap the V8 value map
     * @param key         the key
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean mapSetUndefined(IV8ValueMap iV8ValueMap, V8Value key) throws JavetException {
        return v8Runtime.mapSetUndefined(iV8ValueMap, key);
    }

    /**
     * Evaluates a V8 module.
     *
     * @param <T>            the type of return value
     * @param iV8Module      the V8 module
     * @param resultRequired whether a result is required
     * @return the evaluation result
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public <T extends V8Value> T moduleEvaluate(
            IV8Module iV8Module, boolean resultRequired) throws JavetException {
        return v8Runtime.moduleEvaluate(iV8Module, resultRequired);
    }

    /**
     * Gets the cached data of a V8 module.
     *
     * @param iV8Module the V8 module
     * @return the cached data as a byte array
     * @throws JavetException if a V8 error occurs
     */
    public byte[] moduleGetCachedData(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleGetCachedData(iV8Module);
    }

    /**
     * Gets the exception from a V8 module.
     *
     * @param iV8Module the V8 module
     * @return the V8 value error
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public V8ValueError moduleGetException(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleGetException(iV8Module);
    }

    /**
     * Gets the identity hash of a V8 module.
     *
     * @param iV8Module the V8 module
     * @return the identity hash
     * @throws JavetException if a V8 error occurs
     */
    public int moduleGetIdentityHash(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleGetIdentityHash(iV8Module);
    }

    /**
     * Gets the namespace of a V8 module.
     *
     * @param iV8Module the V8 module
     * @return the namespace V8 value
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public V8Value moduleGetNamespace(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleGetNamespace(iV8Module);
    }

    /**
     * Gets the resource name of a V8 module.
     *
     * @param iV8Module the V8 module
     * @return the resource name
     * @throws JavetException if a V8 error occurs
     */
    public String moduleGetResourceName(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleGetResourceName(iV8Module);
    }

    /**
     * Gets the script ID of a V8 module.
     *
     * @param iV8Module the V8 module
     * @return the script ID
     * @throws JavetException if a V8 error occurs
     */
    public int moduleGetScriptId(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleGetScriptId(iV8Module);
    }

    /**
     * Gets the status of a V8 module.
     *
     * @param iV8Module the V8 module
     * @return the module status
     * @throws JavetException if a V8 error occurs
     */
    public int moduleGetStatus(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleGetStatus(iV8Module);
    }

    /**
     * Instantiates a V8 module.
     *
     * @param iV8Module the V8 module
     * @return true if the module was instantiated successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean moduleInstantiate(IV8Module iV8Module) throws JavetException {
        return v8Runtime.moduleInstantiate(iV8Module);
    }

    /**
     * Checks whether a V8 module is a source text module.
     *
     * @param iV8Module the V8 module
     * @return true if the module is a source text module
     */
    public boolean moduleIsSourceTextModule(IV8Module iV8Module) {
        return v8Runtime.moduleIsSourceTextModule(iV8Module);
    }

    /**
     * Checks whether a V8 module is a synthetic module.
     *
     * @param iV8Module the V8 module
     * @return true if the module is a synthetic module
     */
    public boolean moduleIsSyntheticModule(IV8Module iV8Module) {
        return v8Runtime.moduleIsSyntheticModule(iV8Module);
    }

    /**
     * Deletes a property from a V8 object by key.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @return true if the property was deleted
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectDelete(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.objectDelete(iV8ValueObject, key);
    }

    /**
     * Deletes a private property from a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @param propertyName   the private property name
     * @return true if the private property was deleted
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectDeletePrivateProperty(IV8ValueObject iV8ValueObject, String propertyName) throws JavetException {
        return v8Runtime.objectDeletePrivateProperty(iV8ValueObject, propertyName);
    }

    /**
     * Gets a value from a V8 object by key.
     *
     * @param <T>            the type of V8 value
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @return the V8 value
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public <T extends V8Value> T objectGet(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.objectGet(iV8ValueObject, key);
    }

    /**
     * Gets a boolean value from a V8 object by key.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @return the boolean value, or null
     * @throws JavetException if a V8 error occurs
     */
    public Boolean objectGetBoolean(
            IV8ValueObject iV8ValueObject, V8Value key)
            throws JavetException {
        return v8Runtime.objectGetBoolean(iV8ValueObject, key);
    }

    /**
     * Gets a double value from a V8 object by key.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @return the double value, or null
     * @throws JavetException if a V8 error occurs
     */
    public Double objectGetDouble(
            IV8ValueObject iV8ValueObject, V8Value key)
            throws JavetException {
        return v8Runtime.objectGetDouble(iV8ValueObject, key);
    }

    /**
     * Gets the identity hash of a V8 value reference.
     *
     * @param iV8ValueReference the V8 value reference
     * @return the identity hash
     * @throws JavetException if a V8 error occurs
     */
    public int objectGetIdentityHash(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Runtime.objectGetIdentityHash(iV8ValueReference);
    }

    /**
     * Gets an integer value from a V8 object by key.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @return the integer value, or null
     * @throws JavetException if a V8 error occurs
     */
    public Integer objectGetInteger(
            IV8ValueObject iV8ValueObject, V8Value key)
            throws JavetException {
        return v8Runtime.objectGetInteger(iV8ValueObject, key);
    }

    /**
     * Gets a long value from a V8 object by key.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @return the long value, or null
     * @throws JavetException if a V8 error occurs
     */
    public Long objectGetLong(
            IV8ValueObject iV8ValueObject, V8Value key)
            throws JavetException {
        return v8Runtime.objectGetLong(iV8ValueObject, key);
    }

    /**
     * Gets the own property names of a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @return the own property names as a V8 value array
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public IV8ValueArray objectGetOwnPropertyNames(
            IV8ValueObject iV8ValueObject) throws JavetException {
        return v8Runtime.objectGetOwnPropertyNames(iV8ValueObject);
    }

    /**
     * Gets a private property from a V8 object.
     *
     * @param <T>            the type of V8 value
     * @param iV8ValueObject the V8 value object
     * @param propertyName   the private property name
     * @return the V8 value
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public <T extends V8Value> T objectGetPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName)
            throws JavetException {
        return v8Runtime.objectGetPrivateProperty(iV8ValueObject, propertyName);
    }

    /**
     * Gets a property from a V8 object by key.
     *
     * @param <T>            the type of V8 value
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @return the property V8 value
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public <T extends V8Value> T objectGetProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.objectGetProperty(iV8ValueObject, key);
    }

    /**
     * Gets all property names of a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @return the property names as a V8 value array
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public IV8ValueArray objectGetPropertyNames(IV8ValueObject iV8ValueObject) throws JavetException {
        return v8Runtime.objectGetPropertyNames(iV8ValueObject);
    }

    /**
     * Gets the prototype of a V8 object.
     *
     * @param <T>            the type of V8 value object
     * @param iV8ValueObject the V8 value object
     * @return the prototype V8 value object
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public <T extends IV8ValueObject> T objectGetPrototype(IV8ValueObject iV8ValueObject) throws JavetException {
        return v8Runtime.objectGetPrototype(iV8ValueObject);
    }

    /**
     * Gets a string value from a V8 object by key.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @return the string value, or null
     * @throws JavetException if a V8 error occurs
     */
    public String objectGetString(
            IV8ValueObject iV8ValueObject, V8Value key)
            throws JavetException {
        return v8Runtime.objectGetString(iV8ValueObject, key);
    }

    /**
     * Checks whether a V8 object has the given value.
     *
     * @param iV8ValueObject the V8 value object
     * @param value          the value to check
     * @return true if the object has the value
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectHas(IV8ValueObject iV8ValueObject, V8Value value) throws JavetException {
        return v8Runtime.objectHas(iV8ValueObject, value);
    }

    /**
     * Checks whether a V8 object has an own property with the given key.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @return true if the object has the own property
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectHasOwnProperty(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.objectHasOwnProperty(iV8ValueObject, key);
    }

    /**
     * Checks whether a V8 object has a private property with the given name.
     *
     * @param iV8ValueObject the V8 value object
     * @param propertyName   the private property name
     * @return true if the object has the private property
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectHasPrivateProperty(IV8ValueObject iV8ValueObject, String propertyName) throws JavetException {
        return v8Runtime.objectHasPrivateProperty(iV8ValueObject, propertyName);
    }

    /**
     * Invokes a function on a V8 object by function name.
     *
     * @param <T>            the type of return value
     * @param iV8ValueObject the V8 value object
     * @param functionName   the function name
     * @param returnResult   whether to return the result
     * @param v8Values       the arguments
     * @return the result of the invocation
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public <T extends V8Value> T objectInvoke(
            IV8ValueObject iV8ValueObject, String functionName, boolean returnResult, V8Value... v8Values)
            throws JavetException {
        return v8Runtime.objectInvoke(iV8ValueObject, functionName, returnResult, v8Values);
    }

    /**
     * Checks whether a V8 object is frozen.
     *
     * @param iV8ValueObject the V8 value object
     * @return true if the object is frozen
     */
    public boolean objectIsFrozen(IV8ValueObject iV8ValueObject) {
        return v8Runtime.objectIsFrozen(iV8ValueObject);
    }

    /**
     * Checks whether a V8 object is sealed.
     *
     * @param iV8ValueObject the V8 value object
     * @return true if the object is sealed
     */
    public boolean objectIsSealed(IV8ValueObject iV8ValueObject) {
        return v8Runtime.objectIsSealed(iV8ValueObject);
    }

    /**
     * Sets key-value pairs on a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @param v8Values       the key-value pairs
     * @return true if the pairs were set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectSet(IV8ValueObject iV8ValueObject, V8Value... v8Values) throws JavetException {
        return v8Runtime.objectSet(iV8ValueObject, v8Values);
    }

    /**
     * Sets an accessor (getter/setter) on a V8 object.
     *
     * @param iV8ValueObject              the V8 value object
     * @param propertyName                the property name
     * @param javetCallbackContextGetter  the getter callback context
     * @param javetCallbackContextSetter  the setter callback context
     * @return true if the accessor was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectSetAccessor(
            IV8ValueObject iV8ValueObject,
            V8Value propertyName,
            JavetCallbackContext javetCallbackContextGetter,
            JavetCallbackContext javetCallbackContextSetter) throws JavetException {
        return v8Runtime.objectSetAccessor(
                iV8ValueObject, propertyName, javetCallbackContextGetter, javetCallbackContextSetter);
    }

    /**
     * Sets a boolean value on a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @param value          the boolean value
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectSetBoolean(
            IV8ValueObject iV8ValueObject, V8Value key, boolean value)
            throws JavetException {
        return v8Runtime.objectSetBoolean(iV8ValueObject, key, value);
    }

    /**
     * Sets a double value on a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @param value          the double value
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectSetDouble(
            IV8ValueObject iV8ValueObject, V8Value key, double value)
            throws JavetException {
        return v8Runtime.objectSetDouble(iV8ValueObject, key, value);
    }

    /**
     * Sets an integer value on a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @param value          the integer value
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectSetInteger(
            IV8ValueObject iV8ValueObject, V8Value key, int value)
            throws JavetException {
        return v8Runtime.objectSetInteger(iV8ValueObject, key, value);
    }

    /**
     * Sets a long value on a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @param value          the long value
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectSetLong(
            IV8ValueObject iV8ValueObject, V8Value key, long value)
            throws JavetException {
        return v8Runtime.objectSetLong(iV8ValueObject, key, value);
    }

    /**
     * Sets a null value on a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectSetNull(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.objectSetNull(iV8ValueObject, key);
    }

    /**
     * Sets a private property on a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @param propertyName   the private property name
     * @param propertyValue  the property value
     * @return true if the private property was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectSetPrivateProperty(
            IV8ValueObject iV8ValueObject, String propertyName, V8Value propertyValue)
            throws JavetException {
        return v8Runtime.objectSetPrivateProperty(iV8ValueObject, propertyName, propertyValue);
    }

    /**
     * Sets a property on a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @param value          the value
     * @return true if the property was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectSetProperty(
            IV8ValueObject iV8ValueObject, V8Value key, V8Value value)
            throws JavetException {
        return v8Runtime.objectSetProperty(iV8ValueObject, key, value);
    }

    /**
     * Sets the prototype of a V8 object.
     *
     * @param iV8ValueObject          the V8 value object
     * @param iV8ValueObjectPrototype the prototype V8 value object
     * @return true if the prototype was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectSetPrototype(
            IV8ValueObject iV8ValueObject, IV8ValueObject iV8ValueObjectPrototype)
            throws JavetException {
        return v8Runtime.objectSetPrototype(iV8ValueObject, iV8ValueObjectPrototype);
    }

    /**
     * Sets a string value on a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @param value          the string value
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectSetString(
            IV8ValueObject iV8ValueObject, V8Value key, String value)
            throws JavetException {
        return v8Runtime.objectSetString(iV8ValueObject, key, value);
    }

    /**
     * Sets an undefined value on a V8 object.
     *
     * @param iV8ValueObject the V8 value object
     * @param key            the key
     * @return true if the value was set successfully
     * @throws JavetException if a V8 error occurs
     */
    public boolean objectSetUndefined(IV8ValueObject iV8ValueObject, V8Value key) throws JavetException {
        return v8Runtime.objectSetUndefined(iV8ValueObject, key);
    }

    /**
     * Gets the proto string representation of a V8 value reference.
     *
     * @param iV8ValueReference the V8 value reference
     * @return the proto string
     * @throws JavetException if a V8 error occurs
     */
    public String objectToProtoString(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Runtime.objectToProtoString(iV8ValueReference);
    }

    /**
     * Registers a catch handler on a V8 promise.
     *
     * @param <T>              the type of V8 value promise
     * @param iV8ValuePromise  the V8 value promise
     * @param functionHandle   the catch function handle
     * @return the V8 value promise
     * @throws JavetException if a V8 error occurs
     */
    public <T extends V8ValuePromise> T promiseCatch(
            IV8ValuePromise iV8ValuePromise, IV8ValueFunction functionHandle) throws JavetException {
        return v8Runtime.promiseCatch(iV8ValuePromise, functionHandle);
    }

    /**
     * Gets the promise from a V8 promise.
     *
     * @param iV8ValuePromise the V8 value promise
     * @return the V8 value promise
     * @throws JavetException if a V8 error occurs
     */
    public V8ValuePromise promiseGetPromise(IV8ValuePromise iV8ValuePromise) throws JavetException {
        return v8Runtime.promiseGetPromise(iV8ValuePromise);
    }

    /**
     * Gets the result of a V8 promise.
     *
     * @param <T>             the type of V8 value
     * @param iV8ValuePromise the V8 value promise
     * @return the promise result
     * @throws JavetException if a V8 error occurs
     */
    public <T extends V8Value> T promiseGetResult(IV8ValuePromise iV8ValuePromise) throws JavetException {
        return v8Runtime.promiseGetResult(iV8ValuePromise);
    }

    /**
     * Gets the state of a V8 promise.
     *
     * @param iV8ValuePromise the V8 value promise
     * @return the promise state
     */
    public int promiseGetState(IV8ValuePromise iV8ValuePromise) {
        return v8Runtime.promiseGetState(iV8ValuePromise);
    }

    /**
     * Checks whether a V8 promise has a handler.
     *
     * @param iV8ValuePromise the V8 value promise
     * @return true if the promise has a handler
     */
    public boolean promiseHasHandler(IV8ValuePromise iV8ValuePromise) {
        return v8Runtime.promiseHasHandler(iV8ValuePromise);
    }

    /**
     * Marks a V8 promise as handled.
     *
     * @param iV8ValuePromise the V8 value promise
     */
    public void promiseMarkAsHandled(IV8ValuePromise iV8ValuePromise) {
        v8Runtime.promiseMarkAsHandled(iV8ValuePromise);
    }

    /**
     * Rejects a V8 promise with a value.
     *
     * @param v8ValuePromise the V8 value promise
     * @param v8Value        the rejection value
     * @return true if the promise was rejected successfully
     */
    public boolean promiseReject(V8ValuePromise v8ValuePromise, V8Value v8Value) {
        return v8Runtime.promiseReject(v8ValuePromise, v8Value);
    }

    /**
     * Resolves a V8 promise with a value.
     *
     * @param v8ValuePromise the V8 value promise
     * @param v8Value        the resolution value
     * @return true if the promise was resolved successfully
     */
    public boolean promiseResolve(V8ValuePromise v8ValuePromise, V8Value v8Value) {
        return v8Runtime.promiseResolve(v8ValuePromise, v8Value);
    }

    /**
     * Registers then handlers on a V8 promise.
     *
     * @param <T>                      the type of V8 value promise
     * @param iV8ValuePromise          the V8 value promise
     * @param functionFulfilledHandle   the fulfilled function handle
     * @param functionRejectedHandle    the rejected function handle
     * @return the V8 value promise
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public <T extends V8ValuePromise> T promiseThen(
            IV8ValuePromise iV8ValuePromise, IV8ValueFunction functionFulfilledHandle,
            IV8ValueFunction functionRejectedHandle) throws JavetException {
        return v8Runtime.promiseThen(iV8ValuePromise, functionFulfilledHandle, functionRejectedHandle);
    }

    /**
     * Gets the handler of a V8 proxy.
     *
     * @param iV8ValueProxy the V8 value proxy
     * @return the handler V8 value object
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public V8ValueObject proxyGetHandler(IV8ValueProxy iV8ValueProxy) throws JavetException {
        return v8Runtime.proxyGetHandler(iV8ValueProxy);
    }

    /**
     * Gets the target of a V8 proxy.
     *
     * @param iV8ValueProxy the V8 value proxy
     * @return the target V8 value object
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public V8ValueObject proxyGetTarget(IV8ValueProxy iV8ValueProxy) throws JavetException {
        return v8Runtime.proxyGetTarget(iV8ValueProxy);
    }

    /**
     * Checks whether a V8 proxy is revoked.
     *
     * @param iV8ValueProxy the V8 value proxy
     * @return true if the proxy is revoked
     * @throws JavetException if a V8 error occurs
     */
    public boolean proxyIsRevoked(IV8ValueProxy iV8ValueProxy) throws JavetException {
        return v8Runtime.proxyIsRevoked(iV8ValueProxy);
    }

    /**
     * Revokes a V8 proxy.
     *
     * @param iV8ValueProxy the V8 value proxy
     * @throws JavetException if a V8 error occurs
     */
    public void proxyRevoke(IV8ValueProxy iV8ValueProxy) throws JavetException {
        v8Runtime.proxyRevoke(iV8ValueProxy);
    }

    /**
     * Removes a reference from the V8 runtime.
     *
     * @param iV8ValueReference the V8 value reference
     * @throws JavetException if a V8 error occurs
     */
    public void removeReference(IV8ValueReference iV8ValueReference) throws JavetException {
        v8Runtime.removeReference(iV8ValueReference);
    }

    /**
     * Tests whether two V8 objects have the same value (using SameValue semantics).
     *
     * @param iV8ValueObject1 the first V8 value object
     * @param iV8ValueObject2 the second V8 value object
     * @return true if the objects have the same value
     */
    public boolean sameValue(IV8ValueObject iV8ValueObject1, IV8ValueObject iV8ValueObject2) {
        return v8Runtime.sameValue(iV8ValueObject1, iV8ValueObject2);
    }

    /**
     * Gets the cached data of a V8 script.
     *
     * @param iV8Script the V8 script
     * @return the cached data as a byte array
     * @throws JavetException if a V8 error occurs
     */
    public byte[] scriptGetCachedData(IV8Script iV8Script) throws JavetException {
        return v8Runtime.scriptGetCachedData(iV8Script);
    }

    /**
     * Gets the resource name of a V8 script.
     *
     * @param iV8Script the V8 script
     * @return the resource name
     * @throws JavetException if a V8 error occurs
     */
    public String scriptGetResourceName(IV8Script iV8Script) throws JavetException {
        return v8Runtime.scriptGetResourceName(iV8Script);
    }

    /**
     * Runs a V8 script.
     *
     * @param <T>            the type of return value
     * @param iV8Script      the V8 script
     * @param resultRequired whether a result is required
     * @return the script result
     * @throws JavetException if a V8 error occurs
     */
    public <T extends V8Value> T scriptRun(
            IV8Script iV8Script, boolean resultRequired) throws JavetException {
        return v8Runtime.scriptRun(iV8Script, resultRequired);
    }

    /**
     * Adds a value to a V8 set.
     *
     * @param iV8ValueSet the V8 value set
     * @param key         the value to add
     * @throws JavetException if a V8 error occurs
     */
    public void setAdd(IV8ValueSet iV8ValueSet, V8Value key) throws JavetException {
        v8Runtime.setAdd(iV8ValueSet, key);
    }

    /**
     * Converts a V8 set to a V8 array.
     *
     * @param iV8ValueSet the V8 value set
     * @return the V8 value array
     * @throws JavetException if a V8 error occurs
     */
    @CheckReturnValue
    public V8ValueArray setAsArray(IV8ValueSet iV8ValueSet) throws JavetException {
        return v8Runtime.setAsArray(iV8ValueSet);
    }

    /**
     * Clears all values from a V8 set.
     *
     * @param iV8ValueSet the V8 value set
     */
    public void setClear(IV8ValueSet iV8ValueSet) {
        v8Runtime.setClear(iV8ValueSet);
    }

    /**
     * Deletes a value from a V8 set.
     *
     * @param iV8ValueSet the V8 value set
     * @param key         the value to delete
     * @return true if the value was deleted
     * @throws JavetException if a V8 error occurs
     */
    public boolean setDelete(IV8ValueSet iV8ValueSet, V8Value key) throws JavetException {
        return v8Runtime.setDelete(iV8ValueSet, key);
    }

    /**
     * Gets the size of a V8 set.
     *
     * @param iV8ValueSet the V8 value set
     * @return the set size
     * @throws JavetException if a V8 error occurs
     */
    public int setGetSize(IV8ValueSet iV8ValueSet) throws JavetException {
        return v8Runtime.setGetSize(iV8ValueSet);
    }

    /**
     * Checks whether a V8 set contains the given value.
     *
     * @param iV8ValueSet the V8 value set
     * @param key         the value to check
     * @return true if the set contains the value
     * @throws JavetException if a V8 error occurs
     */
    public boolean setHas(IV8ValueSet iV8ValueSet, V8Value key) throws JavetException {
        return v8Runtime.setHas(iV8ValueSet, key);
    }

    /**
     * Sets a V8 value reference to weak.
     *
     * @param iV8ValueReference the V8 value reference
     */
    public void setWeak(IV8ValueReference iV8ValueReference) {
        v8Runtime.setWeak(iV8ValueReference);
    }

    /**
     * Tests whether two V8 objects are strictly equal.
     *
     * @param iV8ValueObject1 the first V8 value object
     * @param iV8ValueObject2 the second V8 value object
     * @return true if the objects are strictly equal
     */
    public boolean strictEquals(IV8ValueObject iV8ValueObject1, IV8ValueObject iV8ValueObject2) {
        return v8Runtime.strictEquals(iV8ValueObject1, iV8ValueObject2);
    }

    /**
     * Gets the primitive string value of a V8 string object.
     *
     * @param v8ValueStringObject the V8 value string object
     * @return the V8 value string
     */
    public V8ValueString stringObjectValueOf(V8ValueStringObject v8ValueStringObject) {
        return v8Runtime.stringObjectValueOf(v8ValueStringObject);
    }

    /**
     * Gets the description of a V8 symbol.
     *
     * @param v8ValueSymbol the V8 value symbol
     * @return the symbol description
     */
    public String symbolDescription(V8ValueSymbol v8ValueSymbol) {
        return v8Runtime.symbolDescription(v8ValueSymbol);
    }

    /**
     * Gets the primitive symbol value of a V8 symbol object.
     *
     * @param v8ValueSymbolObject the V8 value symbol object
     * @return the V8 value symbol
     */
    public V8ValueSymbol symbolObjectValueOf(V8ValueSymbolObject v8ValueSymbolObject) {
        return v8Runtime.symbolObjectValueOf(v8ValueSymbolObject);
    }

    /**
     * Converts a V8 symbol to a V8 symbol object.
     *
     * @param v8ValueSymbol the V8 value symbol
     * @return the V8 value symbol object
     */
    public V8ValueSymbolObject symbolToObject(V8ValueSymbol v8ValueSymbol) {
        return v8Runtime.symbolToObject(v8ValueSymbol);
    }

    /**
     * Gets the string representation of a V8 value reference.
     *
     * @param iV8ValueReference the V8 value reference
     * @return the string representation
     * @throws JavetException if a V8 error occurs
     */
    public String toString(IV8ValueReference iV8ValueReference) throws JavetException {
        return v8Runtime.toString(iV8ValueReference);
    }
}
