/*
 * Copyright (c) 2021-2021. caoccao.com Sam Cao
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

package com.caoccao.javet.utils;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.values.V8Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type V8 scope is for preventing memory leak when exception is thrown.
 * It needs to be used by try-with-resource.
 * By default, escapable is set to false so that the internal value can be close when exception is thrown.
 * If there is no exception, escapable needs to be set to true before try-with-resource is closed.
 * <p>
 * Usage:
 * <pre>
 * try (V8Scope v8Scope = new V8Scope()) {
 *     V8ValueObject v8ValueObject = v8Scope.add(v8Runtime.createV8ValueObject());
 *     // v8ValueObject will be closed automatically if there is an exception thrown.
 *     v8Scope.setEscapable();
 *     // v8ValueObject will not be closed.
 *     return v8ValueObject;
 * }
 * </pre>
 *
 * @since 0.9.13
 */
public class V8Scope implements IJavetClosable {
    /**
     * The Closed.
     */
    protected boolean closed;
    /**
     * The Escapable.
     */
    protected boolean escapable;
    /**
     * The Values.
     */
    protected List<V8Value> values;

    /**
     * Instantiates a new V8 virtual escapable value.
     *
     * @since 0.9.13
     */
    public V8Scope() {
        closed = false;
        escapable = false;
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
