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

import com.caoccao.javet.enums.V8ValueReferenceType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.primitive.*;
import com.caoccao.javet.values.reference.*;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type V8 scope is for preventing memory leak when exception is thrown.
 * It needs to be used by try-with-resource.
 * By default, escapable is set to false so that the internal value can be close when exception is thrown.
 * If there is no exception, escapable needs to be set to true before try-with-resource is closed.
 * <p>
 * Usage 1 without V8 runtime
 * <pre>
 * try (V8Scope v8Scope = new V8Scope()) {
 *     V8ValueObject v8ValueObject = v8Scope.add(v8Runtime.createV8ValueObject());
 *     // v8ValueObject will be closed automatically if there is an exception thrown.
 *     v8Scope.setEscapable();
 *     // v8ValueObject will not be closed.
 *     return v8ValueObject;
 * }
 * </pre>
 * <p>
 * Usage 2 with V8 runtime
 * <pre>
 * try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
 *     V8ValueObject v8ValueObject = v8Scope.createV8ValueObject();
 *     // v8ValueObject will be closed automatically if there is an exception thrown.
 *     v8Scope.setEscapable();
 *     // v8ValueObject will not be closed.
 *     return v8ValueObject;
 * }
 * </pre>
 *
 * @since 0.9.13
 */
public class V8Scope implements IV8Creatable, IJavetClosable {
    /**
     * The constant ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY.
     *
     * @since 0.9.14
     */
    protected static final String ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY = "V8 runtime cannot be empty.";
    /**
     * The Closed.
     *
     * @since 0.9.13
     */
    protected boolean closed;
    /**
     * The Escapable.
     *
     * @since 0.9.13
     */
    protected boolean escapable;
    /**
     * The V8 runtime.
     *
     * @since 0.9.14
     */
    protected V8Runtime v8Runtime;
    /**
     * The Values.
     *
     * @since 0.9.14
     */
    protected List<V8Value> values;

    /**
     * Instantiates a new V8 scope.
     *
     * @since 0.9.13
     */
    public V8Scope() {
        this(null);
    }

    /**
     * Instantiates a new V8 scope by V8 runtime.
     *
     * @param v8Runtime the V8 runtime
     * @since 0.9.14
     */
    V8Scope(V8Runtime v8Runtime) {
        closed = false;
        escapable = false;
        this.v8Runtime = v8Runtime;
        values = new ArrayList<>();
    }

    /**
     * Add a value.
     *
     * @param <T>   the type parameter
     * @param value the value
     * @return the value
     * @since 0.9.14
     */
    public <T extends V8Value> T add(T value) {
        values.add(Objects.requireNonNull(value));
        return value;
    }

    @Override
    public void close() throws JavetException {
        if (!closed) {
            if (!escapable) {
                JavetResourceUtils.safeClose(values);
            }
            closed = true;
        }
    }

    @Override
    public V8Module createV8Module(String moduleName, IV8ValueObject iV8ValueObject) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8Module(moduleName, iV8ValueObject));
    }

    @Override
    public V8ValueArray createV8ValueArray() throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueArray());
    }

    @Override
    public V8ValueArrayBuffer createV8ValueArrayBuffer(int length) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueArrayBuffer(length));
    }

    @Override
    public V8ValueArrayBuffer createV8ValueArrayBuffer(ByteBuffer byteBuffer) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueArrayBuffer(byteBuffer));
    }

    @Override
    public V8ValueBigInteger createV8ValueBigInteger(BigInteger bigInteger) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueBigInteger(bigInteger));
    }

    @Override
    public V8ValueBigInteger createV8ValueBigInteger(String bigIntegerValue) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueBigInteger(bigIntegerValue));
    }

    @Override
    public V8ValueBoolean createV8ValueBoolean(boolean booleanValue) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return v8Runtime.createV8ValueBoolean(booleanValue);
    }

    @Override
    public V8ValueDataView createV8ValueDataView(V8ValueArrayBuffer v8ValueArrayBuffer) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueDataView(v8ValueArrayBuffer));
    }

    @Override
    public V8ValueDouble createV8ValueDouble(double doubleValue) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return v8Runtime.createV8ValueDouble(doubleValue);
    }

    @Override
    public V8ValueFunction createV8ValueFunction(JavetCallbackContext javetCallbackContext) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueFunction(javetCallbackContext));
    }

    @Override
    public V8ValueFunction createV8ValueFunction(String codeString) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueFunction(codeString));
    }

    @Override
    public V8ValueInteger createV8ValueInteger(int integerValue) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return v8Runtime.createV8ValueInteger(integerValue);
    }

    @Override
    public V8ValueLong createV8ValueLong(long longValue) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return v8Runtime.createV8ValueLong(longValue);
    }

    @Override
    public V8ValueMap createV8ValueMap() throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueMap());
    }

    @Override
    public V8ValueNull createV8ValueNull() {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return v8Runtime.createV8ValueNull();
    }

    @Override
    public V8ValueObject createV8ValueObject() throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueObject());
    }

    @Override
    public V8ValuePromise createV8ValuePromise() throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValuePromise());
    }

    @Override
    public V8ValueProxy createV8ValueProxy(V8ValueObject v8ValueObject) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueProxy(v8ValueObject));
    }

    @Override
    public V8ValueSet createV8ValueSet() throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueSet());
    }

    @Override
    public V8ValueString createV8ValueString(String str) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return v8Runtime.createV8ValueString(str);
    }

    @Override
    public V8ValueSymbol createV8ValueSymbol(String description, boolean global) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueSymbol(description, global));
    }

    @Override
    public V8ValueTypedArray createV8ValueTypedArray(V8ValueReferenceType type, int length) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return add(v8Runtime.createV8ValueTypedArray(type, length));
    }

    @Override
    public V8ValueUndefined createV8ValueUndefined() {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return v8Runtime.createV8ValueUndefined();
    }

    @Override
    public V8ValueZonedDateTime createV8ValueZonedDateTime(long jsTimestamp) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return v8Runtime.createV8ValueZonedDateTime(jsTimestamp);
    }

    @Override
    public V8ValueZonedDateTime createV8ValueZonedDateTime(ZonedDateTime zonedDateTime) throws JavetException {
        Objects.requireNonNull(v8Runtime, ERROR_MESSAGE_V8_RUNTIME_CANNOT_BE_EMPTY);
        return v8Runtime.createV8ValueZonedDateTime(zonedDateTime);
    }

    /**
     * Gets a value by index.
     *
     * @param <T>   the type parameter
     * @param index the index
     * @return the value
     * @since 0.9.13
     */
    @SuppressWarnings("unchecked")
    public <T extends V8Value> T get(int index) {
        return (T) values.get(index);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    /**
     * Is escapable.
     *
     * @return the boolean
     * @since 0.9.13
     */
    public boolean isEscapable() {
        return escapable;
    }

    /**
     * Sets escapable to true.
     *
     * @return the self
     * @since 0.9.13
     */
    @SuppressWarnings("UnusedReturnValue")
    public V8Scope setEscapable() {
        return setEscapable(true);
    }

    /**
     * Sets escapable.
     *
     * @param escapable the escapable
     * @return the self
     * @since 0.9.13
     */
    public V8Scope setEscapable(boolean escapable) {
        this.escapable = escapable;
        return this;
    }
}
