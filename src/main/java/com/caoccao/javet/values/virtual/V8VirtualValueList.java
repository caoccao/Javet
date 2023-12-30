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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The type V8 virtual value list.
 *
 * @since 0.8.5
 */
@SuppressWarnings("unchecked")
public class V8VirtualValueList implements IJavetClosable {
    /**
     * The To be closed values.
     *
     * @since 0.8.5
     */
    protected List<V8Value> toBeClosedValues;
    /**
     * The Values.
     *
     * @since 0.8.5
     */
    protected List<V8Value> values;

    /**
     * Instantiates a new V8 virtual value list.
     *
     * @param v8Runtime the V8 runtime
     * @param converter the converter
     * @param objects   the objects
     * @throws JavetException the javet exception
     * @since 0.8.5
     */
    public V8VirtualValueList(V8Runtime v8Runtime, IJavetConverter converter, Object... objects) throws JavetException {
        Objects.requireNonNull(v8Runtime);
        if (objects == null || objects.length == 0) {
            toBeClosedValues = null;
            values = null;
        } else {
            toBeClosedValues = new ArrayList<>(objects.length);
            values = new ArrayList<>(objects.length);
            for (Object object : objects) {
                if (object instanceof V8Value) {
                    values.add((V8Value) object);
                } else {
                    V8Value value = converter == null ? v8Runtime.toV8Value(object) : converter.toV8Value(v8Runtime, object);
                    values.add(value);
                    toBeClosedValues.add(value);
                }
            }
        }
    }

    @Override
    public void close() throws JavetException {
        JavetResourceUtils.safeClose(toBeClosedValues);
        toBeClosedValues = null;
        values = null;
    }

    /**
     * Get V8 value array.
     *
     * @return the V8 value array
     * @since 0.8.5
     */
    public V8Value[] get() {
        return values == null ? new V8Value[0] : values.toArray(new V8Value[0]);
    }

    @Override
    public boolean isClosed() {
        return values == null;
    }
}
