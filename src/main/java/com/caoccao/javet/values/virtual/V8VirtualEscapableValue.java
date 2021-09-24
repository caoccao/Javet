/*
 * Copyright (c) 2021. caoccao.com Sam Cao
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

package com.caoccao.javet.values.virtual;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interfaces.IJavetClosable;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;

import java.util.Objects;

/**
 * The type V8 virtual escapable value is for preventing memory leak when exception is thrown.
 * It needs to be used by try-with-resource.
 * By default, escapable is set to false so that the internal value can be close when exception is thrown.
 * If there is no exception, escapable needs to be set to true before try-with-resource is closed.
 * <p>
 * Usage:
 * <pre>
 * try (V8VirtualEscapableValue&lt;V8ValueObject&gt; v8VirtualEscapableValueObject =
 *         new V8VirtualEscapableValue&lt;&gt;(v8Runtime.createV8ValueObject())) {
 *     // Do whatever.
 *     return v8VirtualEscapableValueObject.setEscapable().get();
 * }
 * </pre>
 *
 * @param <T> the type parameter
 * @since 0.9.13
 */
public class V8VirtualEscapableValue<T extends V8Value> implements IJavetClosable {
    protected boolean closed;
    protected boolean escapable;
    protected T value;

    /**
     * Instantiates a new V8 virtual escapable value.
     *
     * @param value the value
     * @since 0.9.13
     */
    public V8VirtualEscapableValue(T value) {
        closed = false;
        escapable = false;
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public void close() throws JavetException {
        if (!closed) {
            if (!escapable) {
                JavetResourceUtils.safeClose(value);
            }
            closed = true;
        }
    }

    /**
     * Gets value.
     *
     * @return the value
     * @since 0.9.13
     */
    public T get() {
        return value;
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
     * Sets escapable.
     *
     * @return the self
     * @since 0.9.13
     */
    public V8VirtualEscapableValue<T> setEscapable() {
        return setEscapable(true);
    }

    /**
     * Sets escapable.
     *
     * @param escapable the escapable
     * @return the self
     * @since 0.9.13
     */
    public V8VirtualEscapableValue<T> setEscapable(boolean escapable) {
        this.escapable = escapable;
        return this;
    }
}
