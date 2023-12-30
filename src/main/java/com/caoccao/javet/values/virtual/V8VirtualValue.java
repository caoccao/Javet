/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
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
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.converters.IJavetConverter;
import com.caoccao.javet.utils.JavetResourceUtils;
import com.caoccao.javet.values.V8Value;

import java.util.Objects;

/**
 * The type V8 virtual value.
 *
 * @since 0.8.5
 */
@SuppressWarnings("unchecked")
public class V8VirtualValue implements IJavetClosable {
    /**
     * It indicates whether the value is converted or not.
     * true: converted
     * false: not converted
     *
     * @since 0.8.5
     */
    protected boolean converted;
    /**
     * The Value.
     *
     * @since 0.8.5
     */
    protected V8Value value;

    /**
     * Instantiates a new V8 virtual value.
     *
     * @param v8Runtime the V8 runtime
     * @param converter the converter
     * @param object    the object
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    public V8VirtualValue(V8Runtime v8Runtime, IJavetConverter converter, Object object) throws JavetException {
        Objects.requireNonNull(v8Runtime);
        if (object instanceof V8Value) {
            converted = false;
            value = (V8Value) object;
        } else {
            converted = true;
            value = converter == null ? v8Runtime.toV8Value(object) : converter.toV8Value(v8Runtime, object);
        }
    }

    @Override
    public void close() throws JavetException {
        if (converted) {
            JavetResourceUtils.safeClose(value);
        }
        value = null;
    }

    /**
     * Get the value.
     *
     * @param <T> the type parameter
     * @return the value
     * @since 0.8.5
     */
    public <T extends V8Value> T get() {
        return (T) value;
    }

    @Override
    public boolean isClosed() {
        return value == null || value.isClosed();
    }
}
